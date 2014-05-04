package Util;

/**
 * Unsigned Byte.
 */

public class UnsignedByte {
  private byte value;
  public UnsignedByte (Byte aByte) {
    value = aByte;
  }
  public UnsignedByte (int anInt) {
    value = (byte) anInt;
  }
  public long value () {
    return ((long) value) & 0xff;
  }
}