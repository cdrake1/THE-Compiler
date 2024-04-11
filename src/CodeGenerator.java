/*
    Code Generation file
    Creates a 6502 op codes
*/

//Collin Drakes Code Generator

import java.util.Hashtable;

public class CodeGenerator {
    String[] memory;
    Hashtable<String, staticTableVariable> varTable;
    Hashtable<String, branchTableVariable> stringTable;
    int heapPointer;
    int codePointer;
    int stackPointer;
    int programCounter;
    int codeGenErrors;
    AST ast;
    SymbolTable symbolTable;

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