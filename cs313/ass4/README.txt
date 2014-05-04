Wei You
77610095
r9e7

Question 2)

fwdOrder:
Expected values of registers:
	%eax = %ecx = 2
	%edx = %ebx = 3
Expected number of stalls is 0.
Program passes this test case.

srcAHazard:
Expected values of registers:
	%eax = 1
	%ecx = 2
	%edx = 3
	%ebx = 6
Expected number of stalls (excluding ones inserted by program) is 0.
Program passes this test case.

srcBHazard:
Expected values of registers:
	%eax = 17
	%ecx = 18
	%edx = 19
	%ebx = 16
Expected number of stalls is 0.
Program passes this test case.

aLoadUse:
Expected values of registers:
	%eax = %ecx = %edx = 10
	%ebx = 30
Expected number of stalls is 1.
Program passses this test case.

bLoadUse:
Expected values of registers:
	%eax = %ecx = %edx = 26
	%ebx = 16
Expected number of stalls is 1.
Program passes this test case.

takenJmp:
Expected values of registers:
	%ebx = %esp = 1
Expected number of stalls is 0.
Program passes this test case.

notTkJmp:
Expected values of registers:
	%ecx = %edx = 1
Expected number of stalls is 2, both are shut down when prediction is wrong.
Program passes this test case.

callRtn:
Expected values of registers:
	%eax = %ecx =1
Expected number of stalls is 3, injected by ret.
Program passes this test case.

cmov:
Expected values of registers:
	%eax = 1
	%ebx = 4
Expected number of stalls is 0.
Program passes this test case.


Question 3)

sum.s:
	cCnt = 57
	iCnt = 45
	CPI = cCnt / iCnt = 1.27

max.s:
	cCnt = 128
	iCnt = 98
	CPI = cCnt / iCnt = 1.31

heapsort-student.s:
	cCnt = 3651
	iCnt = 2960
	CPI = cCnt / iCnt = 1.23

CPI's are about half of the numbers from previous assignment.


Question 4)
I implemented predicting forward jumps will not be take, but backward jumps will.


Question 5)

Results of fwdOrder, srcAHazard, srcBHazard, aLoadUse, bLoadUse, callRtn, cmov stay the same, test cases pass.

takenJmp:
Expected values of registers stay the same.
Expected number of stalls is now 2.
Program passes this test case.

notTkJmp:
Expected values of registers stay the same.
Expected number of stalls is now 0.
Program passes this test case.


Question 6)

sum.s:
	cCnt = 57
	iCnt = 45
	CPI = cCnt / iCnt = 1.27

max.s:
	cCnt = 110
	iCnt = 98
	CPI = cCnt / iCnt = 1.12

heapsort-student.s:
	cCnt = 3569
	iCnt = 2960
	CPI = cCnt / iCnt = 1.21

CPI's are about half of the numbers from previous assignment.
CPI value is same for sum.s compare to question 2, but CPI's for max.s and heapsort-student.s are a bit lower comapre to question 2.


This assignment takes me 6 hours to complete.