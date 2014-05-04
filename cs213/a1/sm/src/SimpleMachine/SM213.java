package SimpleMachine;

import GraphicalUI.UI;

/**
 * GUI Simulator for SM213 ISA.
 */

public class SM213 extends SM213Arch {
  UI ui;
  
  public SM213 (String[] args) {
    super ();
    ui = new UI (cpu, memory, "[showMac]");
  }
  
  public static void main (String[] args) {
    new SM213 (args);
  }
}