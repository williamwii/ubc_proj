// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g 2010-03-05 10:44:47

package Arch.SM213.ISA;

import ISA.Memory;
import ISA.MemoryCell;
import ISA.Instruction;
import ISA.Datum;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class AsmSM213Parser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "NewLine", "Comment", "Identifier", "Register", "Hex", "Decimal", "RegisterNumber", "Character", "Digit", "HexDigit", "WS", "':'", "'ld'", "'st'", "'not'", "'inc'", "'inca'", "'dec'", "'deca'", "'gpc'", "'mov'", "'add'", "'and'", "'shl'", "'shr'", "'br'", "'beq'", "'bgt'", "'j'", "'halt'", "'nop'", "','", "'*'", "'$'", "'('", "')'", "'.address'", "'.pos'", "'.long'", "'.data'"
    };
    public static final int T__29=29;
    public static final int T__28=28;
    public static final int T__27=27;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int T__24=24;
    public static final int T__23=23;
    public static final int T__22=22;
    public static final int T__21=21;
    public static final int T__20=20;
    public static final int Decimal=9;
    public static final int EOF=-1;
    public static final int HexDigit=13;
    public static final int Identifier=6;
    public static final int T__19=19;
    public static final int NewLine=4;
    public static final int T__16=16;
    public static final int T__15=15;
    public static final int T__18=18;
    public static final int T__17=17;
    public static final int Comment=5;
    public static final int Register=7;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int Digit=12;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int WS=14;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int Character=11;
    public static final int Hex=8;
    public static final int RegisterNumber=10;

    // delegates
    // delegators


        public AsmSM213Parser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public AsmSM213Parser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return AsmSM213Parser.tokenNames; }
    public String getGrammarFileName() { return "/Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g"; }


    public enum LineType { INSTRUCTION, DATA, NULL };
    Memory memory;
    LineType lineType;
    int pc;
    int opCode;
    int[] op = new int[4];
    int opLength;
    String label;
    String comment;
    int dataValue;
    int dataCount;
    int pass;

    void init (Memory aMemory, int startingAddress) {
      memory      = aMemory;
      pc          = startingAddress;
      lineType    = LineType.NULL;
      comment     = "";
      label       = "";
    }

    public void checkSyntax (Memory aMemory, int startingAddress) throws Assembler.AssemblyException {
      init (aMemory, startingAddress);
      pass = 0;
      try {
        program ();
      } catch (RecognitionException e) {
        throw new Assembler.AssemblyException ("");
      }
    }

    public void passOne (Memory aMemory, int startingAddress) throws Assembler.AssemblyException {
      init (aMemory, startingAddress);
      pass = 1;
      try {
        program ();
      } catch (RecognitionException e) {
        throw new Assembler.AssemblyException ("");
      }
    }

    public void passTwo (Memory aMemory, int startingAddress) throws Assembler.AssemblyException {
      init (aMemory, startingAddress);
      pass = 2;
      try {
        program ();
      } catch (RecognitionException e) {
        throw new Assembler.AssemblyException ("");
      }
    }

    @Override
    public void emitErrorMessage(String msg) {
      throw new Assembler.AssemblyException (msg);
    }

    int getLabelValue (String label) {
      Integer value = memory.getLabelMap ().getAddress (label);
      if (value==null) {
        if (pass==1)
          value = pc;
        else
          emitErrorMessage (java.lang.String.format ("Label not found: %s at address %d", label, pc));
      }
      return value.intValue ();
    }

    void writeLine () throws RecognitionException {
      MemoryCell cell = null;
      switch (lineType) {
        case INSTRUCTION:
          try {
            cell = Instruction.valueOf (memory, pc, opCode, op, label, comment);
            if (cell==null)
              throw new RecognitionException ();
            if (pass==1 && !label.trim ().equals ("")) 
              memory.addLabelOnly (cell);
            else if (pass==2)
              memory.add (cell);
            label = "";
            comment = "";
            pc += cell.length ();
          } catch (IndexOutOfBoundsException e) {
            throw new RecognitionException ();
          }
          break;
        case DATA:
          for (int i=0; i<dataCount; i++) {
            cell = Datum.valueOf (memory, pc, dataValue, label, comment);
            if (cell==null)
              throw new RecognitionException ();
            if (pass==1 && !label.trim ().equals (""))
              memory.addLabelOnly (cell);
            else if (pass==2)
              memory.add (cell);
            label = "";
            comment = "";
            pc += 4;
          }
          label = "";
          comment = "";
          break;
        default:
      }
      lineType = LineType.NULL;
      op[0]=0;
      op[1]=0;
      op[2]=0;
      op[3]=0;
    }



    // $ANTLR start "program"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:139:1: program : ( line )* EOF ;
    public final void program() throws RecognitionException {
        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:139:9: ( ( line )* EOF )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:139:11: ( line )* EOF
            {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:139:11: ( line )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>=NewLine && LA1_0<=Identifier)||(LA1_0>=16 && LA1_0<=34)||(LA1_0>=40 && LA1_0<=43)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:139:11: line
            	    {
            	    pushFollow(FOLLOW_line_in_program46);
            	    line();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            match(input,EOF,FOLLOW_EOF_in_program49); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "program"


    // $ANTLR start "line"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:141:1: line : ( labelDeclaration )? ( instruction | directive )? ( NewLine | ( Comment ) ) ;
    public final void line() throws RecognitionException {
        Token Comment1=null;

        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:141:6: ( ( labelDeclaration )? ( instruction | directive )? ( NewLine | ( Comment ) ) )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:141:8: ( labelDeclaration )? ( instruction | directive )? ( NewLine | ( Comment ) )
            {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:141:8: ( labelDeclaration )?
            int alt2=2;
            alt2 = dfa2.predict(input);
            switch (alt2) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:141:9: labelDeclaration
                    {
                    pushFollow(FOLLOW_labelDeclaration_in_line58);
                    labelDeclaration();

                    state._fsp--;


                    }
                    break;

            }

            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:141:28: ( instruction | directive )?
            int alt3=3;
            int LA3_0 = input.LA(1);

            if ( ((LA3_0>=16 && LA3_0<=34)) ) {
                alt3=1;
            }
            else if ( ((LA3_0>=40 && LA3_0<=43)) ) {
                alt3=2;
            }
            switch (alt3) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:141:30: instruction
                    {
                    pushFollow(FOLLOW_instruction_in_line64);
                    instruction();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:141:44: directive
                    {
                    pushFollow(FOLLOW_directive_in_line68);
                    directive();

                    state._fsp--;


                    }
                    break;

            }

            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:141:57: ( NewLine | ( Comment ) )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==NewLine) ) {
                alt4=1;
            }
            else if ( (LA4_0==Comment) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:141:59: NewLine
                    {
                    match(input,NewLine,FOLLOW_NewLine_in_line75); 

                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:141:69: ( Comment )
                    {
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:141:69: ( Comment )
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:141:70: Comment
                    {
                    Comment1=(Token)match(input,Comment,FOLLOW_Comment_in_line80); 
                     comment = (Comment1!=null?Comment1.getText():null).substring(1).trim(); 

                    }


                    }
                    break;

            }

             writeLine (); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "line"

    public static class labelDeclaration_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "labelDeclaration"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:143:1: labelDeclaration : ( Identifier | operand ) ':' ;
    public final AsmSM213Parser.labelDeclaration_return labelDeclaration() throws RecognitionException {
        AsmSM213Parser.labelDeclaration_return retval = new AsmSM213Parser.labelDeclaration_return();
        retval.start = input.LT(1);

        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:144:2: ( ( Identifier | operand ) ':' )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:144:4: ( Identifier | operand ) ':'
            {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:144:4: ( Identifier | operand )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==Identifier) ) {
                alt5=1;
            }
            else if ( ((LA5_0>=16 && LA5_0<=34)) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:144:5: Identifier
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_labelDeclaration97); 

                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:144:18: operand
                    {
                    pushFollow(FOLLOW_operand_in_labelDeclaration101);
                    operand();

                    state._fsp--;


                    }
                    break;

            }

            match(input,15,FOLLOW_15_in_labelDeclaration104); 
             label = input.toString(retval.start,input.LT(-1)).substring (0, input.toString(retval.start,input.LT(-1)).length ()-1); 

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "labelDeclaration"

    public static class label_return extends ParserRuleReturnScope {
        public int value;
    };

    // $ANTLR start "label"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:145:1: label returns [int value] : ( Identifier | operand ) ;
    public final AsmSM213Parser.label_return label() throws RecognitionException {
        AsmSM213Parser.label_return retval = new AsmSM213Parser.label_return();
        retval.start = input.LT(1);

        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:146:2: ( ( Identifier | operand ) )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:146:4: ( Identifier | operand )
            {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:146:4: ( Identifier | operand )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==Identifier) ) {
                alt6=1;
            }
            else if ( ((LA6_0>=16 && LA6_0<=34)) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:146:5: Identifier
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_label119); 

                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:146:18: operand
                    {
                    pushFollow(FOLLOW_operand_in_label123);
                    operand();

                    state._fsp--;


                    }
                    break;

            }

             retval.value = getLabelValue (input.toString(retval.start,input.LT(-1))); 

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "label"


    // $ANTLR start "instruction"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:149:1: instruction : ( load | store | aluOne | aluTwo | shift | branch | jump | halt | nop ) ;
    public final void instruction() throws RecognitionException {
        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:150:2: ( ( load | store | aluOne | aluTwo | shift | branch | jump | halt | nop ) )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:150:4: ( load | store | aluOne | aluTwo | shift | branch | jump | halt | nop )
            {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:150:4: ( load | store | aluOne | aluTwo | shift | branch | jump | halt | nop )
            int alt7=9;
            switch ( input.LA(1) ) {
            case 16:
                {
                alt7=1;
                }
                break;
            case 17:
                {
                alt7=2;
                }
                break;
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                {
                alt7=3;
                }
                break;
            case 24:
            case 25:
            case 26:
                {
                alt7=4;
                }
                break;
            case 27:
            case 28:
                {
                alt7=5;
                }
                break;
            case 29:
            case 30:
            case 31:
                {
                alt7=6;
                }
                break;
            case 32:
                {
                alt7=7;
                }
                break;
            case 33:
                {
                alt7=8;
                }
                break;
            case 34:
                {
                alt7=9;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:150:5: load
                    {
                    pushFollow(FOLLOW_load_in_instruction138);
                    load();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:150:12: store
                    {
                    pushFollow(FOLLOW_store_in_instruction142);
                    store();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:150:20: aluOne
                    {
                    pushFollow(FOLLOW_aluOne_in_instruction146);
                    aluOne();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:150:29: aluTwo
                    {
                    pushFollow(FOLLOW_aluTwo_in_instruction150);
                    aluTwo();

                    state._fsp--;


                    }
                    break;
                case 5 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:150:38: shift
                    {
                    pushFollow(FOLLOW_shift_in_instruction154);
                    shift();

                    state._fsp--;


                    }
                    break;
                case 6 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:150:46: branch
                    {
                    pushFollow(FOLLOW_branch_in_instruction158);
                    branch();

                    state._fsp--;


                    }
                    break;
                case 7 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:150:55: jump
                    {
                    pushFollow(FOLLOW_jump_in_instruction162);
                    jump();

                    state._fsp--;


                    }
                    break;
                case 8 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:150:62: halt
                    {
                    pushFollow(FOLLOW_halt_in_instruction166);
                    halt();

                    state._fsp--;


                    }
                    break;
                case 9 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:150:69: nop
                    {
                    pushFollow(FOLLOW_nop_in_instruction170);
                    nop();

                    state._fsp--;


                    }
                    break;

            }

            lineType = LineType.INSTRUCTION;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "instruction"


    // $ANTLR start "operand"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:151:1: operand : ( 'ld' | 'st' | 'not' | 'inc' | 'inca' | 'dec' | 'deca' | 'gpc' | 'mov' | 'add' | 'and' | 'shl' | 'shr' | 'br' | 'beq' | 'bgt' | 'j' | 'halt' | 'nop' ) ;
    public final void operand() throws RecognitionException {
        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:151:9: ( ( 'ld' | 'st' | 'not' | 'inc' | 'inca' | 'dec' | 'deca' | 'gpc' | 'mov' | 'add' | 'and' | 'shl' | 'shr' | 'br' | 'beq' | 'bgt' | 'j' | 'halt' | 'nop' ) )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:151:11: ( 'ld' | 'st' | 'not' | 'inc' | 'inca' | 'dec' | 'deca' | 'gpc' | 'mov' | 'add' | 'and' | 'shl' | 'shr' | 'br' | 'beq' | 'bgt' | 'j' | 'halt' | 'nop' )
            {
            if ( (input.LA(1)>=16 && input.LA(1)<=34) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "operand"


    // $ANTLR start "load"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:153:1: load : 'ld' ( ( literal ',' ld= register ) | ( baseOffset4 | index ) ',' d= register ) ;
    public final void load() throws RecognitionException {
        int ld = 0;

        int d = 0;

        int literal2 = 0;

        AsmSM213Parser.baseOffset4_return baseOffset43 = null;

        AsmSM213Parser.index_return index4 = null;


        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:153:7: ( 'ld' ( ( literal ',' ld= register ) | ( baseOffset4 | index ) ',' d= register ) )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:153:9: 'ld' ( ( literal ',' ld= register ) | ( baseOffset4 | index ) ',' d= register )
            {
            match(input,16,FOLLOW_16_in_load227); 
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:153:14: ( ( literal ',' ld= register ) | ( baseOffset4 | index ) ',' d= register )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==37) ) {
                alt9=1;
            }
            else if ( ((LA9_0>=Hex && LA9_0<=Decimal)||LA9_0==38) ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:153:16: ( literal ',' ld= register )
                    {
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:153:16: ( literal ',' ld= register )
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:153:17: literal ',' ld= register
                    {
                    pushFollow(FOLLOW_literal_in_load232);
                    literal2=literal();

                    state._fsp--;

                     opCode=0; op[2]=literal2; 
                    match(input,35,FOLLOW_35_in_load236); 
                    pushFollow(FOLLOW_register_in_load240);
                    ld=register();

                    state._fsp--;

                     op[0]=ld; 

                    }


                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:154:10: ( baseOffset4 | index ) ',' d= register
                    {
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:154:10: ( baseOffset4 | index )
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( ((LA8_0>=Hex && LA8_0<=Decimal)) ) {
                        alt8=1;
                    }
                    else if ( (LA8_0==38) ) {
                        int LA8_2 = input.LA(2);

                        if ( (LA8_2==Register) ) {
                            int LA8_3 = input.LA(3);

                            if ( (LA8_3==39) ) {
                                alt8=1;
                            }
                            else if ( (LA8_3==35) ) {
                                alt8=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 8, 3, input);

                                throw nvae;
                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 8, 2, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 8, 0, input);

                        throw nvae;
                    }
                    switch (alt8) {
                        case 1 :
                            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:154:11: baseOffset4
                            {
                            pushFollow(FOLLOW_baseOffset4_in_load258);
                            baseOffset43=baseOffset4();

                            state._fsp--;

                             opCode=1; op[0]=(baseOffset43!=null?baseOffset43.offset:0); op[1]=(baseOffset43!=null?baseOffset43.base:0); 

                            }
                            break;
                        case 2 :
                            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:155:10: index
                            {
                            pushFollow(FOLLOW_index_in_load274);
                            index4=index();

                            state._fsp--;

                             opCode=2; op[0]=(index4!=null?index4.base:0); op[1]=(index4!=null?index4.index:0); 

                            }
                            break;

                    }

                    match(input,35,FOLLOW_35_in_load288); 
                    pushFollow(FOLLOW_register_in_load292);
                    d=register();

                    state._fsp--;

                     op[2] = d; 

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "load"


    // $ANTLR start "store"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:157:1: store : 'st' s= register ',' ( baseOffset4 | index ) ;
    public final void store() throws RecognitionException {
        int s = 0;

        AsmSM213Parser.baseOffset4_return baseOffset45 = null;

        AsmSM213Parser.index_return index6 = null;


        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:157:7: ( 'st' s= register ',' ( baseOffset4 | index ) )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:157:9: 'st' s= register ',' ( baseOffset4 | index )
            {
            match(input,17,FOLLOW_17_in_store302); 
            pushFollow(FOLLOW_register_in_store306);
            s=register();

            state._fsp--;

             op[0]=s; 
            match(input,35,FOLLOW_35_in_store310); 
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:158:4: ( baseOffset4 | index )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( ((LA10_0>=Hex && LA10_0<=Decimal)) ) {
                alt10=1;
            }
            else if ( (LA10_0==38) ) {
                int LA10_2 = input.LA(2);

                if ( (LA10_2==Register) ) {
                    int LA10_3 = input.LA(3);

                    if ( (LA10_3==39) ) {
                        alt10=1;
                    }
                    else if ( (LA10_3==35) ) {
                        alt10=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 3, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:158:6: baseOffset4
                    {
                    pushFollow(FOLLOW_baseOffset4_in_store318);
                    baseOffset45=baseOffset4();

                    state._fsp--;

                     opCode=3; op[1]=(baseOffset45!=null?baseOffset45.offset:0); op[2]=(baseOffset45!=null?baseOffset45.base:0); 

                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:159:4: index
                    {
                    pushFollow(FOLLOW_index_in_store328);
                    index6=index();

                    state._fsp--;

                     opCode=4; op[1]=(index6!=null?index6.base:0); op[2]=(index6!=null?index6.index:0); 

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "store"


    // $ANTLR start "aluOne"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:160:1: aluOne : ( 'not' | 'inc' | 'inca' | 'dec' | 'deca' | 'gpc' ) register ;
    public final void aluOne() throws RecognitionException {
        int register7 = 0;


        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:160:8: ( ( 'not' | 'inc' | 'inca' | 'dec' | 'deca' | 'gpc' ) register )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:160:10: ( 'not' | 'inc' | 'inca' | 'dec' | 'deca' | 'gpc' ) register
            {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:160:10: ( 'not' | 'inc' | 'inca' | 'dec' | 'deca' | 'gpc' )
            int alt11=6;
            switch ( input.LA(1) ) {
            case 18:
                {
                alt11=1;
                }
                break;
            case 19:
                {
                alt11=2;
                }
                break;
            case 20:
                {
                alt11=3;
                }
                break;
            case 21:
                {
                alt11=4;
                }
                break;
            case 22:
                {
                alt11=5;
                }
                break;
            case 23:
                {
                alt11=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:160:12: 'not'
                    {
                    match(input,18,FOLLOW_18_in_aluOne341); 
                    opCode=0x67;

                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:160:35: 'inc'
                    {
                    match(input,19,FOLLOW_19_in_aluOne347); 
                    opCode=0x63;

                    }
                    break;
                case 3 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:160:58: 'inca'
                    {
                    match(input,20,FOLLOW_20_in_aluOne353); 
                    opCode=0x64;

                    }
                    break;
                case 4 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:160:82: 'dec'
                    {
                    match(input,21,FOLLOW_21_in_aluOne359); 
                    opCode=0x65;

                    }
                    break;
                case 5 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:160:105: 'deca'
                    {
                    match(input,22,FOLLOW_22_in_aluOne365); 
                    opCode=0x66;

                    }
                    break;
                case 6 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:160:129: 'gpc'
                    {
                    match(input,23,FOLLOW_23_in_aluOne371); 
                    opCode=0x6f;

                    }
                    break;

            }

            pushFollow(FOLLOW_register_in_aluOne377);
            register7=register();

            state._fsp--;

            op[1] = register7;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "aluOne"


    // $ANTLR start "aluTwo"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:161:1: aluTwo : ( 'mov' | 'add' | 'and' ) s= register ',' d= register ;
    public final void aluTwo() throws RecognitionException {
        int s = 0;

        int d = 0;


        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:161:8: ( ( 'mov' | 'add' | 'and' ) s= register ',' d= register )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:161:10: ( 'mov' | 'add' | 'and' ) s= register ',' d= register
            {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:161:10: ( 'mov' | 'add' | 'and' )
            int alt12=3;
            switch ( input.LA(1) ) {
            case 24:
                {
                alt12=1;
                }
                break;
            case 25:
                {
                alt12=2;
                }
                break;
            case 26:
                {
                alt12=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:161:12: 'mov'
                    {
                    match(input,24,FOLLOW_24_in_aluTwo388); 
                    opCode=0x60;

                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:161:35: 'add'
                    {
                    match(input,25,FOLLOW_25_in_aluTwo394); 
                    opCode=0x61;

                    }
                    break;
                case 3 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:161:58: 'and'
                    {
                    match(input,26,FOLLOW_26_in_aluTwo400); 
                    opCode=0x62;

                    }
                    break;

            }

            pushFollow(FOLLOW_register_in_aluTwo408);
            s=register();

            state._fsp--;

            op[0]=s;
            match(input,35,FOLLOW_35_in_aluTwo412); 
            pushFollow(FOLLOW_register_in_aluTwo416);
            d=register();

            state._fsp--;

            op[1]=d;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "aluTwo"


    // $ANTLR start "shift"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:162:1: shift : ( ( 'shl' | 'shr' ) literal ',' register ) ;
    public final void shift() throws RecognitionException {
        int register8 = 0;

        int literal9 = 0;


        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:162:7: ( ( ( 'shl' | 'shr' ) literal ',' register ) )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:162:9: ( ( 'shl' | 'shr' ) literal ',' register )
            {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:162:9: ( ( 'shl' | 'shr' ) literal ',' register )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:162:11: ( 'shl' | 'shr' ) literal ',' register
            {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:162:11: ( 'shl' | 'shr' )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==27) ) {
                alt13=1;
            }
            else if ( (LA13_0==28) ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:162:13: 'shl'
                    {
                    match(input,27,FOLLOW_27_in_shift429); 
                    op[1]=1;

                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:162:32: 'shr'
                    {
                    match(input,28,FOLLOW_28_in_shift435); 
                    op[1]=-1;

                    }
                    break;

            }

            pushFollow(FOLLOW_literal_in_shift441);
            literal9=literal();

            state._fsp--;

            match(input,35,FOLLOW_35_in_shift443); 
            pushFollow(FOLLOW_register_in_shift445);
            register8=register();

            state._fsp--;


            }

            opCode=0x7; op[0]=register8; op[1]*=literal9;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "shift"


    // $ANTLR start "branch"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:1: branch : ( ( 'br' ( label | number ) ) | ( ( 'beq' | 'bgt' ) register ',' ( label | number ) ) );
    public final void branch() throws RecognitionException {
        AsmSM213Parser.label_return label10 = null;

        int number11 = 0;

        AsmSM213Parser.label_return label12 = null;

        int number13 = 0;

        int register14 = 0;


        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:9: ( ( 'br' ( label | number ) ) | ( ( 'beq' | 'bgt' ) register ',' ( label | number ) ) )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==29) ) {
                alt17=1;
            }
            else if ( ((LA17_0>=30 && LA17_0<=31)) ) {
                alt17=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:11: ( 'br' ( label | number ) )
                    {
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:11: ( 'br' ( label | number ) )
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:13: 'br' ( label | number )
                    {
                    match(input,29,FOLLOW_29_in_branch459); 
                    opCode=0x8;
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:32: ( label | number )
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==Identifier||(LA14_0>=16 && LA14_0<=34)) ) {
                        alt14=1;
                    }
                    else if ( ((LA14_0>=Hex && LA14_0<=Decimal)) ) {
                        alt14=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 14, 0, input);

                        throw nvae;
                    }
                    switch (alt14) {
                        case 1 :
                            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:33: label
                            {
                            pushFollow(FOLLOW_label_in_branch464);
                            label10=label();

                            state._fsp--;

                            op[1]=(label10!=null?label10.value:0);

                            }
                            break;
                        case 2 :
                            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:63: number
                            {
                            pushFollow(FOLLOW_number_in_branch470);
                            number11=number();

                            state._fsp--;

                            op[1]=number11;

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:97: ( ( 'beq' | 'bgt' ) register ',' ( label | number ) )
                    {
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:97: ( ( 'beq' | 'bgt' ) register ',' ( label | number ) )
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:99: ( 'beq' | 'bgt' ) register ',' ( label | number )
                    {
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:99: ( 'beq' | 'bgt' )
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==30) ) {
                        alt15=1;
                    }
                    else if ( (LA15_0==31) ) {
                        alt15=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 15, 0, input);

                        throw nvae;
                    }
                    switch (alt15) {
                        case 1 :
                            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:101: 'beq'
                            {
                            match(input,30,FOLLOW_30_in_branch482); 
                            opCode=0x9;

                            }
                            break;
                        case 2 :
                            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:122: 'bgt'
                            {
                            match(input,31,FOLLOW_31_in_branch487); 
                            opCode=0xa;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_register_in_branch492);
                    register14=register();

                    state._fsp--;

                    match(input,35,FOLLOW_35_in_branch494); 
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:156: ( label | number )
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==Identifier||(LA16_0>=16 && LA16_0<=34)) ) {
                        alt16=1;
                    }
                    else if ( ((LA16_0>=Hex && LA16_0<=Decimal)) ) {
                        alt16=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 16, 0, input);

                        throw nvae;
                    }
                    switch (alt16) {
                        case 1 :
                            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:157: label
                            {
                            pushFollow(FOLLOW_label_in_branch497);
                            label12=label();

                            state._fsp--;

                            op[1]=(label12!=null?label12.value:0);

                            }
                            break;
                        case 2 :
                            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:163:187: number
                            {
                            pushFollow(FOLLOW_number_in_branch503);
                            number13=number();

                            state._fsp--;

                            op[1]=number13;

                            }
                            break;

                    }

                    op[0]=register14;

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "branch"


    // $ANTLR start "jump"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:164:1: jump : 'j' ( label | b1= baseOffset2 | ( '*' b2= baseOffset4 ) | ( '*' index ) ) ;
    public final void jump() throws RecognitionException {
        AsmSM213Parser.baseOffset2_return b1 = null;

        AsmSM213Parser.baseOffset4_return b2 = null;

        AsmSM213Parser.label_return label15 = null;

        AsmSM213Parser.index_return index16 = null;


        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:164:6: ( 'j' ( label | b1= baseOffset2 | ( '*' b2= baseOffset4 ) | ( '*' index ) ) )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:164:8: 'j' ( label | b1= baseOffset2 | ( '*' b2= baseOffset4 ) | ( '*' index ) )
            {
            match(input,32,FOLLOW_32_in_jump516); 
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:164:12: ( label | b1= baseOffset2 | ( '*' b2= baseOffset4 ) | ( '*' index ) )
            int alt18=4;
            switch ( input.LA(1) ) {
            case Identifier:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                {
                alt18=1;
                }
                break;
            case Hex:
            case Decimal:
            case 38:
                {
                alt18=2;
                }
                break;
            case 36:
                {
                int LA18_3 = input.LA(2);

                if ( ((LA18_3>=Hex && LA18_3<=Decimal)) ) {
                    alt18=3;
                }
                else if ( (LA18_3==38) ) {
                    int LA18_5 = input.LA(3);

                    if ( (LA18_5==Register) ) {
                        int LA18_6 = input.LA(4);

                        if ( (LA18_6==35) ) {
                            alt18=4;
                        }
                        else if ( (LA18_6==39) ) {
                            alt18=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 18, 6, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 18, 5, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:164:14: label
                    {
                    pushFollow(FOLLOW_label_in_jump520);
                    label15=label();

                    state._fsp--;

                    opCode=0xb; op[1]=(label15!=null?label15.value:0);

                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:165:9: b1= baseOffset2
                    {
                    pushFollow(FOLLOW_baseOffset2_in_jump537);
                    b1=baseOffset2();

                    state._fsp--;

                    opCode=0xc; op[0]=(b1!=null?b1.base:0); op[1]=(b1!=null?b1.offset:0);

                    }
                    break;
                case 3 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:166:9: ( '*' b2= baseOffset4 )
                    {
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:166:9: ( '*' b2= baseOffset4 )
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:166:11: '*' b2= baseOffset4
                    {
                    match(input,36,FOLLOW_36_in_jump554); 
                    pushFollow(FOLLOW_baseOffset4_in_jump558);
                    b2=baseOffset4();

                    state._fsp--;

                    opCode=0xd; op[0]=(b2!=null?b2.base:0); op[1]=(b2!=null?b2.offset:0);

                    }


                    }
                    break;
                case 4 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:166:81: ( '*' index )
                    {
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:166:81: ( '*' index )
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:166:82: '*' index
                    {
                    match(input,36,FOLLOW_36_in_jump566); 
                    pushFollow(FOLLOW_index_in_jump568);
                    index16=index();

                    state._fsp--;

                    opCode=0xe; op[0]=(index16!=null?index16.base:0); op[1]=(index16!=null?index16.index:0);

                    }


                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "jump"


    // $ANTLR start "halt"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:167:1: halt : 'halt' ;
    public final void halt() throws RecognitionException {
        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:167:6: ( 'halt' )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:167:8: 'halt'
            {
            match(input,33,FOLLOW_33_in_halt579); 
            opCode=0xf0;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "halt"


    // $ANTLR start "nop"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:168:1: nop : 'nop' ;
    public final void nop() throws RecognitionException {
        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:168:5: ( 'nop' )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:168:7: 'nop'
            {
            match(input,34,FOLLOW_34_in_nop588); 
            opCode=0xff;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "nop"


    // $ANTLR start "literal"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:171:1: literal returns [int value] : '$' ( number | label ) ;
    public final int literal() throws RecognitionException {
        int value = 0;

        int number17 = 0;

        AsmSM213Parser.label_return label18 = null;


        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:172:2: ( '$' ( number | label ) )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:172:4: '$' ( number | label )
            {
            match(input,37,FOLLOW_37_in_literal604); 
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:172:8: ( number | label )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( ((LA19_0>=Hex && LA19_0<=Decimal)) ) {
                alt19=1;
            }
            else if ( (LA19_0==Identifier||(LA19_0>=16 && LA19_0<=34)) ) {
                alt19=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:172:9: number
                    {
                    pushFollow(FOLLOW_number_in_literal607);
                    number17=number();

                    state._fsp--;

                     value = number17; 

                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:172:46: label
                    {
                    pushFollow(FOLLOW_label_in_literal613);
                    label18=label();

                    state._fsp--;

                     value = (label18!=null?label18.value:0); 

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "literal"

    public static class baseOffset2_return extends ParserRuleReturnScope {
        public int offset;
        public int base;
    };

    // $ANTLR start "baseOffset2"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:173:1: baseOffset2 returns [int offset, int base] : ( number )? '(' register ')' ;
    public final AsmSM213Parser.baseOffset2_return baseOffset2() throws RecognitionException {
        AsmSM213Parser.baseOffset2_return retval = new AsmSM213Parser.baseOffset2_return();
        retval.start = input.LT(1);

        int number19 = 0;

        int register20 = 0;


        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:174:2: ( ( number )? '(' register ')' )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:174:4: ( number )? '(' register ')'
            {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:174:4: ( number )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( ((LA20_0>=Hex && LA20_0<=Decimal)) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:174:4: number
                    {
                    pushFollow(FOLLOW_number_in_baseOffset2628);
                    number19=number();

                    state._fsp--;


                    }
                    break;

            }

            match(input,38,FOLLOW_38_in_baseOffset2631); 
            pushFollow(FOLLOW_register_in_baseOffset2633);
            register20=register();

            state._fsp--;

            match(input,39,FOLLOW_39_in_baseOffset2635); 
             retval.offset =number19; retval.base =register20; if ((retval.offset%2)!=0) emitErrorMessage ("Offset must be a multiple of 2");

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "baseOffset2"

    public static class baseOffset4_return extends ParserRuleReturnScope {
        public int offset;
        public int base;
    };

    // $ANTLR start "baseOffset4"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:175:1: baseOffset4 returns [int offset, int base] : ( number )? '(' register ')' ;
    public final AsmSM213Parser.baseOffset4_return baseOffset4() throws RecognitionException {
        AsmSM213Parser.baseOffset4_return retval = new AsmSM213Parser.baseOffset4_return();
        retval.start = input.LT(1);

        int number21 = 0;

        int register22 = 0;


        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:176:2: ( ( number )? '(' register ')' )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:176:4: ( number )? '(' register ')'
            {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:176:4: ( number )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( ((LA21_0>=Hex && LA21_0<=Decimal)) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:176:4: number
                    {
                    pushFollow(FOLLOW_number_in_baseOffset4649);
                    number21=number();

                    state._fsp--;


                    }
                    break;

            }

            match(input,38,FOLLOW_38_in_baseOffset4652); 
            pushFollow(FOLLOW_register_in_baseOffset4654);
            register22=register();

            state._fsp--;

            match(input,39,FOLLOW_39_in_baseOffset4656); 
             retval.offset =number21; retval.base =register22; if ((retval.offset%4)!=0) emitErrorMessage ("Offset must be a multiple of 4");

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "baseOffset4"

    public static class index_return extends ParserRuleReturnScope {
        public int base;
        public int index;
    };

    // $ANTLR start "index"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:177:1: index returns [int base, int index] : '(' b= register ',' i= register ',' decimal ')' ;
    public final AsmSM213Parser.index_return index() throws RecognitionException {
        AsmSM213Parser.index_return retval = new AsmSM213Parser.index_return();
        retval.start = input.LT(1);

        int b = 0;

        int i = 0;

        int decimal23 = 0;


        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:178:2: ( '(' b= register ',' i= register ',' decimal ')' )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:178:4: '(' b= register ',' i= register ',' decimal ')'
            {
            match(input,38,FOLLOW_38_in_index670); 
            pushFollow(FOLLOW_register_in_index674);
            b=register();

            state._fsp--;

            match(input,35,FOLLOW_35_in_index676); 
            pushFollow(FOLLOW_register_in_index680);
            i=register();

            state._fsp--;

            match(input,35,FOLLOW_35_in_index682); 
            pushFollow(FOLLOW_decimal_in_index684);
            decimal23=decimal();

            state._fsp--;

            match(input,39,FOLLOW_39_in_index686); 
             retval.base = b; retval.index = i; if (decimal23!=4) emitErrorMessage ("In index, scale must be 4"); 

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "index"


    // $ANTLR start "register"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:179:1: register returns [int value] : Register ;
    public final int register() throws RecognitionException {
        int value = 0;

        Token Register24=null;

        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:180:2: ( Register )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:180:4: Register
            {
            Register24=(Token)match(input,Register,FOLLOW_Register_in_register700); 
             value = Integer.parseInt ((Register24!=null?Register24.getText():null).substring(1)); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "register"


    // $ANTLR start "number"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:181:1: number returns [int value] : ( decimal | hex );
    public final int number() throws RecognitionException {
        int value = 0;

        int decimal25 = 0;

        int hex26 = 0;


        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:182:3: ( decimal | hex )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==Decimal) ) {
                alt22=1;
            }
            else if ( (LA22_0==Hex) ) {
                alt22=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:182:5: decimal
                    {
                    pushFollow(FOLLOW_decimal_in_number715);
                    decimal25=decimal();

                    state._fsp--;

                     value =decimal25; 

                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:182:42: hex
                    {
                    pushFollow(FOLLOW_hex_in_number721);
                    hex26=hex();

                    state._fsp--;

                     value =hex26; 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "number"


    // $ANTLR start "hex"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:183:1: hex returns [int value] : Hex ;
    public final int hex() throws RecognitionException {
        int value = 0;

        Token Hex27=null;

        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:184:2: ( Hex )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:184:4: Hex
            {
            Hex27=(Token)match(input,Hex,FOLLOW_Hex_in_hex736); 
             value =(int)(Long.parseLong((Hex27!=null?Hex27.getText():null).substring(2),16)); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "hex"


    // $ANTLR start "decimal"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:186:1: decimal returns [int value] : Decimal ;
    public final int decimal() throws RecognitionException {
        int value = 0;

        Token Decimal28=null;

        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:187:3: ( Decimal )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:187:5: Decimal
            {
            Decimal28=(Token)match(input,Decimal,FOLLOW_Decimal_in_decimal755); 
             value =(int)(Long.parseLong((Decimal28!=null?Decimal28.getText():null))); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end "decimal"


    // $ANTLR start "directive"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:189:1: directive : ( address | data );
    public final void directive() throws RecognitionException {
        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:190:2: ( address | data )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( ((LA23_0>=40 && LA23_0<=41)) ) {
                alt23=1;
            }
            else if ( ((LA23_0>=42 && LA23_0<=43)) ) {
                alt23=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:190:4: address
                    {
                    pushFollow(FOLLOW_address_in_directive772);
                    address();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:190:14: data
                    {
                    pushFollow(FOLLOW_data_in_directive776);
                    data();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "directive"


    // $ANTLR start "address"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:191:1: address : ( ( '.address' | '.pos' ) hex ) ;
    public final void address() throws RecognitionException {
        int hex29 = 0;


        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:191:9: ( ( ( '.address' | '.pos' ) hex ) )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:191:11: ( ( '.address' | '.pos' ) hex )
            {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:191:11: ( ( '.address' | '.pos' ) hex )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:191:12: ( '.address' | '.pos' ) hex
            {
            if ( (input.LA(1)>=40 && input.LA(1)<=41) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            pushFollow(FOLLOW_hex_in_address792);
            hex29=hex();

            state._fsp--;

             pc = hex29; 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "address"


    // $ANTLR start "data"
    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:192:1: data : ( ( '.long' | '.data' ) (value= number | label ) ( ',' count= number )? ) ;
    public final void data() throws RecognitionException {
        int value = 0;

        int count = 0;

        AsmSM213Parser.label_return label30 = null;


        try {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:192:6: ( ( ( '.long' | '.data' ) (value= number | label ) ( ',' count= number )? ) )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:192:8: ( ( '.long' | '.data' ) (value= number | label ) ( ',' count= number )? )
            {
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:192:8: ( ( '.long' | '.data' ) (value= number | label ) ( ',' count= number )? )
            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:192:9: ( '.long' | '.data' ) (value= number | label ) ( ',' count= number )?
            {
            if ( (input.LA(1)>=42 && input.LA(1)<=43) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:192:29: (value= number | label )
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( ((LA24_0>=Hex && LA24_0<=Decimal)) ) {
                alt24=1;
            }
            else if ( (LA24_0==Identifier||(LA24_0>=16 && LA24_0<=34)) ) {
                alt24=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:192:30: value= number
                    {
                    pushFollow(FOLLOW_number_in_data814);
                    value=number();

                    state._fsp--;

                    dataValue=value;

                    }
                    break;
                case 2 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:192:71: label
                    {
                    pushFollow(FOLLOW_label_in_data820);
                    label30=label();

                    state._fsp--;

                    dataValue=(label30!=null?label30.value:0);

                    }
                    break;

            }

            // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:192:104: ( ',' count= number )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==35) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /Users/feeley/Documents/Work/Courses/SimpleMachine/project/trunk/grammars/AsmSM213.g:192:105: ',' count= number
                    {
                    match(input,35,FOLLOW_35_in_data826); 
                    pushFollow(FOLLOW_number_in_data830);
                    count=number();

                    state._fsp--;


                    }
                    break;

            }


            }

             lineType = LineType.DATA; dataCount= count>0? count : 1; 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "data"

    // Delegated rules


    protected DFA2 dfa2 = new DFA2(this);
    static final String DFA2_eotS =
        "\26\uffff";
    static final String DFA2_eofS =
        "\26\uffff";
    static final String DFA2_minS =
        "\1\4\1\uffff\1\10\12\7\2\17\1\6\2\7\1\6\2\4\1\uffff";
    static final String DFA2_maxS =
        "\1\53\1\uffff\1\46\12\17\2\45\1\42\2\17\1\46\2\17\1\uffff";
    static final String DFA2_acceptS =
        "\1\uffff\1\1\23\uffff\1\2";
    static final String DFA2_specialS =
        "\26\uffff}>";
    static final String[] DFA2_transitionS = {
            "\2\25\1\1\11\uffff\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1"+
            "\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\5\uffff\4\25",
            "",
            "\2\25\5\uffff\1\1\25\uffff\2\25",
            "\1\25\7\uffff\1\1",
            "\1\25\7\uffff\1\1",
            "\1\25\7\uffff\1\1",
            "\1\25\7\uffff\1\1",
            "\1\25\7\uffff\1\1",
            "\1\25\7\uffff\1\1",
            "\1\25\7\uffff\1\1",
            "\1\25\7\uffff\1\1",
            "\1\25\7\uffff\1\1",
            "\1\25\7\uffff\1\1",
            "\1\1\25\uffff\1\25",
            "\1\1\25\uffff\1\25",
            "\1\25\1\uffff\2\25\5\uffff\1\1\23\25",
            "\1\25\7\uffff\1\1",
            "\1\25\7\uffff\1\1",
            "\1\25\1\uffff\2\25\5\uffff\1\1\23\25\1\uffff\1\25\1\uffff\1"+
            "\25",
            "\2\25\11\uffff\1\1",
            "\2\25\11\uffff\1\1",
            ""
    };

    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    static final short[][] DFA2_transition;

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
        }
    }

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA2_eot;
            this.eof = DFA2_eof;
            this.min = DFA2_min;
            this.max = DFA2_max;
            this.accept = DFA2_accept;
            this.special = DFA2_special;
            this.transition = DFA2_transition;
        }
        public String getDescription() {
            return "141:8: ( labelDeclaration )?";
        }
    }
 

    public static final BitSet FOLLOW_line_in_program46 = new BitSet(new long[]{0x00000F07FFFF0070L});
    public static final BitSet FOLLOW_EOF_in_program49 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_labelDeclaration_in_line58 = new BitSet(new long[]{0x00000F07FFFF0030L});
    public static final BitSet FOLLOW_instruction_in_line64 = new BitSet(new long[]{0x0000000000000030L});
    public static final BitSet FOLLOW_directive_in_line68 = new BitSet(new long[]{0x0000000000000030L});
    public static final BitSet FOLLOW_NewLine_in_line75 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Comment_in_line80 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_labelDeclaration97 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_operand_in_labelDeclaration101 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_15_in_labelDeclaration104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_label119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operand_in_label123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_load_in_instruction138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_store_in_instruction142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aluOne_in_instruction146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aluTwo_in_instruction150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shift_in_instruction154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_branch_in_instruction158 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_jump_in_instruction162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_halt_in_instruction166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nop_in_instruction170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_operand180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_load227 = new BitSet(new long[]{0x0000006000000300L});
    public static final BitSet FOLLOW_literal_in_load232 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_load236 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_register_in_load240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_baseOffset4_in_load258 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_index_in_load274 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_load288 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_register_in_load292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_store302 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_register_in_store306 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_store310 = new BitSet(new long[]{0x0000006000000300L});
    public static final BitSet FOLLOW_baseOffset4_in_store318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_index_in_store328 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_aluOne341 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_19_in_aluOne347 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_20_in_aluOne353 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_21_in_aluOne359 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_22_in_aluOne365 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_23_in_aluOne371 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_register_in_aluOne377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_aluTwo388 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_25_in_aluTwo394 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_26_in_aluTwo400 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_register_in_aluTwo408 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_aluTwo412 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_register_in_aluTwo416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_shift429 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_28_in_shift435 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_literal_in_shift441 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_shift443 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_register_in_shift445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_branch459 = new BitSet(new long[]{0x00000007FFFF0340L});
    public static final BitSet FOLLOW_label_in_branch464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_number_in_branch470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_branch482 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_31_in_branch487 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_register_in_branch492 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_branch494 = new BitSet(new long[]{0x00000007FFFF0340L});
    public static final BitSet FOLLOW_label_in_branch497 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_number_in_branch503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_jump516 = new BitSet(new long[]{0x00000057FFFF0340L});
    public static final BitSet FOLLOW_label_in_jump520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_baseOffset2_in_jump537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_jump554 = new BitSet(new long[]{0x0000004000000300L});
    public static final BitSet FOLLOW_baseOffset4_in_jump558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_jump566 = new BitSet(new long[]{0x0000006000000300L});
    public static final BitSet FOLLOW_index_in_jump568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_halt579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_nop588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_literal604 = new BitSet(new long[]{0x00000007FFFF0340L});
    public static final BitSet FOLLOW_number_in_literal607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_label_in_literal613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_number_in_baseOffset2628 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_baseOffset2631 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_register_in_baseOffset2633 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_baseOffset2635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_number_in_baseOffset4649 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_baseOffset4652 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_register_in_baseOffset4654 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_baseOffset4656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_index670 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_register_in_index674 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_index676 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_register_in_index680 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_index682 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_decimal_in_index684 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_index686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Register_in_register700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_decimal_in_number715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_hex_in_number721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Hex_in_hex736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Decimal_in_decimal755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_address_in_directive772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_data_in_directive776 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_address784 = new BitSet(new long[]{0x0000000000000300L});
    public static final BitSet FOLLOW_hex_in_address792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_data803 = new BitSet(new long[]{0x00000007FFFF0340L});
    public static final BitSet FOLLOW_number_in_data814 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_label_in_data820 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_35_in_data826 = new BitSet(new long[]{0x0000000000000300L});
    public static final BitSet FOLLOW_number_in_data830 = new BitSet(new long[]{0x0000000000000002L});

}