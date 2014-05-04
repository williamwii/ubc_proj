#include <stdlib.h>
#include <stdio.h>
#include "uthread.h"

int q[10];
int q_head, q_tail, i;
uthread_monitor_t* q_mon;
uthread_cv_t* q_not_empty;
uthread_cv_t* q_not_full;

void enqueue (int i) {
	uthread_monitor_enter(q_mon);
	while ((q_tail+1)%10==q_head%10)
		uthread_cv_wait(q_not_full);
	q[q_tail] = i;
	q_tail = (q_tail+1)%10;
	uthread_cv_notify (q_not_empty);
	uthread_monitor_exit(q_mon);
}

int dequeue () {
	uthread_monitor_enter (q_mon);
	while ( q_head==q_tail )
		uthread_cv_wait (q_not_empty);
	int returnVal = q[q_head];
	q_head = (q_head+1)%10;
	uthread_cv_notify (q_not_full);
	uthread_monitor_exit (q_mon);
	
	return returnVal;
}

void* producer ( void* x ) {
	while (1){//for (i=0;i<50;i++){
		enqueue(++i);
		int ran = random();
		if (ran%2==0) uthread_yield();
	}
}

void* consumer ( void* x ) {
	while (1){//for (i=0;i<50;i++){
		int temp = dequeue();
		printf("%d is dequeued\n",temp);
		int ran = random();
		if (ran%2==0) uthread_yield();
	}
}

int main (int* argc, char** argv) {

	q_mon = uthread_monitor_create ();
	q_not_empty = uthread_cv_create (q_mon);
	q_not_full = uthread_cv_create (q_mon);
	q_head = 0;
	q_tail = 0;
	i = 0;
	
	uthread_init (4);
	//int* a = {1,2,3,4};
	uthread_t* t1 = uthread_create (producer, q_mon);
	uthread_t* t2 = uthread_create (producer, q_mon);
	uthread_t* t3 = uthread_create (consumer, q_mon);
	uthread_t* t4 = uthread_create (consumer, q_mon);
	
	uthread_join ( t1 );
	uthread_join ( t3 );
	uthread_join ( t2 );
	uthread_join ( t4 );
  

}