package GraphicalUI;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.Observer;
import java.util.Observable;
import java.util.HashSet;
import java.util.Hashtable;
import java.io.FileNotFoundException;
import java.awt.FileDialog;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.GridLayout; 
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.BorderUIResource;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import SimpleMachine.AbstractUI;
import Machine.AbstractCPU;
import Machine.RegisterSet;
import Machine.Register;
import ISA.Memory;
import ISA.Region;
import ISA.InstructionRegion;
import ISA.InstructionModel;
import ISA.DataRegion;
import Util.CompoundModel;
import Util.PickModel;
import Util.TableCellIndex;
import Util.DataModel;
import Util.AbstractDataModel;
import Util.DataModelEvent;
import Util.HalfByteNumber;
import Util.SixByteNumber;
import Util.MapModel;
import Util.AbstractDataModel;

public class UI extends AbstractUI {
  private Application                 application;
  private Color                       readHl              = new Color (120,120,240);
  private Color                       writeHl             = new Color (240,120,120);
  private Color                       cursorHl            = new Color (120,240,120);
  private ViewFormat.HighlightControl memoryHighlight     = new ViewFormat.HighlightControl (new ViewFormat.BorderHighlight (readHl), new ViewFormat.BorderHighlight (writeHl), null, null);
  private ViewFormat.HighlightControl codeHighlight       = new ViewFormat.HighlightControl (new ViewFormat.BorderHighlight (readHl), new ViewFormat.BorderHighlight (writeHl), new ViewFormat.BorderHighlight (cursorHl), null);
  private String                      monoSpaceFont       = pickMonoSpaceFont ();
  private Font                        addressFont         = new Font  (monoSpaceFont,  Font.PLAIN, 10);
  private Font                        cellFont            = new Font  (monoSpaceFont,  Font.PLAIN, 10);
  private Font                        memAddressFont      = new Font  (monoSpaceFont,  Font.PLAIN, 9);
  private Font                        memCellFont         = new Font  (monoSpaceFont,  Font.PLAIN, 9);
  private Font                        macFont             = new Font  (monoSpaceFont,  Font.PLAIN, 8);
  private Font                        labelFont           = new Font  (monoSpaceFont,  Font.PLAIN, 10);
  private Font                        titleFont           = new Font  ("Default", Font.BOLD, 14);
  private Font                        subTitleFont        = new Font  ("Default", Font.BOLD, 12);
  private Font                        aboutFont           = new Font  ("Default", Font.PLAIN, 10);
  private Font                        nameFont            = new Font  ("Default", Font.PLAIN, 10);
  private Font                        commentFont         = new Font  ("Default", Font.PLAIN, 10);
  private Font                        statusMessageFont   = new Font  ("Default", Font.PLAIN, 11);
  private Font                        curInsFont          = new Font  ("Default", Font.PLAIN, 20);
  private Font                        curInsDscFont       = new Font  ("Default", Font.PLAIN, 11);
  private Font                        curInsTwoColFont    = new Font  ("Default", Font.PLAIN, 13);
  private Font                        curInsDscTwoColFont = new Font  ("Default", Font.PLAIN, 9);
  private Color                       strutColor          = new Color (200,200,200);
  private Color                       toolBarColor        = new Color (180,180,180);
  private Color                       toolBarBorder       = new Color (64,64,64);
  private Color                       statusBarBorder     = new Color (128,128,128);
  private Color                       macColor            = new Color (100,100,100);
  private Color                       labelColor          = new Color (130,0,130);
  private Color                       codeColor           = new Color (0,0,180);
  private Color                       commentColor        = new Color (0,100,0);
  private Color                       addressColor        = new Color (50,50,50);
  private Color                       nameColor           = new Color (50,50,50);
  private Color                       breakpointColor     = new Color (240,0,0);
  public static boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));
  private int                         platformAgnosticMetaMask = MAC_OS_X? ActionEvent.META_MASK : ActionEvent.CTRL_MASK;
  
  // Global Look and Feel properties
  final static Font                   TITLE_FONT         = new Font ("Default", Font.BOLD, 12);
  final static Color                  BACKGROUND_COLOR   = new Color (250,250,250);
  final static Color                  CELL_BORDER_COLOR  = new Color (200,200,200);
  final static Color                  PANE_BORDER_COLOR  = new Color (160,160,160);
  final static Color                  SELECTION_COLOR    = new Color (255,210,0);
  final static Color                  ERROR_BORDER_COLOR = new Color (255,0,0);
  
  static {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
  }
  
  private static String pickMonoSpaceFont () {
    String[] fontsToTry = new String[] { "Monaco", "Consolas", "Courier New" };
    for (String fontFamily : fontsToTry)
      if (new Font (fontFamily, Font.PLAIN, 10).getFamily ().equals (fontFamily))
	return fontFamily;
    return "";
  }
  
  public UI (AbstractCPU aCPU, Memory aMemory, String options) {
    super (aCPU, aMemory, options);
    application = new Application ();
  }
  
  interface StateChangedListener {
    void stateChanged (Object o);
  }
  
  interface IsRunningListener {
    void isRunningChanged ();
  }
  
  /**
   * Application conists of this single frame plus addition memory frames 
   * created and displosed of on demand.
   */
  
  class Application extends JFrame {
    AboutBox                   aboutBox;
    MainPane                   mainPane;
    StatusBar                  statusBar;
    Vector <IsRunningListener> isRunningListeners      = new Vector <IsRunningListener> ();
    MasterSelectionListner     masterSelectionListener = new MasterSelectionListner ();
    ApplicationUndoManager     undoManager             = new ApplicationUndoManager ();
    boolean                    isRunning               = false;
    int                        screenWidth             = Toolkit.getDefaultToolkit ().getScreenSize ().width;
    
    Application () {
      super (applicationFullName);
      memory.addUndoableEditListener (undoManager);
      setDefaultCloseOperation (WindowConstants.DO_NOTHING_ON_CLOSE);
      addWindowListener (
	new WindowAdapter () {
	  public void windowClosing (WindowEvent e) {
	    if (quit ())
	      System.exit (0);
	  }
	});
      addComponentListener (
	new java.awt.event.ComponentAdapter () {
	  public void componentResized (java.awt.event.ComponentEvent ce) {
	    mainPane.resizeToFitWindow ();
	  }
	});
      setJMenuBar (new MenuBar ());
      Container cp = getContentPane ();
      cp.setLayout (new BorderLayout ());
      statusBar = new StatusBar ();
      mainPane  = new MainPane ();
      cp.add (new ToolBar (), BorderLayout.NORTH);
      cp.add (mainPane,       BorderLayout.CENTER);
      cp.add (statusBar,      BorderLayout.SOUTH);
      aboutBox = new AboutBox ();
      doPlatformSpecificInitialization ();
      pack       ();
      setVisible (true);
    }
    
    void adjustHighlights (boolean clear) {
      mainPane.adjustHighlights (clear);
    }
    
    /**
     * Resize window to fit existing components.  Does not change the size of components.
     */
    void setWindowToPreferredWidth () {
      Dimension sz = getSize ();
      sz.width = Math.min (screenWidth, getPreferredSize ().width);
      setSize (sz);
    }
    
    void setRunning (boolean anIsRunning) {
      isRunning = anIsRunning;
      fireIsRunningChanged ();
    }
    
    void doPlatformSpecificInitialization () {
      if (MAC_OS_X) 
	try {
	  OSXAdapter.setAboutHandler (this, getClass ().getDeclaredMethod ("showAbout", (Class[]) null));
	  OSXAdapter.setQuitHandler  (this, getClass ().getDeclaredMethod ("quit",      (Class[]) null));
	} catch (Exception e) {
	  throw new AssertionError ();
	}
    }
    
    boolean quit () {
      if (memory.hasUnsavedChanges ()) {
	int option = JOptionPane.
	  showOptionDialog (this, 
			    "You have unsaved changes.  Do you really want to quit and lose them?",
			    "Quit with UnsavedChanges", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
			    new String[] { "Discard Changes", "Cancel" }, "Cancel");
	return (option == 0);
      } else
	return true;
    }
    
    void showAbout () {
      aboutBox.setLocation ((int) getLocation ().getX () + 22, (int) getLocation ().getY () + 22);
      aboutBox.setVisible (true);
    }
    
    /**
     * Application's "about" box.
     */
    class AboutBox extends JDialog {
      AboutBox () {
	super (Application.this, "About ".concat (applicationName));
	Container cp = getContentPane ();
	cp.setLayout (new BorderLayout (15,15));
	JLabel[] aboutLabel = new JLabel [8+applicationCopyright.length];
	aboutLabel [0] = new JLabel ("");
	aboutLabel [0].setFont(new Font ("Default", Font.PLAIN, 6));
	aboutLabel [1] = new JLabel (applicationName);
	aboutLabel [1].setFont (titleFont);
	aboutLabel [2] = new JLabel ("");
	aboutLabel [2].setFont(new Font ("Default", Font.PLAIN, 6));
	aboutLabel [3] = new JLabel (applicationVersion);
	aboutLabel [3].setFont (aboutFont);
	aboutLabel [4] = new JLabel ("");
	aboutLabel [5] = new JLabel ("");
	aboutLabel [6] = new JLabel ("JDK " + System.getProperty("java.version"));
	aboutLabel [6].setFont (aboutFont);
	aboutLabel [7] = new JLabel();
	aboutLabel [7].setFont (aboutFont);
	JPanel tp = new JPanel (new GridLayout (aboutLabel.length, 1));
	for (int i=0; i<applicationCopyright.length; i++) {
	  aboutLabel [8+i] = new JLabel(applicationCopyright [i]);
	  aboutLabel [8+i].setFont (aboutFont);
	}
	for (int i=0; i<aboutLabel.length; i++) {
	  aboutLabel [i].setHorizontalAlignment (JLabel.CENTER);
	  tp.add (aboutLabel [i]);
	}
	add (tp, BorderLayout.CENTER);
	pack ();
	setSize (250,145+15*applicationCopyright.length);
	setResizable (false);
      }
    }
    
    /**
     * Application's MenuBar.
     */
    class MenuBar extends JMenuBar {
      MenuBar () {
	JMenu menu = new JMenu ("File");
	add (menu);
	menu.add (new JMenuItem (new OpenAction ()));
	menu.addSeparator ();
	menu.add (new JMenuItem (new SaveAction ()));
	menu.add (new JMenuItem (new SaveAsAction ()));
	menu.addSeparator ();
	menu.add (new JMenuItem (new RestoreDataFromCheckpointAction ()));
	menu.add (new JMenuItem (new CheckpointDataAction ()));
	menu = new JMenu ("Edit");
	add (menu);
	menu.add (new JMenuItem (new UndoAction ()));
	menu.add (new JMenuItem (new RedoAction ()));
	menu.addSeparator ();
	menu.add (new JMenuItem (new InsertAboveAction ()));
	menu.add (new JMenuItem (new InsertBelowAction ()));
	menu.add (new JMenuItem (new DeleteAction ()));
	menu = new JMenu ("View");
	add (menu);
	menu.add (new JMenuItem (new IncreaseFontSizeAction ()));
	JMenuItem x = new JMenuItem (new IncreaseFontSizeAction0 ());
	JMenuItem y = new JMenuItem (new IncreaseFontSizeAction1 ());
	x.setVisible (true);
	y.setVisible (true);
	menu.add (x);
	menu.add (y);
	menu.add (new JMenuItem (new DecreaseFontSizeAction ()));
	menu = new JMenu ("Run");
	add (menu);
	menu.add (new JMenuItem (new RunAction ()));
	menu.add (new JMenuItem (new RunSlowlyAction ()));
	menu.add (new JMenuItem (new StepAction ()));
	menu.addSeparator ();
	menu.add (new JMenuItem (new HaltAction ()));
	menu.add (new JMenuItem (new RunFasterAction ()));
	menu.add (new JMenuItem (new RunSlowerAction ()));
	menu.addSeparator ();
	menu.add (new JMenuItem (new ClearHighlightsAction ()));
	menu = new JMenu ("Debug");
	menu.add (new JMenuItem (new ClearAllBreakpointsAction ()));
	add (menu);
      }
    }
    
    /**
     * Application's ToolBar.
     */
    class ToolBar extends JToolBar {
      ToolBar () {
	setBackground (toolBarColor);
	setBorder (new CompoundBorder (new MatteBorder (0,0,1,0, toolBarBorder), getBorder ()));
	add (new OpenAction ());
	add (new SaveAction ());
	add (new SaveAsAction ());
	add (new RestoreDataFromCheckpointAction ());
	add (new CheckpointDataAction ());
	// XXX Removed because clicking on toobar icon changes selection and so insert/delete won't find anything selected
//	addSeparator ();
//	add (new InsertAboveAction ());
//	add (new InsertBelowAction ());
//	add (new DeleteAction ());
	addSeparator ();
	add (new RunAction ());
	add (new RunSlowlyAction ());
	add (new HaltAction ());
	add (new RunSlowerAction ());
	add (new RunFasterAction ());
	add (new StepAction ());
      }
    }
    
    /**
     * Application's StatusBar.
     */
    class StatusBar extends JPanel implements MessageBoard {
      JLabel label;
      StatusBar () {
	super (new GridLayout (1,1));
	setBorder (new CompoundBorder (new MatteBorder (1,0,0,0, statusBarBorder), new EmptyBorder (2,4,2,4)));
	setBackground (BACKGROUND_COLOR);
	label = new JLabel ();
	label.setFont (statusMessageFont);
	label.setHorizontalAlignment (SwingConstants.LEFT);
	label.setText (" ");
	add (label);
      }
      public void showMessage (String message) {
	label.setText (message!=null && !message.trim().equals("")? message : " ");
      }
    }
    
    /////////////////////////
    // ACTIONS
  
    /**
     * Open file dialog action.
     */
    class OpenAction extends AbstractAction implements IsRunningListener {
      public OpenAction () {
	putValue (NAME,              "Open...");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_O);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_O, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Open an assembly- or machine-lanuage file.");
	isRunningListeners.add (this);
      }
      public void actionPerformed (ActionEvent e) {
	if (quit ()) {
	  FileDialog fd = new FileDialog (Application.this, "Open", FileDialog.LOAD);
	  fd.setFilenameFilter (
				new java.io.FilenameFilter() {
				public boolean accept (java.io.File dir, String name) {
				return name.matches (".*\\.(s|machine)");
				}
				});
	  fd.setVisible (true);
	  if (fd.getFile () != null) {
	    String pathname = fd.getDirectory ().concat (fd.getFile ());
	    try {
	      memory.loadFile (pathname);
	      updateMemoryView ();
	      showMessage ("File loaded into memory.");
	    } catch (Memory.FileTypeException fte) {
	      showMessage ("Unable to load file; invalid file type.");
	    } catch (Memory.InputFileSyntaxException ifse) {
	      showMessage (ifse.toString ());
	    } catch (Exception ex) {
	      throw new AssertionError (ex);
	    }
	  }
	}
      }
      public void isRunningChanged () {
	setEnabled (! isRunning);
      }
    }
    
    /**
     * Save file action.
     */
    class SaveAction extends AbstractAction implements Memory.StateChangedListener, IsRunningListener {
      public SaveAction () {
	putValue (NAME,              "Save");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_S);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_S, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Save assembly code to input file.");
	setEnabled (false);
	memory.addStatedChangeListener (this);
	isRunningListeners.add (this);
      }
      public void actionPerformed (ActionEvent e) {
	try {
	  memory.saveToFile (null);
	} catch (FileNotFoundException fnfe) {
	  throw new AssertionError ();
	}
	showMessage ("Saved to assembly file.");
      }
      public void isRunningChanged () {
	setEnabled (! isRunning  && memory.hasLoadedFile () && memory.hasUnsavedChanges ());
      }
      public void fireMemoryStateChanged () {
	setEnabled (! isRunning && memory.hasLoadedFile () && memory.hasUnsavedChanges ());
      }
    }
    
    /**
     * SaveAs file dialog action.
     */
    class SaveAsAction extends AbstractAction implements IsRunningListener {
      public SaveAsAction () {
	putValue (NAME,              "Save As...");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_A);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_S, platformAgnosticMetaMask| ActionEvent.SHIFT_MASK));
	putValue (SHORT_DESCRIPTION, "Save assembly code to specified file.");
	isRunningListeners.add (this);
      }
      public void actionPerformed (ActionEvent e) {
	FileDialog fd = new FileDialog (Application.this, "Save As", FileDialog.SAVE);
	fd.setFilenameFilter (
	  new java.io.FilenameFilter() {
	    public boolean accept (java.io.File dir, String name) {
	      return name.matches (".*\\.(s|machine)");
	    }
	  });
	fd.setVisible (true);
	if (fd.getFile () != null) {
	  try {
	    memory.saveToFile (fd.getDirectory ().concat (fd.getFile ()));
	  } catch (FileNotFoundException fnfe) {
	    showMessage ("File not found.");
	  }
	  showMessage ("Saved to assembly file.");
	} 
      }
      public void isRunningChanged () {
	setEnabled (! isRunning);
      }
    }
    
    /**
     * Checkpoint Data
     */
    class CheckpointDataAction extends AbstractAction implements Memory.StateChangedListener, IsRunningListener {
      public CheckpointDataAction () {
	putValue (NAME,              "Checkpoint Data");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_C);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_C, platformAgnosticMetaMask | ActionEvent.SHIFT_MASK));
	putValue (SHORT_DESCRIPTION, "Checkpoint data regions.");
	isRunningListeners.add (this);
	setEnabled (false);
	memory.addStatedChangeListener (this);
	isRunningListeners.add (this);
      }
      public void actionPerformed (ActionEvent e) {
	memory.checkpointData (true);
	showMessage ("Checkpointed.");
      }
      public void isRunningChanged () {
	setEnabled (! isRunning  && memory.hasLoadedFile ());
      }
      public void fireMemoryStateChanged () {
	setEnabled (! isRunning && memory.hasLoadedFile ());
      }
    }
    
    /**
     * Restore Data from Checkpoint
     */
    class RestoreDataFromCheckpointAction extends AbstractAction implements Memory.StateChangedListener, IsRunningListener {
      public RestoreDataFromCheckpointAction () {
	putValue (NAME,              "Reset Data");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_C);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_C, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Restore all data regions from their most recent checkpoint.");
	isRunningListeners.add (this);
	setEnabled (false);
	memory.addStatedChangeListener (this);
	isRunningListeners.add (this);
      }
      public void actionPerformed (ActionEvent e) {
	if (stopEditing ()) {
	  memory.restoreDataFromCheckpoint ();
	  showMessage ("Data restored from most recent checkpoint.");
	}
      }
      public void isRunningChanged () {
	setEnabled (! isRunning  && memory.hasLoadedFile ());
      }
      public void fireMemoryStateChanged () {
	setEnabled (! isRunning && memory.hasLoadedFile ());
      }
    }
    
    /**
     * Undo action.
     */
    class UndoAction extends AbstractAction implements StateChangedListener {
      public UndoAction () {
	putValue (NAME,              "Undo");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_U);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_Z, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Undo last change.");
	setEnabled (undoManager.canUndo ());
	undoManager.addStateChangedListener (this);
      }
      public void actionPerformed (ActionEvent e) {
	  undoManager.undo ();
      }
      public void stateChanged (Object o) {
	assert o == undoManager;
	if (undoManager.canUndo ()) {
	  putValue (NAME, undoManager.getUndoPresentationName ());
	  setEnabled (true);
	} else {
	  putValue (NAME, "Undo");
	  setEnabled (false);
	}
	setEnabled (undoManager.canUndo ());
      }
    }
    
    /**
     * Redo action.
     */
    class RedoAction extends AbstractAction implements StateChangedListener {
      public RedoAction () {
	putValue (NAME,              "Redo");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_U);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_Z, platformAgnosticMetaMask | ActionEvent.SHIFT_MASK));
	putValue (SHORT_DESCRIPTION, "Redo change.");
	setEnabled (undoManager.canRedo ());
	undoManager.addStateChangedListener (this);
      }
      public void actionPerformed (ActionEvent e) {
	undoManager.redo ();
      }
      public void stateChanged (Object o) {
	assert o == undoManager;
	if (undoManager.canRedo ()) {
	  putValue (NAME, undoManager.getRedoPresentationName ());
	  setEnabled (true);
	} else {
	  putValue (NAME, "Redo");
	  setEnabled (false);
	}
      }
    }
    
    /**
     * Increase font size action.
     */
    class IncreaseFontSizeAction extends AbstractAction implements IsRunningListener {
      public IncreaseFontSizeAction () {
	putValue (NAME, "Make Text Bigger");
	putValue (ACCELERATOR_KEY, KeyStroke.getKeyStroke (KeyEvent.VK_PLUS, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Increase font size of all text.");
	isRunningListeners.add (this);
	setEnabled (true);
      }
      public void actionPerformed (ActionEvent e) {
	mainPane.adjustFontSize (1);
      }
      public void isRunningChanged () {
	setEnabled (! isRunning);
      }
    }
    
    /**
     * Increase font size action.
     */
    class IncreaseFontSizeAction0 extends IncreaseFontSizeAction {
      public IncreaseFontSizeAction0 () {
	super ();
	putValue (NAME, "Make Text Bigger 0");
	putValue (ACCELERATOR_KEY, KeyStroke.getKeyStroke (KeyEvent.VK_EQUALS, platformAgnosticMetaMask));
      }
    }
    
    /**
     * Increase font size action.
     */
    class IncreaseFontSizeAction1 extends IncreaseFontSizeAction {
      public IncreaseFontSizeAction1 () {
	super ();
	putValue (NAME, "Make Text Bigger 1");
	putValue (ACCELERATOR_KEY, KeyStroke.getKeyStroke (KeyEvent.VK_EQUALS, platformAgnosticMetaMask | ActionEvent.SHIFT_MASK));
      }
    }
    
    /**
     * Decrease font size action.
     */
    class DecreaseFontSizeAction extends AbstractAction implements IsRunningListener {
      public DecreaseFontSizeAction () {
	putValue (NAME, "Make Text Smaller");
	putValue (ACCELERATOR_KEY, KeyStroke.getKeyStroke (KeyEvent.VK_MINUS, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Decrease font size of all text.");
	isRunningListeners.add (this);
	setEnabled (true);
      }
      public void actionPerformed (ActionEvent e) {
	mainPane.adjustFontSize (-1);
      }
      public void isRunningChanged () {
	setEnabled (! isRunning);
      }
    }
    
    /**
     * Run action.
     */
    class RunAction extends AbstractAction implements IsRunningListener {
      public RunAction () {
	putValue (NAME, "Run");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_R);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_R, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Start the CPU running from current PC.");
	isRunningListeners.add (this);
      }
      public void actionPerformed (ActionEvent e) {
	new Thread () {
	  @Override
	  public void run () {
	    showMessage ("Running ...");
	    setPauseMilliseconds (1);
	    setRunning (true);
	    UI.super.run (new RunCallback ());
	    setRunning (false);
	  }
	}.start ();
      }
      class RunCallback implements Callback {
	public void fire () {
	  adjustHighlights (false);
	}
      }
      public void isRunningChanged () {
	setEnabled (! isRunning);
      }
    }
    
    /**
     * Run slowly action.
     */
    class RunSlowlyAction extends AbstractAction implements IsRunningListener {
      int pauseMilliseconds = 1500;
      public RunSlowlyAction () {
	putValue (NAME, "Run Slowly");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_U);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_R, platformAgnosticMetaMask | ActionEvent.SHIFT_MASK));
	putValue (SHORT_DESCRIPTION, "Animate running the CPU from current PC.");
	isRunningListeners.add (this);
      }
      public void actionPerformed (ActionEvent e) {
	new Thread () {
	  @Override
	  public void run () {
	    setPauseMilliseconds (pauseMilliseconds);
	    showMessage (String.format ("Running with %3.1f s pause ...", getPauseMilliseconds () / 1000.0));
	    setRunning (true);
	    UI.super.run (new RunSlowlyCallback ());
	    setRunning (false);
	    pauseMilliseconds = getPauseMilliseconds ();
	  }
	}.start ();
      }
      class RunSlowlyCallback implements Callback {
	public void fire () {
	  adjustHighlights (false);
	}
      }
      public void isRunningChanged () {
	setEnabled (! isRunning);
      }
    }
    
    /**
     * Run faster.
     */
    class RunFasterAction extends AbstractAction implements IsRunningListener {
      public RunFasterAction () {
	putValue (NAME, "Faster");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_F);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_OPEN_BRACKET, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Run faster.");   
	setEnabled (false);
	isRunningListeners.add (this);
      }
      public void actionPerformed (ActionEvent e) {
	setPauseMilliseconds (Math.max (1, getPauseMilliseconds () - 500));
	fireIsRunningChanged ();
	showMessage (String.format ("Running with %3.1f s pause ...", getPauseMilliseconds () / 1000.0));
      }
      public void isRunningChanged () {
	setEnabled (isRunning && getPauseMilliseconds () > 1);
      }
    }
    
    /**
     * Run slower.
     */
    class RunSlowerAction extends AbstractAction implements IsRunningListener {
      public RunSlowerAction () {
	putValue (NAME, "Slower");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_S);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_CLOSE_BRACKET, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Run slower.");    
	setEnabled (false);
	isRunningListeners.add (this);
      }
      public void actionPerformed (ActionEvent e) {
	setPauseMilliseconds (Math.max (1, getPauseMilliseconds () + 500));
	fireIsRunningChanged ();
	showMessage (String.format ("Running with %3.1fs pause ...", getPauseMilliseconds () / 1000.0));
      }
      public void isRunningChanged () {
	setEnabled (isRunning && getPauseMilliseconds () < 5000);
      }
    }
    
    /**
     * Halt action.
     */
    class HaltAction extends AbstractAction implements IsRunningListener {
      public HaltAction () {
	putValue (NAME, "Halt");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_H);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_PERIOD, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Halt CPU execution.");   
	setEnabled (false);
	isRunningListeners.add (this);
      }
      public void actionPerformed (ActionEvent e) {
	halt ();
      }
      public void isRunningChanged () {
	setEnabled (isRunning);
      }
    }
    
    /**
     * Step action.
     */
    
    class StepAction extends AbstractAction implements IsRunningListener {
      public StepAction () {
	putValue (NAME, "Step");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_S);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_S, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Execute one instruction and stop.");
	isRunningListeners.add (this);
      }
      public void actionPerformed (ActionEvent e) {
	showMessage ("");
	adjustHighlights (false);
	setRunning (true);
	step ();
	setRunning (false);
      }
      public void isRunningChanged () {
	setEnabled (! isRunning);
      }
    }
    
    /** 
     * Goto PC action.
     */
    class GotoAction extends AbstractAction implements IsRunningListener {
      public GotoAction () {
	putValue (NAME, "Goto");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_G);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_G, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Set PC to specified address."); 
	isRunningListeners.add (this);
      }
      public void actionPerformed (ActionEvent e) {
	;
      }
      public void isRunningChanged () {
	setEnabled (! isRunning);
      }
    }
    
    /**
     * Set breakpoint action.
     */
    class SetBreakpointAction extends AbstractAction {
      public SetBreakpointAction () {
	putValue (NAME, "Set Breakpoint");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_B);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_B, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Set breakpoint."); 
      }
      public void actionPerformed (ActionEvent e) {
	;
      }
    }
    
    /**
     * Clear breakpoint action.
     */
    class ClearBreakpointAction extends AbstractAction {
      public ClearBreakpointAction () {
	putValue (NAME, "Clear Breakpoint");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_C);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_C, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Clear breakpoint."); 
      }
      public void actionPerformed (ActionEvent e) {
	;
      }
    }
    
    /**
     * Clear all breakpoints action.
     */
    class ClearAllBreakpointsAction extends AbstractAction {
      public ClearAllBreakpointsAction () {
	putValue (NAME, "Clear Breaks");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_L);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_C, platformAgnosticMetaMask | ActionEvent.SHIFT_MASK));
	putValue (SHORT_DESCRIPTION, "Clear all breakpoints."); 
      }
      public void actionPerformed (ActionEvent e) {
	clearAllDebugPoints (DebugType.BREAK);
	showMessage ("All breakpoints cleared.");
      }
    }
    
    /**
     * Clear highlights
     */
    class ClearHighlightsAction extends AbstractAction {
      public ClearHighlightsAction () {
	putValue (NAME, "Erase Highlights");
	putValue (MNEMONIC_KEY,      KeyEvent.VK_H);
	putValue (ACCELERATOR_KEY,   KeyStroke.getKeyStroke (KeyEvent.VK_H, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Erase highlights."); 
      }
      public void actionPerformed (ActionEvent e) {
	adjustHighlights (true);
      }
    }
    
    /**
     * Insert Row in Instruction or Data view at currently selected row
     */
    class InsertAboveAction extends AbstractAction implements IsRunningListener, View.SelectionListener {
      public InsertAboveAction () {
	putValue (NAME, "Insert Above");
	putValue (MNEMONIC_KEY, KeyEvent.VK_B);
	putValue (ACCELERATOR_KEY, KeyStroke.getKeyStroke (KeyEvent.VK_ENTER, platformAgnosticMetaMask | ActionEvent.ALT_MASK));
	putValue (SHORT_DESCRIPTION, "Insert row above selected row.");
	setEnabled (false);
	masterSelectionListener.add (this);
	isRunningListeners.add (this);
      }
      public void actionPerformed (ActionEvent e) {
	ViewPane pane = getSelectedPane ();
	if (pane != null) {
	  if (stopEditing ()) {
	    boolean inserted = pane.view.insertAboveSelection ();
	    if (inserted) {
	      showMessage ("Row inserted.");
	    } else 
	      showMessage ("Unable to insert new row.");
	  } 
	} else
	  showMessage ("No row selected for Insert.");	  	
      }
      private void setEnabled () {
	setEnabled (! isRunning && getSelectedPane () != null);
      }
      public void isRunningChanged () {
	setEnabled ();
      }
      public void selectionMayHaveChanged (boolean isKnownToBeSelected) {
	if (isKnownToBeSelected)
	  setEnabled (! isRunning);
	else
	  setEnabled ();
      }
    }
    
    /**
     * Insert Row in Instruction or Data view below currently selected row
     */
    class InsertBelowAction extends AbstractAction implements IsRunningListener, View.SelectionListener {
      public InsertBelowAction () {
	putValue (NAME, "Insert Below");
	putValue (MNEMONIC_KEY, KeyEvent.VK_B);
	putValue (ACCELERATOR_KEY, KeyStroke.getKeyStroke (KeyEvent.VK_ENTER, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Insert row below selected row.");
	setEnabled (false);
	masterSelectionListener.add (this);
	isRunningListeners.add (this);
      }
      public void actionPerformed (ActionEvent e) {
	ViewPane pane = getSelectedPane ();
	if (pane != null) {
	  if (stopEditing ()) {
	    boolean inserted = pane.view.insertBelowSelection ();
	    if (inserted) {
	      showMessage ("Row inserted.");
	    } else 
	      showMessage ("Unable to insert new row.");
	  }
	} else
	  showMessage ("No row selected for Insert Before.");
      }
      private void setEnabled () {
	setEnabled (! isRunning && getSelectedPane () != null);
      }
      public void isRunningChanged () {
	setEnabled ();
      }
      public void selectionMayHaveChanged (boolean isKnownToBeSelected) {
	if (isKnownToBeSelected)
	  setEnabled (! isRunning);
	else
	  setEnabled ();
      }
    }

    /**
     * Delete Row in Instruction or Data view
     */
    class DeleteAction extends AbstractAction implements IsRunningListener, View.SelectionListener {
      public DeleteAction () {
	putValue (NAME, "Delete");
	putValue (MNEMONIC_KEY, KeyEvent.VK_D);
	putValue (ACCELERATOR_KEY, KeyStroke.getKeyStroke (KeyEvent.VK_BACK_SPACE, platformAgnosticMetaMask));
	putValue (SHORT_DESCRIPTION, "Delete row.");
	setEnabled (false);
	masterSelectionListener.add (this);
	isRunningListeners.add (this);
      }
      public void actionPerformed (ActionEvent e) {
	ViewPane pane = getSelectedPane ();
	if (pane != null) {
	  if (stopEditing ()) {
	    boolean deleted = pane.view.deleteSelection ();
	    if (! deleted) 
	      showMessage ("Unable to delete row.");
	  }
	} else 
	  showMessage ("No row selected for Delete.");
      }
      private void setEnabled () {
	setEnabled (! isRunning && getSelectedPane () != null);
      }
      public void isRunningChanged () {
	setEnabled ();
      }
      public void selectionMayHaveChanged (boolean isKnownToBeSelected) {
	if (isKnownToBeSelected)
	  setEnabled (! isRunning);
	else
	  setEnabled ();
      }
    }
    
    //////////////////
    // Action Support
    
    /**
     * Checkbox column data model for setting instruction or data breakpoints
     */
    class BreakpointControlModel extends AbstractDataModel implements Observer {
      Region     region;
      DebugPoint debugPoint;
      BreakpointControlModel (Region aRegion) {
	region     = aRegion;
	debugPoint = region.getType () == Region.Type.INSTRUCTIONS? DebugPoint.INSTRUCTION : DebugPoint.MEMORY_READ;
	addDebugPointObserver (this);
      }
      /**
       * Insert row into breakpoint control model.
       * Row must already have been inserted into region and breakpoints adjusted.
       */
      @Override
      public boolean insertRow (int row) {
	for (int r = row+1; r<region.length (); r++) 
	  if (isDebugPointEnabled (DebugType.BREAK, debugPoint, region.getCellForRowIndex (r).getAddress ()))
	    tellObservers (new DataModelEvent (DataModelEvent.Type.WRITE_BY_USER, Arrays.asList (new TableCellIndex (r-1,0), new TableCellIndex (r,0))));	    
	return true;
      }
      /**
       * Delete row from breakpoint control model. 
       * Row must already have been delete from region and breakpoints adjusted.
       */
      @Override
      public boolean deleteRow (int row) {
	for (int r = row; r<region.length () - 1; r++)
	  if (isDebugPointEnabled (DebugType.BREAK, debugPoint, region.getCellForRowIndex (r).getAddress ())) 
	    tellObservers (new DataModelEvent (DataModelEvent.Type.WRITE_BY_USER, Arrays.asList (new TableCellIndex (r,0), new TableCellIndex (r+1,0))));
	return true;
      }
      @Override
      public boolean canInsertRow (int row) {
	return true;
      }
      @Override
      public boolean canDeleteRow (int row) {
	return true;
      }
      public void update (Observable o, Object arg) {
	Vector<TableCellIndex> updatedCells = new Vector<TableCellIndex> ();
	DataModelEvent event = (DataModelEvent) arg;
	if (event.getType () == DataModelEvent.Type.WRITE || event.getType () == DataModelEvent.Type.WRITE_BY_USER)
	  for (TableCellIndex cell : event.getCells ()) 
	    updatedCells.add (new TableCellIndex (region.getRowIndexForAddress (cell.rowIndex), 0));
	tellObservers (new DataModelEvent (event.getType (), updatedCells));
      }
      public Class   getColumnClass (int columnIndex) {
	return Boolean.class;
      }
      public int     getColumnCount () {
	return 1;
      }
      public String  getColumnName  (int columnIndex) {
	return "B";
      }
      public int     getRowCount    () {
	return region.getRowCount ();
      }
      public Object  getValueAt     (int rowIndex, int columnIndex) {
	return isDebugPointEnabled (DebugType.BREAK, debugPoint, region.getCellForRowIndex (rowIndex).getAddress ());
      }
      public boolean isCellEditable (int rowIndex, int columnIndex) {
	return true;
      }
      public void    setValueAt     (Object aValue, int rowIndex, int columnIndex) {
	setDebugPoint (DebugType.BREAK, debugPoint, region.getCellForRowIndex (rowIndex).getAddress(), (Boolean) aValue);
      }
    }
    
    class ApplicationUndoManager extends UndoManager {
      private Vector<StateChangedListener> stateChangedListeners = new Vector<StateChangedListener> ();
      private void fireStateChanged () {
	for (StateChangedListener l : stateChangedListeners)
	  l.stateChanged (this);
      }
      void addStateChangedListener (StateChangedListener l) {
	stateChangedListeners.add (l);
      }
      @Override
      public boolean addEdit (UndoableEdit e) {
	boolean r = super.addEdit (e);
	if (r) 
	  fireStateChanged ();
	return r;
      }
      @Override
      public void undo () {
	super.undo ();
	fireStateChanged ();
      }
      @Override
      public void redo () {
	super.redo ();
	fireStateChanged ();
      }
    }
    
    void fireIsRunningChanged () {
      for (IsRunningListener l : isRunningListeners)
	l.isRunningChanged ();
    }
    
    ViewPane getSelectedPane () {
      return mainPane.allMemoryPane.getSelectedPane ();
    }
    
    boolean stopEditing () {
      ViewPane sp = getSelectedPane ();
      if (sp != null && sp.view.getCellEditor () != null)
      	return sp.view.getCellEditor ().stopCellEditing ();
      else
	return true;
    }
    
    class MasterSelectionListner extends Vector <View.SelectionListener> implements View.SelectionListener {
      public void selectionMayHaveChanged (boolean isKnownToBeSelected) {
	for (View.SelectionListener l : this)
	  l.selectionMayHaveChanged (isKnownToBeSelected);
      }
    }
        
    /////////////////
    // PANES
    
    class MainPane extends JPanel {
      CpuPane              cpuPane;
      AllMemoryPane        allMemoryPane;
      int                  fontSizeAdjustment = 0;
      MainPane () {
	setLayout (new BoxLayout (this, BoxLayout.LINE_AXIS));
	cpuPane       = new CpuPane ();
	allMemoryPane = new AllMemoryPane ();
	cpuPane.setAlignmentY (0);
	allMemoryPane.setAlignmentY (0);
	add (cpuPane);
	add (allMemoryPane);
      }
      void adjustHighlights (boolean clear) {
	cpuPane.adjustHighlights (clear);
	allMemoryPane.adjustHighlights (clear);
      }
      void adjustFontSize (int increment) {
	fontSizeAdjustment += increment;
	cpuPane.adjustFontSize (increment);
	allMemoryPane.adjustFontSize (increment);
	setWindowToPreferredWidth ();
      }
      public void resizeToFitWindow () {
	allMemoryPane.resizeToFitWindow ();
      }
    } 

    class CpuPane extends JPanel {
      RegisterPane           registerPane;
      RegisterViewPane       registerViewPane;
      ProcessorStatePane     processorStatePane;
      JLabel                 strut = null;
      CpuPane () {
	setLayout (new BoxLayout (this, BoxLayout.LINE_AXIS));
	JPanel col0 = new JPanel ();
	JPanel col1 = null;
	col0.setLayout (new BoxLayout (col0, BoxLayout.PAGE_AXIS));
	col0.setBorder (new MatteBorder (4,0,2,0,Application.this.getBackground ()));
	col0.setAlignmentY (0);
	registerPane           = new RegisterPane      ();
	registerViewPane       = new RegisterViewPane  ();
	processorStatePane     = new ProcessorStatePane ();
	JPanel p = new JPanel ();
	p.setLayout (new BoxLayout (p, BoxLayout.LINE_AXIS));
	p.setAlignmentX (0);
	registerPane.setAlignmentY     (0);
	registerViewPane.setAlignmentY (0);
	JPanel regPart = new JPanel (new BorderLayout ());
	JPanel regGrp  = new JPanel ();
	regGrp.setLayout (new BoxLayout (regGrp, BoxLayout.LINE_AXIS));
	Dimension sz = new Dimension (0,0);
	regGrp.add (new Box.Filler (sz,sz,sz));
	regGrp.add (registerPane);
	regGrp.add (registerViewPane);
	regGrp.add (new Box.Filler (sz,sz,sz));
	regPart.add (regGrp, BorderLayout.CENTER);
	regPart.setAlignmentX (1);
	p.add (regPart);
	p.setAlignmentX (0);
	if (isRegFileInOwnCol) {
	  col1 = new JPanel ();
	  col1.setLayout (new BoxLayout (col1, BoxLayout.PAGE_AXIS));
	  col1.setBorder (new CompoundBorder (new CompoundBorder (new CompoundBorder (new MatteBorder (0,1,0,0,strutColor.darker ()),	
										      new MatteBorder (0,3,0,0,strutColor)),
								  new MatteBorder (0,1,0,0,strutColor.darker ())),
					      new MatteBorder (4,0,2,0,Application.this.getBackground ())));
	  col1.add (p);
	  col1.add (Box.createVerticalGlue ());
	  col1.setAlignmentY (0);
	} else {
	  col0.add (p);
	  strut = new JLabel ();
	  strut.setBorder (new CompoundBorder (new MatteBorder (2,0,2,0, Application.this.getBackground ()),
					   new CompoundBorder (new MatteBorder (1,0,1,0,strutColor.darker ()),
							       new MatteBorder (0,0,2,0,strutColor))));
	  int width = Math.max (registerPane.getMinimumSize ().width+registerViewPane.getMinimumSize ().width, processorStatePane.getMinimumSize ().width);
	  strut.setMinimumSize (new Dimension (width,8));
	  strut.setMaximumSize (new Dimension (width,8));
	  strut.setAlignmentX (0);
	  col0.add (strut);
	}
	p = new JPanel ();
	p.setLayout (new BoxLayout (p, BoxLayout.PAGE_AXIS));
	p.setAlignmentY (0);
	p.setAlignmentX (0);
	p.add (processorStatePane);
	p.setAlignmentX (0);
	col0.add (p);
	add (col0);
	if (col1 != null)
	  add (col1);
      }
      
      void adjustHighlights (boolean clear) {
	registerPane.adjustHighlights (clear);
	registerViewPane.adjustHighlights (clear);
	processorStatePane.adjustHighlights (clear);
      }
      
      void adjustFontSize (int increment) {
	registerPane.adjustFontSize (increment);
	registerViewPane.adjustFontSize (increment);
	processorStatePane.adjustFontSize (increment);
	processorStatePane.setStrutWidth (0);
	int width = Math.max (registerPane.getPreferredSize ().width+registerViewPane.getPreferredSize ().width, processorStatePane.getPreferredSize ().width);
	processorStatePane.setStrutWidth (width+100);
	strut.setMinimumSize (new Dimension (width+100, 8));
	strut.setMaximumSize (new Dimension (width+100, 8));
      }
    }
    
    /**
     * The memory half of the application frame.
     */
    
    final int INSTRUCTION_TO_DATA_COMMENT_WEIGHT = 65;
    final int INSTRUCTION_COMMENT_MIN_COLUMNS    = 20;
    final int DATA_COMMENT_MIN_COLUMNS           = 10;
        
    class AllMemoryPane extends JPanel {
      MemoryRegionSplitPane     instructionMemoryPane;
      MemoryRegionSplitPane     dataMemoryPane;
      Vector <MemoryRegionPane> regionPanes = new Vector<MemoryRegionPane> ();
      int                       numCols;
      int                       instructionCommentColumns = INSTRUCTION_COMMENT_MIN_COLUMNS;
      int                       dataCommentColumns        = DATA_COMMENT_MIN_COLUMNS;
      AllMemoryPane () {
	UIDefaults uidefs = UIManager.getLookAndFeelDefaults ();	
	uidefs.put ("SplitPane.background", new ColorUIResource (strutColor));
	uidefs.put ("SplitPaneDivider.border", new BorderUIResource (new CompoundBorder (new MatteBorder (1,0,1,0, strutColor.darker ()),
											 new MatteBorder (0,0,0,0, strutColor))));
	setLayout (new BoxLayout (this, BoxLayout.LINE_AXIS));
	instructionMemoryPane = new MemoryRegionSplitPane ();
	instructionMemoryPane.setBorder (new CompoundBorder (new CompoundBorder (new CompoundBorder (new MatteBorder (0,1,0,0,strutColor.darker ()),	
												     new MatteBorder (0,3,0,0,strutColor)),
										 new MatteBorder (0,1,0,0,strutColor.darker ())),
							     new MatteBorder (4,0,2,0,Application.this.getBackground ())));
	dataMemoryPane      = new MemoryRegionSplitPane ();
	dataMemoryPane.setBorder (new CompoundBorder (new CompoundBorder (new CompoundBorder (new MatteBorder (0,1,0,0,strutColor.darker ()),	
											      new MatteBorder (0,3,0,0,strutColor)),
									  new MatteBorder (0,1,0,0,strutColor.darker ())),
						      new MatteBorder (4,0,2,0,Application.this.getBackground ())));
	numCols = 2;
	
	instructionMemoryPane.setAlignmentY (0);
	dataMemoryPane.setAlignmentY (0);
	add (instructionMemoryPane);
	if (dataMemoryPane != instructionMemoryPane)
	  add (dataMemoryPane);	
      }
      
      private int commentColsToPoints (int cols) {
	JComponent prototype = regionPanes.get (0).getCommentPrototype ();
	int colWidth = prototype.getFontMetrics (prototype.getFont ()).charWidth ('W');
	return cols * colWidth;
      }
      
      private int pointsToCommentCols (int points) {
	JComponent prototype = regionPanes.get (0).getCommentPrototype ();
	int colWidth = prototype.getFontMetrics (prototype.getFont ()).charWidth ('W');
	return points / colWidth;
      }
      
      /**
       * Fit AllMemoryPane to current window size, by adjusting comment widths and choosing either 1-column or 2-column display.
       * of Instructions and Data.
       */
      void resizeToFitWindow () {
	if (regionPanes.size () > 0) {
	  int adj      = mainPane.getSize ().width - mainPane.getPreferredSize ().width;
	  int newWidth = getPreferredSize ().width + adj;
	  int insWidth = 0;
	  for (MemoryRegionPane regionPane : regionPanes)
	    if (regionPane.region.getType () == Region.Type.INSTRUCTIONS) {
	      insWidth = regionPane.getPreferredSize ().width - commentColsToPoints (instructionCommentColumns - INSTRUCTION_COMMENT_MIN_COLUMNS) + 20;
	      break;
	    }
	  int datWidth = 0;
	  for (MemoryRegionPane regionPane : regionPanes)
	    if (regionPane.region.getType () == Region.Type.DATA) {
	      datWidth = regionPane.getPreferredSize ().width - commentColsToPoints (dataCommentColumns - DATA_COMMENT_MIN_COLUMNS) + 20;
	    }
	  setNumColumns (newWidth >= (insWidth + datWidth)? 2 : 1);
	  if (numCols == 1) {
	    instructionCommentColumns = INSTRUCTION_COMMENT_MIN_COLUMNS + pointsToCommentCols (newWidth - insWidth);
	    dataCommentColumns        = instructionCommentColumns;
	  } else {
	    int totalCommentCols = INSTRUCTION_COMMENT_MIN_COLUMNS + DATA_COMMENT_MIN_COLUMNS + pointsToCommentCols (newWidth - (insWidth + datWidth));
	    instructionCommentColumns = Math.max (INSTRUCTION_COMMENT_MIN_COLUMNS, totalCommentCols * INSTRUCTION_TO_DATA_COMMENT_WEIGHT / 100);
	    dataCommentColumns        = Math.max (DATA_COMMENT_MIN_COLUMNS, totalCommentCols - instructionCommentColumns);
	  }
	  for (MemoryRegionPane regionPane : regionPanes) {
	    if (regionPane.region.getType () == Region.Type.INSTRUCTIONS)
	      regionPane.setCommentColumns (instructionCommentColumns);
	    else
	      regionPane.setCommentColumns (dataCommentColumns);
	  }
	}
	revalidate ();
      }
      
      void setNumColumns (int aNum) {
	if (aNum != numCols) {
	  numCols = aNum;
	  instructionMemoryPane.removeAll ();
	  if (numCols == 2) {
	    dataMemoryPane      = new MemoryRegionSplitPane ();
	    dataMemoryPane.setBorder (new CompoundBorder (new CompoundBorder (new CompoundBorder (new MatteBorder (0,1,0,0,strutColor.darker ()),	
												  new MatteBorder (0,3,0,0,strutColor)),
									      new MatteBorder (0,1,0,0,strutColor.darker ())),
							  new MatteBorder (4,0,2,0,Application.this.getBackground ())));
	    dataMemoryPane.setAlignmentY (0);
	    add (dataMemoryPane);
	  } else {
	    remove (dataMemoryPane);
	    dataMemoryPane = instructionMemoryPane;
	  }
	}
	for (int i=0; i<regionPanes.size (); i+=2) {
	  MemoryRegionPane regionPane0 = regionPanes.get (i);
	  MemoryRegionPane regionPane1 = regionPanes.get (i+1);
	  if (regionPane0.region.getType () == Region.Type.INSTRUCTIONS)
	    instructionMemoryPane.add (regionPane0, regionPane1);
	  else
	    dataMemoryPane.add (regionPane0, regionPane1);
	  ((BoxLayout) regionPane0.getLayout ()).invalidateLayout (regionPane0);
	  ((BoxLayout) regionPane1.getLayout ()).invalidateLayout (regionPane1);
	}
      }
      
      void clear () {
	regionPanes.clear ();
	instructionMemoryPane.removeAll ();
	dataMemoryPane.removeAll ();
      }
      
      void add (MemoryRegionPane regionPane0, MemoryRegionPane regionPane1) {
	if (mainPane.fontSizeAdjustment != 0) {
	  regionPane0.adjustFontSize (mainPane.fontSizeAdjustment);
	  regionPane1.adjustFontSize (mainPane.fontSizeAdjustment);
	}
	regionPanes.add (regionPane0);
	regionPanes.add (regionPane1);
	if (regionPane0.region.getType () == Region.Type.INSTRUCTIONS)
	  instructionMemoryPane.add (regionPane0, regionPane1);
	else
	  dataMemoryPane.add (regionPane0, regionPane1);
      }
      void adjustHighlights (boolean clear) {
	for (MemoryRegionPane p : regionPanes)
	  p.adjustHighlights (clear);
      }
      void adjustFontSize (int increment) {
	for (MemoryRegionPane p : regionPanes)
	  p.adjustFontSize (increment);
      }
      ViewPane getSelectedPane () {
	for (MemoryRegionPane region : regionPanes)
	  if ((region.isaPane.view.getSelectedRow () != -1 && region.isaPane.view.isCellEditable (region.isaPane.view.getSelectedRow (), region.isaPane.view.getSelectedColumn ())) || 
	      region.isaPane.view.getEditorComponent () != null)
	    return region.isaPane;
	return null;
      }
    }
    
    class MemoryRegionSplitPane extends JSplitPane {
      MemoryRegionSplitPane () {
	super (JSplitPane.VERTICAL_SPLIT, true, new MemoryRegionListPane (), new MemoryRegionListPane ());
	setResizeWeight (1.0);
	setOneTouchExpandable (true);
	setVisible (false);
      }
      public void add (MemoryRegionPane regionPane0, MemoryRegionPane regionPane1) {
	((MemoryRegionListPane) getTopComponent    ()).add (regionPane0);
	((MemoryRegionListPane) getBottomComponent ()).add (regionPane1);
	if (! isVisible ()) {
	  setDividerLocation     (2000);
	  setLastDividerLocation (Application.this.getSize ().height / 2);
	}
	setVisible (true);
      }
      public void removeAll () {
	setVisible (false);
	((MemoryRegionListPane) getTopComponent    ()).removeAll ();
	((MemoryRegionListPane) getBottomComponent ()).removeAll ();
      }
    }
    
    class MemoryRegionListPane extends JScrollPane {
      boolean isEmpty = true;
      MemoryRegionListPane () {
	setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	setVerticalScrollBarPolicy   (JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	setBorder (null);
      }
      public void add (MemoryRegionPane regionPane) {
	if (isEmpty) {
	  JPanel p = new JPanel ();
	  p.setLayout (new BoxLayout (p, BoxLayout.PAGE_AXIS));
	  p.add (regionPane);
	  setViewportView (p);
	  isEmpty = false;
	  setVisible (true);
	} else
	  ((JPanel) getViewport ().getView ()).add (regionPane);
      }
      public void removeAll () {
	setViewportView (null);
	isEmpty = true;
	setVisible (false);
      }
    }
    
    class CurrentInstructionPane extends ViewPane {
      CurrentInstructionPane (String name, int width, Register curInsAddr) {
	super (name,
	       new ViewModel (new PickModel (new InstructionModel (memory), Arrays.asList ((DataModel) curInsAddr), Arrays.asList (0), Arrays.asList (1)),
			      0, 1, 1, null, Arrays.asList (1), Arrays.asList (isSmallCurInsDpy? "" : "Current Instruction")), statusBar,
	       Arrays.asList ((ViewFormat) 
			      new ViewLabel    (width, SwingConstants.CENTER,  isTwoProcStateCols? curInsTwoColFont : curInsFont, codeColor, null, new ViewFormat.Format (String.class, "%s"))));
	view.setRowHeight (view.getRowHeight () + (isTwoProcStateCols? 0: 10));
	adjustSize ();
      }
      public void setWidth (int width) {
	view.setColumnWidth (0, width); 
	adjustSize ();
      }
    }
    
    class CurInsDescriptionPane extends ViewPane {
      CurInsDescriptionPane (int width, Register curInsAddr) {
	super ("",
	       new ViewModel (new PickModel (new InstructionModel (memory), Arrays.asList ((DataModel) curInsAddr), Arrays.asList (0), Arrays.asList (1)),
			      0, 1, 1, null, Arrays.asList (2), Arrays.asList ("")), statusBar,
	       Arrays.asList ((ViewFormat) 
			      new ViewLabel    (width, SwingConstants.CENTER, isTwoProcStateCols? curInsDscTwoColFont : curInsDscFont, macColor, null, new ViewFormat.Format (String.class, "%s"))));
	view.setRowHeight (view.getRowHeight () - (isTwoProcStateCols? 6: 3));
	adjustSize ();
      }
      public void setWidth (int width) {
	view.setColumnWidth(0, width); 
	adjustSize ();
      }
    }
    
    class ProcessorStatePane extends JPanel /*JScrollPane*/ {
      Vector <ViewPane> panes         = new Vector <ViewPane> ();
      Vector <ViewPane> flexSizePanes = new Vector <ViewPane> ();
      Vector <JPanel>   setPanes      = new Vector <JPanel>   ();
      Vector <JLabel>   struts        = new Vector <JLabel>   ();
      JPanel            widthBenchmarkPane;
      ProcessorStatePane () {
//	setBorder (null);
//	JPanel vp = new JPanel ();
//	vp.setLayout (new BoxLayout (vp, BoxLayout.PAGE_AXIS));
//	setViewportView (vp);
	setLayout (new BoxLayout (this, BoxLayout.PAGE_AXIS));
	for (RegisterSet regSet : processorState) {
	  JPanel p = new JPanel ();
	  p.setLayout (new BoxLayout (p, BoxLayout.LINE_AXIS));
	  p.setAlignmentY (0);
	  p.setAlignmentX (0);
	  if (isTwoProcStateCols) {
	    JLabel l = new JLabel (regSet.getName ());
	    l.setHorizontalAlignment (SwingConstants.LEFT);
	    l.setFont (subTitleFont);
	    l.setAlignmentY (0);
	    JPanel tp = new JPanel (new GridLayout ());
	    tp.add (l);
	    tp.setBorder (new EmptyBorder (0,2,0,0));
	    tp.setMinimumSize (new Dimension (15,tp.getPreferredSize ().height));
	    tp.setMaximumSize (new Dimension (15,tp.getPreferredSize ().height));
	    tp.setAlignmentY (0);
	    tp.setAlignmentX (0);
	    p.add (tp);
	  }
	  JPanel dp = new JPanel ();
	  dp.setLayout (new BoxLayout (dp, BoxLayout.PAGE_AXIS));
	  dp.setAlignmentY (0);
	  dp.setAlignmentX (0);
	  Register curInsAddr = regSet.get (AbstractCPU.CURRENT_INSTRUCTION_ADDRESS);
	  ViewPane pane;
	  if (isTwoProcStateCols) 
	    pane = new ViewPane ("",
				 new ViewModel (regSet, 0, (regSet.getRowCount ()+1)/2, 2, null, Arrays.asList (0,1), Arrays.asList ("","","","")), statusBar,
				 Arrays.asList  (new ViewLabel     (30, SwingConstants.RIGHT, nameFont, nameColor, null,  new ViewFormat.Format (String.class, "%s:")),
						 new ViewTextField (8,  SwingConstants.LEFT,   cellFont, null,      null, 
								    Arrays.asList ((ViewFormat.Format) 
										   new ViewFormat.NumberFormat     (Integer.class,        "%08x", 16),
										   new ViewFormat.NumberFormat     (Long.class,           "%d",   10),
										   new ViewFormat.NumberFormat     (HalfByteNumber.class, "%01x", 16),
										   new ViewFormat.NumberFormat     (Byte.class,           "%02x", 16),
										   new ViewFormat.NumberFormat     (Short.class,          "%03x", 16),
										   new ViewFormat.TwoIntegerFormat (SixByteNumber.class,  "%04x %08x", 16))),
						 new ViewLabel     (30, SwingConstants.RIGHT, nameFont, nameColor, null,  new ViewFormat.Format (String.class, "%s:")),
						 new ViewTextField (8,  SwingConstants.LEFT,   cellFont, null,      null, 
								    Arrays.asList ((ViewFormat.Format) 
										   new ViewFormat.NumberFormat     (Integer.class,        "%08x", 16),
										   new ViewFormat.NumberFormat     (Long.class,           "%d",   10),
										   new ViewFormat.NumberFormat     (HalfByteNumber.class, "%01x", 16),
										   new ViewFormat.NumberFormat     (Byte.class,           "%02x", 16),
										   new ViewFormat.NumberFormat     (Short.class,          "%03x", 16),
										   new ViewFormat.TwoIntegerFormat (SixByteNumber.class,  "%04x %08x", 16)))));
	  else
	    pane = new ViewPane (curInsAddr != null? "" : regSet.getName (),
				 new ViewModel (regSet, 0, regSet.getRowCount (), 1, null, Arrays.asList (0,1), null), statusBar,
				 Arrays.asList  (new ViewLabel     (99, SwingConstants.RIGHT, nameFont, nameColor, null,  new ViewFormat.Format (String.class, "%s:")),
						 new ViewTextField (13, SwingConstants.LEFT,   cellFont, null,      null,
								    Arrays.asList ((ViewFormat.Format) 
										   new ViewFormat.NumberFormat     (Integer.class,        "%08x", 16),
										   new ViewFormat.NumberFormat     (Long.class,           "%d",   10),
										   new ViewFormat.NumberFormat     (HalfByteNumber.class, "%01x", 16),
										   new ViewFormat.NumberFormat     (Byte.class,           "%02x", 16),
										   new ViewFormat.NumberFormat     (Short.class,          "%03x", 16),
										   new ViewFormat.TwoIntegerFormat (SixByteNumber.class,  "%04x %08x", 16)))));
	  widthBenchmarkPane = pane;
	  int width = pane.getPreferredSize ().width;
	  if (curInsAddr != null) {
	    ViewPane ip = new CurrentInstructionPane ("", width-3, curInsAddr);
	    dp.add (ip);
	    panes.add (ip);
	    flexSizePanes.add (ip);
	    if (!isSmallCurInsDpy) { 
	      ip = new CurInsDescriptionPane  (width-3, curInsAddr);
	      dp.add (ip);
	      panes.add (ip);
	      flexSizePanes.add (ip);
	    }
	  }
	  panes.add (pane);
	  dp.add (pane);
	  p.add (dp);
	  add (p);
	  //vp.add (p);
	  setPanes.add (p);
	  if (isTwoProcStateCols && regSet != processorState.lastElement ()) {
	    JLabel l = new JLabel ();
	    l.setBorder (new CompoundBorder (new MatteBorder (1,0,1,0, Application.this.getBackground ()),
					     new CompoundBorder (new MatteBorder (1,0,0,0,strutColor.darker ()),
								 new MatteBorder (0,0,0,0,strutColor))));
	    l.setMinimumSize (new Dimension (width + 15,8));
	    l.setMaximumSize (new Dimension (width + 15,8));
	    add (l);
	    //vp.add (l);
	    l.setAlignmentX (0);
	    struts.add (l);
	  }
	}
      }
      void adjustHighlights (boolean clear) {
	for (ViewPane pane : panes)
	  pane.adjustHighlights (clear);
      }
      void adjustFontSize (int increment) {
	for (ViewPane pane: panes)
	  pane.adjustFontSize (increment);
	int pw = widthBenchmarkPane.getMinimumSize ().width;
	for (ViewPane pane : flexSizePanes) 
	  pane.setWidth (pw - 3);
      }
      void setStrutWidth (int width) {
	for (JLabel strut : struts) {
	  strut.setMinimumSize (new Dimension (width, 8));
	  strut.setMaximumSize (new Dimension (width, 8));
	}
	int tpw = Math.max (15, width - widthBenchmarkPane.getMinimumSize ().width);
	if (isTwoProcStateCols) {
	  for (JPanel setPane : setPanes) {
	    JPanel titlePanel = (JPanel) setPane.getComponent (0);
	    Dimension sz = new Dimension (tpw, titlePanel.getMinimumSize ().height);
	    titlePanel.setMinimumSize (sz);
	    titlePanel.setMaximumSize (sz);
	    ((BoxLayout) setPane.getLayout ()).invalidateLayout (setPane);
	  }
	}
	setBorder (null);
      }
    }
    
    class RegisterPane extends ViewPane {
      RegisterPane () {
	super ("Register File",
	       new ViewModel (registerFile, 0, registerFile.getRowCount (), 1, Arrays.asList (0), Arrays.asList (1), null), statusBar,
	       Arrays.asList (new ViewLabel     (28, SwingConstants.RIGHT,  addressFont, addressColor, memoryHighlight, new ViewFormat.Format       (String.class, "%s:")),
			      new ViewTextField (8,  SwingConstants.CENTER, cellFont,    null,         memoryHighlight, new ViewFormat.NumberFormat (Integer.class, "%08x", 16))));
      }
    }
    
    class RegisterViewPane extends ViewPane {
      RegisterViewPane () {
	super ("Reg Views",
	       new ViewModel (new ValueView (registerFile, 1, (MapModel) memory.getLabelMap (), "Ref"), 
			      0, registerFile.getRowCount (), 1, null, Arrays.asList (0,1), null), statusBar,
	       Arrays.asList ((ViewFormat)
			      new ViewTextField (6,  SwingConstants.RIGHT, labelFont,    Color.BLACK,  codeHighlight, new ViewFormat.NumberFormat (ValueView.Value.class, "%d", 10)),
			      new ViewTextField (8,  SwingConstants.RIGHT, labelFont,    codeColor,    codeHighlight, new ViewFormat.Format       (String.class,          "%s"))));
      }
    }
    
    class MemoryPane extends ViewPane {
      MemoryPane (int address, int length, boolean isInsMem) {
	super (String.format ("Memory - %x", address, length),
	       new ViewModel (mainMemory, address, (length+3)/4, 4, Arrays.asList (0), Arrays.asList (1), Arrays.asList ("Addr","0","1","2","3")), statusBar,
	       isSmallInsMemDpy && isInsMem?
	       Arrays.asList (new ViewLabel     (24,  SwingConstants.RIGHT,  memAddressFont, addressColor, memoryHighlight, new ViewFormat.NumberFormat (Integer.class, "0x%x:", 16)),
			      new ViewTextField (2,  SwingConstants.CENTER, memCellFont,    null,         memoryHighlight, new ViewFormat.NumberFormat (Byte.class,    "%02x", 16)),
			      new ViewTextField (2,  SwingConstants.CENTER, memCellFont,    null,         memoryHighlight, new ViewFormat.NumberFormat (Byte.class,    "%02x", 16)),
			      new ViewTextField (2,  SwingConstants.CENTER, memCellFont,    null,         memoryHighlight, new ViewFormat.NumberFormat (Byte.class,    "%02x", 16)),
			      new ViewTextField (2,  SwingConstants.CENTER, memCellFont,    null,         memoryHighlight, new ViewFormat.NumberFormat (Byte.class,    "%02x", 16))) :
	       Arrays.asList (new ViewLabel     (44,  SwingConstants.RIGHT,  addressFont, addressColor, memoryHighlight, new ViewFormat.NumberFormat (Integer.class, "0x%x:", 16)),
			      new ViewTextField (2,  SwingConstants.CENTER, cellFont,       null,         memoryHighlight, new ViewFormat.NumberFormat (Byte.class,    "%02x", 16)),
			      new ViewTextField (2,  SwingConstants.CENTER, cellFont,       null,         memoryHighlight, new ViewFormat.NumberFormat (Byte.class,    "%02x", 16)),
			      new ViewTextField (2,  SwingConstants.CENTER, cellFont,       null,         memoryHighlight, new ViewFormat.NumberFormat (Byte.class,    "%02x", 16)),
			      new ViewTextField (2,  SwingConstants.CENTER, cellFont,       null,         memoryHighlight, new ViewFormat.NumberFormat (Byte.class,    "%02x", 16))));
      }
      public void setLength (int len) {
	((ViewModel) view.getModel ()).setLength ((len+3)/4);
      }
    }
    
    class InstructionsPane extends ViewPane implements Region.ByteLengthChangedListener {
      MemoryPane memoryPane;
      Region     region;
      InstructionsPane (InstructionRegion aRegion, MemoryPane aMemoryPane) {
	super (String.format ("Instructions - %x", aRegion.getAddress (), aRegion.length ()),
	       new ViewModel (new CompoundModel (aRegion, new BreakpointControlModel (aRegion)), 
			      0, aRegion.length (), 1, Arrays.asList (5,0), isMacShown? Arrays.asList (1,2,3,4) : Arrays.asList (2,3,4),  null), statusBar,
	       isMacShown?
	       Arrays.asList (new ViewCheckBox  (breakpointColor),
			      new ViewLabel     (44,  SwingConstants.RIGHT, addressFont, addressColor, codeHighlight, new ViewFormat.NumberFormat (Integer.class, "0x%x:", 16)),
			      new ViewTextField (13, SwingConstants.LEFT,  macFont,     macColor,     codeHighlight, new ViewFormat.Format       (String.class,  "%s")),
			      new ViewTextField (8,  SwingConstants.RIGHT, labelFont,   labelColor,   codeHighlight, new ViewFormat.Format       (String.class,  "%s")),
			      new ViewTextField (20, SwingConstants.LEFT,  cellFont,    codeColor,    codeHighlight, new ViewFormat.Format       (String.class,  "%s")),
			      new ViewTextField (INSTRUCTION_COMMENT_MIN_COLUMNS, 
						     SwingConstants.LEFT,  commentFont, commentColor, codeHighlight, new ViewFormat.Format      (String.class,  "%s"))) :
	       Arrays.asList (new ViewCheckBox  (breakpointColor),
			      new ViewLabel     (44,  SwingConstants.RIGHT, addressFont, addressColor, codeHighlight, new ViewFormat.NumberFormat (Integer.class, "0x%x:", 16)),
			      new ViewTextField (8,  SwingConstants.RIGHT, labelFont,   labelColor,   codeHighlight, new ViewFormat.Format       (String.class,  "%s")),
			      new ViewTextField (24, SwingConstants.LEFT,  cellFont,    codeColor,    codeHighlight, new ViewFormat.Format       (String.class,  "%s")),
			      new ViewTextField (INSTRUCTION_COMMENT_MIN_COLUMNS, 
						 SwingConstants.LEFT,  commentFont, commentColor, codeHighlight, new ViewFormat.Format      (String.class,  "%s"))));	       
	memoryPane = aMemoryPane;
	region     = aRegion;
	region.addByteLengthChangedListener (this);
	view.addUndoableEditListener (undoManager);
	view.addMouseListener (new MouseAdapter () {
	  public void mousePressed (MouseEvent e) {
	    if (e.getClickCount() == 2  && view.getSelectedColumn () == 1) {
	      mainPane.adjustHighlights (true);
	      gotoPC ((Integer) view.getValueAt (view.getSelectedRow (), 1));
	    }
	  }});
      }
      public void byteLengthChanged () {
	memoryPane.setLength (region.byteLength ());
      }
      public void setCommentColumns (int columns) {
	view.setColumnWidth (isMacShown? 5 : 4, columns);
	adjustSize ();
      }
      JComponent getCommentPrototype () {
	return view.getColumnFormat (isMacShown? 5 : 4).getRendererPrototype ();
      }
    }
    
    class DataPane extends ViewPane implements Region.ByteLengthChangedListener {
      MemoryPane memoryPane;
      Region     region;
      DataPane (DataRegion aRegion, MemoryPane aMemoryPane) {
	super (String.format ("Data - %x", aRegion.getAddress (), aRegion.length ()), 
	       new ViewModel (aRegion, 0, aRegion.length (), 1, null, Arrays.asList (0,1,2,3), null), statusBar,
	       Arrays.asList ((ViewFormat)
			      new ViewTextField (6,  SwingConstants.RIGHT, labelFont,   Color.BLACK,  codeHighlight, new ViewFormat.NumberFormat (DataRegion.Value.class, "%d", 10)),
			      new ViewTextField (8,  SwingConstants.RIGHT, labelFont,   codeColor,    codeHighlight, new ViewFormat.Format       (String.class,           "%s")),
			      new ViewTextField (8,  SwingConstants.RIGHT, labelFont,   labelColor,   codeHighlight, new ViewFormat.Format       (String.class,           "%s")),
			      new ViewTextField (DATA_COMMENT_MIN_COLUMNS,
						     SwingConstants.LEFT,  commentFont, commentColor, codeHighlight, new ViewFormat.Format       (String.class,           "%s"))));
	memoryPane = aMemoryPane;
	region     = aRegion;
	region.addByteLengthChangedListener (this);
      }
      public void byteLengthChanged () {
	memoryPane.setLength (region.byteLength ());
      }
      public void setCommentColumns (int columns) {
	view.setColumnWidth (3, columns);
	adjustSize ();
      }
      JComponent getCommentPrototype () {
	return view.getColumnFormat (3).getRendererPrototype ();
      }
    }
    
    class MemoryRegionPane extends JPanel {
      Region     region;
      MemoryPane pane;
      ViewPane   isaPane;
      MemoryRegionPane (Region aRegion) {
	region = aRegion;
	setLayout (new BoxLayout (this, BoxLayout.LINE_AXIS));
	pane = new MemoryPane (region.getAddress (), region.byteLength (), region.getType () == Region.Type.INSTRUCTIONS);
	pane.setAlignmentY (0);
	add (pane);
	Dimension sz = new Dimension (1,0);
	add (new Box.Filler (sz, sz, sz));
	if (region.getType () == Region.Type.INSTRUCTIONS)
	  isaPane = new InstructionsPane ((InstructionRegion) region, pane);
	else if (region.getType () == Region.Type.DATA)
	  isaPane = new DataPane ((DataRegion) region, pane);
	else 
	  throw new AssertionError (region.getType ());
	isaPane.view.setRowSelectionAllowed (true);
	isaPane.setAlignmentY (0);
	add(isaPane);
	setAlignmentX (0);
	isaPane.view.addSelectionListener (masterSelectionListener);
      }
      void adjustHighlights (boolean clear) {
	pane.adjustHighlights (clear);
	isaPane.adjustHighlights (clear);
      }
      void adjustFontSize (int increment) {
	pane.adjustFontSize (increment);
	isaPane.adjustFontSize (increment);
      }
      void setCommentColumns (int columns) {
	if (region.getType () == Region.Type.INSTRUCTIONS)
	  ((InstructionsPane) isaPane).setCommentColumns (columns);
	else
	  ((DataPane) isaPane).setCommentColumns (columns);
      }
      JComponent getCommentPrototype () {
	if (region.getType () == Region.Type.INSTRUCTIONS)
	  return ((InstructionsPane) isaPane).getCommentPrototype ();
	else
	  return ((DataPane) isaPane).getCommentPrototype ();
      }
    }
    
    void updateMemoryView () {
      if (memory.hasLoadedFile ()) {
	setTitle (applicationFullName.concat (" - ").concat (memory.getLoadedFilename ()));
	boolean haveSetStartingPC = false;
	mainPane.allMemoryPane.clear ();
	for (Region r : memory.getRegions ()) {
	  MemoryRegionPane p0 = new MemoryRegionPane (r);
	  MemoryRegionPane p1 = new MemoryRegionPane (r);
	  mainPane.allMemoryPane.add (p0, p1);
	  if (r.getType () == Region.Type.INSTRUCTIONS) {
	    if (! haveSetStartingPC) {
	      gotoPC (r.getAddress());
	      haveSetStartingPC = true;
	    }
	  }
	}
	setWindowToPreferredWidth ();
      }
    }
    
    class RunningSpeedSlider extends JFrame {
      RunningSpeedSlider () {
	Container cp = getContentPane ();
	cp.setLayout (new BorderLayout ());
	JLabel tl = new JLabel ("Running Speed", SwingConstants.CENTER);
	tl.setFont (UI.TITLE_FONT);
	cp.add (tl, BorderLayout.NORTH);
	JSlider slider = new JSlider (1, 5000, 1500);
	Hashtable<Integer,JComponent> labels = new Hashtable<Integer,JComponent> ();
	labels.put (1,    new JLabel ("0s"));
	for (int i=1000; i<=5000; i+=1000)
	  labels.put (i, new JLabel (String.format ("%ds",i/1000)));
	slider.setMajorTickSpacing (1000);
	slider.setLabelTable (labels);
	slider.setPaintLabels (true);
	slider.setPaintTicks (true);
	cp.add (slider, BorderLayout.CENTER);
	setVisible (true);
      }
    }
}
  
  /////////////////////////
  // Called by CPU
  //
  
  public void showBreak    (DebugPoint point, int value) {
    showMessage (String.format ("Stopped at %s 0x%x",point==DebugPoint.INSTRUCTION? "breakpoint" : "watchpoint", value));
  }
  
  public void showInstr    (int pc) {
  }
  
  public void showRegRead  (int pc, int regNum, int value) {
  }
  
  public void showRegWrite (int pc, int regNum, int value) {
  }
  
  public void showMemRead  (int pc, int address, byte[] value) {
  }
  
  public void showMemWrite (int pc, int address, byte[] value) {
  }
  
  public void showHalted   () {
    showMessage ("Halted.");
  }  
  
  public void showMessage (String message) {
    application.statusBar.showMessage (message);
  }
}
