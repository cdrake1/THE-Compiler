/*
    Code Generation file
    Creates a 6502 op codes
*/

//Collin Drakes Code Generator

import java.util.Hashtable;

public class CodeGenerator {
    String[] memory;    //stores the code, stack, heap
    Hashtable<String, staticTableVariable> varTable;    //the variable table
    Hashtable<String, branchTableVariable> stringTable; //the branch table
    int heapPointer;    //pointer to where the heap starts
    int codePointer;    //pointer to where the code starts
    int stackPointer;   //pointer to where the stack starts
    int programCounter; //what program are we on?
    int codeGenErrors;  //counts how many errors have occurred
    AST ast;    //ast from semantic analysis
    SymbolTable symbolTable;    //symbol table from semantic analysis

    //Code Generator constructor -- initializes variables
    public CodeGenerator(AST ast, SymbolTable ST, int programCounter){
        memory = new String[256];
        varTable = new Hashtable<>();
        stringTable = new Hashtable<>();
        heapPointer = 0;
        codePointer = 0;
        stackPointer = 0;
        this.programCounter = programCounter;
        this.codeGenErrors = 0;
        this.ast = ast;
        this.symbolTable = ST;
    }

    //outputs the processes completed within the Code Generator
    public void codeGeneratorLog(String output){
        System.out.println("Code Generator - " + output);
    }

    //starts the code generation process
    public void startCodeGen(){
        initMemory();
        inOrder(ast.root);

        if(codeGenErrors == 0){
            codeGeneratorLog("Code Generation Complete... Errors: " + codeGenErrors);
        }
        else{
            codeGeneratorLog("Code Generation Failed... Errors: " + codeGenErrors);
        }
    }

    //initializes all of memory (code, stack, and heap) to 00
    private void initMemory(){
        for(int i = 0; i < memory.length; i++){
            memory[i] = "00";
        }
    }

    //function for the depth first in order traversal of the AST
    public void inOrder(ASTNode node){
        //if the node is null return to avoid errors
        if(node == null){
            return;
        }

        //check the name of the tokens to determine which function to call
        switch (node.name) {
            case "Block":
                break;
            case "Variable declaration":
                break;
            case "Assignment statement":
                break;
            case "Print statement":
                break;
            case "While statement":
                break;
            case "If statement":
                break;
            default:
                //do nothing
                break;
        }

        //iterate through all of the nodes children recursively
        for(ASTNode child : node.children){
            inOrder(child);
        }

        System.out.println(node.name);
    }

    private void genvarDecl(){
        //A9
        //8D
    }

    private void genAssignmentStatement(){
        //A9
        //8D
    }

    private void genPrintStatement(){}
}