#include <stdlib.h>
#include <stdio.h>
#include <string.h>


//////////////////////
//
// STACK

#define STACK_NAME_SIZE 100

struct StackElement {
  char   name[STACK_NAME_SIZE];
  struct StackElement *next;
};

struct StackElement *stackTop = 0;

void push (char* aName) {
  struct StackElement* e = (struct StackElement*) malloc (sizeof (struct StackElement));
  strncpy (e->name, aName, STACK_NAME_SIZE);
  e->next  = stackTop;
  stackTop = e;
}

char* pop () {
  struct StackElement* e = stackTop;
  stackTop = e->next;
  char* c = (char*) malloc (STACK_NAME_SIZE*sizeof (char));
  strncpy (c,e->name,STACK_NAME_SIZE);
  free(e);
  return c;
}

///
//////////////////////


//////////////////////
//
// TEST CASES

#define INT_ARRAY_SIZE (sizeof(struct StackElement)/sizeof(int))

void test1 () {
  printf ("test1:\n");
  int* ip = (int *) malloc (INT_ARRAY_SIZE*sizeof(int));
  int* ipc = ip;
  free (ip);
  push ("Zero");
  printf ("%s\n", pop ());
}

void test2 () {
  printf ("test2:\n");
  int* ip = (int *) malloc (INT_ARRAY_SIZE*sizeof(int));
  int* ipc = ip;
  push ("Zero");  
  while (ipc - ip <= INT_ARRAY_SIZE) 
    *(ipc++) = 0;
  free (ip);  
  printf ("%s\n", pop ());		
}

void test3 () {
  printf ("test3:\n");
  push ("Zero");
  int* ip = (int *) malloc (INT_ARRAY_SIZE*sizeof(int));
  int* ipc = ip;
  free (ip);
  push ("One");
  printf ("%s\n", pop ());
  printf ("%s\n", pop ());
}

void test4 () {
  printf ("test4:\n");
  push ("Zero");
  int* ip = (int *) malloc (INT_ARRAY_SIZE*sizeof(int));
  int* ipc = ip;
  push ("One");  
  while (ipc - ip <= INT_ARRAY_SIZE) 
    *(ipc++) = 0;
  free (ip);
  printf ("%s\n", pop ());
  printf ("%s\n", pop ());
}

void test5 () {
  printf ("test5:\n");
  push ("Zero");
  push ("One");
  printf ("%s\n", pop ()); 
  printf ("%s\n", pop ());
  push("Two");
  push("Three");
  printf ("%s\n", pop ()); 
  printf ("%s\n", pop ());
}

void test6 () {
  printf ("test6:\n");
  char *x[2];
  push ("Zero");
  push ("One");
  x[0] = pop ();
  x[1] = pop ();
  push("Two");
  push("Three");
  printf ("%s\n", x[0]);
  printf ("%s\n", x[1]);
}

//
//////////////////////


//////////////////////
//
// MAIN LINE

#define USAGE "usage: dangling-pointer test-number"

int main (int argc, char** argv) {
  if (argc != 2) {
    printf ("%s\n", USAGE);
    exit (EXIT_FAILURE);
  }
  switch (strtol (argv[1],0,10)) {
    case 1:
      test1 ();
      break;
    case 2:
      test2 ();
      break;
    case 3:
      test3 ();
      break;
    case 4:
      test4 ();
      break;
    case 5:
      test5 ();
      break;
    case 6:
      test6 ();
      break;
    default:
      printf ("%s (where test-number is 1-6)\n", USAGE);
      exit (EXIT_FAILURE);
  }
}
