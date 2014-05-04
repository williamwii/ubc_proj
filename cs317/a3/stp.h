#ifndef __STP_H__
#define __STP_H__

#include <stdint.h>

#define STP_MAXWIN    UINT16_MAX/2 
#define STP_MTU       300 /* Maximum transmission unit (includes header size) */
#define STP_MSS       (STP_MTU - sizeof(stp_header)) /* Maximum segment size (only data) */

#define STP_ERROR_SOCKET (-2)
#define STP_ERROR_BIND (-3)
#define STP_ERROR_RESOLUTION (-4)
#define STP_ERROR_CONNECT (-5)

#define STP_TIMED_OUT (-10)

/*
 * Packet types
 */
#define STP_DATA  0x01
#define STP_ACK   0x02
#define STP_SYN   0x04
#define STP_FIN   0x08
#define STP_RESET 0x10

/*
 * This is the actual definition of the packet header on the
 * wire. Note there is an extra field (data_octets) to be used as the
 * area where the data is actually stored.
 */
typedef struct {
  uint16_t type; 
  uint16_t window; 
  uint16_t seqno;
  unsigned char checksum;
  unsigned char data_octets[];
} stp_header;

int udp_open(char *remote_host, int remote_port, int local_port);
void sendpkt(int fd, int type, uint16_t window, uint16_t seqno, char* data, int len);
void sendpkt_corrupt_opt(int fd, int type, uint16_t window, uint16_t seqno, char* data, int len, int corrupted);
int readpkt(int fd, void* pkt);
int readpkt_timer(int fd, char *pkt, int ms);
void dump(char direction, stp_header* stpHeader, int len);
void reset(int fd);
unsigned char checksum(stp_header *stpHeader, int len);

#endif
