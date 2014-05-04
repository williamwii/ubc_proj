/************************************************************************
 * Adapted from a course at Boston University for use in CPSC 317 at UBC
 * 
 * The implementation for the STP sender, and a simple
 * application-level routine to drive the sender.
 *
 * This routine reads the data to be transferred over the connection
 * from a file specified and invokes the STP send functionality to
 * deliver the packets as an ordered sequence of datagrams.
 *
 *************************************************************************/


#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/uio.h>
#include <unistd.h>
#include <sys/file.h>
#include <time.h>
#include <sys/time.h>
#include <arpa/inet.h>

#include "stp.h"
#include "wraparound.h"

#define STP_SUCCESS 1
#define STP_ERROR (-1)

/* Default timeout in seconds and microseconds */
struct timeval TIMEOUT = { 2 , 0 };

/* Struct to save packets that are sent and need to be
 * acknowledged. Includes a pointer for next packet to implement a
 * list of packets.
 */
typedef struct sent_packet {
    char data[STP_MSS];
    int len;
    uint16_t seqno;
    struct timeval timeout; /* when is the packet expected to timeout */
    int times_sent; /* number of times the packet was already sent */
    struct sent_packet *next;
} sent_packet;

/* Main control block passed along functions. */
typedef struct {  
    int fd; /* socket file descriptor */
    uint16_t ISN; /* initial sequence number */
    uint16_t next_seqno; /* next seqno to be used for new data */
    uint16_t acked_seqno; /* last seqno acknowledged */
    uint16_t last_rwnd; /* last received window size */
    
    int count_acks_same_seqno; /* number of times an ACK was received with acked_seqno */
    
    sent_packet *unacked_packets; /* linked list of packets sent and not acked */
    
} stp_send_ctrl_blk;

/* Function that reads a packet and checks if it is an ACK correctly
 * formatted. It waits only until the time provided as timeout.
 */
static int receive_and_check_ack(int fd, stp_header *ack, struct timeval *timeout) {
    
    int timediff;
    struct timeval now;
    int len;
    
    if (timeout) {
        gettimeofday(&now, NULL);
        timediff = (timeout->tv_sec - now.tv_sec) * 1000000 +
        timeout->tv_usec - now.tv_usec;
        if (timediff <= 0)
            timediff = 0;
        else
        /* Adds 999 so that division by 1000 results in ceiling */
            timediff += 999;
    } else
        timediff = 0;
    
    len = readpkt_timer(fd, (char *) ack, timediff / 1000);
    
    if (len == STP_TIMED_OUT) {
        if (timeout) printf("TIMEOUT!\n");
        return STP_TIMED_OUT;
    }
    else if (len < 0) {
        printf("Error receiving ACK.\n");
        reset(fd);
        return len;
    }
    
    if (len < sizeof(stp_header)) {
        printf("Packet is not long enough for an STP header.\n");
        reset(fd);
        return -1;
    }
    
    if (ack->checksum != checksum(ack, len - sizeof(stp_header))) {
        printf("Checksum incorrect, ignoring...\n");
        /* Checksum ignores packet, so waits for another ACK */
        return receive_and_check_ack(fd, ack, timeout);
    }
    
    /* The only packet that the sender is expected to receive in a
     * normal scenario is an ACK.
     */
    if (ntohs(ack->type) == STP_RESET) {
        printf("Receiver detected a problem and sent reset. Resetting too...\n");
        reset(fd);
        return -1;
    }
    else if (ntohs(ack->type) != STP_ACK) {
        printf("Invalid packet type. Resetting...\n");
        reset(fd);
        return -1;
    }
    
    if (len > sizeof(stp_header)) {
        printf("Ack packet should not have data.\n");
        reset(fd);
        return -1;
    }
    
    return len;
}

/* Sends a packet based on the sent_packet struct. Also sets the
 * timeout properly and increments the number of times the packet was
 * sent.
 */
static void send_packet(int fd, sent_packet *packet, int type) {
    
    struct timeval now;
    sendpkt(fd, type, 0, packet->seqno, packet->len ? packet->data : NULL, packet->len);
    gettimeofday(&now, NULL);
    timeradd(&now, &TIMEOUT, &packet->timeout);
    packet->times_sent++;
}

#define WAIT_FOR_DATA_ACKS_NONE_RECEIVED 0
#define WAIT_FOR_DATA_ACKS_ERROR (-1)
#define WAIT_FOR_DATA_ACKS_OLD_ACKS 1
#define WAIT_FOR_DATA_ACKS_NEW_ACKS 2

/* Waits for an ACK to be received. This function will sometimes
 * return without having any ACK actually received. It should be
 * called within a loop that controls when a condition is satisfied,
 * such as if there is space in the receiver window or if there are
 * packets that need to be ACKed for the process to continue. The
 * function returns -1 in case of error, 0 if no packet was ACKed, 1
 * if only old packets were ACKed, or 2 if at least one new packet was
 * ACKed.
 */
static int wait_for_data_acks(stp_send_ctrl_blk *stp_CB, int wait_until_timeout, int fast_retransmit_process) {
    
    unsigned char wrk[STP_MTU];
    stp_header *ack = (stp_header *) wrk;
    struct timeval now, *first_timeout;
    sent_packet *packet, **pktp;
    int acklen;
    uint16_t seqno;
    
    if (!stp_CB->unacked_packets) return WAIT_FOR_DATA_ACKS_NONE_RECEIVED;
    
    if (wait_until_timeout) {
        /* Find the packet with the earliest timeout, which is the base to
         * identify how long the process should wait for an ACK.
         */
        first_timeout = &stp_CB->unacked_packets->timeout;
        for (packet = stp_CB->unacked_packets->next; packet; packet = packet->next)
            if ((!fast_retransmit_process && timercmp(first_timeout, &packet->timeout, >)) ||
                ( fast_retransmit_process && packet->seqno == stp_CB->acked_seqno))
                first_timeout = &packet->timeout;
    }
    else {
        /* In this case, the process should not wait, it should just check
         * for ACKs that are already in the buffer. This is useful to
         * update the receiver window as soon as possible.
         */
        first_timeout = NULL;
    }
    
    acklen = receive_and_check_ack(stp_CB->fd, ack, first_timeout);
    
    if (acklen == STP_TIMED_OUT) {
        
        /* Checks all packets to identify which ones timed out, and
         * resends them.
         */
        gettimeofday(&now, NULL);
        for (packet = stp_CB->unacked_packets; packet; packet = packet->next) {
            
            if ((!fast_retransmit_process && timercmp(&now, &packet->timeout, >)) ||
                ( fast_retransmit_process && packet->seqno == stp_CB->acked_seqno)) {
                
                /* If the packet was already sent three times, reset */
                if (packet->times_sent >= 3) {
                    printf("Packed %d was sent %d times. Resetting...\n",
                           packet->seqno, packet->times_sent);
                    reset(stp_CB->fd);
                    return WAIT_FOR_DATA_ACKS_ERROR;
                }
                
                send_packet(stp_CB->fd, packet, STP_DATA);
            }
        }
        
        return WAIT_FOR_DATA_ACKS_NONE_RECEIVED;
    }
    else if (acklen < 0)
        return WAIT_FOR_DATA_ACKS_ERROR;
    
    stp_CB->last_rwnd = ntohs(ack->window);
    
    seqno = ntohs(ack->seqno);
    
    if (greater(seqno, stp_CB->next_seqno)) {
        /* This ACK corresponds to a sequence number that was not yet sent. */
        fprintf(stderr, "Acknowledging future packets. Resetting...\n");
        reset(stp_CB->fd);
        return WAIT_FOR_DATA_ACKS_ERROR;
    }
    else if (greater(seqno, stp_CB->acked_seqno)) {
        
        stp_CB->acked_seqno = seqno;
        stp_CB->count_acks_same_seqno = 1;
        
        /* Checks all sent packets, and removes all packets for which the
         * cumulative seqno in the ACK is sufficient to acknowledge the
         * entire packet.
         */
        pktp = &stp_CB->unacked_packets;
        while (*pktp) {
            
            packet = *pktp;
            if (!greater(plus(packet->seqno, packet->len), seqno)) {
                *pktp = packet->next;
                free(packet);
            } else
                pktp = &(*pktp)->next;
        }
        
        return WAIT_FOR_DATA_ACKS_NEW_ACKS;
    }
    else if (seqno == stp_CB->acked_seqno && greater(stp_CB->next_seqno, stp_CB->acked_seqno)) {
        /* Same ACK as before was sent again. Checks if fast
         * retransmission applies. This only applies if there is at least
         * one packet in flight.
         */
        stp_CB->count_acks_same_seqno++;
        
        if (stp_CB->count_acks_same_seqno >= 3 && !fast_retransmit_process) {
            /* Fast retransmission scenario. First, exhausts all ACKs
             * already in the buffer. Because this involves a recursive call
             * to this function, if a new ACK is identified in that
             * scenario, it is ignored so that it is dealt with only once.
             */
            
            printf("%d consecutive ACKs to same seqno. Triggering fast retransmission.\n", stp_CB->count_acks_same_seqno);
            wait_until_timeout = 0;
            
            do {
                
                switch (wait_for_data_acks(stp_CB, wait_until_timeout, 1)) {
                    case WAIT_FOR_DATA_ACKS_ERROR:
                        /* If an error happens, propagates the error */
                        return WAIT_FOR_DATA_ACKS_ERROR;
                    case WAIT_FOR_DATA_ACKS_NEW_ACKS:
                        /* If a new ACK is in buffer, cancel fast retransmission */
                        return WAIT_FOR_DATA_ACKS_NEW_ACKS;
                    case WAIT_FOR_DATA_ACKS_NONE_RECEIVED:
                        /* If no ACK was received, the recursive call already resent
                         * the packet. Wait for timeout now and send again.
                         */
                        wait_until_timeout = 1; break;
                    case WAIT_FOR_DATA_ACKS_OLD_ACKS: default:
                        /* Received a new ACK with old value. Ignore and check again. */
                        break;
                }
            } while (1);
            
        } else
            return WAIT_FOR_DATA_ACKS_OLD_ACKS;
    }
    else {
        /* Previous ACK was resent, probably out of order. */
        return WAIT_FOR_DATA_ACKS_OLD_ACKS;
    }
}

/* Open the sender side of the STP connection. Returns the pointer to
 * a newly allocated control block containing the basic information
 * about the connection. Returns NULL if an error happened.
 */
stp_send_ctrl_blk * stp_open(char *remoteHost, int remotePort, int localPort) {
    
    stp_send_ctrl_blk *stp_CB;
    unsigned char wrk[STP_MTU];
    stp_header *ack = (stp_header *) wrk;
    struct timeval now;
    sent_packet syn;
    int len;
    
    stp_CB = (stp_send_ctrl_blk *) malloc(sizeof(stp_send_ctrl_blk));
    stp_CB->fd = udp_open(remoteHost, remotePort, localPort);
    if (stp_CB->fd < 0) {
        free(stp_CB);
        return NULL;
    }
    
    /* Uses the current time as the basis for random ISN */
    gettimeofday(&now, NULL);
    stp_CB->ISN = (uint16_t) now.tv_usec;
    stp_CB->next_seqno = plus(stp_CB->ISN, 1);
    
    /* SYN packet */
    syn.len = 0;
    syn.seqno = stp_CB->ISN;
    syn.times_sent = 0;
    
    do {
        
        /* If SYN is not ACKed after three times, reset */
        if (syn.times_sent >= 3) {
            reset(stp_CB->fd);
            return NULL;
        }
        
        send_packet(stp_CB->fd, &syn, STP_SYN);
        
        len = receive_and_check_ack(stp_CB->fd, ack, &syn.timeout);
        if (len == -1) {
            reset(stp_CB->fd);
            return NULL;
        }
        
    } while(len == STP_TIMED_OUT);
    
    /* For this scenario the only expected ACK is the SYNACK itself */
    if (ntohs(ack->seqno) != stp_CB->next_seqno) {
        printf("Invalid sequence number. Resetting...\n");
        reset(stp_CB->fd);
        return NULL;
    }
    
    /* Initialize remaining fields in the control block */
    stp_CB->acked_seqno = stp_CB->next_seqno;
    stp_CB->count_acks_same_seqno = 1;
    stp_CB->last_rwnd = ntohs(ack->window);
    stp_CB->unacked_packets = NULL;
    
    return stp_CB;
}

/*
 * Send STP. This routine sends a data packet no greater than MSS
 * bytes. If more than MSS bytes are to be sent, the routine breaks
 * the data into multiple packets. It will keep sending data until the
 * send window is full. At that point it reads data from the network
 * to, hopefully, get enough ACKs to open the window.
 * 
 * The function returns STP_SUCCESS on success, or STP_ERROR on error.
 */
int stp_send(stp_send_ctrl_blk *stp_CB, unsigned char* data, int length) {
    
    sent_packet *pkt, **last;
    
    if (length < 0) {
        fprintf(stderr, "Invalid data length.\n");
        return STP_ERROR;
    }
    
    /* Fragmentation is done through a recursive call to the same function. */
    while (length > STP_MSS) {
        stp_send(stp_CB, data, STP_MSS);
        data += STP_MSS;
        length -= STP_MSS;
    }
    
    /* Check if there are any ACKs already available locally. ACKs are
     * checked without wait to ensure that fast forwarding is executed
     * when possible. Also, if the window is reduced, this change is
     * effective as soon as possible.
     */
    while (wait_for_data_acks(stp_CB, 0, 0) != WAIT_FOR_DATA_ACKS_NONE_RECEIVED);
    
    /* Window full. Wait for ACKs. This loop will exit on two
     * situations: either there is a new ACK that frees some space in
     * the window, or the latest ACK increased the window, adding more
     * space for additional packets.
     */
    while (minus(stp_CB->next_seqno, stp_CB->acked_seqno) + length >= stp_CB->last_rwnd) {
        if (wait_for_data_acks(stp_CB, 1, 0) == WAIT_FOR_DATA_ACKS_ERROR)
            return STP_ERROR;
    }
    
    /* Build the packet to be sent. The timeout member is updated in send_packet(). */
    pkt = (sent_packet *) malloc(sizeof(sent_packet));
    memcpy(pkt->data, data, length);
    pkt->len = length;
    pkt->seqno = stp_CB->next_seqno;
    pkt->times_sent = 0;
    pkt->next = NULL;
    
    /* Adds the packet to the end of the linked list */
    for (last = &stp_CB->unacked_packets; *last; last = &(*last)->next);
    *last = pkt;
    
    stp_CB->next_seqno = plus(stp_CB->next_seqno, length);
    
    send_packet(stp_CB->fd, pkt, STP_DATA);
    
    return STP_SUCCESS;
}


/*
 * Make sure all the outstanding data has been transmitted and
 * acknowledged, and then initiate closing the connection. This
 * function is also responsible for freeing and closing all necessary
 * structures that were not previously freed, including the control
 * block itself. Returns STP_SUCCESS on success or STP_ERROR on error.
 */
int stp_close(stp_send_ctrl_blk *stp_CB) {
    
    unsigned char wrk[STP_MTU];
    stp_header *ack = (stp_header *) wrk;
    sent_packet fin;
    int len;
    
    /* Wait for all outstanding ACKs */
    while (greater(stp_CB->next_seqno, stp_CB->acked_seqno)) {
        if (wait_for_data_acks(stp_CB, 1, 0) == WAIT_FOR_DATA_ACKS_ERROR)
            return STP_ERROR;
    }
    
    /* FIN packet */
    fin.len = 0;
    fin.seqno = stp_CB->next_seqno;
    fin.times_sent = 0;
    
    stp_CB->next_seqno = plus(stp_CB->next_seqno, 1);
    
    do {
        
        /* If FIN (or FINACK) is lost three times, reset. */
        if (fin.times_sent >= 3) {
            reset(stp_CB->fd);
            return STP_ERROR;
        }
        
        send_packet(stp_CB->fd, &fin, STP_FIN);
        
        do {
            
            len = receive_and_check_ack(stp_CB->fd, ack, &fin.timeout);
            if (len == -1) {
                reset(stp_CB->fd);
                return STP_ERROR;
            }
            
            /* If a repeated ACK comes for a previous packet, wait more time for the FINACK. */
        } while(len != STP_TIMED_OUT && greater(stp_CB->next_seqno, ntohs(ack->seqno)));
        
    } while(len == STP_TIMED_OUT);
    
    /* At this point the seqno is either the correct one or greater. If
     * it were smaller it would be stuck in the inner do-while above.
     */
    if (greater(ntohs(ack->seqno), stp_CB->next_seqno)) {
        printf("Invalid sequence number. Resetting...\n");
        reset(stp_CB->fd);
        return STP_ERROR;
    }
    
    /* Close socket and free control block. */
    close(stp_CB->fd);
    free(stp_CB);
    return STP_SUCCESS;
}


/*
 * This application is to invoke the send-side functionality. Feel
 * free to rewrite or write your own application to test your
 * code. Some examples of other applications that could be used
 * instead:
 * 
 * - A program that reads or generates a log and transmits it through
 *   STP;
 * - A program that reads the standard input and transmits it through
 *   STP;
 */
int main(int argc, char *argv[]) {
    
    stp_send_ctrl_blk *stp_CB;
    
    char *remoteHost;
    int remotePort, localPort;
    int file;
    
    /* You might want to change the size of this buffer to test how your
     * code deals with different packet sizes.
     */
    unsigned char buffer[STP_MSS];
    int num_read_bytes;
    
    /* Verify that the arguments are right */
    if (argc != 5) {
        fprintf(stderr, "usage: SendApp remoteIPAddress/Name remotePort localPort filename\n");
        exit(1);
    }
    
    /*
     * Open connection to destination.  If stp_open succeeds the
     * stp_CB should be correctly initialized.
     */
    
    remoteHost = argv[1];
    remotePort = atoi(argv[2]);
    localPort = atoi(argv[3]);
    
    /* Open file for transfer */
    file = open(argv[4], O_RDONLY);
    if (file < 0) {
        perror(argv[4]);
        exit(1);
    }
    
    stp_CB = stp_open(remoteHost, remotePort, localPort);
    if (stp_CB == NULL) {
        fprintf(stderr, "Error opening STP connection.\n");
        close(file);
        exit(1);
    }
    
    /* Start to send data in file via STP to remote receiver. Chop up
     * the file into pieces as large as max packet size and transmit
     * those pieces.
     */
    while((num_read_bytes = read(file, buffer, sizeof(buffer))) > 0) {
        
        if(stp_send(stp_CB, buffer, num_read_bytes) == STP_ERROR) {
            fprintf(stderr, "Error sending data.\n");
            close(file);
            stp_close(stp_CB);
            exit(1);
        }
    }
    
    /* Close the connection to remote receiver */   
    if (stp_close(stp_CB) == STP_ERROR) {
        fprintf(stderr, "Error closing connection.\n");
        /* Continues running because other resources need to be closed. */
    }
    
    close(file);
    
    return 0;
}


