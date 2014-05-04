package Arch.SM213.Machine;

import Machine.AbstractCPU;
import Machine.Register;
import Machine.RegisterSet;
import Machine.AbstractMainMemory;
import Util.HalfByteNumber;
import Util.SixByteNumber;

public abstract class AbstractSM213CPU extends AbstractCPU {
  protected RegisterSet ps;
  protected Register    pc;           // address of next instruction
  protected Register    instruction;  // value of current instruction
  protected Register    insOpCode;    // opcode 
  protected Register    insOp0;       // operand 0
  protected Register    insOp1;       // operand 1
  protected Register    insOp2;       // operand 2
  protected Register    insOpImm;     // immdiate operand
  protected Register    insOpExt;     // extended operand
  protected Register    curInst; 
  
  public AbstractSM213CPU (AbstractMainMemory aMem) {
    super (aMem);
    for (int r=0; r<8; r++)
      reg.addSigned (String.format ("r%d", r), true);
    ps = new RegisterSet ("");
    processorState.add (ps);
    pc          = ps.addUnsigned ("PC",          Integer.class, true);
    instruction = ps.addUnsigned ("Instruction", SixByteNumber.class);
    insOpCode   = ps.addUnsigned ("Ins Op Code", HalfByteNumber.class);
    insOp0      = ps.addUnsigned ("Ins Op 0",    HalfByteNumber.class);
    insOp1      = ps.addUnsigned ("Ins Op 1",    HalfByteNumber.class);
    insOp2      = ps.addUnsigned ("Ins Op 2",    HalfByteNumber.class);
    insOpImm    = ps.addSigned   ("Ins Op Imm",  Byte.class);
    insOpExt    = ps.addSigned   ("Ins Op Ext",  Integer.class);
    curInst     = ps.add         (AbstractCPU.CURRENT_INSTRUCTION_ADDRESS, Integer.class, true, false, false, -1);
  }

  /**
   * Computes one cycle of the SM213 CPU
   */
  protected void cycle () throws InvalidInstructionException, MachineHaltException, AbstractMainMemory.InvalidAddressException {
    try {
      try {
	curInst.write (pc.read ());
	fetch ();
      } finally {
	tickClock ();
      }
      try {
	execute ();
      } finally {
	tickClock ();
      }
    } catch (RegisterSet.InvalidRegisterNumberException ire) {
      throw new InvalidInstructionException ();
    }
  }
  
  @Override
  public void setPC (int aPC) {
    ps.tickClock (Register.ClockTransition.BUBBLE);
    super.setPC (aPC);
  }
  
  /**
   * Implemented by STUDENTS to fetch next instruction from memory into CPU processorState registers.
   */
  protected abstract void fetch () throws AbstractMainMemory.InvalidAddressException;
  
  /**
   * Impemented by STUDENTS to execute instruction currently loaded in processorState registers.
   */
  protected abstract void execute () throws InvalidInstructionException, MachineHaltException, RegisterSet.InvalidRegisterNumberException, AbstractMainMemory.InvalidAddressException ;
  
  /**
   * Implement a clock tick by telling registerFile and processorState to save their current
   * input values and to start presenting them on their outputs.
   */
  private void tickClock () {
    reg.tickClock (Register.ClockTransition.NORMAL);
    ps.tickClock  (Register.ClockTransition.NORMAL);
  }
}