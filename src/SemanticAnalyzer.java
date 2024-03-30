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
    SymbolTable symbolTable;    //creates a symbol table

    //Semantic Analysis constructor -- initializes all variables
    public SemanticAnalyzer(ArrayList<Token> programTokenStream, int programCounter){
        this.tokenStream = programTokenStream;
        this.currentToken = tokenStream.get(0);
        this.tokenStreamIndex = 0;
        this.semanticErrors = 0;
        this.programCounter = programCounter;
        ast = new AST();
        symbolTable = new SymbolTable();
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
            symbolTable.inOrder(ast.root);  //test output of depth first in order traversal
            symbolTable.testScopes();
        }
        else{
            SemanticAnalyzerLog("Semantic Analysis Failed... Errors: " + semanticErrors);   //output message if semantic fails
        }
    }

    //parse block -- semantic
    private void semanticBlock(){
        ast.addNodeAST("root", "Block");    //add block as root node
        incrementToken();   //match
        semanticStatementList();    //statementlist
        incrementToken();   //match
        ast.moveUpAST();
    }

    //parse statement list -- semantic
    private void semanticStatementList(){
        //check tokent type to determine what function to call
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
                SemanticAnalyzerLog("Incorrect token found...");    //update this error message later
                semanticErrors++;
                break;
        }
    }

    //parse print statement -- semantic
    private void semanticPrintStatement(){
        ast.addNodeAST("branch", "Print statement");    //add branch node
        incrementToken();   //2 match function calls -- increment token index
        incrementToken();
        semanticExpr(); //call expr
        incrementToken();   //match
        ast.moveUpAST();    //move up because we created a branch node
    }

    //parse assignment statement -- semantic
    private void semanticAssignmentStatement(){
        ast.addNodeAST("branch", "Assignment statement");   //add a branch node
        semanticID();   //add a leaf node
        incrementToken();
        semanticExpr();
        ast.moveUpAST();    //move up branch node
    }

    //parse variable declaration -- semantic
    private void semanticVarDecl(){
        ast.addNodeAST("branch", "Variable declaration");   //add a branch node
        semanticType(); //add 2 leaf nodes -- type and id
        semanticID();
        ast.moveUpAST();    //move up branch node
    }

    //parse while statement -- semantic
    private void semanticWhileStatement(){
        ast.addNodeAST("branch", "While statement");    //add a branch node
        incrementToken();
        semanticBooleanExpr();
        semanticBlock();
        ast.moveUpAST();    //move up branch node
    }

    //parse if statement -- semantic
    private void semanticIfStatement(){
        ast.addNodeAST("branch", "If statement");   //add a branch node
        incrementToken();
        semanticBooleanExpr();
        semanticBlock();
        ast.moveUpAST();    //move up branch node
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
                SemanticAnalyzerLog("Incorrect token found...");    //update this error message later
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
        semanticCharList(); //call charlist to create the string token
        incrementToken();
    }

    //parse boolean expression -- semantic
    private void semanticBooleanExpr(){
        //check token type to determine what function to call
        if(currentToken.tokenType.equals("OPENING_PARENTHESIS")){
            incrementToken();
            semanticBoolOp();   //find the bool op and add it first
            semanticExpr();
            incrementToken();
            semanticExpr();
            incrementToken();
            ast.moveUpAST();    // move up because we added a branch node
        }
        else{
            semanticBoolVal();
        }
    }

    //parse char list -- semantic
    private void semanticCharList(){
        //use a stringbuilder to iterate through a charlist and create 1 string variable
        StringBuilder charlist = new StringBuilder();
        charlist.append("\"");
        while(!currentToken.lexeme.equals("\"")){
            charlist.append(currentToken.lexeme);
            incrementToken();
        }
        charlist.append("\"");
        ast.addNodeAST("leaf", charlist.toString());
    }

    //parse ID -- semantic
    private void semanticID(){
        ast.addNodeAST("leaf", currentToken.lexeme);    //add leaf
        incrementToken();
    }

    //parse types: int, string, bool -- semantic
    private void semanticType(){
        ast.addNodeAST("leaf", currentToken.lexeme);    //add leaf
        incrementToken();
    }

    //parse digits 0-9 -- semantic
    private void semanticDigit(){
        ast.addNodeAST("leaf", currentToken.lexeme);    //add leaf
        incrementToken();
    }

    //parse bool operators -- semantic
    private void semanticBoolOp(){
        int i = tokenStreamIndex;   //create temp index
        //keep looking until find the bool op
        while(!tokenStream.get(i).lexeme.equals("!=") && !tokenStream.get(i).lexeme.equals("==")){
            i++;
        }
        ast.addNodeAST("branch", tokenStream.get(i).lexeme);  //add branch for bool ops
    }

    //parse bool values -- semantic
    private void semanticBoolVal(){
        ast.addNodeAST("leaf", currentToken.lexeme);    //add leaf
        incrementToken();
    }

    //parse add op (+) -- semantic
    private void semanticIntOp(){
        ast.addNodeAST("branch", "+");  // add + op as branch
    }
}