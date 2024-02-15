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

    //creates a parser and initializes all variables. We are prepared to start parsing!
    public Parser(){}

    //outputs the results of the parser. Also outputs warnings and errors...
    private void parserLog(String output){
        System.out.println("PARSER - " + output);
    }

    //calls the other functions to parse the tokenstream and build out the CST
    public void parseTokenStream(ArrayList<Token> programTokenStream){}

    private void parseProgram(){
        match();
    }

    private void parseBlock(){
        match();
    }

    private void parseStatementList(){
        match();
    }

    private void parseStatement(){
        match();
    }

    private void parsePrintStatement(){
        match();
    }

    private void parseAssignmentStatement(){
        match();
    }

    private void parseVarDecl(){
        match();
    }

    private void parseWhileStatement(){
        match();
    }

    private void parseIfStatement(){
        match();
    }

    private void parseExpr(){
        match();
    }

    private void parseIntExpr(){
        match();
    }

    private void parseStringExpr(){
        match();
    }

    private void parseBooleanExpr(){
        match();
    }

    //parse ID which is a char
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

    private void match(){}
}