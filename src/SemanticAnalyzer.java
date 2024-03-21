/*
    Semantic Analysis file
    Takes a stream of tokens as input and creates an AST (intermediate representation)
*/


//import arraylist, list, and arrays
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    //intro function to semantic analysis
    public void startSemanticAnalysis(){
        SemanticAnalyzerLog("Semantically Analyzing Program " + programCounter);

        //call the block function to begin semantic analysis
        semanticBlock();
    }

    //parse block -- semantic
    private void semanticBlock(){
        ast.addNodeAST("root", "Block"); //is this a root or branch node?
        tokenStreamIndex++;
        currentToken = getCurrentToken();
        semanticStatementList();
        tokenStreamIndex++;
        currentToken = getCurrentToken();
        ast.moveUpAST(); //move up the tree
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
        ast.moveUpAST();
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
        }
        ast.moveUpAST();
    }

    //parse print statement -- semantic
    private void semanticPrintStatement(){
        ast.addNodeAST("branch", "Print statement");
        tokenStreamIndex++;
        tokenStreamIndex++;
        currentToken = getCurrentToken();
        semanticExpr();
        tokenStreamIndex++;
        currentToken = getCurrentToken();
        ast.moveUpAST();
    }

    //parse assignment statement -- semantic
    private void semanticAssignmentStatement(){
        ast.addNodeAST("branch", "Assignment statement");
        parseID();
        tokenStreamIndex++;
        currentToken = getCurrentToken();
        semanticExpr();
        ast.moveUpAST();
    }

    //parse var decl -- semantic
    private void semanticVarDecl(){
        ast.addNodeAST("branch", "Variable declaration");
        parseType();
        parseID();
        ast.moveUpAST();
    }

    //parse while statement -- semantic
    private void semanticWhileStatement(){
        ast.addNodeAST("branch", "While statement");
        tokenStreamIndex++;
        currentToken = getCurrentToken();
        semanticBooleanExpr();
        semanticBlock();
        ast.moveUpAST();
    }

    //parse if statement -- semantic
    private void semanticIfStatement(){
        ast.addNodeAST("branch", "If statement");
        tokenStreamIndex++;
        currentToken = getCurrentToken();
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
                parseID();
                break;
            default:
                //throw error?      
        }
        ast.moveUpAST();
    }

    //parse int expression -- semantic
    private void semanticIntExpr(){
        //check if next token is '+'
        if(tokenStream.get(tokenStreamIndex+1).tokenType.equals("ADD")){
            parseDigit();
            parseIntOp();
            semanticExpr();
        }
        else{
            parseDigit();
        }
        ast.moveUpAST();
    }

    //parse string expression -- semantic
    private void semanticStringExpr(){
        tokenStreamIndex++;
        currentToken = getCurrentToken();

        parseCharList(); //do I do something differently with charlists?

        tokenStreamIndex++;
        currentToken = getCurrentToken();
        ast.moveUpAST();
    }

    //parse boolean expression -- semantic
    private void semanticBooleanExpr(){
        //check token type to determine what function to call
        if(currentToken.tokenType.equals("OPENING_PARENTHESIS")){
            tokenStreamIndex++;
            currentToken = getCurrentToken();
            semanticExpr();
            parseBoolOp();
            semanticExpr();
            tokenStreamIndex++;
            currentToken = getCurrentToken();
        }
        else{
            parseBoolVal();
        }
        ast.moveUpAST();
    }

    //DO I NEED THE FUNCTIONS BELOW?? -------------------------------


    //parse expr -- semantic
    private void semanticID(){}
    //parse expr -- semantic
    private void semanticCharList(){}
    //parse expr -- semantic
    private void semanticType(){}
}