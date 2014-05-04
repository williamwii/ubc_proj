package Machine;

import java.util.HashMap;
import java.lang.reflect.Constructor;
import Util.AbstractDataModel;
import Util.DataModelEvent;
import Util.HalfByteNumber;
import Util.SixByteNumber;
import Util.UnsignedByte;

public class Register extends AbstractDataModel {
  private static HashMap <Class, Integer> classBitLengths = new HashMap <Class, Integer> ();
  private String                          name;
  private boolean                         isUserEditable;
  private boolean                         isUnsigned;
  private long                            outputValue;
  private long                            inputValue;
  private boolean                         inputStable;
  private long                            bubbleValue;
  private Class                           valueClass;
  private long                            signExtend, valueMask, signMask;

  public enum ClockTransition { NORMAL, STALL, BUBBLE };
  
  public class ReadInputException extends RuntimeException {}
  
  static {
    classBitLengths.put (HalfByteNumber.class, 4);
    classBitLengths.put (Byte.class,           8);
    classBitLengths.put (Short.class,          16);
    classBitLengths.put (Integer.class,        32);
    classBitLengths.put (SixByteNumber.class,  48);
    classBitLengths.put (Long.class,           64);    
  }
  
  Register (String aName, Class aValueClass, boolean anIsUnsigned, boolean anIsUserEditable, long aBubbleValue) {
    name           = aName;
    isUserEditable = anIsUserEditable;
    isUnsigned     = anIsUnsigned; 
    valueClass     = aValueClass;
    bubbleValue    = aBubbleValue;
    inputValue     = bubbleValue;
    outputValue    = bubbleValue;
    initTwosComplementMasks ();
  }
  
  private void initTwosComplementMasks () {
    Integer bitLength = classBitLengths.get (valueClass);
    if (bitLength==null)
      throw new AssertionError ();
    if (bitLength!=64) {
      signExtend = ((long) -1) << bitLength;
      valueMask  = ~signExtend;
      signMask   = 1 << (bitLength-1);
    } else {
      signExtend = 0;
      valueMask  = (0xffffffff << 32) | 0xffffffff;
      signMask   = 0;
    }
  }
  
  private int signExtend (int value) {
    return (int) (value | (((value & signMask) != 0)? signExtend : 0)); 
  }
  
  private Class getInputValueWrapperClass () {
    if (valueClass == HalfByteNumber.class)
      return byte.class;
    else if (valueClass == Byte.class)
      return byte.class;
    else if (valueClass == Short.class)
      return short.class;
    else if (valueClass == Integer.class)
      return int.class;
    else 
      return long.class;
  }
  
  private Number castNumberToWrapper (long number) {
    if (valueClass == HalfByteNumber.class)
      return new Byte ((byte) number);
    else if (valueClass == Byte.class)
      return new Byte ((byte) number);
    else if (valueClass == Short.class)
      return new Short ((short) number);
    else if (valueClass == Integer.class)
      return new Integer ((int) number);
    else 
      return new Long (number);
  }
  
  public String getName () {
    return name;
  }
  
  private synchronized void writeSilently (long aValue) {
    inputValue  = aValue & valueMask;
    inputStable = true;
    notifyAll ();
  }
  
  public void write (long aValue) {
    writeSilently (aValue);
    tellObservers (new DataModelEvent (DataModelEvent.Type.WRITE, 0, 1));
  }
  
  public int read () {
    return isUnsigned? readUnsigned () : signExtend (readUnsigned ());
  }
  
  public int readUnsigned () {
    tellObservers (new DataModelEvent (DataModelEvent.Type.READ, 0, 1));
    return (int) outputValue;
  }
  
  public synchronized int readInput () {
    return isUnsigned? (int) readInputUnsigned () : signExtend ((int) readInputUnsigned ());
  }
  
  public synchronized int readInputUnsigned () {
    try {
      if (! inputStable)
	wait (2000);
      if (! inputStable)
	throw new InterruptedException ();
      tellObservers (new DataModelEvent (DataModelEvent.Type.READ, 0, 1));
      return (int) inputValue;
    } catch (InterruptedException e) {
      e.printStackTrace ();
      throw new ReadInputException ();
    }
  }
  
  public synchronized void tickClock (ClockTransition transition) {
    switch (transition) {
      case NORMAL:
	outputValue = inputValue;
	break;
      case STALL:
	inputValue  = outputValue;
	break;
      case BUBBLE:
	outputValue = bubbleValue;
	inputValue  = bubbleValue;
	break;
    }
    inputStable = false;
    tellObservers (new DataModelEvent (DataModelEvent.Type.WRITE_BY_USER, 0, 1));
  }
  
  public int getColumnCount () {
    return 2;
  }
  
  public Class getColumnClass (int columnIndex) {
    if (columnIndex==0)
      return String.class;
    else if (columnIndex==1)
      return valueClass;
    else
      throw new AssertionError ();
  }
  
  public String getColumnName (int columnIndex) {
    if (columnIndex==0)
      return "Reg";
    else if (columnIndex==1)
      return "Value";
    else
      throw new AssertionError ();
  }
  
  public int getRowCount () {
    return 1;
  }
  
  public Object getValueAt (int rowIndex, int columnIndex) {
    if (columnIndex==0)
      return name;
    else if (columnIndex==1) {
      try {
	Constructor constructor = valueClass.getConstructor (getInputValueWrapperClass ());
	return constructor.newInstance (castNumberToWrapper (inputValue));
      } catch (Exception e) {
	throw new AssertionError (e);
      }
    } else
      throw new AssertionError ();
  }
  
  public boolean isCellEditable (int rowIndex, int columnIndex) {
    if (columnIndex==0)
      return false;
    else if (columnIndex==1)
      return isUserEditable;
    else
      throw new AssertionError ();
  }
  
  @Override
  public void setValueAt (Object aValue, int rowIndex, int columnIndex) {
    if (columnIndex==1) {
      if (!(aValue instanceof Number))
	throw new ClassCastException ();
      writeSilently (((Number)aValue).longValue ());
      tickClock (ClockTransition.NORMAL);
      tellObservers (new DataModelEvent (DataModelEvent.Type.WRITE, 0, 1));
    } else
      throw new AssertionError ();
  }  
  
  @Override
  public void setValueAtByUser (Object aValue, int rowIndex, int columnIndex) {
    if (columnIndex==1) {
      if (!(aValue instanceof Number))
	throw new ClassCastException ();
      writeSilently (((Number)aValue).longValue ());
      tickClock (ClockTransition.NORMAL);
      tellObservers (new DataModelEvent (DataModelEvent.Type.WRITE_BY_USER, 0, 1));
    } else
      throw new AssertionError ();
  }  
}