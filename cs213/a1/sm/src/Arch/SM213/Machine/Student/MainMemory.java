package Arch.SM213.Machine.Student;

import Util.UnsignedByte;

/**
 * Main Memory of Simple CPU
 *
 * Provides an abstraction of main memory (DRAM).
 */

public class MainMemory extends Machine.AbstractMainMemory {
  
  /**
   * Allocate memory.
   * @param byteCapacity size of memory in bytes
   */
  public MainMemory (int byteCapacity) {
    ;
  }

  /**
   * Determine whether an address is aligned to specified length.
   * @param address memory address
   * @param length byte length
   * @return true iff address is aligned to length
   */
  protected boolean isAccessAligned (int address, int length) {
    return false;
  }
  
  /**
   * Convert an sequence of four bytes into a Big Endian integer.
   * @param byteAtAddrPlus0 value of byte with lowest memory address (base address)
   * @param byteAtAddrPlus1 value of byte at base address plus 1
   * @param byteAtAddrPlus2 value of byte at base address plus 2
   * @param byteAtAddrPlus3 value of byte at base address plus 3 (highest memory address)
   * @return Big Endian integer formed by these four bytes
   */
  public int bytesToInteger (UnsignedByte byteAtAddrPlus0, UnsignedByte byteAtAddrPlus1, UnsignedByte byteAtAddrPlus2, UnsignedByte byteAtAddrPlus3) {
    return 0;
  }
  
  /**
   * Convert a Big Endian integer into an array of 4 bytes organized by memory address.
   * @param  i an Big Endian integer
   * @return an array of UnsignedByte where [0] is value of low-address byte of the number etc.
   */
  public UnsignedByte[] integerToBytes (int i) {
    return null;
  }
  
  /**
   * Fetch a sequence of bytes from memory.
   * @param address address of the first byte to fetch
   * @param length  number of bytes to fetch
   * @return an array of UnsignedByte where [0] is memory value at address, [1] is memory value at address+1 etc.
   */
  protected UnsignedByte[] get (int address, int length) throws InvalidAddressException {
    return null;
  }

  /**
   * Store a sequence of bytes into memory.
   * @param  address                  address of the first byte in memory to receive the specified value
   * @param  value                    an array of UnsignedByte values to store in memory at the specified address
   * @throws InvalidAddressException  if any address in the range address to address+value.length-1 is invalid
   */
  protected void set (int address, UnsignedByte[] value) throws InvalidAddressException {
    ;
  }
  
  /**
   * Determine the size of memory.
   * @return the number of bytes allocated to this memory.
   */
  public int length () {
    return 0;
  }
}
