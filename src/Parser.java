/*
    Parser file
    Takes a stream of tokens as an input and creates and Concrete Syntax Tree (Parse tree)
    "Takes tokens and groups them into phrases according to the syntax specification"
    Uses recursive descent
*/

//import necessary packages
import java.util.ArrayList;

//Collin Drakes Parser
public class Parser{
    int parseError; //counts the number of parse errors
    int parseWarnings;   //counts the number of parse warnings

    //creates a parser and initializes all variables. We are prepared to start parsing!
    public Parser(){
        this.parseError = 0;
        this.parseWarnings = 0;
    }

    //outputs the results of the parser. Also outputs warnings and errors...
    private void parserLog(String output){
        System.out.println("PARSER - " + output);
    }

    //calls the other functions to parse the tokenstream and build out the CST
    public void parseTokenStream(ArrayList<Token> programTokenStream){}

    //parse program
    private void parseProgram(){
        parserLog("Parsing program");
        parseBlock();
        match();
    }

    //parse block
    private void parseBlock(){
        match();
    }

    //parse statement list
    private void parseStatementList(){
        match();
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
    private void match(){}
}