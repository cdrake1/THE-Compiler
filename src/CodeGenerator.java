/*
    Code Generation file
    Creates 6502 op codes
*/

//import arraylists and hashtables
import java.util.ArrayList;
import java.util.Hashtable;

//Collin Drakes Code Generator
public class CodeGenerator {
    String[] memory;    //stores the op codes. Stack and heap included
    Hashtable<String, staticTableVariable> staticTable;    //the static variable table
    ArrayList<staticTableVariable> staticTableOrder;    //keeps track of the order of static variables added to aid in backpatching
    Hashtable<String, branchTableVariable> branchTable; //the branch/jump table
    int currentIndex;   //keeps track of the current index of the opCodes array
    int tempCounter;    //keeps track of temp number for static variable table "T0XX"
    int tempJumpCounter;    //keeps track of temp number for branch table variables
    int currentScope;   //keeps track of what scope we are in
    boolean rootCreated;    //assists when keeping track of scope

    SymbolTableNode currentSTScope; //helps with obtaining values from the correct scope by keeping track of the current scope within the ST
    int codePointer;    //pointer to where the code starts
    int stackPointer;   //pointer to where the stack starts -- directly after code
    int heapPointer;    //pointer to where the heap starts -- builds from end of the array until it crashes into the stack
    int staticTableOffset;  //the offset of the variables in the static table (var table) -- do I need this??
    String boolTrueAddress; //the starting memory address of the bool val true
    String boolFalseAddress;    //the starting memory address of the bool val false

    int programCounter; //what program are we on?
    int codeGenErrors;  //counts how many errors have occurred
    AST ast;    //ast from semantic analysis
    SymbolTable symbolTable;    //symbol table from semantic analysis

    //Code Generator constructor -- initializes variables
    public CodeGenerator(AST ast, SymbolTable ST, int programCounter){
        this.memory = new String[256];
        this.staticTable = new Hashtable<>();
        this.branchTable = new Hashtable<>();
        this.staticTableOrder = new ArrayList<>();
        this.currentIndex = 0;
        this.tempCounter = 0;
        this.currentScope = 0;
        this.rootCreated = false;
        this.currentSTScope = null;
        this.codePointer = 0;
        this.stackPointer = 0;
        this.heapPointer = 244;
        this.staticTableOffset = 0;
        boolTrueAddress = Integer.toHexString(250).toUpperCase();
        boolFalseAddress = Integer.toHexString(244).toUpperCase();
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
        backPatchBranch();  //backpatches the branch table variables within memory
        backPatch();    //fill out the stack

        //code generation finished. Did errors occurr?
        if(codeGenErrors == 0){ //succeeded
            codeGeneratorLog("Code Generation Complete... Errors: " + codeGenErrors + "\n");
            for(int i = 0; i < memory.length; i++){
                System.out.print(" " + memory[i]);
            }
        }
        else{   //failed
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
        if(node == null || node.processed == true){
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

        //mark nodes as processed when done -- implemented to mitigate code generation of if statements blocks twice
        node.processed = true;

        //iterate through all of the nodes children recursively
        for(ASTNode child : node.children){
            inOrder(child);
        }

        //close scope after processing block nodes
        if(node.name.equals("Block")) {
            currentScope--;
        }
    }

    //function to increment the scope pointer and keep track of the scope we are on
    private void codeGenOpenScope(){
        if(rootCreated == false){   //check if we scope 0 exists
            currentSTScope = symbolTable.root;  //set symbol table node to root
            rootCreated = true; //root flag
        }
        else{
            currentScope++; //increment scope
        }
    }

    //produces the op codes for variable declarations
    private void codeGenVarDecl(ASTNode currentNode){
        ASTNode idNode = currentNode.children.get(1);   //grab the id node

        addOpCode("A9");    //load accumulator with constant
        addOpCode("00");    
        addOpCode("8D");    //store accumulator in memory
        
        //add the temp address to memory
        String tempAddress = "T" + Integer.toString(tempCounter);
        addOpCode(tempAddress);

        //add the variable to the static variable table
        String tempKey = idNode.name + Integer.toString(currentScope);
        staticTableVariable tempVar = new staticTableVariable(tempAddress, idNode.name, currentScope, staticTableOffset);

        //increment counters
        tempCounter++;
        staticTableOffset++;

        //add the variable to the hastable and arraylist
        staticTable.put(tempKey, tempVar);
        staticTableOrder.add(tempVar);
        addOpCode("00");
    }

    //produces the op codes for assignment statements
    private void codeGenAssignmentStatement(ASTNode currentNode){
        ASTNode idNode = currentNode.children.get(0);   //left node
        ASTNode exprNode = currentNode.children.get(1); //right node

        //get the ID variables temp address (left node)
        String tempKey = idNode.name + Integer.toString(currentScope);
        staticTableVariable tempVar = staticTable.get(tempKey);
        if(tempVar == null){
            int correctScope = lookupVariable(idNode.name).scope;
            tempKey = idNode.name + Integer.toString(correctScope);
            tempVar = staticTable.get(tempKey);
        }
        
        //check the token type of the expression node (right node)
        switch (exprNode.token.tokenType) {
            case "ID":
                addOpCode("AD");
                
                //get the expr variables temp address (right node)
                String tempKeyExpr = exprNode.name + Integer.toString(currentScope);
                staticTableVariable tempVarExpr = staticTable.get(tempKeyExpr);
                if(tempVarExpr == null){
                    int correctScope = lookupVariable(exprNode.name).scope;
                    tempKeyExpr = exprNode.name + Integer.toString(correctScope);
                    tempVarExpr = staticTable.get(tempKeyExpr);
                }

                addOpCode(tempVarExpr.tempAddress); //add the temp address
                addOpCode("00");
                break;
            case "DIGIT":
                addOpCode("A9");
                addOpCode("0" + exprNode.name); //add digit
                break;
            case "ADD":
                codeGenIntOp(exprNode); //call intop function
                break;
            case "String Literal":
                addOpCode("A9");
                //add to heap
                String stringLiteral = exprNode.token.lexeme.substring(1, exprNode.token.lexeme.length() - 1);
                int heapSpot = heapPointer - stringLiteral.length() - 1;    //subtract 1 for 00
                int tempSpot = heapSpot;
                for(int i = 0; i < stringLiteral.length(); i++){
                    memory[tempSpot] = Integer.toHexString((int) stringLiteral.charAt(i)).toUpperCase();
                    tempSpot++;
                }
                memory[tempSpot] = "00";
                heapPointer = heapSpot;
                addOpCode(Integer.toHexString(heapSpot).toUpperCase());
                break;
            case "EQUALITY_OP":
            case "INEQUALITY_OP":
                codeGenBoolOps(exprNode);   //call bool op function
                break;
            case "BOOL_TRUE":
                addOpCode("A9");
                addOpCode(boolTrueAddress); //point to location in memory (heap)
                break;
            case "BOOL_FALSE":
                addOpCode("A9");
                addOpCode(boolFalseAddress);    //point to location in memory (heap)
                break;
            default:
                //do nothing
                break;
        }
        //store the left node in memory
        addOpCode("8D");
        addOpCode(tempVar.tempAddress);
        addOpCode("00");
    }

    //produces the op codes for print statements
    private void codeGenPrintStatement(ASTNode currentNode){
        ASTNode exprNode = currentNode.children.get(0); //expr node

        //check the nodes type
        switch (exprNode.token.tokenType) {
            case "ID":
                addOpCode("AC");
                
                //get the variables temp address
                String tempKeyExpr = exprNode.name + Integer.toString(currentScope);
                staticTableVariable tempVarExpr = staticTable.get(tempKeyExpr);
                if(tempVarExpr == null){
                    int correctScope = lookupVariable(exprNode.name).scope;
                    tempKeyExpr = exprNode.name + Integer.toString(correctScope);
                    tempVarExpr = staticTable.get(tempKeyExpr);
                }

                addOpCode(tempVarExpr.tempAddress);
                addOpCode("00");
                break;
            case "DIGIT":
                addOpCode("A0");
                addOpCode("0" + exprNode.name); //add digit
                break;
            case "String Literal":
                addOpCode("A0");
                //add to heap
                String stringLiteral = exprNode.token.lexeme.substring(1, exprNode.token.lexeme.length() - 1);
                int heapSpot = heapPointer - stringLiteral.length() - 1;    //subtract 1 for 00
                int tempSpot = heapSpot;
                for(int i = 0; i < stringLiteral.length(); i++){
                    memory[tempSpot] = Integer.toHexString((int) stringLiteral.charAt(i)).toUpperCase();
                    tempSpot++;
                }
                memory[tempSpot] = "00";
                heapPointer = heapSpot;
                addOpCode(Integer.toHexString(heapSpot).toUpperCase());
                break;
            case "BOOL_TRUE":
                addOpCode("A0");
                addOpCode(boolTrueAddress); //point to location in memory (heap)
                break;
            case "BOOL_FALSE":
                addOpCode("A0");
                addOpCode(boolFalseAddress);    //point to location in memory (heap)
                break;
            case "EQUALITY_OP":
            case "INEQUALITY_OP":
                codeGenBoolOps(exprNode); //call bool op function
                addOpCode("8D");
                addOpCode("00");    //some temp address location
                addOpCode("00");
                addOpCode("AC");
                addOpCode("00");    //some temp address location
                addOpCode("00");
                break;
            case "ADD":
                codeGenIntOp(exprNode); //call int op function
                addOpCode("8D");
                addOpCode("00");    //some temp address location
                addOpCode("00");
                addOpCode("AC");
                addOpCode("00");    //some temp address location
                addOpCode("00");
                break;
            default:
                //do nothing
                break;
        }

        //grab symbol from the symbol table
        Symbol symbol = lookupVariable(exprNode.name);

        //if the expr is a string/boolean/boolop then load 2 into the Y register. Otherwise load 1
        if(exprNode.token.tokenType.equals("String Literal") || exprNode.token.tokenType.equals("BOOL_FALSE") || exprNode.token.tokenType.equals("BOOL_TRUE") || exprNode.token.tokenType.equals("EQUALITY_OP") || exprNode.token.tokenType.equals("INEQUALITY_OP") || (exprNode.token.tokenType.equals("ID") && symbol != null && symbol.type.equals("string")) || (exprNode.token.tokenType.equals("ID") && symbol != null && symbol.type.equals("boolean"))){
            addOpCode("A2");
            addOpCode("02");
        }
        else{
            addOpCode("A2");
            addOpCode("01");
        }
        addOpCode("FF");    //break
    }

    //produces op codes for assignment statements int op nodes
    private void codeGenIntOp(ASTNode intOpNode){
        ASTNode leftNode = intOpNode.children.get(0);  //always a digit
        ASTNode rightNode = intOpNode.children.get(1); //digit, +, or ID

        //check the right nodes type
        switch (rightNode.token.tokenType) {
            case "ID":
                addOpCode("AD");
                //get the right nodes temp address (right node)
                String tempKeyExpr = rightNode.name + Integer.toString(currentScope);
                staticTableVariable tempVarExpr = staticTable.get(tempKeyExpr);
                if(tempVarExpr == null){
                    int correctScope = lookupVariable(rightNode.name).scope;
                    tempKeyExpr = rightNode.name + Integer.toString(correctScope);
                    tempVarExpr = staticTable.get(tempKeyExpr);
                }

                //add the temp address
                addOpCode(tempVarExpr.tempAddress);
                addOpCode("00");
                break;
            case "DIGIT":
                //just add digit
                addOpCode("A9");
                addOpCode("0" + rightNode.name);
                break;
            case "ADD":
                codeGenIntOp(rightNode);    //call int op
                break;
            default:
                //do nothing
                break;
        }

        addOpCode("8D");
        addOpCode("00");    //some temp address location
        addOpCode("00");
        addOpCode("A9");
        addOpCode("0" + leftNode.name);
        addOpCode("6D");
        addOpCode("00");    //some temp address location
        addOpCode("00");
    }

    //produces op codes for assignment statements bool op nodes
    private void codeGenBoolOps(ASTNode boolOpNode){
        ASTNode leftNode = boolOpNode.children.get(0); //expr
        ASTNode rightNode = boolOpNode.children.get(1);    //expr

        //-----left node check-----save to memory-----
        switch (leftNode.token.tokenType) {
            case "ID":
                addOpCode("AD");
                //get the right nodes temp address (right node)
                String tempKeyExpr = leftNode.name + Integer.toString(currentScope);
                staticTableVariable tempVarExpr = staticTable.get(tempKeyExpr);
                if(tempVarExpr == null){
                    int correctScope = lookupVariable(leftNode.name).scope;
                    tempKeyExpr = leftNode.name + Integer.toString(correctScope);
                    tempVarExpr = staticTable.get(tempKeyExpr);
                }

                //add the temp address
                addOpCode(tempVarExpr.tempAddress);
                addOpCode("00");
                break;
            case "DIGIT":
                //just add the digit
                addOpCode("A9");
                addOpCode("0" + leftNode.name);
                break;
            case "String Literal":
                addOpCode("A9");
                //add to heap
                String stringLiteral = leftNode.token.lexeme.substring(1, leftNode.token.lexeme.length() - 1);
                int heapSpot = heapPointer - stringLiteral.length() - 1;    //subtract 1 for 00
                int tempSpot = heapSpot;
                for(int i = 0; i < stringLiteral.length(); i++){
                    memory[tempSpot] = Integer.toHexString((int) stringLiteral.charAt(i)).toUpperCase();
                    tempSpot++;
                }
                memory[tempSpot] = "00";
                heapPointer = heapSpot;
                addOpCode(Integer.toHexString(heapSpot).toUpperCase());
                break;
            case "BOOL_TRUE":
                addOpCode("A9");
                addOpCode(boolTrueAddress); //point to location in memory (heap)
                break;
            case "BOOL_FALSE":
                addOpCode("A9");
                addOpCode(boolFalseAddress);    //point to location in memory (heap)
                break;
            case "ADD":
                codeGenIntOp(leftNode); //call int op
                break;
            case "EQUALITY_OP":
            case "INEQUALITY_OP":
                codeGenBoolOps(leftNode);   //call bool op
                //temp op codes?
                break;
            default:
                //do nothing
                break;
        }

        addOpCode("8D"); //load into memory
        addOpCode("00");    //some temp address location
        addOpCode("00");

        //-----right node check-----add to x register-----
        switch (rightNode.token.tokenType) {
            case "ID":
                addOpCode("AE");
                //get the right nodes temp address (right node)
                String tempKeyExpr = rightNode.name + Integer.toString(currentScope);
                staticTableVariable tempVarExpr = staticTable.get(tempKeyExpr);
                if(tempVarExpr == null){
                    int correctScope = lookupVariable(rightNode.name).scope;
                    tempKeyExpr = rightNode.name + Integer.toString(correctScope);
                    tempVarExpr = staticTable.get(tempKeyExpr);
                }

                //add the temp address
                addOpCode(tempVarExpr.tempAddress);
                addOpCode("00");
                break;
            case "DIGIT":
                //just add digit
                addOpCode("A2");
                addOpCode("0" + rightNode.name);
                break;
            case "String Literal":
                addOpCode("A2");
                //add to heap
                String stringLiteral = rightNode.token.lexeme.substring(1, rightNode.token.lexeme.length() - 1);
                int heapSpot = heapPointer - stringLiteral.length() - 1;    //subtract 1 for 00
                int tempSpot = heapSpot;
                for(int i = 0; i < stringLiteral.length(); i++){
                    memory[tempSpot] = Integer.toHexString((int) stringLiteral.charAt(i)).toUpperCase();
                    tempSpot++;
                }
                memory[tempSpot] = "00";
                heapPointer = heapSpot;
                addOpCode(Integer.toHexString(heapSpot).toUpperCase());
                break;
            case "BOOL_TRUE":
                addOpCode("A2");
                addOpCode(boolTrueAddress); //point to location in memory (heap)
                break;
            case "BOOL_FALSE":
                addOpCode("A2");
                addOpCode(boolFalseAddress);    //point to location in memory (heap)
                break;
            case "ADD":
                codeGenIntOp(rightNode);    //call int op
                break;
            case "EQUALITY_OP":
            case "INEQUALITY_OP":
                codeGenBoolOps(rightNode);  //call bool op
                //temp opcodes?
                break;
            default:
                //do nothing
                break;
        }

        addOpCode("EC");    //compare x register to byte in mem
        addOpCode("00");    //some temp address location
        addOpCode("00");
        addOpCode("A9");
        addOpCode(boolFalseAddress);    //false pointer
        addOpCode("D0");    //jump depending on z flag
        addOpCode("02");
        addOpCode("A9");
        addOpCode(boolTrueAddress); //true pointer
    }

    //creates op codes for while statement nodes
    private void codeGenWhileStatement(ASTNode currentNode){
        ASTNode boolExprNode = currentNode.children.get(0); //boolop or boolval
        ASTNode blockNode = currentNode.children.get(1);    //block node

        //check what type the boolean expr node is
        switch (boolExprNode.token.tokenType) {
            case "EQUALITY_OP":
            case "INEQUALITY_OP":
                codeGenBoolOps(boolExprNode);   //call bool op
                break;
            case "BOOL_TRUE":
            case "BOOL_FALSE":
                //idk yet
                break;
            default:
                //do nothing
                break;
        }

        //---branch calculation to jump over block---
        addOpCode("D0");    //branch n bytes if Z flag = 0: D0
        addOpCode("J" + tempJumpCounter);    //add jump table var

        //calculate the start of while body, the current jump node, and add that information to the branch table
        int bodyJumpStart = currentIndex;
        String bodyJumpBranch = "J" + tempJumpCounter;
        branchTableVariable whileBodyBranch = new branchTableVariable(bodyJumpBranch, bodyJumpStart);
        branchTable.put(bodyJumpBranch, whileBodyBranch); //add to jump table.. jump table node (distance of jump)
        tempJumpCounter++;

        inOrder(blockNode); //generate the op codes for the block node

        //load op codes
        addOpCode("A2");    //load 1 into x reg
        addOpCode("01");
        addOpCode("EC");    //compare 1 to 0
        addOpCode("FF");
        addOpCode("00");

        //---branch calculation to jump back into while block---
        addOpCode("D0");    //branch n bytes if Z flag = 0: D0
        addOpCode("J" + tempJumpCounter);    //add jump table var

        int goBackIntoWhileLoop = (255 - (currentIndex - bodyJumpStart)) - 1;
        String branchIntoWhileBranch = "J" + tempJumpCounter;
        branchTableVariable branchBackIntoWhileVar = new branchTableVariable(branchIntoWhileBranch, currentIndex);
        branchTable.put(branchIntoWhileBranch, branchBackIntoWhileVar);
        tempJumpCounter++;

        branchBackIntoWhileVar.distance = goBackIntoWhileLoop;  //go back and add correct distance to branch table

        //replace the distance within the branch table
        int distanceToJumpWhileBlock = currentIndex - bodyJumpStart;
        whileBodyBranch.distance = distanceToJumpWhileBlock;
    }

    //creates op codes for if statement nodes
    private void codeGenIfStatement(ASTNode currentNode){
        ASTNode boolExprNode = currentNode.children.get(0); //boolop or boolval
        ASTNode blockNode = currentNode.children.get(1);    //block node

        //-----bool expr node check-----
        switch(boolExprNode.token.tokenType){
            case "EQUALITY_OP":
            case "INEQUALITY_OP":
                codeGenBoolOps(boolExprNode);   //call bool op
                break;
            case "BOOL_TRUE":
                //always fall in
                addOpCode("A9");    //load 1 into memory
                addOpCode("01");
                addOpCode("8D");
                addOpCode("00");    //some temp address
                addOpCode("00");
                addOpCode("A2");
                addOpCode("01");    //compare x reg to mem (1 to 1)
                addOpCode("EC");
                addOpCode("00");    //some temp address
                addOpCode("00");
                break;
            case "BOOL_FALSE":
                //always branch
                addOpCode("A9");    //load 1 into memory
                addOpCode("01");
                addOpCode("8D");
                addOpCode("00");    //some temp address
                addOpCode("00");
                addOpCode("A2");
                addOpCode("02");    //compare x reg to mem (1 to 2)
                addOpCode("EC");
                addOpCode("00");    //some temp address
                addOpCode("00");
                break;
            default:
                //do nothing
                break;
        }

        addOpCode("D0");    //branch n bytes if Z flag = 0: D0
        addOpCode("J" + tempJumpCounter);    //add jump table var

        //save information for later
        int startJumpAddress = currentIndex;
        String currentBranchNumber = "J" + tempJumpCounter;
    

        //create a branch table variable and add it to the hashtable
        branchTableVariable branchtemp = new branchTableVariable("J" + tempJumpCounter, currentIndex);
        branchTable.put("J" + tempJumpCounter, branchtemp); //add to jump table.. jump table node (distance of jump)
        tempJumpCounter++;

        inOrder(blockNode); //call in order recursively
        int jumpDistance = currentIndex - startJumpAddress;    //calculate how far to jump

        //update the branch variables distance
        branchTableVariable branchTempTwo = branchTable.get(currentBranchNumber);
        branchTempTwo.distance = jumpDistance;
    }

    //lookups the variable we need within the symbol table and returns it -- changes made for code gen
    private Symbol lookupVariable(String idName){
        findScope(symbolTable.root);    //find the current scope we are in using the ST

        //temp variables and pointers used to traverse through the tree
        Symbol temp = null;
        SymbolTableNode tempNode = currentSTScope;
        int tempPointer = currentScope;
        
        //continuously iterate until you find it or return null
        while(tempPointer != -1 && temp == null && tempNode != null){
            temp = tempNode.symbols.get(idName);
            if(temp != null){
                return temp;
            }
            else{
                tempPointer--;
                tempNode = tempNode.parent;
            }
        }
        return null;
    }

    //finds the current scope we are in and sets the ST node
    private void findScope(SymbolTableNode currentNodeST){
        //if the current scope is the root, set it and return
        if(currentScope == 0){
            currentSTScope = symbolTable.root;  //set to root
            return;
        }

        //iterate through the symbol table until you finc the scope we are currently in
        for(SymbolTableNode child : currentNodeST.children){
            if(child.scope == currentScope){    //check what scope the child is
                currentSTScope = child;
                return;
            }
            else{
                findScope(child);   //call itself recursively
            }
        }
    }

    //adds opcodes to the array and increments the index
    private void addOpCode(String opCode){
        //check if the index has exceeded memory just incase lol
        if(currentIndex > memory.length){
            codeGeneratorLog("ERROR! INVALID MEMORY LOCATION... INDEX EVALUATES AS GREATER THAN THE MEMORY LENGTH");
            codeGenErrors++;
        }
        else{
            memory[currentIndex] = opCode;
            currentIndex++;
        }
    }

    //goes through and backpatches temp address with new addresses (stack)...
    private void backPatch(){
        stackPointer = currentIndex + 1;  //set stack pointer to position directly after code

        //check if the stack and heap collide
        if(stackPointer >= heapPointer){
            codeGeneratorLog("ERROR! STACK OVERFLOW...THE STACK AND HEAP COLLIDED");
            codeGenErrors++;
        }
        else{
            //iterate through all of the static table variables (use the arraylist as they are in order)
            for(staticTableVariable temp : staticTableOrder){
                String currentKeysTempAddress = temp.tempAddress;

                //iterate through memory (code section)
                for(int i = 0; i < memory.length; i++){
                    if(memory[i].equals(currentKeysTempAddress)){
                        //replace in static table??
                        memory[i] = Integer.toHexString(stackPointer).toUpperCase();
                    }
                }
                stackPointer++;
            }
        }
    }

    //backpatches branch table jumps
    private void backPatchBranch(){
        //iterate through all of the branch table
        for(branchTableVariable branchVariable : branchTable.values()){
            String currentBranchVariableName = branchVariable.temp;
            String jumpDistanceHex = Integer.toHexString(branchVariable.distance).toUpperCase();    //calculate hex value

            //iterate through all of memory
            for(int i = 0; i < memory.length; i++){
                //find and replace
                if(memory[i].equals(currentBranchVariableName)){
                    if(jumpDistanceHex.length() == 1){
                        memory[i] = "0" + jumpDistanceHex;
                    }
                    else{
                        memory[i] = jumpDistanceHex;
                    }
                }
            }
        }
    }
}