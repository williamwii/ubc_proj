import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.HashMap;
import java.util.WeakHashMap;

class MemoryLeak {
  
  Random         randomSeed     = new Random ();
  ThingMogrifier thingMogrifier = new ThingMogrifier ();
  ThingMogrifier1 thingMogrifier1 = new ThingMogrifier1 ();
  ThingMogrifier2 thingMogrifier2 = new ThingMogrifier2 ();
    
  /**
   * Stores a unique ID and a value.  For the test the ID is a random number
   * and the value is just 128 B of garbage.
   */
  class Thing {
    public Integer id;
    public char    value[];
    Thing () {
      id    = randomSeed.nextInt (1000000000);
      value = new char[128];
    }
  }
  
  /**
   * The ThingMogrifier models a class that whose receive method creates Things
   * when the arrive (perhaps from the network), returns them to the sender, and
   * catlogues them by their ID so that a subsequent Find operaiton can find them.
   * This catalogue is meant to be a convenience to the client of ThingMogrifier
   * but it should not prevent Things from being garbage collected when the client
   * no longer holds references to them.
   */
  class ThingMogrifier {
    private HashMap<Integer,Thing> map = new HashMap<Integer,Thing> ();
    public Thing receive () {
      Thing aThing = new Thing ();
      while ( find(aThing.id)!=null )
    	  aThing = new Thing();
      map.put (aThing.id,aThing);
      return aThing;
    }
    public Thing find (Integer anID) {
      return map.get (anID);
    }
  }
  
  
  class ThingMogrifier1{
	  private HashMap<Integer,Thing> map = new HashMap<Integer,Thing> ();
	    public Thing receive () {
	      Thing aThing = new Thing ();
	      map.put (aThing.id,aThing);
	      return aThing;
	    }
	    public Thing find (Integer anID) {
	      return map.get (anID);
	    }
	    public void flush ( Integer anID ){
	    	map.remove( anID );
	    }
  }
  
  class ThingMogrifier2{
	  private WeakHashMap<Integer,WeakReference<Thing>> map = new WeakHashMap<Integer,WeakReference<Thing>>();
	  
	  public Thing receive(){
		  Thing aThing = new Thing();
		  WeakReference<Thing> weak_thing = new WeakReference<Thing>(aThing);
		  map.put( aThing.id, weak_thing );
		  return aThing;
	  }
	  
	  public Thing find (Integer anID){
		  return map.get( anID ).get();
	  }
	  
  }
  /**
   * The inner loop of this test is mean to model the a typical behaviour of a
   * ThingMogrifier client that receives a limited number of things, operates on
   * them localally and then moves on.  The outer loop repeats this behaviour
   * multiple times to model the long-term behaviour of a program and thus
   * expose an potential memory leaks as the serious bugs they would be for
   * any such long-running program.
   */
  void test (int numTests, int numThings) {
    for (int i=0; i<numTests; i++) {
      Thing localThings[] = new Thing[numThings];
      for (int j=0; j<numThings; j++) {
        localThings[j] = thingMogrifier.receive ();
      }
      for (int j=numThings-1; j>=0; j--) {
        Thing aThing = thingMogrifier.find (localThings[j].id);
        if (aThing.id < localThings[j].id)
          throw new RuntimeException ("Things don't match");
      }
    }
  }
  
  void test1 (int numTests, int numThings) {
	    for (int i=0; i<numTests; i++) {
	      Thing localThings[] = new Thing[numThings];
	      for (int j=0; j<numThings; j++) {
	        localThings[j] = thingMogrifier1.receive ();
	      }
	      for (int j=numThings-1; j>=0; j--) {
	        Thing aThing = thingMogrifier1.find (localThings[j].id);
	        if (aThing.id < localThings[j].id)
	          throw new RuntimeException ("Things don't match");
	        thingMogrifier1.flush(localThings[j].id);
	      }
	    }
	  }
  
  void test2 (int numTests, int numThings) {
	    for (int i=0; i<numTests; i++) {
	      Thing localThings[] = new Thing[numThings];
	      for (int j=0; j<numThings; j++) {
	        localThings[j] = thingMogrifier2.receive ();
	      }
	      
	      int j=numThings-1;
	      while(j>=0){
	    	  try{
	    		  Thing aThing = thingMogrifier2.find (localThings[j].id);
	  	        if (aThing.id < localThings[j].id)
	  	          throw new RuntimeException ("Things don't match");
		    	j--;
	    	  }
	    	  catch( NullPointerException exp ){j--;}
	      }
	    }
	  }
  
  static final String usage = "java MemoryLeak test-number";
  public static void main (String args[]) {
    if (args.length != 1) {
      System.out.printf ("%s\n",usage);
      System.exit (-1);
    }
    try {
      MemoryLeak memoryLeak = new MemoryLeak ();
      switch (Integer.valueOf (args[0])) {
        case 1:
          memoryLeak.test (100,100);
          break;
        case 2:
          memoryLeak.test (1000,100);
          break;
        case 3:
          memoryLeak.test (10000,100);
          break;
        case 4:
          memoryLeak.test (100000,100);
          break;
        
        case 5:
          memoryLeak.test1(100, 100);
          break;
        case 6:
          memoryLeak.test1 (1000,100);
          break;
        case 7:
          memoryLeak.test1 (10000,100);
          break;
        case 8:
          memoryLeak.test1 (100000,100);
          break;
          
        case 9:
          memoryLeak.test2(100, 100);
          break;
        case 10:
          memoryLeak.test2 (1000,100);
          break;
        case 11:
          memoryLeak.test2 (10000,100);
          break;
        case 12:
          memoryLeak.test2 (100000,100);
          break;
          
        default:
          System.out.printf ("%s (test-number is 1 or 2)\n", usage);
          System.exit (-1);
      }
    } catch (java.lang.NumberFormatException nfe) {
      System.out.printf ("%s test-number must be a number\n",usage);
      System.exit (-1);
    }
  }
}