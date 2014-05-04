//
// Written by Mike Feeley, University of BC, 2010
// Do not redistribute or otherwise make available any portion of this code to anyone without written permission of the author.
//

#if __CYGWIN__
#ifndef NATIVE_STACK_SIZE
#define NATIVE_STACK_SIZE (1*1024*1024)
#endif
#ifndef PTHREAD_SUPPORT
#define PTHREAD_SUPPORT 1
#endif
#ifndef PTHREAD_SETSTACK_SUPPORT
#define PTHREAD_SETSTACK_SUPPORT 0
#endif
#else
#ifndef NATIVE_STACK_SIZE
#define NATIVE_STACK_SIZE (8*1024*1024)
#endif
#ifndef PTHREAD_SUPPORT
#define PTHREAD_SUPPORT  1
#endif
#ifndef PTHREAD_SETSTACK_SUPPORT
#define PTHREAD_SETSTACK_SUPPORT 0
#endif
#endif

#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <assert.h>
#if PTHREAD_SUPPORT
#include <pthread.h>
#endif
#include "uthread.h"

#define TS_NASCENT 0
#define TS_RUNNING 1
#define TS_RUNABLE 8
#define TS_BLOCKED 3
#define TS_DYING   4
#define TS_DEAD    5

#define STACK_SIZE     (8*1024*1024)

// UBC CPSC 213 Winter 2010 Term 1 Assignment 9.  -JW

//
// SPINLOCKS
//

/**
 * spinlock_create
 * create a lock with unlock state
 */

void spinlock_create (spinlock_t* lock) {
  *lock = 0;
}

/**
 * spinlock_lock
 * acquire the spinlock if it is not already held for other threads
 */

void spinlock_lock (spinlock_t* lock) {
  int already_held=1;
  do {
    //wait until the lock is free
    while (*lock);
    //switch the threads
    asm volatile ("xchg  %0, %1\n\t" : "=m" (*lock), "=r" (already_held) : "1" (already_held));
  } 
  // make sure the lock is still free to use during the switch
  while (already_held);
}

/**
 * spinlock_unlock
 * unlock the spinlock so it is free for others
 */

void spinlock_unlock (spinlock_t* lock) {
  *lock = 0;
}

//
// THREAD CONTROL BLOCK
//

struct uthread_TCB {
  volatile int         state;                 
  volatile uintptr_t   saved_sp;              
  void*              (*start_proc) (void*);
  void*                start_arg;
  void*                return_val;
  void*                stack;
  spinlock_t           join_spinlock;
  uthread_t*           joiner;
  struct uthread_TCB*  next;
};

//
// THREAD QUEUE
//

struct uthread_queue {
  uthread_t  *head, *tail;
};

typedef struct uthread_queue uthread_queue_t;

/**
 * initqueue
 */

static void initqueue (uthread_queue_t* queue) {
  queue->head = 0;
  queue->tail = 0;
}

/**
 * enqueue
 */

static void enqueue (uthread_queue_t* queue, uthread_t* thread) {
  thread->next = 0;
  if (queue->tail)
    queue->tail->next = thread;
  queue->tail = thread;
  if (queue->head==0)
    queue->head = queue->tail;
}

/**
 * dequeue
 */

static uthread_t* dequeue (uthread_queue_t* queue) {
  uthread_t* thread;
  if (queue->head) {    
    thread = queue->head;
    queue->head = queue->head->next;
    if (queue->head==0)
      queue->tail=0;
  } else 
    thread=0;
  return thread;
}

//
// READY QUEUE
//

static spinlock_t      ready_queue_spinlock;
static uthread_queue_t ready_queue;

/**
 * ready_queue_enqueue
 */

static void ready_queue_enqueue (uthread_t* thread) {
  spinlock_lock (&ready_queue_spinlock);
  enqueue (&ready_queue, thread);
  spinlock_unlock (&ready_queue_spinlock);
}

/**
 * ready_queue_dequeue
 */

static uthread_t* ready_queue_dequeue () {
  uthread_t* thread;
  spinlock_lock (&ready_queue_spinlock);
  thread = dequeue (&ready_queue);
  spinlock_unlock (&ready_queue_spinlock);
  return thread;
}

static void ready_queue_init () {
  spinlock_create (&ready_queue_spinlock);
  initqueue       (&ready_queue);
}


//
// INITIALIZATION 
//

static uthread_t* uthread_alloc      ();
static uthread_t* uthread_new_thread (void* (*)(void*), void*);

static uthread_t* base_thread;
static uintptr_t  base_sp_lower_bound, base_sp_upper_bound;

#if PTHREAD_SETSTACK_SUPPORT==0
#define MAX_PTHREADS 100
static spinlock_t num_pthreads_spinlock = 0;
static int        num_pthreads = 0;
static uthread_t* pthread_base_thread[MAX_PTHREADS];
static uintptr_t  pthread_base_sp_lower_bound[MAX_PTHREADS], pthread_base_sp_upper_bound[MAX_PTHREADS];
#endif



static void* pthread_base (void* arg) {
#if PTHREAD_SETSTACK_SUPPORT==0
  if (arg) {
    spinlock_lock (&num_pthreads_spinlock);
    assert (num_pthreads < MAX_PTHREADS);
    pthread_base_thread         [num_pthreads] = (uthread_t*) arg;
    pthread_base_sp_upper_bound [num_pthreads] = (((uintptr_t)&arg) + 1024);
    pthread_base_sp_lower_bound [num_pthreads] = (((uintptr_t)&arg) - NATIVE_STACK_SIZE);
    num_pthreads += 1;
    spinlock_unlock (&num_pthreads_spinlock);
  }
#endif
  while (1)
    uthread_yield ();  // XXX block pthread
}

/**
 * uthread_init
 */

void uthread_init (int num_processors) {
  int dummy_local, i;
  uthread_t* uthread;
#if PTHREAD_SUPPORT
  pthread_t* pthread;
  pthread_attr_t attr;
#else
  assert (num_processors==1);
#endif
  
  base_sp_upper_bound = (((uintptr_t)&dummy_local) + 1024);
  base_sp_lower_bound = (((uintptr_t)&dummy_local) - NATIVE_STACK_SIZE);
  base_thread         = uthread_alloc ();
  base_thread->state  = TS_RUNNING;
  base_thread->stack  = 0;
  ready_queue_init      ();
  uthread_create        (pthread_base, 0);
#if PTHREAD_SUPPORT
  for (i=0; i<num_processors-1; i++) {
    uthread = uthread_new_thread (pthread_base, 0);
    uthread->state = TS_RUNNING;
    pthread = (pthread_t*) malloc (sizeof (pthread_t));
    pthread_attr_init (&attr);
#if PTHREAD_SETSTACK_SUPPORT
    pthread_attr_setstack (&attr, (void*) ((((uintptr_t) uthread->stack) + STACK_SIZE - 1) & ~(STACK_SIZE - 1)), STACK_SIZE);
#endif
    pthread_create (pthread, &attr, pthread_base, uthread);
  }
#endif
}

//
// UTHREAD PRIVATE IMPLEMENTATION 
//

static void       uthread_stop  (int);
static void       uthread_start (uthread_t*);
static void       uthread_free  (uthread_t*);

/**
 * uthread_alloc
 */

static uthread_t* uthread_alloc () {
  uthread_t* thread = (uthread_t*) malloc (sizeof (uthread_t));
  thread->state      = TS_NASCENT;
  thread->start_proc = 0;
  thread->start_arg  = 0;
  thread->stack      = 0;
  thread->saved_sp   = 0;
  thread->joiner     = 0;
  spinlock_create (&thread->join_spinlock);
  return thread;
}

/**
 * uthread_new_thread
 */

static uthread_t* uthread_new_thread (void* (*start_proc)(void*), void* start_arg) {
  uthread_t* thread  = uthread_alloc ();
  thread->state      = TS_NASCENT;
  thread->start_proc = start_proc;
  thread->start_arg  = start_arg;
  thread->stack      = malloc (STACK_SIZE * 2);
  thread->saved_sp   = ((((uintptr_t) thread->stack) + STACK_SIZE - 1) & ~(STACK_SIZE - 1)) + STACK_SIZE;
  *(uthread_t**) (thread->saved_sp -1 & ~(STACK_SIZE -1)) = thread;
  asm volatile (
#if __LP64__
// IA32-64
#define CLOBBERED_REGISTERS "%rax", "%rcx"
                "movq  %%rsp, %%rax\n\t"
                "movq %c1(%0), %%rsp\n\t"
                "subq $512, %%rsp\n\t"    // frame for uthread_switch
                "pushq $0\n\t"
                "pushq $0\n\t"
                "pushq $0\n\t"
                "pushq $0\n\t"
                "pushq $0\n\t"
                "pushq %%r8\n\t"
                "pushq %%r9\n\t"
                "pushq %%r10\n\t"
                "pushq %%r11\n\t"
                "pushq %%r12\n\t"
                "pushq %%r13\n\t"
                "pushq %%r14\n\t"
                "pushq %%r15\n\t"
                "pushfq\n\t"
                "movq  %%rsp, %%rcx\n\t"
                "addq  $256, %%rcx\n\t" // locate saved ebp within frame
                "pushq %%rcx\n\t"
                "movq %%rsp, %c1(%0)\n\t"
                "movq  %%rax, %%rsp\n\t"
#else
// IA32-32
#define CLOBBERED_REGISTERS "%eax", "%ecx"
                "movl  %%esp, %%eax\n\t"
                "movl  %c1(%0), %%esp\n\t"
                "subl  $512, %%esp\n\t"
                "pushl $0\n\t"
                "pushl $0\n\t"
                "pushl $0\n\t"
                "pushl $0\n\t"
                "pushl $0\n\t"
                "pushfl\n\t"
                "movl  %%esp, %%ecx\n\t"
                "addl  $256, %%ecx\n\t"
                "pushl %%ecx\n\t"
                "movl %%esp, %c1(%0)\n\t"
                "movl  %%eax, %%esp\n\t"
#endif
                : : "r" (thread), 
                "i" ((uintptr_t)&thread->saved_sp-(uintptr_t)thread) 
                : CLOBBERED_REGISTERS);
  return thread;
}

/**
 * uthread_switch
 */

static void uthread_switch (uthread_t* to_thread, int from_thread_state) {
  uthread_t* from_thread = uthread_self ();

  if (from_thread == to_thread)
   return;
  
  // May be racing with blocking of to_thread so spin until it has blocked
  while (to_thread->state == TS_RUNNING) {}
  
  asm volatile (
#if __LP64__
// IA32-64
                "pushq %%rbx\n\t"
                "pushq %%rcx\n\t"
                "pushq %%rdx\n\t"
                "pushq %%rsi\n\t"
                "pushq %%rdi\n\t"
                "pushq %%r8\n\t"
                "pushq %%r9\n\t"
                "pushq %%r10\n\t"
                "pushq %%r11\n\t"
                "pushq %%r12\n\t"
                "pushq %%r13\n\t"
                "pushq %%r14\n\t"
                "pushq %%r15\n\t"
                "pushfq\n\t"
                "pushq %%rbp\n\t"
                "movq  %%rsp, %c5(%1)\n\t"
                "movl  %3, %c4(%1)\n\t"
                "movq  %c5(%2), %%rsp\n\t"
                "popq  %%rbp\n\t"
                "movq  %1, %0\n\t"
                "popfq\n\t"
                "popq  %%r15\n\t"
                "popq  %%r14\n\t"
                "popq  %%r13\n\t"            
                "popq  %%r12\n\t"
                "popq  %%r11\n\t"
                "popq  %%r10\n\t"
                "popq  %%r9\n\t"
                "popq  %%r8\n\t"
                "popq  %%rdi\n\t"
                "popq  %%rsi\n\t"
                "popq  %%rdx\n\t"
                "popq  %%rcx\n\t"
                "popq  %%rbx\n\t"
#else
// IA32-32
                "pushl %%ebx\n\t"
                "pushl %%ecx\n\t"
                "pushl %%edx\n\t"
                "pushl %%esi\n\t"
                "pushl %%edi\n\t"
                "pushfl\n\t"
                "pushl %%ebp\n\t"
                "movl  %%esp, %c5(%1)\n\t"
                "movl  %3, %c4(%1)\n\t"
                "movl  %c5(%2), %%esp\n\t"
                "popl  %%ebp\n\t"
                "movl  %1, %0\n\t"
                "popfl\n\t"
                "popl  %%edi\n\t"
                "popl  %%esi\n\t"
                "popl  %%edx\n\t"
                "popl  %%ecx\n\t"
                "popl  %%ebx\n\t"
#endif
                : "=m" (from_thread) : "r" (from_thread), "r" (to_thread), "r" (from_thread_state), 
                "i" ((uintptr_t)&to_thread->state-(uintptr_t)to_thread), 
                "i" ((uintptr_t)&to_thread->saved_sp-(uintptr_t)to_thread));
  
  if (from_thread->state == TS_DYING) {
    spinlock_lock (&from_thread->join_spinlock);
    if (from_thread->joiner == (uthread_t*) -1)
      uthread_free (from_thread);
    else {
      if (from_thread->joiner != 0)
        uthread_start (from_thread->joiner);
      from_thread->state = TS_DEAD;  
      spinlock_unlock (&from_thread->join_spinlock);
      // at this point uthread_detach could free from_thread, so don't touch it after setting it to DEAD
    }
  }
  
  to_thread = uthread_self ();
  if (to_thread->state == TS_NASCENT) {
    to_thread->state      = TS_RUNNING;
    to_thread->return_val = to_thread->start_proc (to_thread->start_arg);
    uthread_stop (TS_DYING);
  } else
    to_thread->state = TS_RUNNING;
}

/**
 * uthread_stop
 */

static void uthread_stop (int stopping_thread_state) {
  uthread_t* to_thread = ready_queue_dequeue ();
  assert         (to_thread);
  uthread_switch (to_thread, stopping_thread_state);
}

/**
 * uthread_start
 */

static void uthread_start (uthread_t* thread) {
  // XXX Possible race with blocking of this thread would result in the
  // thread on the ready queue in the TS_BLOCKED state
  if (thread->state != TS_RUNNING)
    thread->state = TS_RUNABLE;
  ready_queue_enqueue (thread);
}

/**
 * uthread_free
 */

static void uthread_free (uthread_t* thread) {
  if (thread->stack)
    free (thread->stack);
  free (thread);
}


//
// UTHREAD PUBLIC INTERFACE
//

/**
 * uthread_create
 */

uthread_t* uthread_create (void* (*start_proc)(void*), void* start_arg) {
  uthread_t* thread = uthread_new_thread  (start_proc, start_arg);
  ready_queue_enqueue (thread);
  return thread;
}                                   

/**
 * uthread_self
 */

uthread_t* uthread_self () {
  int dummy_local, i;

  if ((uintptr_t)&dummy_local >= base_sp_lower_bound && (uintptr_t) &dummy_local <= base_sp_upper_bound)
    return base_thread;
  else {
#if PTHREAD_SETSTACK_SUPPORT==0
    for (i=0; i<num_pthreads; i++)
      if ((uintptr_t)&dummy_local >= pthread_base_sp_lower_bound[i] && (uintptr_t) &dummy_local <= pthread_base_sp_upper_bound[i])
        return pthread_base_thread[i];
#endif
    return *(uthread_t**) (void*) (((uintptr_t) &dummy_local) & ~(STACK_SIZE-1));
  }
}

/**
 * uthead_yield
 */

void uthread_yield () {
  ready_queue_enqueue (uthread_self ());
  uthread_stop        (TS_RUNABLE); 
}

/**
 * uthread_join
 */
 
 void* uthread_join (uthread_t* thread) {
  void* return_val;
  
  if (thread->joiner == 0) {
    spinlock_lock (&thread->join_spinlock);
    if (thread->state != TS_DEAD) {
      thread->joiner = uthread_self ();
      spinlock_unlock (&thread->join_spinlock);
      uthread_stop (TS_BLOCKED);
    }
    return_val = thread->return_val;
    uthread_free (thread);
  } else 
    return 0;
  
  return return_val;
}

/**
 * uthread_detach
 */

void uthread_detach (uthread_t* thread) {
  if (thread->joiner == 0) {
    spinlock_lock (&thread->join_spinlock);
    if (thread->state != TS_DEAD) {
      thread->joiner = (uthread_t*) -1;
      spinlock_unlock (&thread->join_spinlock);
    } else 
      uthread_free (thread);
  }
}

//
// SEMAPHORES
//

// 
struct uthread_semaphore {
  // count is never less than 0
  int             count;
  spinlock_t      spinlock;
  // a queue to store the waiting calls
  uthread_queue_t waiter_queue;
};

/**
 * uthread_semaphore_create
 */
// create a semaphore with the intial count, a spin lock and a empty queue
uthread_semaphore_t* uthread_semaphore_create (int initial_count) {
  uthread_semaphore_t* sem = (uthread_semaphore_t*) malloc (sizeof (uthread_semaphore_t));
  
  sem->count = initial_count;
  spinlock_create (&sem->spinlock);
  initqueue       (&sem->waiter_queue);
  return sem;
}

/**
 * uthread_semaphore_free
 */

void uthread_semaphore_free (uthread_semaphore_t* sem) {
  free (sem);
}

/**
 * uthread_V
 */

void uthread_V (uthread_semaphore_t* sem) {
  uthread_t* waiter_thread;
  
  spinlock_lock (&sem->spinlock);
  //increament count
  sem->count += 1;
  // dequeue and start a thread in the waiter queue
  waiter_thread = dequeue (&sem->waiter_queue);
  if (waiter_thread)
    uthread_start (waiter_thread);
  spinlock_unlock (&sem->spinlock);
}

/**
 * uthread_P
 */

void uthread_P (uthread_semaphore_t* sem) {
  uthread_t* waiter_thread;
  
  spinlock_lock (&sem->spinlock);
  // if count is less than 1, enqueue the thread into the waiter queue
  // block itself and wait until count is not less than one
  while (sem->count < 1) {
    enqueue (&sem->waiter_queue, uthread_self ());
    spinlock_unlock (&sem->spinlock);
    uthread_stop (TS_BLOCKED);
    spinlock_lock (&sem->spinlock);
  }
  // then decreament count
  sem->count -= 1;
  spinlock_unlock (&sem->spinlock);
}

//
// MONITORS 
//

// Assignment 9
// Multi-Reader Single-Writer Monitor: you will need a reader count and something to keep track of the readers/writers

// A struct of monitor that takes control of resource so only one thread can use the resource
// and others and put into a queue
struct uthread_monitor {

  // the thread that is using this resource
  uthread_t*      holder;
  spinlock_t      spinlock;
  // a queue for threads that are waiting for this resource
  uthread_queue_t waiter_queue;

  uthread_queue_t reader_queue;
  uthread_queue_t writer_queue;

  // reader count
  int rCount;

};

// Assignment 9
// Multi-Reader Single-Writer Monitor: implement new functions and change existing functions as needed.

void enter_for_reading ( uthread_monitor_t* monitor ){
  spinlock_lock (&monitor->spinlock);

  while (monitor->holder) {
    enqueue         (&monitor->reader_queue, uthread_self ());
    spinlock_unlock (&monitor->spinlock);
    uthread_stop    (TS_BLOCKED);
    spinlock_lock   (&monitor->spinlock);
  }
  monitor->rCount++;
  spinlock_unlock (&monitor->spinlock);
}

void enter_for_writing ( uthread_monitor_t* monitor ){
  spinlock_lock (&monitor->spinlock);

  while (monitor->holder || monitor->rCount!=0) {
    enqueue         (&monitor->writer_queue, uthread_self ());
    spinlock_unlock (&monitor->spinlock);
    uthread_stop    (TS_BLOCKED);
    spinlock_lock   (&monitor->spinlock);
  }
  monitor->holder = uthread_self ();
  spinlock_unlock (&monitor->spinlock);
}
/*
	exit for read and write
*/
void uthread_monitor_exit ( uthread_monitor_t* monitor ){
  uthread_t* waiter_thread;
  spinlock_lock (&monitor->spinlock);
  
  if ( monitor->holder )
    monitor->holder = 0;
  else monitor->rCount--;
  
  if ( monitor->rCount==0 ) {
    waiter_thread = dequeue ( &monitor->writer_queue );
	if ( waiter_thread!=NULL )
		uthread_start ( waiter_thread );
  }
  else {
	waiter_thread = dequeue ( &monitor->reader_queue );
	while ( waiter_thread!=NULL ) {
		if ( waiter_thread!=NULL )
			uthread_start ( waiter_thread );
		waiter_thread = dequeue ( &monitor->reader_queue );
	}
  }
  
  spinlock_unlock (&monitor->spinlock);

}

/**
 * uthread_monitor_create
 * create a new monitor with its own spinlock and waiter queue
 */

uthread_monitor_t* uthread_monitor_create () {
  uthread_monitor_t* monitor = (uthread_monitor_t*) malloc (sizeof (uthread_monitor_t));
  monitor->holder = 0;
  monitor->rCount = 0;
  spinlock_create (&monitor->spinlock);

  //initqueue       (&monitor->waiter_queue);
  initqueue (&monitor->reader_queue);
  initqueue (&monitor->writer_queue);

  return monitor;
}

/**
 * uthread_monitor_free
 */

void uthread_monitor_free (uthread_monitor_t* monitor) {
  free (monitor);
}

/**
 * uthread_monitor_enter
 * enter a thread into this monitor. If the monitor is locked, enqueue the thread into
 * the waiter queue and block itself.
 * wait until the monitor is unused and set the holder of the monitor to this thread
 */

void uthread_monitor_enter (uthread_monitor_t* monitor) {
  spinlock_lock (&monitor->spinlock);
  while (monitor->holder) {
    enqueue         (&monitor->waiter_queue, uthread_self ());
    spinlock_unlock (&monitor->spinlock);
    uthread_stop    (TS_BLOCKED);
    spinlock_lock   (&monitor->spinlock);
  }
  monitor->holder = uthread_self ();
  spinlock_unlock (&monitor->spinlock);
}

/**
 * uthread_monitor_exit
 * exit the thread from monitor and start the thread
 */

/*void uthread_monitor_exit (uthread_monitor_t* monitor) {
  uthread_t* waiter_thread;
  
  spinlock_lock (&monitor->spinlock);
  // make sure the thread is the holder of monitor
  assert (monitor->holder == uthread_self ());
  // set the monitor to be free
  monitor->holder = 0;
  // dequeue the thread from waiter queue
  waiter_thread   = dequeue (&monitor->waiter_queue);
  spinlock_unlock (&monitor->spinlock);
  if (waiter_thread)
    // start the thread
    uthread_start (waiter_thread);
}*/


//
// CONDITION VARIABLES
//

struct uthread_cv {
  // For Assignment 10
  uthread_monitor_t* monitor;
  uthread_queue_t waiter_queue;
};

uthread_cv_t* uthread_cv_create (uthread_monitor_t* monitor) {
  // For Assignment 10
  uthread_cv_t* cv = (uthread_cv_t*) malloc(sizeof(uthread_cv_t));
  cv->monitor = monitor;
  initqueue (&cv->waiter_queue);
  return cv;
}

void uthread_cv_wait (uthread_cv_t* cv) {
  // For Assignment 10
    enqueue         (&cv->waiter_queue, uthread_self ());
	uthread_monitor_exit (cv->monitor);
    uthread_stop    (TS_BLOCKED);
	uthread_monitor_enter (cv->monitor);
}

void uthread_cv_notify (uthread_cv_t* cv) {
  // For Assignment 10
  //spinlock_unlock ( &cv->monitor->spinlock );
  uthread_t* t = dequeue (&cv->waiter_queue);
  if ( t ) uthread_start(t);
  //uthread_stop ( TS_BLOCKED );
  //spinlock_lock ( &cv->monitor->spinlock);
}

void uthread_cv_notify_all (uthread_cv_t* cv) {
  // For Assignment 10
  uthread_t* t = dequeue (&cv->waiter_queue);
  while ( t ) {
	uthread_start ( t );
	t = dequeue (&cv->waiter_queue);
  }
  //spinlock_unlock ( &cv->monitor->spinlock );
  //uthread_stop ( TS_BLOCKED );
  //spinlock_lock ( &cv->monitor->spinlock);
}
