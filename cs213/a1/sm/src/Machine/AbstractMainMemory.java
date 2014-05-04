package Machine;

import java.util.List;
import java.util.Vector;
import Util.AbstractDataModel;
import Util.DataModelEvent;
import Util.TableCellIndex;
import Util.UnsignedByte;

public abstract class AbstractMainMemory extends AbstractDataModel {
  
  public static class InvalidAddressException extends Exception {}
  
  //////////////////
  // RE-IMPLEMENT THESE METHODS IN CONCRETE MAIN MEMORY FILE CLASS
  //
  
  /**
   * Read a sequence of bytes from memory.  Protected method called by public read method. 
   * 
   * @param  address  byte address of first byte to read.
   * @param  length   number of bytes to read.
   *
   * @throws InvalidAddressException if address is out of range.
   */
  protected abstract UnsignedByte[] get (int address, int length) throws InvalidAddressException;
  
  /**
   * Write a sequence of bytes to memory.  Protected method called by public write method. 
   *
   * @param  address  byte address of first byte to write.
   * @param  value    array of unsigned bytes to write to memory at this address.
   *
   * @throws InvalidAddressException if address is out of range.
   */
  protected abstract void set (int address, UnsignedByte[] value) throws InvalidAddressException;
  
  /**
   * Determine whether specified address and length represent an ALIGNED access.  Protected method called 
   * by public read and write methods.
   *
   * An address is aligned if and only if the address modulo value.length is 0 (i.e., the low order log 2 
   * (length) bits are 0. Aligned memory access is faster than unaligned access and so compilers should 
   * attempt to used aligned access whenever possible.  It is sometimes not possible, however, particularlly 
   * for reading instructions in architectures that support variable instruction lengths such as SM213 and Y86,
   * for example.
   *
   * @return true iff access to address of length bytes is an aligned access
   */
  protected abstract boolean isAccessAligned (int address, int length);
  
  /**
   * Convert a byte array to an integer.
   *
   * @param byteAtAddrPlus0  value of byte at some memory address addr
   * @param byteAtAddrPlus1  value of byte at some memory address addr + 1
   * @param byteAtAddrPlus2  value of byte at some memory address addr + 2
   * @param byteAtAddrPlus3  value of byte at some memory address addr + 3
   * @return integer comprised of this four bytes organized according to the Endianness of the target ISA
   */
  public abstract int bytesToInteger (UnsignedByte byteAtAddrPlus0, UnsignedByte byteAtAddrPlus1, UnsignedByte byteAtAddrPlus2, UnsignedByte byteAtAddrPlus3);
  
  /**
   * Convert an integer to a byte array.
   *
   * @param i an 32-bit integer value
   * @return an array of bytes that comprise the integer in address order according to the Endianness of the target ISA
   */
  public abstract UnsignedByte[] integerToBytes (int i);
  
  /*
   * Byte capacity of memory.
   */
  public abstract int length ();
  
  /////////////////////
  // For Machine
  //

  /**
   * READ a sequence of bytes from memory starting at specified ALIGNED.  An address is aligned
   * if and only if the address modulo length is 0 (i.e., the low order log 2 (length) bits are 0.
   *
   * @throws InvalidAddressException if address is out of range or is not aligned.
   */
  final public UnsignedByte[] read (int address, int length) throws InvalidAddressException {
    if (isAccessAligned (address,length))
      return readUnaligned (address, length);
    else
      throw new InvalidAddressException ();
  }
  
  /**
   * WRITE a sequence of bytes to memory starting at specified ALIGNED.  An address is aligned
   * if and only if the address modulo value.length is 0 (i.e., the low order log 2 (value.length) bits are 0.
   *
   * @throws InvalidAddressException if address is out of range or is not aligned.
   */
  final public void write (int address, UnsignedByte[] value) throws InvalidAddressException {
    if (isAccessAligned (address, value.length)) 
      writeUnaligned (address, value);
    else 
      throw new InvalidAddressException ();
  }
  
  /**
   * READ a sequence of bytes from memory starting at specified possibly-UNALIGNED address.  An address is aligned
   * if and only if the address modulo length is 0 (i.e., the low order log 2 (length) bits are 0.
   *
   * Unaligned memory access is, in real hardware, slower than aligned access and so clients should use the
   * aligned read and write methods whenever possible.
   *
   * @throws InvalidAddressException if address is out of range.
   */
  final public UnsignedByte[] readUnaligned (int address, int length) throws InvalidAddressException {
    UnsignedByte[] value = get (address, length);
    List<TableCellIndex> cells = new Vector<TableCellIndex> ();
    for (int i=0; i<length; i++)
      cells.add (new TableCellIndex (address+i, 1));
    tellObservers (new DataModelEvent (DataModelEvent.Type.READ, cells));
    return value;
  }
  
  /**
   * WRITE a sequence of bytes to memory starting at specified possibly-UNALIGNED address.  An address is aligned
   * if and only if the address modulo value.length is 0 (i.e., the low order log 2 (value.length) bits are 0.
   *
   * Unaligned memory access is, in real hardware, slower than aligned access and so clients should use the
   * aligned read and write methods whenever possible.
   *
   * @throws InvalidAddressException if address is out of range.
   */
  final public void writeUnaligned (int address, UnsignedByte[] value) throws InvalidAddressException {
    set (address, value);
    List<TableCellIndex> cells = new Vector<TableCellIndex> ();
    for (int i=0; i<value.length; i++)
      cells.add (new TableCellIndex (address+i, 1));
    tellObservers (new DataModelEvent (DataModelEvent.Type.WRITE, cells));
  }
  
  /**
   * Read a four-byte integer from memory at ALIGNED address.
   */
  public int readInteger (int address) throws InvalidAddressException {
    if (isAccessAligned (address, 4))
      return readIntegerUnaligned (address);
    else
      throw new InvalidAddressException ();
  }
  
  /*
   * Write a four-byte integer to memory at ALIGNED, power-of-two address.
   */
  public void writeInteger (int address, int value) throws InvalidAddressException {
    if (isAccessAligned (address, 4))
      writeIntegerUnaligned (address, value);
    else
      throw new InvalidAddressException ();
  }
  
  
  /**
   * Read a four-byte Big-Endian integer from memory at possibly UNALIGNED address.
   *
   * Unaligned memory access is, in real hardware, slower than aligned access and so clients should use the
   * aligned read and write methods whenever possible.
   *
   */
  public int readIntegerUnaligned  (int address) throws InvalidAddressException {
    UnsignedByte b[] = readUnaligned (address, 4);
    return bytesToInteger (b[0], b[1], b[2], b[3]);
  }
  
  /*
   * Write a four-byte Big-Endian integer to memory at possibly UNALIGNED address.
   *
   * Unaligned memory access is, in real hardware, slower than aligned access and so clients should use the
   * aligned read and write methods whenever possible.
   *
   */
  public void writeIntegerUnaligned (int address, int value) throws InvalidAddressException {
    writeUnaligned (address, integerToBytes (value));
  }
  
  
  /////////////////////////
  // Simulator Glue, implements DataModel
  //
  
  public Class getColumnClass (int columnIndex) {
    if (columnIndex==0)
      return Integer.class;
    else if (columnIndex==1)
      return Byte.class; 
    else
      throw new AssertionError ();
  }
  
  public int getColumnCount () {
    return 2;
  }
  
  public String getColumnName (int columnIndex) {
    if (columnIndex==0)
      return "Address";
    else if (columnIndex==1)
      return "Value";
    else
      throw new AssertionError ();
  }
  
  public int getRowCount () {
    return length ();
  }
  
  public Object getValueAt (int rowIndex, int columnIndex) {
    if (columnIndex==0)
      return new Integer (rowIndex);
    else if (columnIndex==1)
      try {
	return new Byte ((byte) get (rowIndex, 1) [0].value ());
      } catch (InvalidAddressException e) {
	throw new IndexOutOfBoundsException ();
      }
    else
      throw new AssertionError ();
  }
  
  public boolean isCellEditable (int rowIndex, int columnIndex) {
    if (columnIndex==0)
      return false;
    else if (columnIndex==1)
      return true;
    else
      throw new AssertionError ();
  }
  
  @Override
  public void setValueAt (Object[] aValue, int rowIndex, int columnIndex) {
    if (columnIndex==1) {
      Vector<TableCellIndex> writtenCells = new Vector<TableCellIndex> ();
      try {
	Byte[] b = (Byte[]) aValue;
	for (int i=0; i<b.length; i++) {
	  set (rowIndex+i, new UnsignedByte[] { new UnsignedByte (b[i]) });
	  writtenCells.add (new TableCellIndex (rowIndex+i, columnIndex));
	}
      } catch (InvalidAddressException e) {
	throw new IndexOutOfBoundsException ();
      } finally {
	tellObservers (new DataModelEvent (DataModelEvent.Type.WRITE, writtenCells));	
      }
    } else
      throw new AssertionError ();
  }
  
  @Override
  public void setValueAtByUser (Object[] aValue, int rowIndex, int columnIndex) {
    if (columnIndex==1) {
      Vector<TableCellIndex> writtenCells = new Vector<TableCellIndex> ();
      try {
	Byte[] b = (Byte[]) aValue;
	for (int i=0; i<b.length; i++) {
	  set (rowIndex+i, new UnsignedByte[] { new UnsignedByte (b[i]) });
	  writtenCells.add (new TableCellIndex (rowIndex+i, columnIndex));
	}
      } catch (InvalidAddressException e) {
	throw new IndexOutOfBoundsException ();
      } finally {
	tellObservers (new DataModelEvent (DataModelEvent.Type.WRITE_BY_USER, writtenCells));	
      }
    } else
      throw new AssertionError ();
  }
  
  @Override
  public void setValueAt (Object aValue, int rowIndex, int columnIndex) {
    setValueAt (new Byte [] { (Byte) aValue }, rowIndex, columnIndex);
  }
  
  @Override
  public void setValueAtByUser (Object aValue, int rowIndex, int columnIndex) {
    setValueAtByUser (new Byte[] { (Byte) aValue }, rowIndex, columnIndex);
  }
}