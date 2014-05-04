package Arch.SM213.Machine.Student;

import Machine.AbstractMainMemory.InvalidAddressException;
import Util.UnsignedByte;

public class MainMemoryTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MainMemory mem = new MainMemory(10);
		System.out.println("The Length of memory is (expect 10): " + mem.length() );
		System.out.println();
		
		System.out.println("Is aligned? (expect true) " + mem.isAccessAligned(4, 2) );
		System.out.println("Is aligned? (expect false) " + mem.isAccessAligned(5, 3) );
		System.out.println();

		int i = 100;
		UnsignedByte[] iBytes = mem.integerToBytes(i);
		int j = mem.bytesToInteger(iBytes[0], iBytes[1], iBytes[2], iBytes[3]);
		System.out.println(i + " = " + j);
		System.out.println();

		try{
			mem.set(0, iBytes);
			mem.set(6, iBytes);
			UnsignedByte[] bytes = mem.get(0, 4);
			UnsignedByte[] bytes2 = mem.get(6, 4);
			System.out.println("expect 100: " + mem.bytesToInteger(bytes[0], bytes[1], bytes[2], bytes[3]));
			System.out.println("expect 100: " + mem.bytesToInteger(bytes2[0], bytes2[1], bytes2[2], bytes2[3]));
		}
		catch(InvalidAddressException exp){
			System.out.println("InvalidAddressException");
		}
		finally{
			System.out.println();
		}
		
		try{
			System.out.println("expect InvalidAddressExpection from mem.set");
			mem.set(8, iBytes);
		}
		catch(InvalidAddressException exp){
			System.out.println("InvalidAddressException");
		}
		finally{
			System.out.println();
		}
		
		try{
			mem.set(0, iBytes);
			System.out.println("expect InvalidAddressExpection from mem.get");
			UnsignedByte[] b = mem.get(2, 9);
		}
		catch(InvalidAddressException exp){
			System.out.println("InvalidAddressException");
		}
		finally{
			System.out.println();
		}
	}

}
