Wei You
77610095

Q1)
iaddl V, rB; isubl V, rB; ixorl V, rB; iandl V, rB;
imull V, rB; idivl V, rB; imodl V, rB;

Fetch:
	iCd:iFn <- M1[PC]
	rA:rB <- M1[PC + 1]
	valC <- M4[PC + 2]
	valP <- PC + 6

Decode:
	srcB <- rB
	dstE <- rB
	valB <- R[srcB]

Execute:
	valE <- valB OP valC
	

Memory:

Write-back:
	R[dstE] <- valE

PC-update:
	PC <- valP

The test cases covers all instructions with any boundary cases,
such as overflowing integer values and dividing by zero.
Beside each instruction, the expected value of register %eax is indicated,
and %eax should end up being 0 when the program halts. All tests pass.


Q2)
call rA

Fetch:
	iCd:iFn <- M1[PC]
	rA:rB <- M1[PC + 1]
	valP <- PC + 2

Decode:
	srcA <- rA
	srcB <- %esp
	dstE <- %esp
	valA <- R[srcA]
	valB <- R[srcB]

Execute:
	valE <- valB - 4

Memory:
	M[valE] <- valP

Write-back:
	R[dstE] <- valE

PC-update:
	PC <- valA
	
The test provides sufficient test coverage because it does a function call within
a function, and indicates that the stack does grow and function does return.
There are no other edge/boundary cases.
The test program calls fib(10) and return the 10th Fibonacci number in %eax.
In the function fib, there is a inner function that does nothing, this can
indicate the stack grows proportionally to the number of calls.
When the program halts, %eax = 55.


Q3)
jmp *D(rB)

Fetch:
	iCd:iFn <- M1[PC]
	rA:rB <- M1[PC + 1]
	valC <- M4[PC + 4]
	valP <- PC + 6

Decode:
	srcB <- rB
	valB <- R[srcB]

Execute:
	valE <- valC + 4 * valB

Memory:
	valM <- M4[valE]

Write-back:

PC-update:
	PC <- valM
	
The test covers all cases because it tests with a table of 3 and all cases work.
The test initializes %eax to 1 and loops through a switch table of 3 items and 
do operations +4, -(-5) and *4 in sequence. When the program finishes, %eax = 40.


It takes 8 hours to finish this assignment.