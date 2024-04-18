/*
    Code Generation file
    Creates a 6502 op codes
*/

import java.util.HashMap;
import java.util.Hashtable;

//Collin Drakes Code Generator
public class CodeGenerator {
    String[] memory;    //stores the code, stack, heap
    Hashtable<String, staticTableVariable> staticTable;    //the variable table
    Hashtable<String, branchTableVariable> branchTable; //the branch table
    int currentIndex;   //keeps track of the current index of the opCodes array
    int tempCounter;    //keeps track of temp number for variable table "T0XX"
    int currentScope;   //keeps track of what scope we are in
    boolean rootCreated;

    int codePointer;    //pointer to where the code starts
    int stackPointer;   //pointer to where the stack starts -- directly after code
    int heapPointer;    //pointer to where the heap starts -- builds from end of the array until it crashes into the stack
    int staticTableOffset;  //the offset of the variables in the static table (var table)

    int programCounter; //what program are we on?
    int codeGenErrors;  //counts how many errors have occurred
    AST ast;    //ast from semantic analysis
    SymbolTable symbolTable;    //symbol table from semantic analysis

    //Code Generator constructor -- initializes variables
    public CodeGenerator(AST ast, SymbolTable ST, int programCounter){
        this.memory = new String[256];
        this.staticTable = new Hashtable<>();
        this.branchTable = new Hashtable<>();
        this.currentIndex = 0;
        this.tempCounter = 0;
        this.currentScope = 0;
        this.rootCreated = false;


        this.codePointer = 0;
        this.stackPointer = 0;
        this.heapPointer = 0;
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
        initMemory();   //init all index of the opCodes array to 00
        inOrder(ast.root);      //create the op codes and populate memory
        backPatch();    //fill out the stack

        if(codeGenErrors == 0){
            codeGeneratorLog("Code Generation Complete... Errors: " + codeGenErrors + "\n");
            for(int i = 0; i < memory.length; i++){
                System.out.print(" " + memory[i]);
            }
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

        //---add true false to end of heap---

        //--false--
        memory[244] = "66"; //f
        memory[245] = "61"; //a
        memory[246] = "6C"; //l
        memory[247] = "73"; //s
        memory[248] = "65"; //e
        memory[249] = "00"; //0
        //--true--
        memory[250] = "74"; //t
        memory[251] = "72"; //r
        memory[252] = "75"; //u
        memory[253] = "65"; //e
        memory[254] = "00"; //0
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
                codeGenOpenScope();
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

    //increment the scope pointer
    private void codeGenOpenScope(){
        if(rootCreated == false){
            rootCreated = true;
        }
        else{
            currentScope++; //increment scope
        }
    }

    //produces the op codes for variable declarations
    private void codeGenVarDecl(ASTNode currentNode){
        ASTNode typeNode = currentNode.children.get(0);
        ASTNode idNode = currentNode.children.get(1);

        addOpCode("A9");
        addOpCode("00");
        addOpCode("8D");
        if(typeNode.name.equals("string")){
            //add pointer to heap??
            //unsure how to do this
        }
        else{
            //add the temp address to the memory
            String tempAddress = "T" + Integer.toString(tempCounter);
            addOpCode(tempAddress);

            //add the variable to the static variable table
            System.out.println(idNode.name +" " + currentScope);
            String tempKey = idNode.name + Integer.toString(currentScope);
            staticTableVariable tempVar = new staticTableVariable(tempAddress, idNode.name, currentScope, staticTableOffset);
            tempCounter++;
            staticTableOffset++;
            staticTable.put(tempKey, tempVar);
        }
        addOpCode("00");
    }

    //produces the op codes for assignment statements
    private void codeGenAssignmentStatement(ASTNode currentNode){
        ASTNode idNode = currentNode.children.get(0);   //left node
        ASTNode exprNode = currentNode.children.get(1); //right node

        //get the ID variables temp address (left node)
        String tempKey = idNode.name + Integer.toString(currentScope);
        staticTableVariable tempVar = staticTable.get(tempKey);
        
        //check the token type of the expression node (right node)
        switch (exprNode.token.tokenType) {
            case "ID":
                addOpCode("AD");
                
                //get the expr variables temp address (right node)
                String tempKeyExpr = exprNode.name + Integer.toString(currentScope);
                staticTableVariable tempVarExpr = staticTable.get(tempKeyExpr);

                //add the temp address
                addOpCode(tempVarExpr.tempAddress);
                addOpCode("00");
                break;
            case "DIGIT":
                //add digit
                addOpCode("A9");
                addOpCode("0" + exprNode.name);
                break;
            case "ADD":
                break;
            case "String Literal":
                //add string to heap and address pointer (first location of string address in heap) to static table
                break;
            case "EQUALITY_OP":
            case "INEQUALITY_OP":
                break;
            case "BOOL_TRUE":
                addOpCode("A9");
                addOpCode("74");
                break;
            case "BOOL_FALSE":
                addOpCode("A9");
                addOpCode("66");
                break;
        
            default:
                break;
        }
        addOpCode("8D");
        addOpCode(tempVar.tempAddress);    //temp right
        addOpCode("00");
    }

    private void codeGenPrintStatement(ASTNode currentNode){
        ASTNode exprNode = currentNode.children.get(0); //expr node

        //check the token type of the expression node
        switch (exprNode.token.tokenType) {
            case "ID":
                //grab the temp address for the expr node
                String tempKeyExpr = exprNode.name + Integer.toString(currentScope);
                staticTableVariable tempVarExpr = staticTable.get(tempKeyExpr);

                //load the Y register from memory
                addOpCode("AC");
                addOpCode(tempVarExpr.tempAddress);
                addOpCode("00");
                break;
            case "DIGIT":
                //load constant
                addOpCode("A0");
                addOpCode("0" + exprNode.name);
                break;
            case "ADD":
                break;
            case "String Literal":
                //add string to heap and address pointer (first location of string address in heap) to static table
                break;
            case "EQUALITY_OP":
            case "INEQUALITY_OP":
                break;
            case "BOOL_TRUE":
                addOpCode("A0");
                addOpCode("74");
                break;
            case "BOOL_FALSE":
                addOpCode("A0");
                addOpCode("66");
                break;
        
            default:
                break;
        }
        addOpCode("A2");
        addOpCode("01");
        addOpCode("FF");  
    }
    private void codeGenWhileStatement(ASTNode currentNode){}
    private void codeGenIfStatement(ASTNode currentNode){}

    //adds opcodes to the array and increments the index
    private void addOpCode(String opCode){
        if(currentIndex > memory.length){
            codeGeneratorLog("ERROR! INVALID MEMORY LOCATION... INDEX EVALUATES AS GREATER THAN THE MEMORY LENGTH");
            codeGenErrors++;
        }
        else{
            memory[currentIndex] = opCode;
            currentIndex++;
        }
    }

    //goes through and ...
    private void backPatch(){
        //replace all temp address with actual addresses
        //actual addresses directly after code FF 00
        //stack pointer?
    }
}