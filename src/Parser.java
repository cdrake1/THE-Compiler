/*
    Parser file
    Takes a stream of tokens as an input and creates and Concrete Syntax Tree (Parse tree)
    "Takes tokens and groups them into phrases according to the syntax specification"
    Uses recursive descent, LL1, and top down parsing
*/

//import arraylist, list, arrays
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Collin Drakes Parser
public class Parser{
    ArrayList<Token> tokenStream;   //the current token stream
    Token currentToken; //the current token we are on
    int tokenStreamIndex; //keeps track of current position within the token stream
    int parseErrors; //counts the number of parse errors
    int parseWarnings;   //counts the number of parse warnings

    //expected lists of tokens
    List<String> expectedType;
    List<String> expectedChar;
    List<String> expectedDigit;
    List<String> expectedBoolOp;
    List<String> expectedBoolVal;

    //creates a parser and initializes all variables. We are prepared to start parsing!
    public Parser(ArrayList<Token> programTokenStream){
        this.tokenStream = programTokenStream;
        this.tokenStreamIndex = 0;
        this.parseErrors = 0;
        this.parseWarnings = 0;

        //initialize expected lists
        expectedType = Arrays.asList("string", "int", "boolean");
        expectedChar = new ArrayList<>();
        for(char c = 'a'; c <= 'z'; c++){
            expectedChar.add(String.valueOf(c));
        }
        expectedDigit = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        expectedBoolOp = Arrays.asList("==", "!=");
        expectedBoolVal = Arrays.asList("true", "false");
    }

    public void test(){
        for(int i = 0; i < tokenStream.size(); i++){
            System.out.println(tokenStream.get(i).tokenType);
            System.out.println(tokenStream.get(i).lexeme);
        }
        
        

    }

}