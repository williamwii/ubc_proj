package Machine;

import java.util.Vector;
import java.util.Observable;
import Util.DataModel;

public abstract class AbstractCPU extends Observable {
  
  String name;
  
  /**
   * General purpose register file.
   */
  protected RegisterSet reg = new RegisterSet ("Register File");
  
  /**
   * Processor state registers.
   */
  protected Vector <RegisterSet> processorState              = new Vector <RegisterSet> ();
  public final static String     PC                          = "PC";
  public final static String     CURRENT_INSTRUCTION_ADDRESS = "CurrentInstructionAddress";
  
  /**
   * Interrupt flag
   */
  protected boolean     isInterrupt;
  
  protected AbstractMainMemory mem = null;
  
  public AbstractCPU (String aName, AbstractMainMemory aMem) {
    name = aName;
    mem  = aMem;
  }
  
  public AbstractCPU (AbstractMainMemory aMem) {
    this ("", aMem);
  }
  
  public AbstractMainMemory   getMainMemory ()     { return mem; }
  public RegisterSet          getRegisterFile ()   { return reg; }
  public Vector <RegisterSet> getProcessorState () { return processorState;  }
  
  /**
   * Name of this ISA implementation (optional).
   */
  public String getName () {
    return name;
  }
  
  /**
   * Export the PC register.
   */
  public DataModel getPC () {
    return processorState.get (0).get (PC);
  }
  
  /**
   * Set PC to value
   */
  public void setPC (int aPC) {
    Register pc = processorState.get (0).get (PC);
    pc.write (aPC);
    pc.tickClock (Register.ClockTransition.NORMAL);
  }
  
  /**
   * Exception indicating than an invalid instruction was just detected by the CPU.
   */
  public static class InvalidInstructionException extends Exception {};
  
  /** 
   * Exception indicating that the CPU just retired a halt instruction.
   */
  public static class MachineHaltException extends Exception {}
  
  /**
   * Abstract class that implements cpu control logic.
   */
  protected abstract void cycle () throws InvalidInstructionException, MachineHaltException, AbstractMainMemory.InvalidAddressException;
  
  public synchronized void triggerInterrupt () {
    isInterrupt = true;
  }
  
  /**
   * Start processor execution.
   */
  public boolean start () throws InvalidInstructionException, AbstractMainMemory.InvalidAddressException {
    isInterrupt = false;
    try {
      while (true) {
	
	setChanged      ();
	notifyObservers ();
	
	if (isInterrupt) {
	  isInterrupt = false;
	  return false;
	}
	
	cycle ();
	
      }
    } catch (MachineHaltException e) {
      return true;
    }
  }
}

