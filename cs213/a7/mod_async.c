#include <stdlib.h>
#include <stdio.h>
#include "uthread.h"


struct Triple {
  int arg0;
  int arg1;
  // use to check completion of add/sub
  // 1 if is done
  //int done;
};


void* add (void* xp) {
  struct Triple* temp = (struct Triple*) xp;
  int* sum = (int*) malloc (sizeof(int));
  *sum =  temp->arg0 + temp->arg1;
  return sum;
  //temp->done = 1;
  //printf("%d\n",temp->result);
}

void* sub(void* xp) {
  struct Triple* temp = (struct Triple*) xp;
  int* dif = (int*) malloc (sizeof(int));
  *dif = temp->arg0 - temp->arg1;
  return dif;
  //temp->done = 1;
  //printf("%d\n",temp->result);
}


int main (int argc, char** argv) {
  
  uthread_t *add_thread, *add1_thread, *sub_thread;
  uthread_init(2);
  int temp, temp1, temp2, result;

  
  struct Triple xp, xp1, xp2;


  //struct Triple xp3;

  xp.arg0 = 1;
  xp.arg1 = 2;
  xp1.arg0 = 3;
  xp1.arg1 = 4;

  add_thread = uthread_create (add,&xp);
  add1_thread = uthread_create (add,&xp1);
  temp = *(int*)uthread_join (add_thread);
  temp1 = *(int*)uthread_join (add1_thread);

  xp2.arg0 = temp;
  xp2.arg1 = temp1;

  sub_thread = uthread_create (sub,&xp2);
  temp2 = *(int*)uthread_join (sub_thread);

  xp.arg0 = temp2;
  xp.arg1 = 7;

  add_thread = uthread_create (add,&xp);
  result = *(int*)uthread_join (add_thread);
  
  printf("Result is %d\n", result);
}
