/*
    Code Generation file
    Creates a 6502 op codes
*/

//Collin Drakes Code Generator

import java.util.Hashtable;

public class CodeGenerator {
    String[] opCodes;    //stores the code, stack, heap
    Hashtable<String, staticTableVariable> varTable;    //the variable table
    Hashtable<String, branchTableVariable> stringTable; //the branch table
    int currentIndex;   //keeps track of the current index of the opCodes array
    int tempCounter;    //keeps track of temp number for variable table "T0XX"
    int currentScope;   //keeps track of what scope we are in

    int heapPointer;    //pointer to where the heap starts
    int codePointer;    //pointer to where the code starts
    int stackPointer;   //pointer to where the stack starts
    int staticTableOffset;  //the offset of the variables in the static table (var table)

    int programCounter; //what program are we on?
    int codeGenErrors;  //counts how many errors have occurred
    AST ast;    //ast from semantic analysis
    SymbolTable symbolTable;    //symbol table from semantic analysis

    //Code Generator constructor -- initializes variables
    public CodeGenerator(AST ast, SymbolTable ST, int programCounter){
        this.opCodes = new String[256];
        this.varTable = new Hashtable<>();
        this.stringTable = new Hashtable<>();
        this.currentIndex = 0;
        this.tempCounter = 0;
        this.currentScope = 0;


        this.heapPointer = 0;
        this.codePointer = 0;
        this.stackPointer = 0;
        this.staticTableOffset = 0;

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
        initArray();   //init all index of the opCodes array to 00
        inOrder(ast.root);

        if(codeGenErrors == 0){
            codeGeneratorLog("Code Generation Complete... Errors: " + codeGenErrors);
        }
        else{
            codeGeneratorLog("Code Generation Failed... Errors: " + codeGenErrors);
        }
    }

    //initializes all of memory (code, stack, and heap) to 00
    private void initArray(){
        for(int i = 0; i < opCodes.length; i++){
            opCodes[i] = "00";
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
                currentScope++; //increment scope
                break;
            case "Variable declaration":
                codeGenVarDecl(node);
                break;
            case "Assignment statement":
                codeGenAssignmentStatement(node);
                break;
            case "Print statement":
                codeGenPrintStatement(node);
                break;
            case "While statement":
                codeGenWhileStatement(node);
                break;
            case "If statement":
                codeGenIfStatement(node);
                break;
            default:
                //do nothing
                break;
        }

        //iterate through all of the nodes children recursively
        for(ASTNode child : node.children){
            inOrder(child);
        }

        //close scope after processing block nodes
        if(node.name.equals("Block")) {
            currentScope--;
        }
        System.out.println(node.name);  //test to output nodes
    }

    //produces the op codes for variable declarations
    private void codeGenVarDecl(ASTNode currentNode){
        ASTNode typeNode = currentNode.children.get(0);
        ASTNode idNode = currentNode.children.get(1);

        addOpCode("A9");
        addOpCode("00");
        addOpCode("8D");
        if(typeNode.name.equals("string")){
            //add pointer??
            staticTableVariable temp = new staticTableVariable("T" + Integer.toString(tempCounter), idNode.name, currentScope, staticTableOffset);
            tempCounter++;
            staticTableOffset++;
            varTable.put(null, temp);
        }
        else{
            //address is the spots after the code
            staticTableVariable temp = new staticTableVariable("T" + Integer.toString(tempCounter), idNode.name, currentScope, staticTableOffset);
            tempCounter++;
            staticTableOffset++;
            varTable.put(null, temp);
        }
        addOpCode("00");
    }

    private void codeGenAssignmentStatement(ASTNode currentNode){
        //A9
        //8D
    }

    private void codeGenPrintStatement(ASTNode currentNode){}
    private void codeGenWhileStatement(ASTNode currentNode){}
    private void codeGenIfStatement(ASTNode currentNode){}

    //adds opcodes to the array and increments the index
    private void addOpCode(String opCode){
        if(currentIndex > opCodes.length){
            codeGeneratorLog("ERROR! INVALID MEMORY LOCATION... INDEX EVALUATES AS GREATER THAN THE MEMORY LENGHT");
            codeGenErrors++;
        }
        else{
            opCodes[currentIndex] = opCode;
            currentIndex++;
        }
    }
}