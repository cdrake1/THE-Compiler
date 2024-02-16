/*
    Parser file
    Takes a stream of tokens as an input and creates and Concrete Syntax Tree (Parse tree)
    "Takes tokens and groups them into phrases according to the syntax specification"
    Uses recursive descent
*/

//import arraylist
import java.util.ArrayList;

//Collin Drakes Parser
public class Parser{
    ArrayList<Token> tokenStream;   //the current token stream
    int tokenStreamIndex; //keeps track of current position within the token stream
    int parseError; //counts the number of parse errors
    int parseWarnings;   //counts the number of parse warnings

    //creates a parser and initializes all variables. We are prepared to start parsing!
    public Parser(ArrayList<Token> programTokenStream){
        this.tokenStream = programTokenStream;
        this.tokenStreamIndex = 0;
        this.parseError = 0;
        this.parseWarnings = 0;
    }

    //outputs the results of the parser. Also outputs warnings and errors...
    private void parserLog(String output){
        System.out.println("PARSER - " + output);
    }

    //returns the next token in the token stream
    public Token getNextToken(){
        return tokenStream.get(tokenStreamIndex);
    }
    public Token getCurrentToken(){}    //not sure if needed


    ////programs and match below

    //parse program
    public void parseProgram(){
        parserLog("Parsing program");
        parseBlock();
        match(); //passes in expected token
    }

    //parse block
    private void parseBlock(){
        match();
        parseStatementList();
        match();
    }

    //parse statement list
    private void parseStatementList(){
        match();
        if(){
            parseStatement();
            parseStatementList();
        }
        else{
            
        }
    }

    //parse statement
    private void parseStatement(){
        match();
    }

    //parse print statement
    private void parsePrintStatement(){
        match();
    }

    //parse assignment statement
    private void parseAssignmentStatement(){
        match();
    }

    //parse variable declaration
    private void parseVarDecl(){
        match();
    }

    //parse while statement
    private void parseWhileStatement(){
        match();
    }

    //parse if statement
    private void parseIfStatement(){
        match();
    }

    //parse expression
    private void parseExpr(){
        match();
    }

    //parse int expression
    private void parseIntExpr(){
        match();
    }

    //parse string expression
    private void parseStringExpr(){
        match();
    }

    //parse boolean expression
    private void parseBooleanExpr(){
        match();
    }

    //parse ID
    private void parseID(){
        match();
    }

    //parse charlist: char, space, Empty statement
    private void parseCharList(){
        //switch or if to check if char space or empty
        match();
    }

    //parse types: int, string, bool
    private void parseType(){
        match();
    }

    //parse chars a-z
    private void parseChar(){
        match();
    }

    //parse whitespace
    private void parseSpace(){
        match();
    }

    //parse digits 0-9
    private void parseDigit(){
        match();
    }

    //parse bool operators
    private void parseBoolOp(){
        match();
    }

    //parse bool values
    private void parseBoolVal(){
        match();
    }

    //parse add op (+)
    private void parseIntOp(){
        match();
    }

    //matches and consumes tokens
    private void match(Token expectedToken){}
}