/*
    Semantic Analysis file
    Takes a stream of tokens as input and creates an AST (intermediate representation)
    Repurposes Parse functions to generate the AST
*/

//import arraylist, list, and arrays
import java.util.ArrayList;

//Collin Drakes Semantic Analyzer
public class SemanticAnalyzer {
    ArrayList<Token> tokenStream;   //the current token stream
    Token currentToken; //the current token we are on
    int tokenStreamIndex; //keeps track of current position within the token stream
    int semanticErrors; //counts the number of parse errors
    int programCounter; //counts programs
    AST ast;    //creates an abstract syntax tree

    //Semantic Analysis constructor -- initializes all variables
    public SemanticAnalyzer(ArrayList<Token> programTokenStream, int programCounter){
        this.tokenStream = programTokenStream;
        this.currentToken = tokenStream.get(0);
        this.tokenStreamIndex = 0;
        this.semanticErrors = 0;
        this.programCounter = programCounter;
        ast = new AST();
    }

    //outputs the results of Semantic Analysis. Also outputs errors...
    private void SemanticAnalyzerLog(String output){
        System.out.println("SEMANTIC ANALYZER - " + output);
    }

    //returns the current token in the token stream
    public Token getCurrentToken(){
        return tokenStream.get(tokenStreamIndex);
    }

    //increments the index and sets the current token
    public void incrementToken(){
        tokenStreamIndex++;
        currentToken = getCurrentToken();
    }

    //intro function to semantic analysis
    public void startSemanticAnalysis(){
        SemanticAnalyzerLog("Semantically Analyzing Program " + programCounter);    //output to update user on progress
        semanticBlock();    //call the block function to begin semantic analysis reparsing
        
        //check if reparsing/semantic analysis fails
        if(semanticErrors == 0){
            SemanticAnalyzerLog("Semantic Analysis Complete... Errors: " + semanticErrors);
            ast.outputAST();    //outputs the ast after reparsing
        }
        else{
            SemanticAnalyzerLog("Semantic Analysis Failed... Errors: " + semanticErrors);
        }
    }

    //parse block -- semantic
    private void semanticBlock(){
        ast.addNodeAST("root", "Block");    //add block as root node
        incrementToken();   //match
        semanticStatementList();    //statementlist
        incrementToken();   //match
    }

    //parse statement list -- semantic
    private void semanticStatementList(){
        switch (currentToken.tokenType) {
            case "PRINT":
            case "ID":
            case "TYPE_INT":
            case "TYPE_STRING":
            case "TYPE_BOOLEAN":
            case "WHILE":
            case "IF":
            case "OPENING_BRACE":
                semanticStatement();
                semanticStatementList();
                break;
            default:
                //do nothing
                //epsilon case
                break;
        }
    }

    //parse statement -- semantic
    private void semanticStatement(){
        //check token type to determine what function to call
        switch (currentToken.tokenType) {
            case "PRINT":
                semanticPrintStatement();
                break;
            case "ID":
                semanticAssignmentStatement();
                break;
            case "TYPE_INT":
            case "TYPE_STRING":
            case "TYPE_BOOLEAN":
                semanticVarDecl();
                break;
            case "WHILE":
                semanticWhileStatement();
                break;
            case "IF":
                semanticIfStatement();
                break;
            case "OPENING_BRACE":
                semanticBlock();
                break;
            default:
                //throw error?
                semanticErrors++;
                break;
        }
    }

    //parse print statement -- semantic
    private void semanticPrintStatement(){
        ast.addNodeAST("branch", "Print statement");
        incrementToken();
        incrementToken();
        semanticExpr();
        incrementToken();
        ast.moveUpAST();
    }

    //parse assignment statement -- semantic
    private void semanticAssignmentStatement(){
        ast.addNodeAST("branch", "Assignment statement");
        semanticID();
        incrementToken();
        semanticExpr();
        ast.moveUpAST();
    }

    //parse variable declaration -- semantic
    private void semanticVarDecl(){
        ast.addNodeAST("branch", "Variable declaration");
        semanticType();
        semanticID();
        ast.moveUpAST();
    }

    //parse while statement -- semantic
    private void semanticWhileStatement(){
        ast.addNodeAST("branch", "While statement");
        incrementToken();
        semanticBooleanExpr();
        semanticBlock();
        ast.moveUpAST();
    }

    //parse if statement -- semantic
    private void semanticIfStatement(){
        ast.addNodeAST("branch", "If statement");
        incrementToken();
        semanticBooleanExpr();
        semanticBlock();
        ast.moveUpAST();
    }

    //parse expression -- semantic
    private void semanticExpr(){
        //check token type to determine what function to call
        switch (currentToken.tokenType) {
            case "DIGIT":
                semanticIntExpr();
                break;
            case "QUOTE":
                semanticStringExpr();
                break;
            case "OPENING_PARENTHESIS":
            case "BOOL_TRUE":
            case "BOOL_FALSE":
                semanticBooleanExpr();
                break;
            case "ID":
                semanticID();
                break;
            default:
                //throw error??
                semanticErrors++;
                break;
        }
    }

    //parse int expression -- semantic
    private void semanticIntExpr(){
        //check if next token is '+'
        if(tokenStream.get(tokenStreamIndex+1).tokenType.equals("ADD")){
            semanticIntOp();    //add + op first -- as branch node
            semanticDigit();    //add digit node as leaf
            incrementToken();   //increment for + op
            semanticExpr(); // call expr
            ast.moveUpAST();    //move up because we are adding a branch node
        }
        else{
            semanticDigit();    //add leaf node for digit
        }
    }

    //parse string expression -- semantic
    private void semanticStringExpr(){
        incrementToken();
        semanticCharList();
        incrementToken();
    }

    //parse boolean expression -- semantic
    private void semanticBooleanExpr(){
        //check token type to determine what function to call
        if(currentToken.tokenType.equals("OPENING_PARENTHESIS")){   // need to add some type of checking to make branch nodes correctly
            incrementToken();
            semanticExpr();
            semanticBoolOp();
            semanticExpr();
            incrementToken();
        }
        else{
            semanticBoolVal();
        }
    }

    //parse char list -- semantic
    private void semanticCharList(){
        //use a stringbuilder to iterate through a charlist and create 1 string variable
        StringBuilder charlist = new StringBuilder();
        while(!currentToken.lexeme.equals("\"")){
            charlist.append(currentToken.lexeme);
            incrementToken();
        }
        ast.addNodeAST("leaf", charlist.toString());
    }

    //parse ID -- semantic
    private void semanticID(){
        ast.addNodeAST("leaf", currentToken.lexeme);
        incrementToken();
    }

    //parse types: int, string, bool -- semantic
    private void semanticType(){
        ast.addNodeAST("leaf", currentToken.lexeme);
        incrementToken();
    }

    //parse digits 0-9 -- semantic
    private void semanticDigit(){
        ast.addNodeAST("leaf", currentToken.lexeme);
        incrementToken();
    }

    //parse bool operators -- semantic
    private void semanticBoolOp(){
        ast.addNodeAST("branch", currentToken.lexeme);
        incrementToken();
        ast.moveUpAST();
    }

    //parse bool values -- semantic
    private void semanticBoolVal(){
        ast.addNodeAST("leaf", currentToken.lexeme);
        incrementToken();
    }

    //parse add op (+) -- semantic
    private void semanticIntOp(){
        ast.addNodeAST("branch", "+");  // add + op as branch
    }
}