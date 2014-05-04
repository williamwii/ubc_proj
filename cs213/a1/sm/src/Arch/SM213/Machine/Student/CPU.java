package Arch.SM213.Machine.Student;

import Arch.SM213.Machine.AbstractSM213CPU;
import Machine.RegisterSet;
import Machine.Register;
import Util.HalfByteNumber;
import Util.SixByteNumber;
import Util.UnsignedByte;

/**
 * The Simple Machine CPU.
 *
 * Simulate the execution of a single cycle of the Simple Machine CPU. 
 *
 * @see AbstractSM213CPU
 */

public class CPU extends AbstractSM213CPU {
  
  public CPU (MainMemory aMem) {
    super (aMem);
  }
  
  /**
   * Fetch Stage of CPU Cycle.
   * Fetch instruction at address stored in "pc" register from memory into instruction register 
   * and set "pc" to point to the next instruction to execute.
   *
   * Input register:   pc.
   * Output registers: pc, instruction, insOpCode, insOp0, insOp1, insOp2, insOpImm, insOpExt
   * @see AbstractSM213CPU for pc, instruction, insOpCode, insOp0, insOp1, insOp2, insOpImm, insOpExt
   *
   * @throws MainMemory.InvalidAddressException when program counter contains an invalid memory address
   */
  protected void fetch () throws MainMemory.InvalidAddressException {
    ;
  }
  
  /**
   * Execution Stage of CPU Cucle.
   * Execute instruction that was fetched by Fetch stage.
   *
   * Input state: pc, instruction, insOpCode, insOp0, insOp1, insOp2, insOpImm, insOpExt, reg, mem
   * Ouput state: pc, reg, mem
   * @see AbstractSM213CPU for pc, instruction, insOpCode, insOp0, insOp1, insOp2, insOpImm, insOpExt
   * @see MainMemory       for mem
   * @see AbstractCPU      for reg
   *
   * @throws InvalidInstructionException                when instruction format is invalid
   * @throws MachineHaltException                       when instruction is the HALT instruction
   * @throws RegisterSet.InvalidRegisterNumberException when instruction references an invalid register (i.e, not 0-7)
   * @throws MainMemory.InvalidAddressException         when instruction references an invalid memory address
   */
  protected void execute () throws InvalidInstructionException, MachineHaltException, RegisterSet.InvalidRegisterNumberException, MainMemory.InvalidAddressException {
    ;
  }
}