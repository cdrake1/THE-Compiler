package temp;

import java.util.List;

public class temp {

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

        //check if there are still tokens left
        if(!tokenStream.isEmpty()){
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
        match("\"");
        parseCharList();
        match("\"");
    }

    //parse boolean expression
    private void parseBooleanExpr(){
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
        parseChar();
    }

    //parse charlist: char, space, Empty statement
    private void parseCharList(){
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
        matchKind(expectedType);
    }

    //parse chars a-z
    private void parseChar(){
        matchKind(expectedChar);
    }

    //parse whitespace
    private void parseSpace(){
        match(" ");
    }

    //parse digits 0-9
    private void parseDigit(){
        matchKind(expectedDigit); 
    }

    //parse bool operators
    private void parseBoolOp(){
        matchKind(expectedBoolOp);
    }

    //parse bool values
    private void parseBoolVal(){
        matchKind(expectedBoolVal);
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
            parseErrors++;
        }
    }

    //matches and consumes tokens when expected token is a range.... ie CHAR, ID
    private void matchKind(List<String> expectedTokens){
        if(expectedTokens.contains(currentToken.lexeme)){

            //if equal consume and increment index
            tokenStreamIndex++;
            currentToken = getCurrentToken();
        }
        else{

            //throw error if it doesnt match
            parseErrors++;
        }
    }
}
    
}