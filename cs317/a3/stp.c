/*
 * Adapted from a course at Boston University for use in CPSC 317 at
 * UBC
 */

#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <ctype.h>
#include <netdb.h>
#include <sys/types.h>
#include <sys/time.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <errno.h>

#include "stp.h"

/*
 * Open a UDP connection. Note that a UDP connection is not actually
 * established.
 *
 * Note, to simplify things we use connect(). When used with a UDP
 * socket all packets then sent and received on the given file
 * descriptor go to and are received from the specified host. Reads
 * and writes are still completed in a datagram unit size, but the
 * application does not have to do the multiplexing and
 * demultiplexing. This greatly simplifies things but restricts the
 * number of "connections" to the number of file descriptors and isn't
 * very good for a pure request response protocol like DNS where there
 * is no long term relationship between the client and server.
 */
int udp_open(char *remote_host, int remote_port, int local_port) {
  
  int fd, rv;
  struct sockaddr_in sin;
  struct addrinfo hints, *servinfo, *p;
  char remote_port_str[11];
  
  printf ("Configuring UDP \"connection\" to <%s, port %d>\n", remote_host, remote_port);
  
  memset(&hints, 0, sizeof hints);
  hints.ai_family = AF_INET;
  hints.ai_socktype = SOCK_DGRAM;
  sprintf(remote_port_str, "%d", remote_port);
  
  if ((rv = getaddrinfo(remote_host, remote_port_str, &hints, &servinfo)) != 0) {
    fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
    return STP_ERROR_RESOLUTION;
  }
  
  // loop through all the results and connect to the first we can
  for(p = servinfo; p != NULL; p = p->ai_next) {
    
    if ((fd = socket(p->ai_family, p->ai_socktype, p->ai_protocol)) == -1) {
      perror("socket");
      continue;
    }
    
    memset((char *) &sin, 0, sizeof(sin));
    sin.sin_family = AF_INET;
    sin.sin_port = htons(local_port);
    
    if (bind(fd, (struct sockaddr *) &sin, sizeof(sin)) < 0) {
      close(fd);
      if (errno != EINVAL)
	perror("Bind failed");
      continue;
    }
  
    /* Connect, i.e. prepare to accept UDP packets from <remote_host,
     * remote_port>.  listen() and accept() are not necessary with UDP
     * connection setup.
     */ 
    if (connect(fd, p->ai_addr, p->ai_addrlen) == -1) {
      close(fd);
      perror("connect");
      continue;
    }
    
    break;
  }
  
  if (!p)
    return STP_ERROR_SOCKET;
  
  /* Bind the local socket to listen at the local_port. */
  printf("UDP \"connection\" to <%s port %d> configured\n", remote_host, remote_port);
  
  return fd;
}

/*
 * Print an STP packet to standard output. direction is either 's'ent
 * or 'r'eceived packet
 */
void dump(char direction, stp_header *stpHeader, int len) {
  
  uint16_t type = ntohs(stpHeader->type);
  uint16_t seqno = ntohs(stpHeader->seqno);
  uint16_t win = ntohs(stpHeader->window);
  
  printf("%c %s seq %u win %u len %d\n", direction,
         (type == STP_DATA) ? "dat" : 
         (type == STP_ACK) ? "ack" : 
         (type == STP_SYN) ? "syn" : 
         (type == STP_FIN) ? "fin" : 
         (type == STP_RESET) ? "rst" : "???",
         seqno, win, len);
}

/*
 * Helper function to calculate the sum of the bytes in a packet.
 */
unsigned char checksum(stp_header *stpHeader, int len) {
  
  unsigned char sum = 0;
  int i;
  sum += (stpHeader->type   & 0xff) + (stpHeader->type   >> 8);
  sum += (stpHeader->window & 0xff) + (stpHeader->window >> 8);
  sum += (stpHeader->seqno  & 0xff) + (stpHeader->seqno  >> 8);
  
  for (i = 0; i < len; i++)
    sum += stpHeader->data_octets[i];
  
  return sum;
}

/*
 * Helper function to send an stp packet over the network.
 * As a side effect print the packet header to standard output.
 */
void sendpkt(int fd, int type, uint16_t window,
             uint16_t seqno, char* data, int len)
{
  sendpkt_corrupt_opt(fd, type, window, seqno, data, len, 0);
}

/*
 * Helper function to send an STP packet over the network.
 * As a side effect print the packet header to standard output.
 * Has an option to corrupt the packet.
 */
void sendpkt_corrupt_opt(int fd, int type, uint16_t window,
			 uint16_t seqno, char* data, int len, int corrupted) {
  
  /* The wrk variable is a buffer for packets to be sent. An alternative would be a malloc call for stpHeader */
  unsigned char wrk[STP_MTU];
  stp_header *stpHeader = (stp_header *)wrk;
  
  stpHeader->type = htons(type);
  stpHeader->window = htons(window);
  stpHeader->seqno = htons(seqno);
  
  if (data != 0 && len > 0)
    memcpy((char*)(stpHeader + 1), data, len);
  
  stpHeader->checksum = checksum(stpHeader, len);
  
  if (corrupted) {
    int random_byte = lrand48() % (sizeof(stp_header) + len);
    int random_bit = lrand48() % 8;
    wrk[random_byte] ^= (char) (1 << random_bit);
    printf("SENT PACKET CORRUPTED\n");
  }
  
  dump('s', stpHeader, len + sizeof(stp_header));
  if (send(fd, stpHeader, len + sizeof(stp_header), 0) < 0) {
    perror("write");
    exit(1);
  }
}

/*
 * Helper function to read a STP packet from the network. pkt has to
 * have space for STP_MTU bytes. As a side effect print the packet
 * header to standard output.
 */
int readpkt(int fd, void *pkt) {
  
  int cc = recv(fd, pkt, STP_MTU, 0);
  if (cc > 0)
    dump('r', (stp_header *) pkt, cc);
  return cc;
}

/*
 * Reset the network connection by sending an RESET packet, print an error
 * message to standard output, and exit.
 */
void reset(int fd) {
  
  fprintf(stderr, "protocol error encountered... resetting connection\n");
  sendpkt(fd, STP_RESET, 0, 0, 0, 0);
  exit(0);
}


/*
 * Read a packet from the network but if "ms" milliseconds transpire
 * before a packet arrives, abort the read attempt and return
 * STP_TIMED_OUT. Otherwise, return the length of the packet read.
 */
int readpkt_timer(int fd, char *pkt, int ms) {
  
  int s;
  fd_set fds;
  struct timeval tv;
  
  tv.tv_sec = ms / 1000;
  tv.tv_usec = (ms - tv.tv_sec * 1000) * 1000;
  
  FD_ZERO(&fds);
  FD_SET(fd, &fds);
  s = select(fd + 1, &fds, 0, 0, &tv);
  if (FD_ISSET(fd, &fds))
    return readpkt(fd, pkt);
  else
    return STP_TIMED_OUT;
}
