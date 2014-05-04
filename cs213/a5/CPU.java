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
    insOp0.write( byteAry[0].value() & 0x0f );
    
    insOp1.write( (byteAry[1].value()>>>4) );
    insOp2.write( (byteAry[1].value()) & 0x0f );
    
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
	  case 0x1: reg.write(insOp2.read(), mem.readIntegerUnaligned(reg.read(insOp1.read())+4*insOp0.read())); break;
	  case 0x2: reg.write(insOp2.read(), mem.readIntegerUnaligned(reg.read(insOp0.read())+(4*reg.read(insOp1.read())))); break;
	  case 0x3: mem.writeInteger(reg.read(insOp2.read())+(4*insOp1.read()), reg.read(insOp0.read())); break;
	  case 0x4: mem.writeInteger(reg.read(insOp1.read())+(4*reg.read(insOp2.read())), reg.read(insOp0.read())); break;
	  case 0x6:
		  switch( insOp0.read() ){
		  case 0x0: reg.write(insOp2.read(), reg.read(insOp1.read())); break;
		  case 0x1: reg.write(insOp2.read(), reg.read(insOp1.read()) + reg.read(insOp2.read())); break;
		  case 0x2: reg.write(insOp2.read(), reg.read(insOp1.read()) & reg.read(insOp2.read())); break;
		  case 0x3: reg.write(insOp2.read(), reg.read(insOp2.read()) + 0x1); break;
		  case 0x4: reg.write(insOp2.read(), reg.read(insOp2.read()) + 0x4); break;
		  case 0x5: reg.write(insOp2.read(), reg.read(insOp2.read()) - 0x1); break;
		  case 0x6: reg.write(insOp2.read(), reg.read(insOp2.read()) - 0x4); break;
		  case 0x7: reg.write(insOp2.read(), ~reg.read(insOp2.read())); break;
		  case 0xf: reg.write(insOp2.read(), pc.read()); break;
		  default: throw new InvalidInstructionException();
		  }
		  break;
	  case 0x7:
		  int imm = insOpImm.read();
		  if ( imm<0 ){
			  int temp = ~imm + 1;
			  reg.write(insOp0.read(), reg.read(insOp0.read())>>temp);
		  }
		  else{
			  reg.write(insOp0.read(), reg.read(insOp0.read())<<imm);
		  }
		  break;
	  case 0x8: pc.write( pc.read()+insOpImm.read()*0x2 ); break;
	  case 0x9:
		  if ( reg.read(insOp0.read())==0x0 )
			  pc.write( pc.read()+insOpImm.read()*0x2 );
		  break;
	  case 0xa:
		  if ( reg.read(insOp0.read())>0x0 )
			  pc.write( pc.read()+insOpImm.read()*0x2 );
		  break;
	  case 0xb: pc.write( insOpExt.read() ); break;
	  case 0xc: pc.write(reg.read(insOp0.read())+2*insOpImm.read()); break;
	  case 0xd: pc.write(mem.readIntegerUnaligned(reg.read(insOp0.read())+4*insOpImm.read())); break;
	  case 0xe: pc.write(mem.readIntegerUnaligned(reg.read(insOp0.read())+4*reg.read(insOp1.read()))); break;
	  case 0xf:
		  switch( insOp0.read() ){
		  case 0x0: throw new MachineHaltException();
		  case 0xf: break;
		  default: throw new InvalidInstructionException();
		  }
		  break;
	  default: throw new InvalidInstructionException();
	  }
  }

}