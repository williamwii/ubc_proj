Wei You
77610095

Question 1:
1.
For test case sBHazard, after all the instructions, the expected register values are:
	%eax = 17
	%ecx = 18
	%edx = 19
	%ebx = 16
But the erroneous outputs are:
	%eax = 16
	%ecx = 16
	%edx = 16
	%ebx = 16
Stepping through the instructions, seems like the CPU is not inserting nop even though the new value of %eax is not yet written to register when instruciton "addl   %ebx, %eax" is about to enter execute stage.
This is because CPU.java is not checking in execute stage, srcB is data hazard or not. This can be fixed by changing the line <if (isDataHazardOnReg (d.srcA.getValueProduced()))> to <if (isDataHazardOnReg (d.srcA.getValueProduced()) || isDataHazardOnReg (d.srcB.getValueProduced()))>
to check both srcA and srcB's data hazard.


2.
For test case aLoadUse, the expected %ebx value when program halts is 30, but the output is 10.
The program starts executing "addl   %eax, %ebx" before the value of %eax is written back to %eax from memory.
This is because the CPU is not checking if any stages after execute is writing values from memory to registers srcA and/or srcB.
The fix is to check if dstM in execute, memory and write back is equal to srcA or srcB, if yes, stall the CPU.
  private boolean isDataHazardOnReg (int reg)
  {
    return reg != R_NONE && (E.dstE.get() == reg || E.dstM.get() == reg
	    			|| M.dstE.get() == reg || M.dstM.get() == reg
	    			|| W.dstE.get() == reg || W.dstM.get() == reg);
  }

  
3.
In test case notTkJmp, the first line of JNT is executed which it should not be.
This is because the program passes the instruction in predicted PC to decode and gets executed before the conditional jump finishes execution.
The fix for this is to have one more condition when checking for conditional jump control hazard, If execution stage is handling a conditional jump, insert another stall.
    else if ((D.iCd.get()==I_JXX && D.iFn.get()!=C_NC)
	    || (E.iCd.get()==I_JXX && E.iFn.get()!=C_NC)) {
      F.stall  = true;
      D.bubble = true;
    }
    
    
Question 2
sum.s:
	cCnt: 117
	iCnt: 45
	CPI = cCnt / iCnt = 2.6
	
max.s:
	cCnt: 236
	iCnt: 98
	CPI = 2.408
	
heapsort-student.s:
	cCnt: 7635
	iCnt: 2960
	CPI = 2.58
	

This assignment takes me 4 hours to complete.