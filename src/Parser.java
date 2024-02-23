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
    int programCounter = 1; //counts programs

    //expected lists of tokens
    List<String> expectedType;
    List<String> expectedChar;
    List<String> expectedDigit;
    List<String> expectedBoolOp;
    List<String> expectedBoolVal;

    //creates a parser and initializes all variables. We are prepared to start parsing!
    public Parser(ArrayList<Token> programTokenStream){
        this.tokenStream = programTokenStream;
        this.currentToken = tokenStream.get(0);
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

    //outputs the results of the parser. Also outputs warnings and errors...
    private void parserLog(String output){
        System.out.println("PARSER - " + output);
    }

    //returns the current token in the token stream
    public Token getCurrentToken(){
        return tokenStream.get(tokenStreamIndex);
    }

    //parse program
    public void parseProgram(){
        programCounter++;
        parserLog("Parsing program " + programCounter);
        parserLog("Parsing program");
        parseBlock();
        match("$"); //passes in expected token
    }

    //parse block
    private void parseBlock(){
        parserLog("Parsing block");
        match("{");
        parseStatementList();
        match("}");
    }

    //parse statement list
    private void parseStatementList(){
        parserLog("Parsing statement list");

        //check if there are still tokens left
        if(!currentToken.lexeme.equals("}")){
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
        parserLog("Parsing statement");

        //check token to determine what function to call
        switch (currentToken.tokenType) {
            case "PRINT":
                parsePrintStatement();
                break;
            case "ID":
                parseAssignmentStatement();
                break;
            case "TYPE_INT":
            case "TYPE_STRING":
            case "TYPE_BOOLEAN":
                parseVarDecl();
                break;
            case "WHILE":
                parseWhileStatement();
                break;
            case "IF":
                parseIfStatement();
                break;
            case "OPENING_BRACE":
                parseBlock();
                break;
        }
    }

    //parse print statement
    private void parsePrintStatement(){
        parserLog("Parsing print statement");

        match("print");
        match("(");
        parseExpr();
        match(")");
    }

    //parse assignment statement
    private void parseAssignmentStatement(){
        parserLog("Parsing assignment statement");

        parseID();
        match("=");
        parseExpr();
    }

    //parse variable declaration
    private void parseVarDecl(){
        parserLog("Parsing variable declaration");

        parseType();
        parseID();
    }

    //parse while statement
    private void parseWhileStatement(){
        parserLog("Parsing while statement");

        match("while");
        parseBooleanExpr();
        parseBlock();
    }

    //parse if statement
    private void parseIfStatement(){
        parserLog("Parsing if statement");

        match("if");
        parseBooleanExpr();
        parseBlock();
    }

    //parse expression
    private void parseExpr(){
        parserLog("Parsing expression");

        //check token to determine what function to call
        switch (currentToken.tokenType) {
            case "DIGIT":
                parseIntExpr();
                break;
            case "QUOTE":
                parseStringExpr();
                break;
            case "OPENING_PARENTHESIS":
            case "BOOL_TRUE":
            case "BOOL_FALSE":
                parseBooleanExpr();
                break;
            case "ID":
                parseID();
                break;
        }
    }

    //parse int expression
    private void parseIntExpr(){
        parserLog("Parsing int expression");

        //check if next token is '+'
        if(tokenStream.get(tokenStreamIndex+1).tokenType.equals("ADD")){
            parseDigit();
            parseIntOp();
            parseExpr();
        }
        else{
            parseDigit();
        }
    }

    //parse string expression
    private void parseStringExpr(){
        parserLog("Parsing string expression");

        match("\"");
        parseCharList();
        match("\"");
    }

    //parse boolean expression
    private void parseBooleanExpr(){
        parserLog("Parsing boolean expression");

        if(currentToken.tokenType.equals("OPENING_PARENTHESIS")){
            match("(");
            parseExpr();
            parseBoolOp();
            parseExpr();
            match(")");
        }
        else{
            parseBoolVal();
        }
    }

    //parse ID
    private void parseID(){
        parserLog("Parsing ID");

        parseChar();
    }

    //parse charlist: char, space, Empty statement
    private void parseCharList(){
        parserLog("Parsing char list");

        //switch or if to check if char space or empty
        if(currentToken.tokenType.equals("CHAR")){
            parseChar();
            parseCharList();
        }
        else if(currentToken.tokenType.equals("WHITESPACE")){
            parseSpace();
            parseCharList();
        }
        else{
            //do nothing
            //epsilon case
        }
    }

    //parse types: int, string, bool
    private void parseType(){
        parserLog("Parsing type");

        matchKind(expectedType);
    }

    //parse chars a-z
    private void parseChar(){
        parserLog("Parsing char");
        
        matchKind(expectedChar);
    }

    //parse whitespace
    private void parseSpace(){
        parserLog("Parsing whitespace");
        
        match(" ");
    }

    //parse digits 0-9
    private void parseDigit(){
        parserLog("Parsing digit");

        matchKind(expectedDigit); 
    }

    //parse bool operators
    private void parseBoolOp(){
        parserLog("Parsing boolean operation");

        matchKind(expectedBoolOp);
    }

    //parse bool values
    private void parseBoolVal(){
        parserLog("Parsing boolean value");

        matchKind(expectedBoolVal);
    }

    //parse add op (+)
    private void parseIntOp(){
        parserLog("Parsing int operation");

        match("+");
    }

    //matches and consumes tokens
    private void match(String expectedToken){

        //check if the current token is equal to the expected
        if(currentToken.lexeme.equals(expectedToken)){

            //if equal consume and increment index
            tokenStreamIndex++;
            if(tokenStreamIndex < tokenStream.size()){
                currentToken = getCurrentToken();
            }
        }
        else{

            //throw error if it doesnt match
            parseErrors++;
        }
    }

    //matches and consumes tokens when expected token is a range.... ie CHAR, ID
    private void matchKind(List<String> expectedTokens){
        if(expectedTokens.contains(currentToken.lexeme)){

           //if equal consume and increment index
           tokenStreamIndex++;
           if(tokenStreamIndex < tokenStream.size()){
               currentToken = getCurrentToken();
           }
        }
        else{

            //throw error if it doesnt match
            parseErrors++;
        }
    }
}