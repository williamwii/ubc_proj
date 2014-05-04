package Machine;

import java.util.Vector;
import java.util.HashMap;
import java.util.Observer;
import java.util.Observable;
import Util.AbstractDataModel;
import Util.DataModelEvent;

public class RegisterSet extends AbstractDataModel implements Observer {
  String name;
  Vector  <Register>         registers   = new Vector  <Register>         ();
  HashMap <String, Register> registerMap = new HashMap <String, Register> ();
  
  public class InvalidRegisterNumberException extends Exception {};
  public class InvalidRegisterNameError       extends Error {};
  
  public RegisterSet (String aName) {
    name = aName;
  }
  
  public Register add (String regName, Class regClass, boolean isUnsigned, boolean isUserEditable, boolean isVisible, long bubbleValue) {
    Register reg = new Register (regName, regClass, isUnsigned, isUserEditable, bubbleValue);
    if (isVisible)
      registers.add (reg);
    registerMap.put (regName, reg);
    reg.addObserver (this);
    return reg;
  }
  
  public Register addUnsigned (String regName, Class regClass, long bubbleValue) {
    return add (regName, regClass, true, false, true, bubbleValue);
  }
  
  public Register addUnsigned (String regName, Class regClass, boolean isUserEditable) {
    return add (regName, regClass, true, isUserEditable, true, 0);
  }
  
  public Register addUnsigned (String regName, Class regClass) {
    return add (regName, regClass, true, false, true, 0);
  }
  
  public Register addSigned (String regName, Class regClass) {
    return add (regName, regClass, false, false, true, 0);
  }
  
  public Register addUnsigned (String regName, boolean isUserEditable) {
    return add (regName, Integer.class, true, isUserEditable, true, 0);
  }
  
  public Register addSigned (String regName, boolean isUserEditable) {
    return add (regName, Integer.class, false, isUserEditable, true, 0);
  }
  
  public Register addUnsigned (String regName) {
    return add (regName, Integer.class, true, false, true, 0);
  }
  
  public Register addSigned (String regName) {
    return add (regName, Integer.class, false, false, true, 0);
  }
  
  public Register get (String aName) {
    return registerMap.get (aName);
  }
  
  public String getName () {
    return name;
  }
  
  public int read (int regIndex) throws InvalidRegisterNumberException {
    try {
      return registers.get (regIndex).read ();
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new InvalidRegisterNumberException ();
    }
  }
  
  public void write (int regIndex, long value) throws InvalidRegisterNumberException {
    try {
      registers.get (regIndex).write (value);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new InvalidRegisterNumberException ();
    }
  }
  
  public int read (String regName) throws InvalidRegisterNameError {
    try {
      return registerMap.get (regName).read ();
    } catch (NullPointerException e) {
      throw new InvalidRegisterNameError ();
    }
  }
  
  public int readInput (String regName) throws InvalidRegisterNameError {
    try {
      return registerMap.get (regName).readInput ();
    } catch (NullPointerException e) {
      throw new InvalidRegisterNameError ();
    }
  }
  
  public int readInput (int regIndex) throws InvalidRegisterNumberException {
    try {
      return registers.get (regIndex).readInput ();
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new InvalidRegisterNumberException ();
    }
  }
  
  public void write (String regName, long value) throws InvalidRegisterNameError {
    try {
      registerMap.get (regName).write (value);
    } catch (NullPointerException e) {
      throw new InvalidRegisterNameError ();
    }
  }
  
  public void tickClock (Register.ClockTransition transition) {
    for (Register r : registerMap.values ())
      r.tickClock (transition);
  }
  
  public void update (Observable o, Object obj) {
    Register       reg   = (Register)       o;
    DataModelEvent event = (DataModelEvent) obj;
    tellObservers (new DataModelEvent (event.getType (), registers.indexOf (reg), 1));
  }
  
  public int getColumnCount () {
    return 2;
  }
  
  public Class getColumnClass (int columnIndex) {
    if (columnIndex==0)
      return String.class;
    else if (columnIndex==1)
      return Long.class;
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
    return registers.size ();
  }
  
  public Object getValueAt (int rowIndex, int columnIndex) {
    return registers.get (rowIndex).getValueAt (rowIndex, columnIndex);
  }
  
  public boolean isCellEditable (int rowIndex, int columnIndex) {
    return registers.get (rowIndex).isCellEditable (0,columnIndex);
  }
  
  public void setValueAt (Object aValue, int rowIndex, int columnIndex) {
    registers.get (rowIndex).setValueAt (aValue, rowIndex, columnIndex);
  }
  
  public void setValueAtByUser (Object aValue, int rowIndex, int columnIndex) {
    registers.get (rowIndex).setValueAtByUser (aValue, rowIndex, columnIndex);
  }
}