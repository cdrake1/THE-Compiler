/*
    Parser file
    Takes a stream of tokens as an input and creates and Concrete Syntax Tree (Parse tree)
    "Takes tokens and groups them into phrases according to the syntax specification"
    Uses recursive descent, LL1, and top down parsing
*/

//import arraylist
import java.util.ArrayList;

//Collin Drakes Parser
public class Parser{
    ArrayList<Token> tokenStream;   //the current token stream
    Token currentToken; //the current token we are on
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
    public Token getCurrentToken(){
        return tokenStream.get(tokenStreamIndex);
    }

    //parse program
    public void parseProgram(){
        parserLog("Parsing program");
        parseBlock();
        match("$"); //passes in expected token
    }

    //parse block
    private void parseBlock(){
        match("{");
        parseStatementList();
        match("}");
    }

    //parse statement list
    private void parseStatementList(){
        //switch case or if statement?
        if(currentToken.lexeme.equals("print")){
            parseStatement();
            parseStatementList();
        }
        else{
            //do nothing
            //epsilon case
        }
    }

    //parse statement
    private void parseStatement(){
        //switch case or if statement?

        //change to if?
        switch (currentToken.tokenType) {
            case "PRINT":
                parsePrintStatement();
                break;
            case "ID":
                parseAssignmentStatement();
                break;
            case "print":
                parseVarDecl();
                break;
            case "WHILE":
                parseWhileStatement();
                break;
            case "IF":
                parseIfStatement();
                break;
            case "{":
                parseBlock();
                break;
        
            
        }
    }

    //parse print statement
    private void parsePrintStatement(){
        match("print");
        match("(");
        parseExpr();
        match(")");
    }

    //parse assignment statement
    private void parseAssignmentStatement(){
        parseID();
        match("=");
        parseExpr();
    }

    //parse variable declaration
    private void parseVarDecl(){
        parseType();
        parseID();
    }

    //parse while statement
    private void parseWhileStatement(){
        match("while");
        parseBooleanExpr();
        parseBlock();
    }

    //parse if statement
    private void parseIfStatement(){
        match("if");
        parseBooleanExpr();
        parseBlock();
    }

    //parse expression
    private void parseExpr(){
        //switch case or if statement?
    }

    //parse int expression
    private void parseIntExpr(){
        //switch case or if statement?
    }

    //parse string expression
    private void parseStringExpr(){
        match("\"");
        parseCharList();
        match("\"");
    }

    //parse boolean expression
    private void parseBooleanExpr(){
        //switch case or if statement?
    }

    //parse ID
    private void parseID(){
        //switch case or if statement?
        //always call match with char? 
    }

    //parse charlist: char, space, Empty statement
    private void parseCharList(){
        //switch or if to check if char space or empty
    }

    //parse types: int, string, bool
    private void parseType(){
        //switch case or if statement?
        //always call match with types?
    }

    //parse chars a-z
    private void parseChar(){
        //switch case or if statement?
        //always call match with char?
    }

    //parse whitespace
    private void parseSpace(){
        match(" ");
    }

    //parse digits 0-9
    private void parseDigit(){
        //switch case or if statement?
        //always call match with digit? 
    }

    //parse bool operators
    private void parseBoolOp(){
        //switch case or if statement?
        //always call match with bool op? 
    }

    //parse bool values
    private void parseBoolVal(){
        //switch case or if statement?
        //always call match with char? 
    }

    //parse add op (+)
    private void parseIntOp(){
        match("+");
    }

    //matches and consumes tokens
    private void match(String expectedToken){

        //check if the current token is equal to the expected
        if(currentToken.lexeme.equals(expectedToken)){

            //if equal consume and increment index
            tokenStreamIndex++;
            currentToken = getCurrentToken();
        }
        else{
            //throw error if it doesnt match
        }
    }
}