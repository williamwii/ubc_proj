#include <stdlib.h>
#include <stdio.h>
#include "uthread.h"

#define NUM_ITERATIONS 1000

void* ping (void* x) {
  int i,j;
  for (i=0; i<NUM_ITERATIONS; i++) {
    printf ("I");
    for (j=0; j<10000; j++) {}
  uthread_yield();
  }
}

void* pong (void* x) {
  int i,j;
  for (i=0; i<NUM_ITERATIONS; i++) {
    printf ("O");
    for (j=0; j<10000; j++) {}
  uthread_yield();
  }
}

int main (int* argc, char** argv) {
  uthread_t *ping_thread, *pong_thread;
  int i;
  uthread_init (2);
  ping_thread = uthread_create (ping, 0);
  pong_thread = uthread_create (pong, 0);
  uthread_join (ping_thread);
  uthread_join (pong_thread);
  printf ("\n");
}
