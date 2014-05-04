import java.lang.reflect.InvocationTargetException;

public class SimpleMachine {
  static final boolean assertionsEnabled = true;
  static final int      DEFAULT_MACHINE  = 0;  // CHANGE THIS TO SET THE MACHINE BEING SIMULATED
  static final String[] DEFINED_MACHINES = new String[] { "SM213", "Y86Seq", "Y86PipeMinus", "Y86Pipe", "Y86PipeSuper" };
  static {
    ClassLoader.getSystemClassLoader ().setPackageAssertionStatus ("Arch.SM213.ISA",                      assertionsEnabled);
    ClassLoader.getSystemClassLoader ().setPackageAssertionStatus ("Arch.SM213.Machine",                  assertionsEnabled);
    ClassLoader.getSystemClassLoader ().setPackageAssertionStatus ("Arch.SM213.Machine.Solution",         assertionsEnabled);
    ClassLoader.getSystemClassLoader ().setPackageAssertionStatus ("Arch.SM213.Machine.Student",          assertionsEnabled);
    ClassLoader.getSystemClassLoader ().setPackageAssertionStatus ("Arch.Y86.ISA",                        assertionsEnabled);
    ClassLoader.getSystemClassLoader ().setPackageAssertionStatus ("Arch.Y86.Machine",                    assertionsEnabled);
    ClassLoader.getSystemClassLoader ().setPackageAssertionStatus ("Arch.Y86.Machine.Seq.Solution",       assertionsEnabled);
    ClassLoader.getSystemClassLoader ().setPackageAssertionStatus ("Arch.Y86.Machine.Seq.Student",        assertionsEnabled);
    ClassLoader.getSystemClassLoader ().setPackageAssertionStatus ("Arch.Y86.Machine.PipeMinus.Solution", assertionsEnabled);
    ClassLoader.getSystemClassLoader ().setPackageAssertionStatus ("Arch.Y86.Machine.PipeMinus.Student",  assertionsEnabled);
    ClassLoader.getSystemClassLoader ().setPackageAssertionStatus ("GraphicalUI",                         assertionsEnabled);
    ClassLoader.getSystemClassLoader ().setPackageAssertionStatus ("ISA",                                 assertionsEnabled);
    ClassLoader.getSystemClassLoader ().setPackageAssertionStatus ("Machine",                             assertionsEnabled);
    ClassLoader.getSystemClassLoader ().setPackageAssertionStatus ("SimpleMachine",                       assertionsEnabled);
  }
  public static void main (String[] args) {
    String   machine   = args.length>0? args[0] : DEFINED_MACHINES [DEFAULT_MACHINE];
    String[] childArgs = new String[args.length>0? args.length-1: 0];
    for (int i=1; i<args.length; i++)
      childArgs[i-1] = args[i];
    boolean validMachine = false;
    for (String m : DEFINED_MACHINES)
      if (machine.equals (m)) {
	validMachine = true;
	break;
      }
    if (! validMachine) 
      System.out.printf ("Undefined machine name %s\n", machine);
    else
      try {
	Class.forName ("SimpleMachine.".concat (machine)).getMethod ("main", String[].class).invoke (null, (Object) childArgs);
      } catch (ClassNotFoundException cnfe) {
	throw new AssertionError (cnfe);
      } catch (NoSuchMethodException nsme) {
	throw new AssertionError (nsme);
      } catch (IllegalAccessException iae) {
	throw new AssertionError (iae);
      } catch (InvocationTargetException ite) {
	throw new AssertionError (ite);
      }
  }
}