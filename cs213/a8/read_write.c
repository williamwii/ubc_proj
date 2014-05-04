#include <stdlib.h>
#include <stdio.h>
#include "uthread.h"

int i;

void* read( void* monitor ) {
	while (1) {
		enter_for_reading (monitor);
		printf("%d\n", i);
		uthread_monitor_exit (monitor);
		int y = random();
		if (y%2==0) uthread_yield();
	}
}

void* write ( void* monitor ) {
	while (1) {
		enter_for_writing (monitor);
		i++;
		uthread_monitor_exit (monitor);
		int y = random();
		if (y%2==0) uthread_yield();
	}
}

int main (int* argc, char** argv) {
	i = 0;
	uthread_t *read1, *read2, *read3, *read4, *write1;
	
	uthread_init (1);
	uthread_monitor_t* mon = uthread_monitor_create ();
	read1 = uthread_create (read, mon);
	read2 = uthread_create (read, mon);
	read3 = uthread_create (read, mon);
	read4 = uthread_create (read, mon);
	write1 = uthread_create (write, mon);
	uthread_join ( read1 );
	uthread_join ( read2 );
	uthread_join ( write1 );
	uthread_join ( read3 );
	uthread_join ( read4 );
	
	printf("\n");
}