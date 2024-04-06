/*
    Symbol Table file
    Creates a Symbol Table otherwise known as tree of hashtables
    Used in Symantic Analysis
*/

//Collin Drakes Symbol Table
public class SymbolTable {
    SymbolTableNode root;  //pointer to the root node
    SymbolTableNode current;   //pointer to the current node
    int currentScope;   //scope pointer
    int scopeCount; //counter for how many scopes we have (used for node numbering)
    int outputScope;    //pointer for output function
    int STErrors;   //counts total errors
    int STWarnings; //counts total warnings
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

    //function for the depth first in order traversal of the AST
    public void inOrder(ASTNode node){
        //if the node is null return to avoid errors
        if(node == null){
            return;
        }

        //check the name of the tokens to determine which function to call
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

        //iterate through all of the nodes children recursively
        for(ASTNode child : node.children){
            inOrder(child);
        }

        //close scope after processing block nodes
        if(node.name.equals("Block")) {
            closeScope();
        }
    }

    //open scope -- opens a new scope within the tree
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

    //close scope -- closes the most recently opened scope
    private void closeScope(){
        //if the current scope is not the root node then move the pointer to its parent
        if(current.scope != 0 && current.parent != null){
            current = current.parent;
            currentScope--;
        }
    }

    //var decl -- adds a symbol to the current nodes hash table
    private void STVarDecl(ASTNode currentNode){
        //grab the type, id, and line
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

    //assignment statement -- lookup each node and check their types
    private void STAssignmentStatement(ASTNode currentNode){
        Symbol firstChild = lookupSymbol(currentNode.children.get(0).name); //this is an id
        ASTNode secondChild = currentNode.children.get(1); //this is an expr
        String lineNumber =  currentNode.children.get(0).token.line;    //the line number

        //check if the firstchild symbol was created. If it was check its type
        if(firstChild == null){
            symbolTableLog("ERROR! ATTEMPT TO USE UNDECLARED VARIABLE: [ " + currentNode.children.get(0).name + " ] ON LINE " + lineNumber);
            STErrors++;
            return;
        }
        else{
            switch (firstChild.type) {
                case "int":
                    //since the type is an int we need to check for digits, +, or IDs. Throw errors for anything else...
                    if(secondChild.name.equals("+")){
                        STIntOP(currentNode.children.get(1));
                    }
                    else if(secondChild.name.matches("(==|!=)")){
                        symbolTableLog("ERROR! EVALUATION OF: [ " + secondChild.name + " ] LEADS TO TYPE MISMATCH ON LINE " + lineNumber);
                    }
                    else if(secondChild.name.matches("[a-z]")){
                        Symbol temp = lookupSymbol(secondChild.name);
                        if(temp == null){
                            symbolTableLog("ERROR! ATTEMPT TO USE UNDECLARED VARIABLE: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                            STErrors++;
                            return;
                        }
                        else{
                            if(!temp.type.equals("int")){
                                symbolTableLog("ERROR! TYPE MISMATCH: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                                STErrors++;
                                return;
                            }
                            else{
                                temp.isUsed = true; //mark that the variable was used
                            }
                        }
                    }
                    else if((secondChild.name.matches("(true|false)")) || secondChild.token.tokenType.equals("String Literal")){
                        symbolTableLog("ERROR! TYPE MISMATCH: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                        STErrors++;
                        return;
                    }
                    else if(!secondChild.name.matches("[0-9]")){
                        symbolTableLog("ERROR! INVALID SYNTAX IN ASSIGNMENT STATEMENT: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                        STErrors++;
                        return;
                    }
                    break;

                case "string":
                    //since the type is a string we need to check for IDs or other string literals. Throw errors for anything else...
                    if(secondChild.name.matches("[a-z]")){
                        Symbol temp = lookupSymbol(secondChild.name);
                        if(temp == null){
                            symbolTableLog("ERROR! ATTEMPT TO USE UNDECLARED VARIABLE: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                            STErrors++;
                            return;
                        }
                        else{
                            if(!temp.type.equals("string")){
                                symbolTableLog("ERROR! TYPE MISMATCH: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                                STErrors++;
                                return;
                            }
                            else{
                                temp.isUsed = true; //mark that the variable was used
                            }
                        }
                    }
                    else if(secondChild.name.matches("(==|!=)")){
                        symbolTableLog("ERROR! EVALUATION OF: [ " + secondChild.name + " ] LEADS TO TYPE MISMATCH ON LINE " + lineNumber);
                    }
                    else if(secondChild.name.matches("[0-9]") || (secondChild.name.matches("(true|false)") && !secondChild.token.tokenType.equals("String Literal"))){
                        symbolTableLog("ERROR! TYPE MISMATCH: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                        STErrors++;
                        return;
                    }
                    else if(!secondChild.token.tokenType.equals("String Literal")){
                        symbolTableLog("ERROR! INVALID SYNTAX IN ASSIGNMENT STATEMENT ON LINE " + lineNumber);
                        STErrors++;
                        return;
                    }
                    break;
                case "boolean":
                    //since the type is a boolean we need to check for boolops, boolvals or IDs. Throw errors for anything else...
                    if(secondChild.name.matches("(==|!=)")){
                        STBoolOP(secondChild);
                    }
                    else if(secondChild.name.matches("[a-z]")){
                        Symbol temp = lookupSymbol(secondChild.name);
                        if(temp == null){
                            symbolTableLog("ERROR! ATTEMPT TO USE UNDECLARED VARIABLE: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                            STErrors++;
                            return;
                        }
                        else{
                            if(!temp.type.equals("boolean")){
                                symbolTableLog("ERROR! TYPE MISMATCH: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                                STErrors++;
                                return;
                            }
                            else{
                                temp.isUsed = true; //mark that the variable was used
                            }
                        }
                    }
                    else if(secondChild.name.matches("[0-9]") || secondChild.token.tokenType.equals("String Literal")){
                        symbolTableLog("ERROR! TYPE MISMATCH: [ " + secondChild.name + " ] ON LINE " + lineNumber);
                        STErrors++;
                        return;
                    }
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

    //print statement -- check the scope and initialized flag of IDs
    private void STPrintStatement(ASTNode currentNode){
        //grab the child node and line number
        ASTNode child = currentNode.children.get(0);
        String lineNumber = child.token.line;

        //if its an id check the scope and if its initialized
        if(child.name.matches("[a-z]")){
            Symbol temp = lookupSymbol(child.name);
            if(temp == null){
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

    //while statement -- call boolop or throw an error
    private void STWhileStatement(ASTNode whileNode){
        ASTNode booleanExpr = whileNode.children.get(0);    //boolexpr
        String lineNumber = booleanExpr.token.line; //line number

        //check if this is a boolop or throw an error
        if (booleanExpr.name.matches("(==|!=)")){
            STBoolOP(booleanExpr);
        }
        else if (!booleanExpr.name.matches("(true|false)")){
            symbolTableLog("ERROR! INVALID WHILE STATEMENT: [ " + booleanExpr.name + " ] ON LINE " + lineNumber);
            STErrors++;
            return;
        }
    }

    //if statement -- call boolop or throw an error
    private void STIfStatement(ASTNode ifNode){
        ASTNode booleanExpr = ifNode.children.get(0);   //boolexpr
        String lineNumber = booleanExpr.token.line; //line number

        //check if this is a boolop or throw an error
        if (booleanExpr.name.matches("(==|!=)")){
            STBoolOP(booleanExpr);
        }
        else if (!booleanExpr.name.matches("(true|false)")){
            symbolTableLog("ERROR! IF STATEMENT: [ " + booleanExpr.name + " ] ON LINE " + lineNumber);
            STErrors++;
            return;
        }
    }

    //Int Op -- check the types of both child nodes when using the + operator
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

        //check for digits, +, or IDs
        if(rightNode.name.equals("+")){
            STIntOP(rightNode);
        }
        else{
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
                        symbol.isUsed = true;   //mark that the variable was used
                    }
                }
            }
            else if((rightNode.name.matches("(true|false)")) || rightNode.token.tokenType.equals("String Literal")){
                symbolTableLog("ERROR! TYPE MISMATCH: [ " + rightNode.name + " ] ON LINE " + lineNumber);
                STErrors++;
                return;
            }
            else if(!rightNode.name.matches("[0-9]")){
                symbolTableLog("ERROR! INVALID SYNTAX DURING INTEGER OPERATION: [ " + rightNode.name + " ] ON LINE " + lineNumber);
                STErrors++;
                return;
            }
        }
    }

    //Bool Op -- checks the types of both child nodes when using bool operators
    private String STBoolOP(ASTNode boolopNode){
        ASTNode leftNode = boolopNode.children.get(0); //expr
        ASTNode rightNode = boolopNode.children.get(1);    //expr
        String lineNumber = leftNode.token.line;    //the line number

        //check types
        String leftNodeType = "";
        String rightNodeType = "";

        //-----left node check-----
        //check if its an int op or a boolop. Otherwise mark down the type
        if(leftNode.name.equals("+")){
            STIntOP(leftNode);
            leftNodeType = "int";
        }
        else if(leftNode.name.matches("(==|!=)")){
            leftNodeType =  STBoolOP(leftNode);
        }
        else{
            if(leftNode.name.matches("[a-z]")){
                Symbol symbol = lookupSymbol(leftNode.name);
                if (symbol == null){
                    symbolTableLog("ERROR! UNDECLARED IDENTIFIER: [ " + leftNode.name + " ] ON LINE " + lineNumber);
                    STErrors++;
                    return null;
                } 
                else{
                    leftNodeType = symbol.type;
                    symbol.isUsed = true;   //mark that the variable was used
                }
            }
            else if(leftNode.name.matches("[0-9]")){
                leftNodeType = "int";
            }
            else if (leftNode.name.matches("(true|false)")){
                leftNodeType = "boolean";
            }
            else if(leftNode.token.tokenType.equals("String Literal")){
                leftNodeType = "string";
            }
            else{
                symbolTableLog("ERROR! INVALID SYNTAX DURING BOOLEAN OPERATION: [ " + leftNode.name + " ] ON LINE " + lineNumber);
                STErrors++;
                return null;
            }
        }

        //-----right node check-----
        //check if its an int op or a boolop. Otherwise mark down the type
        if(rightNode.name.equals("+")){
            STIntOP(rightNode);
            rightNodeType = "int";
        }
        else if(rightNode.name.matches("(==|!=)")){
            rightNodeType =  STBoolOP(rightNode);
        }
        else{
            if(rightNode.name.matches("[a-z]")){
                Symbol symbol = lookupSymbol(rightNode.name);
                if (symbol == null){
                    symbolTableLog("ERROR! UNDECLARED IDENTIFIER: [ " + rightNode.name + " ] ON LINE " + lineNumber);
                    STErrors++;
                    return null;
                } 
                else{
                    rightNodeType = symbol.type;
                    symbol.isUsed = true;   //mark that the variable was used
                }
            }
            else if(rightNode.name.matches("[0-9]")){
                rightNodeType = "int";
            }
            else if (rightNode.name.matches("(true|false)")){
                rightNodeType = "boolean";
            }
            else if(rightNode.token.tokenType.equals("String Literal")){
                rightNodeType = "string";
            }
            else{
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

    //Add Symbol -- adds a symbol to the current scope of the Symbol Table
    private void addSymbol(String id, String type, String line){
        //if the symbol already exists return. Otherwise create a symbol and add it to the current scope
        if(current.symbols.contains(id)){
            return;
        }
        else{
            Symbol newSymbol = new Symbol(id, type, currentScope, line);
            current.symbols.put(newSymbol.name, newSymbol);
        }
    }

    //Lookup Symbol -- looks up a symbol within each ST/scope and returns it
    private Symbol lookupSymbol(String id){
        //check if the current node is null to avoid errors
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

    //add node -- adds a node to the symbol table
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
            STScopes();   //outputs symbol table tree
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
        
        //iterates through the symbol table
        for(SymbolTableNode child : node.children){
            STWarningOutput(child);
        }
    }

    //----------outputs the symbol table as a tree--------------

    //outputs scope
    public void STScopes(){
        expand(root, 0);
        System.out.print("\n");
        symbolTableLog("\n" + traversal);
    }

    //ST expand
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