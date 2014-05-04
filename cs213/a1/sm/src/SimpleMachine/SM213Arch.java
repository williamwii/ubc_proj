package SimpleMachine;

import ISA.Memory;
import Arch.SM213.Machine.Student.MainMemory;
import Arch.SM213.Machine.Student.CPU;
//import Arch.SM213.Machine.Solution.MainMemory;
//import Arch.SM213.Machine.Solution.CPU;
import Arch.SM213.ISA.ISA;

/**
 * UI-Independent implementation of SM213 ISA.
 */

public class SM213Arch {
  final static int MEMORY_MB = 1;
  MainMemory       mainMemory;
  CPU              cpu;
  ISA              isa;
  Memory           memory;
   
  public SM213Arch () {
    isa          = new ISA        ();
    mainMemory   = new MainMemory (MEMORY_MB * 1024*1024);
    cpu          = new CPU        (mainMemory);
    memory       = new Memory     (isa, mainMemory, cpu.getPC ()); 
  }
}