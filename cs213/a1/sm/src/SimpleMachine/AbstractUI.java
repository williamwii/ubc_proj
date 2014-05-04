package SimpleMachine;

import java.util.Observer;
import java.util.Observable;
import java.util.EnumMap;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Vector;
import java.util.Date;
import Machine.AbstractCPU;
import Machine.AbstractMainMemory;
import Machine.RegisterSet;
import ISA.Memory;
import Util.DataModel;
import Util.DataModelEvent;
import Util.TableCellIndex;

/**
 * Base class for Simulator UI.  Registers to receive CPU events to implement
 * break/watch/trace points.  Extended by UI implementations.
 */

public abstract class AbstractUI implements Observer {
  
  protected static String           applicationName      = "Simple Machine";
  protected String                  applicationFullName;
  protected static String           applicationVersion   = "Version 2.4";
  protected static String[]         applicationCopyright = new String[] {"University of British Columbia", "Copyright \u00A9 2010 Mike Feeley.", "All Rights Reserved."};
  protected enum                    DebugType  { BREAK, TRACE }
  protected enum                    DebugPoint { INSTRUCTION, MEMORY_READ, MEMORY_WRITE, REGISTER_READ, REGISTER_WRITE }
  private   EnumMap<DebugType,EnumMap<DebugPoint,SortedSet<Integer>>> debugPointSet;
  private   DebugPointSetObservable debugPointSetObservable = new DebugPointSetObservable ();
  private   boolean                 isSingleStep;
  private   boolean                 isTrace;
  private   boolean                 isContinueRunning;
  private   int                     pauseMilliseconds;
  protected AbstractCPU             cpu;
  protected Memory                  memory;
  protected AbstractMainMemory      mainMemory;
  protected DataModel               registerFile;
  protected Vector <RegisterSet>    processorState;
  protected DataModel               pc;
  private   boolean                 isObservingCPU;
  protected boolean                 isMacShown;
  protected boolean                 isTwoProcStateCols;
  protected boolean                 isSmallInsMemDpy;
  protected boolean                 isRegFileInOwnCol;
  protected boolean                 isSmallCurInsDpy;
  private   int                     curPC;
  private   boolean                 firstInstruction;
  private   Object                  threadInterrupt = new Object ();
  private   DebugPointMonitor       debugPointMonitor;
  
  public AbstractUI (AbstractCPU aCPU, Memory aMemory, String options) {
    cpu                 = aCPU;
    memory              = aMemory;
    isMacShown          = options.contains       ("[showMac]");
    isTwoProcStateCols  = options.contains       ("[twoProcStateCols]");
    isSmallInsMemDpy    = options.contains       ("[smallInsMemDpy]");
    isRegFileInOwnCol   = options.contains       ("[regFileInOwnCol]");
    isSmallCurInsDpy    = options.contains       ("[isSmallCurInsDpy]");
    applicationFullName = applicationName.concat (String.format (" (%s%s)", memory.getIsaName (), ! cpu.getName ().equals ("")? "-".concat (cpu.getName ()) : ""));
    debugPointMonitor   = new DebugPointMonitor  ();
    mainMemory          = cpu.getMainMemory      ();
    registerFile        = cpu.getRegisterFile    ();
    processorState      = cpu.getProcessorState  ();
    pc                  = cpu.getPC              ();
    cpu.addObserver          (this);
    registerFile.addObserver (this);
    mainMemory.addObserver   (this);
    debugPointSet = new EnumMap<DebugType,EnumMap<DebugPoint,SortedSet<Integer>>> (DebugType.class);
    for (DebugType type : DebugType.values ()) {
      debugPointSet.put (type, new EnumMap<DebugPoint,SortedSet<Integer>> (DebugPoint.class));
      for (DebugPoint point : DebugPoint.values ()) 
	debugPointSet.get(type).put (point, new TreeSet<Integer> ());
    }
    isSingleStep   = false;
    isTrace        = false;
    isObservingCPU = false;
  }
  
  //
  // Upcalls from CPU execution
  //
  
  public void update (Observable o, Object arg) {
    if (isObservingCPU) {
      isObservingCPU = false;
      if (o instanceof AbstractCPU) {
	preInstr ();
      } else if (o == registerFile) {
	regAccess ((DataModelEvent) arg);
      } else if (o == mainMemory) {
	memAccess ((DataModelEvent) arg);
      } 
      isObservingCPU = true;
    }
  }
  
  void preInstr () {
    if (!firstInstruction) {
      // do after instruction executes (curPC is address of previous instruction executed)
      if (isTrace || debugPointSet.get(DebugType.TRACE).get(DebugPoint.INSTRUCTION).contains (curPC)) 
	showInstr (curPC);
      if (isSingleStep) {
	showInstr (curPC);
	cpu.triggerInterrupt ();	  	
      }
      // do before instruction executes
      if (debugPointSet.get(DebugType.BREAK).get(DebugPoint.INSTRUCTION).contains (pc.getValueAt (0,1))) {
	isContinueRunning = false;
	showBreak (DebugPoint.INSTRUCTION, (Integer) pc.getValueAt (0,1));
	cpu.triggerInterrupt ();	  	
      }
    } else
      firstInstruction = false;	
    curPC = (Integer) pc.getValueAt (0,1);
  }
  
  void regAccess (DataModelEvent event) {
    int regNum   = event.getRowIndex ();
    DebugPoint accessType = event.getType () == DataModelEvent.Type.READ? DebugPoint.REGISTER_READ : DebugPoint.REGISTER_WRITE;
    if (isTrace || isSingleStep || 
	debugPointSet.get(DebugType.TRACE).get(DebugPoint.INSTRUCTION).contains (curPC) || 
	debugPointSet.get(DebugType.TRACE).get(accessType).contains (regNum)) {
      int regValue = (Integer) registerFile.getValueAt (regNum, 1);
      if (event.getType () == DataModelEvent.Type.READ)
	showRegRead (curPC, regNum, regValue); 
      else
	showRegWrite (curPC, regNum, regValue); 
    }
    if (debugPointSet.get(DebugType.BREAK).get(accessType).contains (event.getRowIndex())) {
      isContinueRunning = false;
      showBreak (accessType, regNum);
      cpu.triggerInterrupt ();
    }
  }
  
  void memAccess (DataModelEvent event) {
    int memAddress = event.getCells ().get (0).rowIndex;
    DebugPoint accessType = event.getType () == DataModelEvent.Type.READ? DebugPoint.MEMORY_READ : DebugPoint.MEMORY_WRITE;
    if (isTrace || isSingleStep || 
	debugPointSet.get(DebugType.TRACE).get(DebugPoint.INSTRUCTION).contains (memAddress) ||
	debugPointSet.get(DebugType.TRACE).get(accessType).contains (memAddress)) {
      byte[] memValue = null;
      if (event.getType () == DataModelEvent.Type.READ)
	showMemRead (curPC, memAddress, memValue);      
      else
	showMemWrite (curPC, memAddress, memValue);      
    }
    if (debugPointSet.get(DebugType.BREAK).get(accessType).contains (memAddress)) {
      showBreak (accessType, memAddress);
      cpu.triggerInterrupt ();
    }
  }
  
  //
  // Public interface to UI
  //

  public boolean start () {
    boolean isHalted = false;;
    try {
      firstInstruction = true;
      isObservingCPU   = true;
      isHalted = cpu.start ();
      isObservingCPU   = false;
      if (isHalted) {
	showHalted ();
      }
    } catch (AbstractCPU.InvalidInstructionException e) {
      showMessage (String.format ("Invalid instruction at PC 0x%x\n", pc.getValueAt (0,1)));
    } catch (AbstractMainMemory.InvalidAddressException e) {
      showMessage (String.format ("Invalid address at PC 0x%x\n", pc.getValueAt (0,1)));
    } catch (Machine.Register.ReadInputException rie) {
      showMessage (String.format ("Timeout while attempting to readInput from register with no computed value (see console for details)."));
    } finally {
      return isHalted;
    }
  }
  
  public boolean step () {
    isSingleStep = true;
    boolean isHalted = start ();
    isSingleStep = false;
    return isHalted;
  }
  
  public interface Callback {
    void fire ();
  }
  
  public int getPauseMilliseconds () {
    return pauseMilliseconds;
  }
  
  public void setPauseMilliseconds (int aPause) {
    pauseMilliseconds = aPause;
    synchronized (threadInterrupt) {
      threadInterrupt.notifyAll ();
    }
  }
  
  public void run (Callback startingNewInstruction) {
    isContinueRunning = true;
    while (isContinueRunning) {
      startingNewInstruction.fire ();
      if (step ())
	break;
      long waitStart = new Date ().getTime ();
      while (isContinueRunning && (new Date ().getTime () - waitStart < pauseMilliseconds))
	try {
	  synchronized (threadInterrupt) {
	    threadInterrupt.wait (pauseMilliseconds);
	  }
	} catch (InterruptedException e) {}
    }
  }
  
  public void halt () {
    isContinueRunning = false;
    cpu.triggerInterrupt ();
    synchronized (threadInterrupt) {
      threadInterrupt.notifyAll ();
    }
    showHalted ();
  }
  
  public void gotoPC (int aPC) {
    cpu.setPC (aPC);
  }
  
  public boolean isDebugPointEnabled (DebugType debugType, DebugPoint debugPoint, int value) {
    SortedSet<Integer> dpSet = debugPointSet.get (debugType) .get (debugPoint);
    return dpSet.contains (value);
  }
  
  public void setDebugPoint (DebugType debugType, DebugPoint debugPoint, int value, boolean isEnabled) {
    SortedSet<Integer> dpSet = debugPointSet.get (debugType) .get (debugPoint);
    if (isEnabled)
      dpSet.add (value);
    else
      dpSet.remove (value);
    debugPointSetObservable.tellObservers (new DataModelEvent (DataModelEvent.Type.WRITE_BY_USER, value,0));
  }
  
  public void clearAllDebugPoints (DebugType debugType) {
    Vector<TableCellIndex> bpList = new Vector<TableCellIndex> ();
    for (SortedSet<Integer> dpSet : debugPointSet.get (debugType).values ()) {
      for (Integer adr : dpSet)
	bpList.add (new TableCellIndex (adr, 0));
      dpSet.clear ();
    }
    debugPointSetObservable.tellObservers (new DataModelEvent (DataModelEvent.Type.WRITE_BY_USER, bpList));
  }  
  
  protected void addDebugPointObserver (Observer o) {
    debugPointSetObservable.addObserver (o);
  }
  
  class DebugPointSetObservable extends Observable {
    void tellObservers (DataModelEvent event) {
      setChanged ();
      notifyObservers (event);
    }
  }
  
  /**
   * Monitor memory to adjust breakpoints addresses when memory data inserted or deleted.
   */
  class DebugPointMonitor implements Memory.LengthChangedListener {
    DebugPointMonitor () {
      memory.addLengthChangedListener (this);
    }
    public void changed (int address, int length, int lastAddress, Type type) {
      for (EnumMap<DebugPoint,SortedSet<Integer>> dType : debugPointSet.values ())
	for (SortedSet<Integer> dPoint : dType.values ()) {
	  Vector<Integer> chg = new Vector<Integer> ();
	  for (Integer dAdr : dPoint)
	    if (dAdr >= address && dAdr <= lastAddress)
	      chg.add (dAdr);
	  for (Integer dAdr : chg) 
	    dPoint.remove (dAdr);
	  for (Integer dAdr : chg)
	    if (type == Memory.LengthChangedListener.Type.DELETED) {
	      if (dAdr > address)
		dPoint.add (dAdr - length);
	    } else if (type == Memory.LengthChangedListener.Type.INSERTED) {
	      dPoint.add (dAdr + length);
	    } else
	      throw new AssertionError (type);
	}
    }
  }
  
  //
  // Defined by subclasses
  //
  
  public abstract void showBreak    (DebugPoint point, int value);
  public abstract void showInstr    (int pc);
  public abstract void showRegRead  (int pc, int regNum, int value);
  public abstract void showRegWrite (int pc, int regNum, int value);
  public abstract void showMemRead  (int pc, int address, byte[] value);
  public abstract void showMemWrite (int pc, int address, byte[] value);
  public abstract void showHalted   ();
  public abstract void showMessage  (String message);
}