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
    int val = pc.read();
    UnsignedByte[] byteAry = mem.readUnaligned(val, 2);
    byte opCode = (byte) (byteAry[0].value()>>>4);
    insOpCode.write( opCode );
    insOp0.write( (byte)byteAry[0].value() );
    
    insOp1.write( (byte)(byteAry[1].value()>>>4) );
    insOp2.write( (byte)(byteAry[1].value()) );
    
    insOpImm.write( byteAry[1].value() );
    
    if ( opCode==0 ){
    	insOpExt.write( mem.readIntegerUnaligned(val+2) );
    	val += 6;
    	instruction.write( byteAry[0].value()<<40 | byteAry[1].value()<<32 | insOpExt.read() );

    }
    else{
    	val += 2;
    	instruction.write( byteAry[0].value()<<40 | byteAry[1].value()<<32 );
    }
    
    pc.write( val );
   
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
    
	  switch( insOpCode.read() ){
	  case 0x0: reg.write(insOp0.read(), insOpExt.read()); break;
	  case 0x1: reg.write(insOp2.read(), mem.readIntegerUnaligned(reg.read(insOp1.read()))); break;
	  case 0x2: reg.write(insOp2.read(), mem.readIntegerUnaligned(reg.read(insOp0.read())+(4*reg.read(insOp1.read())))); break;
	  case 0x3: mem.writeIntegerUnaligned(reg.read(insOp2.read()), reg.read(insOp0.read())); break;
	  case 0x4: mem.writeIntegerUnaligned(reg.read(insOp1.read())+(4*reg.read(insOp2.read())), reg.read(insOp0.read())); break;
	  default: throw new InvalidInstructionException();
	  }
  }

}