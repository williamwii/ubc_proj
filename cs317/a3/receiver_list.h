#include "stp.h"

#ifndef __RECEIVER_LIST_H__
#define __RECEIVER_LIST_H__

#include <stdint.h>

/* Structure used to manage received packets in order of
 * seqno. Packets are kept on the list only if they were received
 * ahead, and are removed once they can be consumed.
 */
typedef struct pktbuf_tag {
  
  struct pktbuf_tag *next;
  
  uint16_t seqno;
  int len;
  char data[STP_MTU];
  
} pktbuf;

void add_packet(pktbuf **, uint16_t, int, char *);
pktbuf *get_packet(pktbuf **, uint16_t);
void free_packet(pktbuf *);

#endif
