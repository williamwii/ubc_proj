/*
 * This code is adapted from a course at 
 * Boston University for use in CPSC 317 at UBC.
 *
 *
 * A list that stored packets that have already been received
 * by the receiver.
 * 
 * Version 1.0
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "stp.h"
#include "receiver_list.h"
#include "wraparound.h"

/* 
 * Adds a packet in the receiver queue. The queue is kept in order of
 * seqno.
 */
void add_packet(pktbuf **recv_queue, uint16_t seqno, int len, char *data) {
  
  pktbuf *current_packet;
  
  /* Find the slot where the packet fits */
  for (; *recv_queue != NULL && greater(seqno, (*recv_queue)->seqno); recv_queue = &(*recv_queue)->next);
  
  /* If the seqno is the same, the data is already in the queue. Nothing should be done. */
  if(*recv_queue != NULL && (*recv_queue)->seqno == seqno)
    return;	
  
  /* Create a new packet structure */
  current_packet = (pktbuf *)malloc(sizeof(pktbuf));
  current_packet->seqno = seqno;
  current_packet->len = len;
  memcpy(current_packet->data, data, len);
  current_packet->next = *recv_queue;
  *recv_queue = current_packet;
}

void free_packet(pktbuf *pbuf) {
  
  free(pbuf);
}

/*
 * Returns a packet whose sequence number equals to seqno.
 * If no such packet, returns NULL.
 *
 */
pktbuf *get_packet(pktbuf **recv_queue, uint16_t seqno) {
  
  pktbuf *retval;
  
  /* Finds the packet with the corresponding sequence number */
  for(; *recv_queue && (*recv_queue)->seqno < seqno; recv_queue = &(*recv_queue)->next);
  
  retval = *recv_queue;
  if (retval) {
    /* If the list doesn't contain this sequence number, returns NULL. */
    if (retval->seqno != seqno) return NULL;
    *recv_queue = retval->next;
  }
  return retval;
}
