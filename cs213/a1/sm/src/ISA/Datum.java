package ISA;

import Util.BitString;
import Util.BitStream;


/**
 * A single concrete instance of data in memory.
 */

public class Datum extends MemoryCell {
  String lastRequestedLabel = null;
  
  Datum (Memory aMemory, int anAddress, BitString aValue, String aLabel, String aComment) {
    super (aMemory, anAddress, aValue, aLabel, aComment);
  }
  
  public static Datum valueOfMemory (Memory memory, int address, int length, String label, String comment) {
    return new Datum (memory, address, new BitStream (memory, address).getValue (length*8), label, comment);
  }
  
  public static Datum valueOf (Memory memory, int address, int value, String label, String comment) {
    return new Datum (memory, address, new BitString (32, value), label, comment);
  }
  
  @Override
  public void copyFrom (MemoryCell aCell) {
    super.copyFrom (aCell);
    Datum dat = (Datum) aCell;
    lastRequestedLabel = dat.lastRequestedLabel;
  }
  
  String toAsm () {
    return String.format (".long 0x%08x", value.getValue ());
  }
  
  String toSavableAsm () {
    return String.format (".long 0x%08x", getSavableValue ().getValue ());
  }
  
  String toMac () {
    return String.format ("%08x", value.getValue ());
  }
  
  String valueAsLabel () {
    Byte b[] = value.toBytes ();
    int  v = (b[0]<<24 & 0xff000000) | (b[1]<<16 & 0xff0000) | (b[2]<<8 & 0xff00) | (b[3] & 0xff);
    lastRequestedLabel = memory.getLabelMap ().getLabel (v);
    return lastRequestedLabel;
  }
  
  boolean memoryResyncedFromAsm () {
    String lvLast = lastRequestedLabel;
    String lv     = valueAsLabel ();
    return ! (lvLast!=null? lvLast : "").trim ().equals ((lv!=null? lv : "").trim ());
  }
  
  boolean asmResyncedFromMemory () {
    return memoryResyncedFromAsm ();
  }
}

