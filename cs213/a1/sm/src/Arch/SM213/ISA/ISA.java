package Arch.SM213.ISA;

import java.lang.String;
import ISA.AbstractISA;
import Util.DataModel;

public class ISA extends AbstractISA {
  
  InsLayout opCode, opCodeALU, opCodeShift, literal32, literal8, shiftField, shiftBits, register, scaledRegister, offset4, offset8, offset8i, baseOffset4, 
  baseOffset8, baseOffset8i, index, absolute, pcRelative, null4, null8, null12;
  
  public ISA () {
    super ("SM213", new Assembler ()); 
    
    opCode         = new OpCodeField      (4, "%x",    "%s",           "%s");
    opCodeALU      = new OpCodeField      (8, "%x",    "%s",           "%s");
    opCodeShift    = new ShiftOpCodeField (4, "%x",    "%s", "l", "r", "%s", "<<", ">>");
    literal32      = new LabelableField   (32," %08x", "$0x%x", "$%s", "0x%x", "%s");
    literal8       = new SimpleField      (8, "%02x",  "$%d",          "%d");
    shiftField     = new ShiftField       (8, "%02x",  "$%d",          "%d");
    register       = new SimpleField      (4, "%x",    "r%d",          "r[%d]");
    scaledRegister = new ScaledField      (4, "%x",    "r%d",          "r[%d]", 4);
    offset4        = new ScaledField      (4, "%x",    "0x%x",         "0x%x", 4);
    offset8        = new ScaledField      (8, "%02x",  "0x%x",         "0x%x", 4);
    offset8i       = new ScaledField      (8, "%02x",  "0x%x",         "0x%x", 2);
    baseOffset4    = new CompoundField    (new InsLayout[] {offset4, register},  new int[] {0,1}, new String[] {"%s",  "(%s)" },    new int[] {0,1}, new String[] {"m[ %s + ","%s ]"});
    baseOffset8    = new CompoundField    (new InsLayout[] {register, offset8},  new int[] {1,0}, new String[] {"%s",  "(%s)" },    new int[] {0,1}, new String[] {"m[ %s + ","%s ]"});
    baseOffset8i   = new CompoundField    (new InsLayout[] {register, offset8i}, new int[] {1,0}, new String[] {"%s",  "(%s)" },    new int[] {0,1}, new String[] {"m[ %s + ","%s ]"});
    index          = new CompoundField    (new InsLayout[] {register, register}, new int[] {0,1}, new String[] {"(%s", ", %s, 4)"}, new int[] {0,1}, new String[] {"m[ %s + ","%s ]"});
    absolute       = new LabelableField   (32, " %08x", "0x%x", "%s", "0x%x", "%s");
    pcRelative     = new PCRelativeField  (8, "%02x", "0x%x", "%s",   "0x%x", "%s"); 
    null4          = new SimpleField      (4, "-",  "", "");
    null8          = new SimpleField      (8, "--", "", "");
    null12         = new SimpleField      (12,"---","", "");
    
    define (0x0,  new CompoundField (new InsLayout[] {opCode, register, null8, literal32}, new int[] {0,3,1}, new String[] {"ld   ", "%s", ", %s"}, new int[] {1,3},     new String[] {"%s <= ",    "%s"}));
    define (0x1,  new CompoundField (new InsLayout[] {opCode, baseOffset4, register},      new int[] {0,1,2}, new String[] {"ld   ","%s",", %s"},   new int[] {2,1},     new String[] {"%s <= ",    "%s"}));
    define (0x2,  new CompoundField (new InsLayout[] {opCode, index, register},            new int[] {0,1,2}, new String[] {"ld   ","%s",", %s"},   new int[] {2,1},     new String[] {"%s <= ",    "%s"}));
    define (0x3,  new CompoundField (new InsLayout[] {opCode, register, baseOffset4},      new int[] {0,1,2}, new String[] {"st   ","%s",", %s"},   new int[] {2,1},     new String[] {"%s <= ",    "%s"}));
    define (0x4,  new CompoundField (new InsLayout[] {opCode, register, index},            new int[] {0,1,2}, new String[] {"st   ","%s",", %s"},   new int[] {2,1},     new String[] {"%s <= ",    "%s"}));
    define (0x60, new CompoundField (new InsLayout[] {opCodeALU, register, register},      new int[] {0,1,2}, new String[] {"mov  ","%s",", %s"},   new int[] {2,1},     new String[] {"%s <= ",    "%s"}));
    define (0x61, new CompoundField (new InsLayout[] {opCodeALU, register, register},      new int[] {0,1,2}, new String[] {"add  ","%s",", %s"},   new int[] {2,2,1},   new String[] {"%s <= ",    "%s + ", "%s"}));
    define (0x62, new CompoundField (new InsLayout[] {opCodeALU, register, register},      new int[] {0,1,2}, new String[] {"and  ","%s",", %s"},   new int[] {2,2,1},   new String[] {"%s <= ",    "%s & ", "%s"}));
    define (0x63, new CompoundField (new InsLayout[] {opCodeALU, null4, register},         new int[] {0,2},   new String[] {"inc  ","%s"},          new int[] {2,2},     new String[] {"%s <= ",    "%s + 1"}));
    define (0x64, new CompoundField (new InsLayout[] {opCodeALU, null4, register},         new int[] {0,2},   new String[] {"inca ","%s"},          new int[] {2,2},     new String[] {"%s <= ",    "%s + 4"}));
    define (0x65, new CompoundField (new InsLayout[] {opCodeALU, null4, register},         new int[] {0,2},   new String[] {"dec  ","%s"},          new int[] {2,2},     new String[] {"%s <= ",    "%s - 1"}));
    define (0x66, new CompoundField (new InsLayout[] {opCodeALU, null4, register},         new int[] {0,2},   new String[] {"deca ","%s"},          new int[] {2,2},     new String[] {"%s <= ",    "%s - 4"}));
    define (0x67, new CompoundField (new InsLayout[] {opCodeALU, null4, register},         new int[] {0,2},   new String[] {"not  ","%s"},          new int[] {2,2},     new String[] {"%s <= ",    "~ %s"}));
    define (0x6f, new CompoundField (new InsLayout[] {opCodeALU, null4, register},         new int[] {0,2},   new String[] {"gpc  ","%s"},          new int[] {2},       new String[] {"%s <= pc"}));
    define (0x7,  new ShiftInsField (new InsLayout[] {opCodeShift, register, shiftField},  new int[] {0,2,1}, new String[] {"sh%s  ","%s",", %s"},  new int[] {1,1,0,2}, new String[] {"%s <= ",    "%s", " %s ", "%s"}, 0, 2));
    define (0x8,  new CompoundField (new InsLayout[] {opCode, null4, pcRelative},          new int[] {0,2},   new String[] {"br   ","%s"},          new int[] {2},       new String[] {"goto %s"}));
    define (0x9,  new CompoundField (new InsLayout[] {opCode, register, pcRelative},       new int[] {0,1,2}, new String[] {"beq  ","%s",", %s"},   new int[] {2,1},     new String[] {"goto %s",  " if %s >= 0"}));
    define (0xa,  new CompoundField (new InsLayout[] {opCode, register, pcRelative},       new int[] {0,1,2}, new String[] {"bgt  ","%s",", %s"},   new int[] {2,1},     new String[] {"goto %s",  " if %s > 0"}));
    define (0xb,  new CompoundField (new InsLayout[] {opCode, null12, absolute},           new int[] {0,2},   new String[] {"j    ","%s"},          new int[] {2},       new String[] {"goto %s"}));
    define (0xc,  new CompoundField (new InsLayout[] {opCode, baseOffset8i},               new int[] {0,1},   new String[] {"j    ","%s"},          new int[] {1},       new String[] {"goto %s"}));
    define (0xd,  new CompoundField (new InsLayout[] {opCode, baseOffset8},                new int[] {0,1},   new String[] {"j    ","*%s"},         new int[] {1},       new String[] {"goto m[ %s ]"}));
    define (0xe,  new CompoundField (new InsLayout[] {opCode, index, null4},               new int[] {0,1},   new String[] {"j    ","*%s"},         new int[] {1},       new String[] {"goto m[ %s ]"}));
    define (0xf0, new CompoundField (new InsLayout[] {opCodeALU, null8},                   new int[] {0},     new String[] {"halt "},               new int[] {0},       new String[] {"halt"}));
    define (0xff, new CompoundField (new InsLayout[] {opCodeALU, null8},                   new int[] {0},     new String[] {"nop  "},               new int[] {0},       new String[] {"nop"}));
    
    setPlaceholderInstruction (0xff);
  }
}