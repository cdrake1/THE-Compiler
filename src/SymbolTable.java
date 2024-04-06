/*
    Symbol Table file
    Creates a symbol table or a tree of hashmaps
    To be used in Symantic Analysis
*/

//Collin Drakes Symbol Table
public class SymbolTable {
    SymbolTableNode root;  //pointer to the root node
    SymbolTableNode current;   //pointer to the current node
    int currentScope;   //scope pointer
    int scopeCount; //counter for what scope we are on
    int outputScope;    //pointer for output function
    int STErrors;   //counts total errors
    int STWarnings; //countst total warnings
    String traversal;   //string to hold symbol table traversal

    //Symbol Table constructor -- initializes variables
    public SymbolTable(){
        this.root = null;
        this.current = null;
        this.currentScope = 0;
        this.scopeCount = 0;
        this.outputScope = 0;
        this.STErrors = 0;
        this.STWarnings = 0;
        this.traversal = "";
    }   

    //outputs the processes completed within the symbol table
    public void symbolTableLog(String output){
        System.out.println("Symantic Analyzer - Symbol Table - " + output);
    }

    //function for depth first in order traversal of AST
    public void inOrder(ASTNode node){
        //if null return 
        if(node == null){
            return;
        }

        //switch case for each node-------
        switch (node.name) {
            case "Block":
                openScope();
                break;
            case "Variable declaration":
                STVarDecl(node);
                break;
            case "Assignment statement":
                STAssignmentStatement(node);
                break;
            case "Print statement":
                STPrintStatement(node);
                break;
            case "While statement":
                STWhileStatement(node);
                break;
            case "If statement":
                STIfStatement(node);
                break;
            default:
                //do nothing
                break;
        }

        //iterate through all children recursively
        for(ASTNode child : node.children){
            inOrder(child);
        }

        //close scope after processing block nodes
        if(node.name.equals("Block")) {
            closeScope();
        }
    }

    //ST -- open scope -- opens a new scope within the symbol table
    private void openScope(){
        //if there is no root then create 1. Otherwise increment the pointer and create a child node
        if(root == null){
            addNodeSymbolTable(scopeCount);
        }
        else{
            scopeCount++;
            currentScope = scopeCount;
            addNodeSymbolTable(scopeCount);
        }
    }

    //ST -- close scope --closes the most recently opened scope
    private void closeScope(){
        //if the current scope is not the root node then move the pointer to its parent
        if(current.scope != 0 && current.parent != null){
            current = current.parent;
            currentScope--;
        }
    }

    //ST -- var decl -- add a symbol to the current nodes hash table
    private void STVarDecl(ASTNode currentNode){
        //grab the type, id, and line. Add the symbol to the hash table
        String varType = currentNode.children.get(0).name;
        String varID = currentNode.children.get(1).name;
        String line = currentNode.children.get(0).token.line;

        //check for a redeclaration error
        Symbol temp = lookupSymbol(varID);
        if(temp != null && temp.scope == currentScope){
            symbolTableLog("ERROR! VARIABLE [ " + varID + " ] ON LINE " + line + " IS ALREADY DECLARED WITHIN THIS SCOPE");
            STErrors++;
            return;
        }
        addSymbol(varID, varType, line);    //add the symbol to the current scope
    }

    //ST -- assignment statement -- lookup each node and check their types
    private void STAssignmentStatement(ASTNode currentNode){
        Symbol firstChild = lookupSymbol(currentNode.children.get(0).name); //this is an id
        ASTNode secondChild = currentNode.children.get(1); //this is an expr
        String lineNumber =  currentNode.children.get(0).token.line;    //the line number

        //check if the first child symbol was created
        if(firstChild == null){
            //throw an error because this variable doesnt exist
            symbolTableLog("ERROR! ATTEMPT TO USE UNDECLARED VARIABLE: [ " + currentNode.children.get(0).name + " ] ON LINE " + lineNumber);
            STErrors++;
            return;
        }
        else{
            //variable was declared so check its type
            switch (firstChild.type) {
                //type int
                case "int":
                    if(secondChild.name.equals("+")){   //right child is +
                        STIntOP(currentNode.children.get(1));
                    }
                    //error for boolop
                    else if(secondChild.name.matches("(==|!=)")){
                        symbolTableLog("ERROR! EVALUATION OF: [ " + secondChild.name + " ] LEADS TO TYPE MISMATCH ON LINE " + lineNumber);
                    }
                    else if(secondChild.name.matches("[a-z]")){ //right child is a variable/id
                        Symbol temp = lookupSymbol(secondChild.name);
                        if(temp == null){   //does this variable exist?
                            symbolTableLog("ERROR! ATTEMPT TO USE UNDECLARED VARIABLE: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                            STErrors++;
                            return;
                        }
                        else{   //if the variable exists check its type
                            if(!temp.type.equals("int")){
                                symbolTableLog("ERROR! TYPE MISMATCH: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                                STErrors++;
                                return;
                            }
                            else{
                                temp.isUsed = true; //mark true
                            }
                        }
                    }
                    //check if the second child is a different type
                    else if((secondChild.name.matches("(true|false)")) || secondChild.token.tokenType.equals("String Literal")){
                        symbolTableLog("ERROR! TYPE MISMATCH: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                        STErrors++;
                        return;
                    }
                    //throw an error for anything unaccounted for
                    else if(!secondChild.name.matches("[0-9]")){
                        symbolTableLog("ERROR! INVALID SYNTAX IN ASSIGNMENT STATEMENT: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                        STErrors++;
                        return;
                    }
                    break;

                //type string
                case "string":
                    //second child is an id/variable
                    if(secondChild.name.matches("[a-z]")){
                        Symbol temp = lookupSymbol(secondChild.name);
                        if(temp == null){   //scope checking
                            symbolTableLog("ERROR! ATTEMPT TO USE UNDECLARED VARIABLE: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                            STErrors++;
                            return;
                        }
                        else{   //type checking
                            if(!temp.type.equals("string")){
                                symbolTableLog("ERROR! TYPE MISMATCH: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                                STErrors++;
                                return;
                            }
                            else{
                                temp.isUsed = true; //mark used
                            }
                        }
                    }
                    //error for boolop
                    else if(secondChild.name.matches("(==|!=)")){
                        symbolTableLog("ERROR! EVALUATION OF: [ " + secondChild.name + " ] LEADS TO TYPE MISMATCH ON LINE " + lineNumber);
                    }
                    //check if it is a digit or boolean value
                    else if(secondChild.name.matches("[0-9]") || (secondChild.name.matches("(true|false)") && !secondChild.token.tokenType.equals("String Literal"))){
                        symbolTableLog("ERROR! TYPE MISMATCH: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                        STErrors++;
                        return;
                    }
                    //throw an error for anything else unaccounted for
                    else if(!secondChild.token.tokenType.equals("String Literal")){
                        symbolTableLog("ERROR! INVALID SYNTAX IN ASSIGNMENT STATEMENT ON LINE " + lineNumber);
                        STErrors++;
                        return;
                    }
                    break;
                
                //type boolean
                case "boolean":
                    if(secondChild.name.matches("(==|!=)")){    //right child is a boolop
                        STBoolOP(secondChild);
                    }
                    else if(secondChild.name.matches("[a-z]")){ //its an id/variable
                        Symbol temp = lookupSymbol(secondChild.name);
                        if(temp == null){ //check scope
                            symbolTableLog("ERROR! ATTEMPT TO USE UNDECLARED VARIABLE: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                            STErrors++;
                            return;
                        }
                        else{   //check type
                            if(!temp.type.equals("boolean")){
                                symbolTableLog("ERROR! TYPE MISMATCH: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                                STErrors++;
                                return;
                            }
                            else{
                                temp.isUsed = true;   //mark used
                            }
                        }
                    }
                    //check if it is a digit or string literal
                    else if(secondChild.name.matches("[0-9]") || secondChild.token.tokenType.equals("String Literal")){
                        symbolTableLog("ERROR! TYPE MISMATCH: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                        STErrors++;
                        return;
                    }
                    //throw an error for anything unaccounted for
                    else if(!secondChild.name.matches("(true|false)")){
                        symbolTableLog("ERROR! INVALID SYNTAX IN ASSIGNMENT STATEMENT ON LINE " + lineNumber);
                        STErrors++;
                        return;
                    }
                    break;
                default:
                        //throw an error for anything unaccounted for
                        symbolTableLog("ERROR! INVALID ASSIGNMENT STATEMENT ON LINE " + lineNumber);
                        STErrors++;
                        return;
            }
        }
        firstChild.isINIT = true;   //mark variable as initialied if no errors are thrown
    }

    //ST -- print statement -- check the scope and initialization of what is being printed
    private void STPrintStatement(ASTNode currentNode){
        //grab the child node and line number
        ASTNode child = currentNode.children.get(0);
        String lineNumber = child.token.line;

        //if its an id check the scope and if its initialized
        if(child.name.matches("[a-z]")){
            Symbol temp = lookupSymbol(child.name);
            if(temp == null){   //scope check and throw error if it doesnt exist
                symbolTableLog("ERROR! ATTEMPT TO USE UNDECLARED VARIABLE: [ " + child.name + " ] ON LINE " + lineNumber);
                STErrors++;
                return;
            }
            else if(!temp.isINIT){
                symbolTableLog("ERROR! ATTEMPT TO USE UNINITIALIZED VARIABLE: [ " + child.name + " ] ON LINE " + lineNumber);
                STErrors++;
                return;
            }
            else{
                temp.isUsed = true; //mark that the variable was used
            }
        }
    }

    //ST -- while statement -- call boolop or throw an error
    private void STWhileStatement(ASTNode whileNode){
        ASTNode booleanExpr = whileNode.children.get(0);    //boolexpr
        String lineNumber = booleanExpr.token.line; //line number

        //check if this is a boolop
        if (booleanExpr.name.matches("(==|!=)")){
            STBoolOP(booleanExpr);
        }
        //throw an error if it is not a boolval
        else if (!booleanExpr.name.matches("(true|false)")){
            symbolTableLog("ERROR! INVALID WHILE STATEMENT: [ " + booleanExpr.name + " ] ON LINE " + lineNumber);
            STErrors++;
            return;
        }
    }

    //ST -- if statement -- call boolop or throw an error
    private void STIfStatement(ASTNode ifNode){
        ASTNode booleanExpr = ifNode.children.get(0);   //boolexpr
        String lineNumber = booleanExpr.token.line; //line number

        //check if this is a boolop
        if (booleanExpr.name.matches("(==|!=)")){  //boolop
            STBoolOP(booleanExpr);
        }
        //throw an error if it is not a boolval
        else if (!booleanExpr.name.matches("(true|false)")){   //boolval
            symbolTableLog("ERROR! IF STATEMENT: [ " + booleanExpr.name + " ] ON LINE " + lineNumber);
            STErrors++;
            return;
        }
    }

    //ST -- Int Op -- check the types of both child nodes when using the + operator
    private void STIntOP(ASTNode intopNode){
        ASTNode leftNode = intopNode.children.get(0);  //always a digit
        ASTNode rightNode = intopNode.children.get(1); //digit, +, or ID
        String lineNumber = leftNode.token.line;    //the line number

        //if the left node is not a digit throw an error
        if(!leftNode.name.matches("[0-9]")){
            symbolTableLog("ERROR! INVALID INT EXPRESSION: [ " + leftNode.name + " ] ON LINE " + lineNumber);
            STErrors++;
            return;
        }

        //if its an intop recursively call itself if not...
        if(rightNode.name.equals("+")){
            STIntOP(rightNode);
        }
        else{
            //if its an ID check the scope and type
            if(rightNode.name.matches("[a-z]")){
                Symbol symbol = lookupSymbol(rightNode.name);
                if (symbol == null){
                    symbolTableLog("ERROR! UNDECLARED IDENTIFIER: [ " + rightNode.name + " ] ON LINE " + lineNumber);
                    STErrors++;
                    return;
                } 
                else{
                    if(!symbol.type.equals("int")){
                        symbolTableLog("ERROR! TYPE MISMATCH: [ " + rightNode.name + " ] ON LINE " + lineNumber);
                        STErrors++;
                        return;
                    }
                    else{
                        symbol.isUsed = true;   //mark used
                    }
                }
            }
            //check if the right child is a different type
            else if((rightNode.name.matches("(true|false)")) || rightNode.token.tokenType.equals("String Literal")){
                symbolTableLog("ERROR! TYPE MISMATCH: [ " + rightNode.name + " ] ON LINE " + lineNumber);
                STErrors++;
                return;
            }
            //throw an error for anything unaccounted for
            else if(!rightNode.name.matches("[0-9]")){
                symbolTableLog("ERROR! INVALID SYNTAX DURING INTEGER OPERATION: [ " + rightNode.name + " ] ON LINE " + lineNumber);
                STErrors++;
                return;
            }
        }
    }

    //ST -- Bool Op -- checks the types of both child nodes when using bool operators
    private String STBoolOP(ASTNode boolopNode){
        ASTNode leftNode = boolopNode.children.get(0); //expr
        ASTNode rightNode = boolopNode.children.get(1);    //expr
        String lineNumber = leftNode.token.line;    //the line number

        //used to check types
        String leftNodeType = "";
        String rightNodeType = "";

        //-----left node check-----
        //call intop
        if(leftNode.name.equals("+")){
            STIntOP(leftNode);
            leftNodeType = "int";
        }
        //call itself recursively
        else if(leftNode.name.matches("(==|!=)")){
            leftNodeType =  STBoolOP(leftNode);
        }
        else{   //otherwise
            //if its an ID check scope or set type
            if(leftNode.name.matches("[a-z]")){
                Symbol symbol = lookupSymbol(leftNode.name);
                if (symbol == null){
                    symbolTableLog("ERROR! UNDECLARED IDENTIFIER: [ " + leftNode.name + " ] ON LINE " + lineNumber);
                    STErrors++;
                    return null;
                } 
                else{
                    leftNodeType = symbol.type; //set type
                    symbol.isUsed = true;   //mark true
                }
            }
            //if its a digit set the type to int
            else if(leftNode.name.matches("[0-9]")){
                leftNodeType = "int";
            }
            //if its a boolean set the type to boolean
            else if (leftNode.name.matches("(true|false)")){
                leftNodeType = "boolean";
            }
            //if its a string literal set the type to string
            else if(leftNode.token.tokenType.equals("String Literal")){
                leftNodeType = "string";
            }
            else{   //otherwise throw an error
                symbolTableLog("ERROR! INVALID SYNTAX DURING BOOLEAN OPERATION: [ " + leftNode.name + " ] ON LINE " + lineNumber);
                STErrors++;
                return null;
            }
        }

        //-----right node check-----
        //check if its an int op
        if(rightNode.name.equals("+")){
            STIntOP(rightNode);
            rightNodeType = "int";
        }
        //call itself recursively
        else if(rightNode.name.matches("(==|!=)")){
            rightNodeType =  STBoolOP(rightNode);
        }
        else{   //otherwise
            //if its an ID check the scope or set type
            if(rightNode.name.matches("[a-z]")){
                Symbol symbol = lookupSymbol(rightNode.name);
                if (symbol == null){
                    symbolTableLog("ERROR! UNDECLARED IDENTIFIER: [ " + rightNode.name + " ] ON LINE " + lineNumber);
                    STErrors++;
                    return null;
                } 
                else{
                    rightNodeType = symbol.type;    //set type
                    symbol.isUsed = true;   //mark true
                }
            }
            //if its a digit set the type to int
            else if(rightNode.name.matches("[0-9]")){
                rightNodeType = "int";
            }
            //if its a boolean set the type to boolean
            else if (rightNode.name.matches("(true|false)")){
                rightNodeType = "boolean";
            }
            //if its a string literal set the type to string
            else if(rightNode.token.tokenType.equals("String Literal")){
                rightNodeType = "string";
            }
            else{   //otherwise throw an error
                symbolTableLog("ERROR! INVALID SYNTAX DURING BOOLEAN OPERATION: [ " + rightNode.name + " ] ON LINE " + lineNumber);
                STErrors++;
                return null;
            }
        }

        //check if the types match...if they dont throw an error
        if(!leftNodeType.equals(rightNodeType)){
            symbolTableLog("ERROR! TYPE MISMATCH ON LINE " + lineNumber);
            STErrors++;
            return null;
        }
        else{
            return "boolean";
        }
    }

    //ST -- Add Symbol -- adds a symbol to the symbol table
    private void addSymbol(String id, String type, String line){
        //if the symbol already exists return
        if(current.symbols.contains(id)){
            return;
        }
        else{   //otherwise create a symbol and add it to the current scope
            Symbol newSymbol = new Symbol(id, type, currentScope, line);
            current.symbols.put(newSymbol.name, newSymbol);
        }
    }

    //ST -- Lookup Symbol -- looks up a symbol within each ST/scope and returns it
    private Symbol lookupSymbol(String id){
        //check if the current node is null
        if(current == null){
            return null;
        }

        //temp variables and pointers used to traverse through the tree
        Symbol temp = null;
        SymbolTableNode tempNode = current;
        int tempPointer = currentScope;
        
        //continuously iterate until you find it or return null
        while(tempPointer != -1 && temp == null && tempNode != null){
            temp = tempNode.symbols.get(id);
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

    //ST -- add node -- adds a node to the symbol table
    public void addNodeSymbolTable(int scope){
        //create a new node to be added to the symbol table
        SymbolTableNode newNode = new SymbolTableNode(scope);

        //check if the tree has a root node
        if(root == null){
            //update root node to new node
            root = newNode;
            newNode.parent = null;
        }
        else{
            //if there is already a root then add the newnode to child array
            newNode.parent = current;
            current.children.add(newNode);
        }
        //move current to new node
        current = newNode;
    }


    //--------------------------------Symbol Table Output Functions-----------------------------------------

    //evaluates the success of the symbol table
    public void STEvaluate(){
        if(STErrors > 0){
            STWarningOutput(root);
            symbolTableLog("Symbol Table Generation Failed... Warnings: " + STWarnings + " Errors: " + STErrors);
        }
        else{
            STWarningOutput(root);
            symbolTableLog("Symbol Table Generated Successfully... Warnings: " + STWarnings + " Errors: " + STErrors);
            testScopes();   //outputs symbol table tree
            printSymbolTable(); //outputs the symbol table
        }
    }

    private void STWarningOutput(SymbolTableNode node){
        //iterates through the symbols and outputs warnings
        for(Symbol symbol : node.symbols.values()){
            if(!symbol.isUsed){
                symbolTableLog("WARNING! VARIABLE IS DECLARED BUT NOT USED: [ " + symbol.name + " ]");
                STWarnings++;
            }
            if(!symbol.isINIT){
                symbolTableLog("WARNING! VARIABLE IS DECLARED BUT NOT INITIALIZED: [ " + symbol.name + " ]");
                STWarnings++;
            }
        }
        
        //iterates through the scope tree
        for(SymbolTableNode child : node.children){
            STWarningOutput(child);
        }
    }

    //----------outputs the symbol table as a tree--------------

    //ST -- test scopes
    public void testScopes(){
        expand(root, 0);
        System.out.print("\n");
        symbolTableLog("\n" + traversal);
    }

    //ST -- expand
    public void expand(SymbolTableNode node, int depth){
        //space nodes out based off of the current depth
        for(int i = 0; i < depth; i++){
            traversal += "-";
        }

        //if this node is a leaf node output the name
        if(node.children.size() == 0){
            traversal += "[" + node.scope + "]";
            traversal += "\n";
        }
        else{
            //this node is not a leaf node
            traversal += "<" + node.scope + "> \n";

            //recursion!!! -- call the next child and increment the depth
            for(int j = 0; j < node.children.size(); j++){
                expand(node.children.get(j), depth + 1);
            }
        }
    }

    //----------outputs the symbol table as a table--------------
    
    //ST -- print ST
    public void printSymbolTable(){
        //basic output structure
        symbolTableLog("------------------------------------------");
        symbolTableLog("Name\tType\tScope\tisINIT\tisUsed\tLine");
        symbolTableLog("------------------------------------------");
        //call expand
        STexpand(root);
    }

    //ST -- expand v2
    public void STexpand(SymbolTableNode node){
        //iterates through the symbols
        for(Symbol symbol : node.symbols.values()){
            symbolTableLog(symbol.name + "\t" + symbol.type + "\t" + symbol.scope + "\t" + symbol.isINIT + "\t" + symbol.isUsed + "\t" + symbol.line);
        }
        
        //iterates through the scope tree
        for(SymbolTableNode child : node.children){
            STexpand(child);
        }
    }
}