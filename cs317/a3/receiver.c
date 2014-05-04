/* This code is adapted from a course at Boston University for use in
 * CPSC 317 at UBC.
 *
 * Implementation of a STP receiver. This module implements the
 * receiver-side of the protocol and dumps the contents of the
 * connection to a file called "OutputFile" in the current directory.
 *
 * It also simulates network misbehavior by dropping packets, dropping
 * ACKs, corrupting packets and swapping some packets so that they
 * arrive out of order.
 *
 * Version 1.0 
 */

#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/file.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <time.h>
#include <sys/time.h>

#include "stp.h"
#include "receiver_list.h"
#include "wraparound.h"

/* Window size */
#define STP_MIN_WINDOW (2 * STP_MSS)
#define STP_MAX_WINDOW (0x7fff - STP_MSS)
#define STP_SSTHRESHOLD 5000

#define OUTPUT_FILENAME "OutputFile"

/* State types */
#define STP_ESTABLISHED 0x20
#define STP_CLOSED      0x21
#define STP_LISTEN      0x22
#define STP_TIME_WAIT   0x23


/* Default time_wait_timeout in ms */
#define TIME_WAIT_TIMEOUT 4000

/* 
 * All of the receiver's state is stored in the following structure,
 * including the received messages, which have to be delivered to
 * ReceiveApp in order.
 */
typedef struct {
    
    int state;           /* protocol state: normally ESTABLISHED */
    int fd;              /* UDP socket descriptor */
    
    int waiting_for_lost_packet; /* used to check if it is waiting for lost packet */
    	
    uint16_t largest_window_received; /* largest window recieve over time */
    
    uint16_t rwnd;       /* latest advertised window */
    
    uint16_t window_count_down; /* counter used to keep track of STP window size changes*/

    uint16_t NBE;        /* next byte expected */
    uint16_t LBRead;     /* last byte read */
    uint16_t LBReceived; /* last byte received */
    
    uint16_t ISN;        /* initial sequence number */
    
    pktbuf *recvQueue;   /* Pointer to the first node of the receive queue */
    
} stp_recv_ctrl_blk;

/* Global variables corresponding to arguments provided in command-line */
double PacketLossProbability              = 0.0; /* packet loss probability */
double AckLossProbability                 = 0.0; /* ACK loss probability */
double OutOfOrderPacketArrivalProbability = 0.0; /* probability of an out-of-order arrival */
double CorruptedPacketProbability         = 0.0; /* probability of corruption on arrived packet */
double CorruptedACKProbability            = 0.0; /* probability of corruption on ACK */

/* Global file descriptor for the output file. */
int outFile = -1;

/* Since the protocol STP is event driven, we define a structure
 * stp_event to describe the event coming in, which enables a state
 * transition.  All events are in the form of packets from our peer.
 */

typedef struct stp_event_t {
    char *pkt; /* Pointer to the packet from peer */
    int len;   /* The length of the packet */
} stp_event;

/* Return 1 with probability p, and 0 otherwise */
int event_happens(double p) {
    
    return (drand48() < p);
}

/*
 * Send an STP ACK back to the source. The stp_CB tells
 * us what frame we expect, so we ack that sequence number.
 */
void stp_send_ack(stp_recv_ctrl_blk *stp_CB) {
    
    if (!event_happens(AckLossProbability) || stp_CB->state == STP_LISTEN) {
        sendpkt_corrupt_opt(stp_CB->fd, STP_ACK, stp_CB->rwnd, stp_CB->NBE, 0, 0,
                            event_happens(CorruptedACKProbability));
    } else {
        printf("ACK (%u) dropped\n", stp_CB->NBE); 
    }
}

/*
 * Routine that simulates handing off a message to the application.
 * (We'll just write it to the output file directly.)
 * STP ensures that messages are delivered in sequence.
 */
void stp_consume(char *pkt, int len) {
    
    write(outFile, pkt, len);
    printf("consume: %d bytes\n", len);
    
    /* Uncomment below to print the contents for debugging
     * purposes. This should not be used with binary content.
     */
    /* printf("Contents: <%.*s>\n", len, (char *) pkt); */
}

/*
 * Checks a received packet and updates the receiver state based on its contents.
 */
int stp_receive_state_transition_machine(stp_recv_ctrl_blk *stp_CB, stp_event *pe) {
    
    uint16_t seqno;
    stp_header *srh = (stp_header *) pe->pkt;
    int type;
    uint16_t LBA; /* Last byte that can be expected based on the window */
    
    int len;
    unsigned char pkt[STP_MTU];
    
    /* If the length is too short for a header, that's an error */
    if (pe->len < sizeof(*srh)) {
        printf("Size too short.\n");
        reset(stp_CB->fd); 
        return -1;
    }
    
    /* Checks if the sum of bytes is correct */
    if (srh->checksum != checksum(srh, pe->len - sizeof(stp_header))) {
        printf("Sum of bytes doesn't match. Ignoring packet.\n");
        /* An incorrect checksum does not return an error, just ignores the packet. */
        return 0;
    }
    
    /* Strip out the fields of the header from the packet */
    type = ntohs(srh->type);
    seqno = ntohs(srh->seqno);
    
    switch (stp_CB->state) {
            
        case STP_LISTEN: /* LISTEN state: waiting for a connection establishment. */
            
            if (type != STP_SYN) {
                printf("Not SYN.\n");
                reset(stp_CB->fd); 
                return -1;
            }
            
            stp_CB->ISN = seqno;
            stp_CB->LBRead = seqno;
            stp_CB->LBReceived = seqno;
            stp_CB->NBE = plus(seqno, 1);
            stp_CB->state = STP_ESTABLISHED;
            stp_send_ack(stp_CB);
            return 0;
            
        case STP_ESTABLISHED: /* ESTABLISHED: Actual data transmission. */
            
            switch (type) {
                    
                case STP_RESET:
                    
                    fprintf(stderr, "Reset received from sender -- closing\n");
                    sendpkt_corrupt_opt(stp_CB->fd, STP_RESET, 0, 0, 0, 0,
                                        event_happens(CorruptedACKProbability));
                    return -1; 
                    
                case STP_SYN: 
                    
                    if(seqno == stp_CB->ISN) {
                        
                        /* this is a retransmission of the first SYN, acknowledge */
                        sendpkt_corrupt_opt(stp_CB->fd, STP_ACK, stp_CB->rwnd, plus(stp_CB->ISN, 1),
                                            0, 0, event_happens(CorruptedACKProbability));
                        return 0;
                    } else {
                        printf("New SYN received with different seqno (%u != %u).\n", seqno, stp_CB->ISN);
                        reset(stp_CB->fd);
                        return -1;
                    }
                    
                case STP_FIN:
                    
                    /* This FIN is only valid if its sequence number is NBE.
                     * Otherwise the FIN is occuring prior to our receipt of all
                     * data.
                     */
                    if (seqno != stp_CB->NBE) {
                        printf("FIN seq not equal to NBE (%u != %u).\n", seqno, stp_CB->NBE);
                        reset(stp_CB->fd);
                        return -1;
                    }
                    
                    /* If seqno is NBE, but not LBReceived+1, it means there is data
                     * in the queue
                     */
                    if (seqno != plus(stp_CB->LBReceived, 1)) {
                        printf("FIN received, but there are out of order packets to process.\n");
                        reset(stp_CB->fd);
                        return -1;
                    }
                    
                    stp_CB->state = STP_TIME_WAIT;
                    stp_CB->rwnd = 0;
                    
                    /* Increment NBE so that the FIN-ACK actually sends the next
                     * seqno, then decrements again in case the FIN-ACK is lost.
                     */
                    stp_CB->NBE = plus(stp_CB->NBE, 1); 
                    stp_send_ack(stp_CB);
                    stp_CB->NBE = minus(stp_CB->NBE, 1); 
                    
                    
                /*
                 * ADDED
                 * STP_TIME_WAIT state
                 * STP_FIN goes directly into this state.
                 * Wait for 4 seconds for any new packet.
                 * If not further packets are received, terminate.
                 * Otherwise, handle the packet.
                 */
                case STP_TIME_WAIT:

                    /* Wait for additional 4 seconds if more packet is received */
                    while (1){

						len = readpkt_timer(stp_CB->fd, (char*) pkt, TIME_WAIT_TIMEOUT);
						if (len == STP_TIMED_OUT)
							return 1;
						
						else{
							type = ntohs(((stp_header *)pkt)->type);
							seqno = ntohs(((stp_header *)pkt)->seqno);
							
							switch (type) {
								case STP_SYN:
                                    /* SYN packet is received with the correct initial
                                     * sequence number, resend ACK 
                                     */
									if (seqno == stp_CB->ISN){
										stp_CB->NBE = plus(stp_CB->NBE, 1);
										stp_send_ack(stp_CB);
										stp_CB->NBE = minus(stp_CB->NBE, 1); 
                                        break;
									}
									else{
                                        printf("Received SYN with invalid seqno, resetting.");
                                        sendpkt_corrupt_opt(stp_CB->fd, STP_RESET, 0, 0, 0, 0, event_happens(CorruptedACKProbability));
                                        return -1;
                                    }
								
								case STP_DATA:
                                    /* DATA packet is received with a sequence number 
                                     * smaller than the FIN number, it is to be considered
                                     * a delayed packet, resend ACK
                                     */
									if (greater(stp_CB->NBE,seqno)){
										stp_CB->NBE = plus(stp_CB->NBE, 1);
										stp_send_ack(stp_CB);
										stp_CB->NBE = minus(stp_CB->NBE, 1); 
                                        break;
									}
									else{
                                        printf("Received DATA with invalid seqno, resetting.");
                                        sendpkt_corrupt_opt(stp_CB->fd, STP_RESET, 0, 0, 0, 0, event_happens(CorruptedACKProbability));
                                        return -1;
									}

									
								case STP_FIN:
                                    /* FIN is received with the same sequence number,
                                     * resend ACK
                                     */
									if (seqno == stp_CB->NBE){
										stp_CB->NBE = plus(stp_CB->NBE, 1);
										stp_send_ack(stp_CB);
										stp_CB->NBE = minus(stp_CB->NBE, 1);
										break;
									}
									else{
                                        printf("Received FIN with invalid seqno, resetting.");
                                        sendpkt_corrupt_opt(stp_CB->fd, STP_RESET, 0, 0, 0, 0, event_happens(CorruptedACKProbability));
                                        return -1;
                                    }

								default:
                                    printf("Received invalid packet, resetting.");
                                    sendpkt_corrupt_opt(stp_CB->fd, STP_RESET, 0, 0, 0, 0, event_happens(CorruptedACKProbability));
                                    return -1;							
							}
						}
					}
                    
                    
                case STP_DATA: 
                    
                    /* The last byte accepted is based on the receiver window. Note
                     * that, if the window is reduced, this has to take into
                     * consideration that the sender may have previously sent
                     * packets that go beyond the advertised window if, at the
                     * moment the sender sent them, the ACK with the updated window
                     * had not been received yet, so the receiver should allow a
                     * bigger window than just the last advertised window.
                     */
                    
                    
                    LBA = plus(stp_CB->LBRead,stp_CB->largest_window_received);
                    

                    if (greater(stp_CB->NBE, seqno)) {
                        
                        /* retransmitted packet that we've already received, nothing
                         * to do except send an ACK (at function bottom)
                         */
                    }
                    
                    else if(greater(seqno, LBA)) {
                        printf("Packet seqno too large to fit in receive window.\n");
                        reset(stp_CB->fd);
                        return -1;
                    }
                    
                    /*
                     * New data has arrived. If the ACK arrives in order, hand the
                     * data directly to the application (consume it) and see if
                     * we've filled a gap in the sequence space. Otherwise, stash
                     * the packet in a buffer. In either case, send back an ACK for
                     * the highest contiguously received packet.
                     */
                    else if (seqno == stp_CB->NBE) {
                        
                        pktbuf *next;
                        uint16_t lastByte = plus(seqno, (pe->len - sizeof(*srh) -1));
                        
                        /* packet in order - send to application */
                        stp_consume(pe->pkt + sizeof(*srh), pe->len - sizeof(*srh));
                        seqno = plus(seqno, (pe->len - sizeof(*srh)));
                        
                        if (greater(lastByte, stp_CB->LBReceived))
                            stp_CB->LBReceived = lastByte;
                        
                        /* Now check if the arrival of this packet allows us to
                         * consume any more packets that had been previously received
                         * out of order.
                         */
                        while((next = get_packet(&stp_CB->recvQueue, seqno)) != NULL) {
                            printf("Batch reading!!\n");
                            stp_consume(next->data, next->len);
                            seqno = plus(seqno, next->len);
                            free_packet(next);
                        }
                        
                        stp_CB->NBE = seqno;
                        stp_CB->LBRead = minus(seqno,1);
                        
                        /* Waiting for more out of order packet, cut the window size by half again */
                        if ((stp_CB->recvQueue!=NULL)&&(stp_CB->waiting_for_lost_packet==1)){
                            printf("Waiting for another packet, cutting window size by half again.\n");
                            stp_CB->rwnd = (uint16_t) (stp_CB->rwnd / 2);

                            stp_CB->window_count_down = (int) (stp_CB->rwnd / STP_MSS - 1);

                        }
                        
                        /* Packets back in order */
                        else if ((stp_CB->recvQueue==NULL)&&(stp_CB->waiting_for_lost_packet==1)){
                            printf("Packets in order, resume normal transition.\n");
                            stp_CB->rwnd = plus(stp_CB->rwnd,STP_MSS);
                            stp_CB->window_count_down = (int) (stp_CB->rwnd / STP_MSS - 1);
                            
                            if (greater(stp_CB->rwnd,stp_CB->largest_window_received))
                                stp_CB->largest_window_received = stp_CB->rwnd;
                            
                            stp_CB->waiting_for_lost_packet = 0;
                        }
                        
                        /* Not waiting for any lost packet(packets in order) */
                        else {
                            
                            /* Window size is smaller or equal to STP_SSTHRESHOLD, increase the window size by STP_MSS */
                            if ((greater(STP_SSTHRESHOLD,stp_CB->rwnd))||(stp_CB->rwnd==STP_SSTHRESHOLD)){
                                stp_CB->rwnd = plus (stp_CB->rwnd,STP_MSS);
                                
                                if (greater(stp_CB->rwnd,stp_CB->largest_window_received))
                                    stp_CB->largest_window_received = stp_CB->rwnd;
                                
                                stp_CB->window_count_down = 1;
                            }
                            
                            /* Otherwise, slow down the increasing by creating window_count_down  */
                            else {
                                /* Count down reaches 0, increase window size by STP_MSS */
                                if (stp_CB->window_count_down==0){
                                    stp_CB->rwnd = plus(stp_CB->rwnd,STP_MSS);
                                    stp_CB->window_count_down = (int) (stp_CB->rwnd / STP_MSS - 1);
                                    
                                    if (greater(stp_CB->rwnd,stp_CB->largest_window_received))
                                        stp_CB->largest_window_received = stp_CB->rwnd;
                                    
                                    printf("Count down to zero, new count down is %d.\n",stp_CB->window_count_down);

                                }
                                /* Counting down */
                                else{
                                    printf("Counting down, %d.\n",stp_CB->window_count_down);
                                    stp_CB->window_count_down--;
                                }
                            }
                        }
                        
                    } 
                    
                    else {
                        
                        /* packet out of order but within receive window, copy the
                         * data and record the seqno to validate the buffer
                         */
                        
                        if (stp_CB->waiting_for_lost_packet==0){
                            printf("Packet out of order. Cutting window size by half.\n");
                            stp_CB->rwnd = (uint16_t) (stp_CB->rwnd / 2);
                            stp_CB->window_count_down = (int) (stp_CB->rwnd / STP_MSS - 1);

                            stp_CB->waiting_for_lost_packet = 1;
                        }
                        
                        
                        uint16_t lastByte = plus(seqno, (pe->len - sizeof(*srh) -1));
                        
                        add_packet(&stp_CB->recvQueue, seqno, pe->len - sizeof(*srh), pe->pkt + sizeof(*srh));
                        
                        if (greater(lastByte, stp_CB->LBReceived))
                            stp_CB->LBReceived = lastByte;
                    }
                    
                    
                    /* Use STP_MAX_WINDOW if window size is greater than STP_MAX_WINDOW */
                    if (greater(stp_CB->rwnd,STP_MAX_WINDOW))
                        stp_CB->rwnd = STP_MAX_WINDOW;
                    /* Use STP_MIN_WINDOW if window size is less than STP_MIN_WINDOW */
                    else if (greater(STP_MIN_WINDOW,stp_CB->rwnd))
                        stp_CB->rwnd = STP_MIN_WINDOW;
                    
                    /* Debug print outs */
                    //printf("stp_CB->LBReceived is %d.\nstp_CB->LBRead is %d.\nstp_CB->rwnd is %d.\nLBA is %d.\nLargest window received is %d.\n",stp_CB->LBReceived,stp_CB->LBRead,stp_CB->rwnd,LBA,stp_CB->largest_window_received);
                    
                    if (minus(stp_CB->LBReceived, stp_CB->LBRead) > stp_CB->largest_window_received) {
                        printf("Not in feasible window.\n");
                        reset(stp_CB->fd);
                        return -1;
                    }
                    
                    printf("rwnd adjusted: (%u)\n", stp_CB->rwnd);
                    
                    /* Always send an ACK back to the sender. */
                    stp_send_ack(stp_CB);
                    return 0;
                    
                default: 
                    /* Invalid packet received */
                    printf("Invalid packet.\n");
                    reset(stp_CB->fd); 
                    return -1;
                    
            } /* end of switch(type) in ESTABLISHED state */
            
        default: /* Current state is invalid or not implemented */
            return -1;
            
    } /* end of switch (stp_CB->state) */
    
} /* end of stp_receive_state_transition_machine */

/* Run the receiver polling loop: allocate and initialize the
 * stp_recv_ctrl_blk then enter an infinite loop to process incoming
 * packets.
 */
int stp_receiver_run(char *remoteHost, int remotePort, int localPort) {
    
    stp_recv_ctrl_blk *stp_CB = (stp_recv_ctrl_blk *) malloc(sizeof(*stp_CB));
    
    /* Variables used to simulate out-of-order arrivals */
    int  delay_pkt_set = 0;
    int  delay_pkt_len;
    char delay_pkt[STP_MTU];
    
    stp_event *pe = (stp_event *) malloc(sizeof(*pe));
    
    /* Initialize the receiver's stp_CB block 
     * and open the underlying UDP/IP communication channel.
     */
    if ((stp_CB->fd = udp_open(remoteHost, remotePort, localPort)) < 0) return -1;
    stp_CB->state = STP_LISTEN;
    stp_CB->rwnd = STP_MIN_WINDOW;
    stp_CB->window_count_down = 1;
    stp_CB->waiting_for_lost_packet = 0;
    stp_CB->largest_window_received = STP_MIN_WINDOW;
    stp_CB->LBRead = 0;
    stp_CB->LBReceived = 0;
    stp_CB->NBE = 1;
    stp_CB->recvQueue = NULL;
    
    /* Enter an infinite loop reading packets from the network and
     * processing them. The receiver processing loop is simple because
     * (unlike the sender) we do not need to schedule timers or handle
     * any asynchronous events.
     */
    while(1) {
        
        int len;
        unsigned char pkt[STP_MTU];
        
        /* Block until a new packet arrives */
        while ((len = readpkt(stp_CB->fd, pkt)) <= 0);   /* Busy wait */
        
        if (event_happens(CorruptedPacketProbability)) {
            int random_byte = lrand48() % len, random_bit = lrand48() % 8;
            printf("RECEIVED PACKET CORRUPTED\n");
            pkt[random_byte] ^= (char) (1 << random_bit);
        }
        
        pe->pkt = (char *) pkt;
        pe->len = len;
        
        /* Do the processing associated with a new packet arrival. But,
         * with probability PLP, we pretend this packet got lost in the
         * network.
         */
        if (event_happens(PacketLossProbability)) { 
            printf("PACKET DROPPED\n");  /* Do nothing */
            continue;
        }
        else if (!delay_pkt_set && event_happens(OutOfOrderPacketArrivalProbability)) {        
            printf("PACKET DELAYED\n");
            memcpy (delay_pkt, pkt, len);
            delay_pkt_len = len; 
            delay_pkt_set = 1;
            continue;
        }
        
        if (delay_pkt_set) {
            /* Process the packets in reverse order and unset delay_pkt_set bit */
            
            if (stp_receive_state_transition_machine(stp_CB, pe) == -1)
                return -1;
            
            pe->pkt = delay_pkt;
            pe->len = delay_pkt_len;
            delay_pkt_set = 0;
        }
        
        /* Now we are in normal operating mode */
        switch (stp_receive_state_transition_machine(stp_CB,pe)) {
            case -1:  /* Error */
                return -1;
            case 1:   /* File transfer complete */ 
                return 0;
        } 
    }
}

int main(int argc, char **argv) {
    
    char* remoteHost;
    int remotePort, localPort;
    
    if (argc < 4 || argc > 9) {
        fprintf(stderr, "usage: ReceiveApp ReceiveDataFromHost doRecvOnPort sendResponseToPort "
                "[packetLossProb [ACKlossProb [DelayedPacketProb "
                "[CorruptedPacketProb [CorruptedACKProb]]]]]\n");
        exit(1);
    }
    
    /* Seeds the randomizer to avoid getting the same events all the time. */
    srand48(time(NULL));
    
    /* Extract the arguments */
    int argIndex = 1;
    remoteHost = argv[argIndex++];
    remotePort = atoi(argv[argIndex++]);
    localPort = atoi(argv[argIndex++]);
    
    if (argc > argIndex)
        PacketLossProbability = strtod(argv[argIndex++], NULL);
    
    if (argc > argIndex)
        AckLossProbability = strtod(argv[argIndex++], NULL);
    
    if (argc > argIndex)
        OutOfOrderPacketArrivalProbability = strtod(argv[argIndex++], NULL);
    
    if (argc > argIndex)
        CorruptedPacketProbability = strtod(argv[argIndex++], NULL);
    
    if (argc > argIndex)
        CorruptedACKProbability = strtod(argv[argIndex++], NULL);
    
    printf("Listening on port %d for connections from host %s port %d\n"
           "PacketLossProb %4.2f AckLossProb %4.2f OutOfOrderProb %4.2f\n"
           "CorruptedPacketProb %4.2f CorruptedAckProb %4.2f\n",
           localPort, remoteHost, remotePort,
           PacketLossProbability, AckLossProbability, OutOfOrderPacketArrivalProbability,
           CorruptedPacketProbability, CorruptedACKProbability);
    
    /* Open the output file for writing.  The STP sender tranfers a file
     * to us and we simply dump it to disk.
     */
    outFile = open(OUTPUT_FILENAME, O_CREAT|O_WRONLY|O_TRUNC, 0644);
    if (outFile < 0) {
        perror(OUTPUT_FILENAME " could not be created");
        return 0;
    }
    
    /* "Run" the receiver protocol.  Application can check the return value. */
    if (stp_receiver_run(remoteHost, remotePort, localPort) != 0)
        printf("File transfer failed.\n");
    else
        printf("File transfer completed successfully.\n");
    
    return 0;
}



