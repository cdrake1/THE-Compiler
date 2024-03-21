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
        ast.outputAST();
    }

    //parse block -- semantic
    private void semanticBlock(){
        SemanticAnalyzerLog("block");

        ast.addNodeAST("root", "Block"); //is this a root or branch node?
        tokenStreamIndex++;
        currentToken = getCurrentToken();
        semanticStatementList();
        tokenStreamIndex++;
        currentToken = getCurrentToken();
    }

    //parse statement list -- semantic
    private void semanticStatementList(){
        SemanticAnalyzerLog("statementlist");
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
        SemanticAnalyzerLog("statement");
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
    }

    //parse print statement -- semantic
    private void semanticPrintStatement(){
        SemanticAnalyzerLog("print");
        ast.addNodeAST("branch", "Print statement");
        tokenStreamIndex += 2;
        currentToken = getCurrentToken();
        semanticExpr();
        tokenStreamIndex++;
        currentToken = getCurrentToken();
        ast.moveUpAST();
    }

    //parse assignment statement -- semantic
    private void semanticAssignmentStatement(){
        SemanticAnalyzerLog("assignment");
        ast.addNodeAST("branch", "Assignment statement");
        ast.addNodeAST("leaf", currentToken.lexeme);
        tokenStreamIndex++;
        currentToken = getCurrentToken();
        ast.addNodeAST("leaf", currentToken.lexeme);
        tokenStreamIndex++;
        currentToken = getCurrentToken();
        semanticExpr();
        ast.moveUpAST();
    }

    //parse var decl -- semantic
    private void semanticVarDecl(){
        SemanticAnalyzerLog("vardecl");
        ast.addNodeAST("branch", "Variable declaration");
        ast.addNodeAST("leaf", currentToken.lexeme);
        tokenStreamIndex++;
        currentToken = getCurrentToken();
        ast.addNodeAST("leaf", currentToken.lexeme);
        tokenStreamIndex++;
        currentToken = getCurrentToken();
        ast.moveUpAST();
    }

    //parse while statement -- semantic
    private void semanticWhileStatement(){
        SemanticAnalyzerLog("while");
        ast.addNodeAST("branch", "While statement");
        tokenStreamIndex++;
        currentToken = getCurrentToken();
        semanticBooleanExpr();
        semanticBlock();
        ast.moveUpAST();
    }

    //parse if statement -- semantic
    private void semanticIfStatement(){
        SemanticAnalyzerLog("if");
        ast.addNodeAST("branch", "If statement");
        tokenStreamIndex++;
        currentToken = getCurrentToken();
        semanticBooleanExpr();
        semanticBlock();
        ast.moveUpAST();
    }

    //parse expression -- semantic
    private void semanticExpr(){
        SemanticAnalyzerLog("expr");
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
                ast.addNodeAST("leaf", currentToken.lexeme);
                tokenStreamIndex++;
                currentToken = getCurrentToken();
                break;
            default:
                //throw error?      
        }
    }

    //parse int expression -- semantic
    private void semanticIntExpr(){
        SemanticAnalyzerLog("int expr");
        //check if next token is '+'
        if(tokenStream.get(tokenStreamIndex+1).tokenType.equals("ADD")){
            ast.addNodeAST("leaf", currentToken.lexeme);
            tokenStreamIndex++;
            currentToken = getCurrentToken();
            ast.addNodeAST("leaf", currentToken.lexeme);
            tokenStreamIndex++;
            currentToken = getCurrentToken();
            semanticExpr();
        }
        else{
            ast.addNodeAST("leaf", currentToken.lexeme);
            tokenStreamIndex++;
            currentToken = getCurrentToken();
        }
    }

    //parse string expression -- semantic
    private void semanticStringExpr(){
        SemanticAnalyzerLog("string");
        tokenStreamIndex++;
        currentToken = getCurrentToken();
        semanticCharList();
        tokenStreamIndex++;
        currentToken = getCurrentToken();
    }

    //parse boolean expression -- semantic
    private void semanticBooleanExpr(){
        SemanticAnalyzerLog("bool");
        //check token type to determine what function to call
        if(currentToken.tokenType.equals("OPENING_PARENTHESIS")){
            tokenStreamIndex++;
            currentToken = getCurrentToken();
            semanticExpr();
            ast.addNodeAST("leaf", currentToken.lexeme);
            tokenStreamIndex++;
            currentToken = getCurrentToken();
            semanticExpr();
            tokenStreamIndex++;
            currentToken = getCurrentToken();
        }
        else{
            ast.addNodeAST("leaf", currentToken.lexeme);
            tokenStreamIndex++;
            currentToken = getCurrentToken();
        }
    }

     //parse char list -- semantic
    private void semanticCharList(){
        SemanticAnalyzerLog("charlist");
        StringBuilder charlist = new StringBuilder();
        charlist.append("\"");
        while(!currentToken.lexeme.equals("\"")){
            charlist.append(currentToken.lexeme);
            tokenStreamIndex++;
            currentToken = getCurrentToken();
        }
        charlist.append("\"");
        ast.addNodeAST("leaf", charlist.toString());
    }
}