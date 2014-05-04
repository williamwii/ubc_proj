#include <stdio.h>
#include <signal.h>
#include <unistd.h>

/*
  a struct that holds a function pointer and its two arguments.
*/
struct Comp {
  void (*handler) (void*, int);
  void* buf;
  int   siz;
};

struct Triple {
  int arg0;
  int arg1;
  int result;
  // use to check completion of add/sub
  // 1 if is done
  int done;
};

//a circular queue with size 1000 of struct Comp
struct Comp comp[1000];
//head of queue
int compHead = 0;
//tail of queue
int compTail = 0;

int s;

/*
  enqueue a new struct with the function aHander and
  its two arguements aBuf and aSiz
*/
void doAsync (void (*aHander)(void*, int), void* aBuf, int aSiz) {
  //get the position of the head of queue
  compHead = (compHead + 1) % (sizeof (comp) / sizeof (struct Comp));
  comp[compHead].handler = aHander;
  comp[compHead].buf     = aBuf;
  comp[compHead].siz     = aSiz;
}
              
/*
  dequeue the struct from the queue and call the function
  store in the struct
*/
void interruptServiceRoutine () {
  struct Comp c;
  if (compHead != compTail) {
    compTail = (compTail + 1) % (sizeof (comp) / sizeof (struct Comp));
    c = comp[compTail];
    c.handler (c.buf, c.siz);
  }
}

/*
  a function that print out buf
  siz is unused
*/
void printString (void* buf, int siz) {
  printf ("%s\n", (char*) buf); fflush (stdout);
}

void add (void* xp, int n) {
  struct Triple* temp = (struct Triple*) xp;
  temp->result = temp->arg0 + temp->arg1;
  temp->done = 1;
  //printf("%d\n",temp->result);
}

void sub(void* xp, int n) {
  struct Triple* temp = (struct Triple*) xp;
  temp->result = temp->arg0 - temp->arg1;
  temp->done = 1;
  //printf("%d\n",temp->result);
}

/*
  when a signal is caught, the interruptServiceRoutine function is called.
  signal is sent every second.
*/
void boot () {
  signal (SIGALRM, interruptServiceRoutine);
  // JimmyW - This line doesn't work on the department linux machines such as lulu, so we use 999999 instead.
  // ualarm (1000000,1000000);

  //signal every second
  ualarm (999999,999999);
}

int main (int argc, char** argv) {
  boot ();
  printf ("*** Please run this program on lulu.ugrad.cs.ubc.ca to get the correct behavior.\n");
  printf ("Scheduling some things to do asynchronously\n"); fflush (stdout);

  struct Triple xp;

  struct Triple xp1;

  struct Triple xp2;

  struct Triple xp3;

  //enqueue three things.
  doAsync (printString, "Thing 1", 0);
  doAsync (printString, "Thing 2", 0);
  doAsync (printString, "Thing 3", 0);

  xp.arg0 = 1;
  xp.arg1 = 2;
  xp1.arg0 = 3;
  xp1.arg1 = 4;
  doAsync (add, &xp, 0);
  doAsync (add, &xp1, 0);

  printf ("Now waiting for them to complete\n"); fflush (stdout);
  while (!(xp.done==1&&xp1.done==1));
  
  xp2.arg0 = xp.result;
  xp2.arg1 = xp1.result;
  doAsync(sub,&xp2,0);

  while (!xp2.done==1);

  xp3.arg0 = xp2.result;
  xp3.arg1 = 7;
  doAsync(add,&xp3,0);

  while (!xp3.done==1);

  s = xp3.result;
  
  printf("Result is %d\n", s);

}
