Wei You
77610095

#2
1st trace:
#0  printString (buf=0x400a34, siz=0) at async.c:32
#1  0x00000000004007d1 in interruptServiceRoutine ()
    at async.c:27
#2  <signal handler called>
#3  main (argc=1, argv=0x7fffffffe038) at async.c:50

bottom is the frame for main function.
Then the signal handler is initialize (boot()).
Then interruptServiceRoutine () interrupt the "cpu".
Then printString is dequeued and called from the circular queue.

2nd trace:
#0  printString (buf=0x400a04, siz=0) at async.c:32
#1  0x0000000000400871 in main (argc=1, argv=0x7fffffffe038)
    at async.c:49
	
bottom is the frame for main function.
Then the printString function is pushed and called.

The difference is, for the 1st trace, the cpu is calling printString depending on interruption.
1st trace push the boot() on the stack and use the signal to call printString.
For the 2nd trace, the function printString is called directly without depending on other functions.


#3
All my tests for modified async.c (mod_async.c) passed.
It printed the result of 3.