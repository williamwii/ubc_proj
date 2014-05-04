package Arch.SM213.ISA;

import java.util.Arrays;
import java.util.List;
import java.io.StringReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.RecognitionException;
import ISA.AbstractAssembler;
import ISA.Memory;

public class Assembler extends AbstractAssembler {
  
  public void assembleFile (String filename, Memory memory) throws AssemblyException, FileNotFoundException, IOException {
    try {
    if (filename==null)
      throw new FileNotFoundException ();
    (new AsmSM213Parser (new CommonTokenStream (new AsmSM213Lexer (new ANTLRFileStream (filename))))).passOne (memory, 0);
    (new AsmSM213Parser (new CommonTokenStream (new AsmSM213Lexer (new ANTLRFileStream (filename))))).passTwo (memory, 0);
    } catch (AssemblyException ae) {
      throw new AssemblyException ("Assembly error: ".concat (ae.toString ()). concat ("."));
    }
  }
  
  public void assembleLine (int address, String label, String statement, String comment, Memory memory) throws AssemblyException {
    String asm = String.format ("%s%s%s\n", 
				(label     != null && ! label.trim     ().equals (""))? String.format ("%s: ",label)    : "", 
				(statement != null && ! statement.trim ().equals (""))? statement                       : "nop",
				(comment   != null && ! comment.trim   ().equals (""))? String.format (" # %s",comment) : "");
    try {
      (new AsmSM213Parser (new CommonTokenStream (new AsmSM213Lexer (new ANTLRReaderStream (new StringReader (asm)))))).passTwo (memory, address);
    } catch (AssemblyException ae) {
      throw new AssemblyException ("Assembly error: ".concat (ae.toString ()));
    } catch (IOException e) {
      throw new AssertionError (e);
    }
  }
  
  public void checkLineSyntax (int address, String label, String statement, String comment, Memory memory) throws AssemblyException {
    String line = String.format ("%s%s%s\n", 
				 (label!=null && !label.trim().equals(""))? String.format ("%s: ",label) : "", 
				 statement, 
				 (comment!=null && !comment.trim().equals(""))? String.format (" # %s",comment) : "");
    try {
      StringReader sr = new StringReader (line);
      (new AsmSM213Parser (new CommonTokenStream (new AsmSM213Lexer (new ANTLRReaderStream (new StringReader (line)))))).checkSyntax (memory, address);
    } catch (AssemblyException ae) {
      throw new AssemblyException ("Assembly error: ".concat (ae.toString ()));
    } catch (IOException e) {
      throw new AssertionError (e);
    }
  }
  
  public void checkLabelSyntax (String label, Memory memory) throws AssemblyException {
    String line = String.format ("%s\n", 
				 (label!=null && !label.trim().equals(""))? String.format ("%s: ",label) : "");
    try {
      StringReader sr = new StringReader (line);
      (new AsmSM213Parser (new CommonTokenStream (new AsmSM213Lexer (new ANTLRReaderStream (new StringReader (line)))))).checkSyntax (memory, 0);
    } catch (AssemblyException ae) {
      throw new AssemblyException ("Invalid label.");
    } catch (IOException e) {
      throw new AssertionError (e);
    }
  }
}