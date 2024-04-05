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
    String traversal;   //string to hold symbol table traversal

    //Symbol Table constructor -- initializes variables
    public SymbolTable(){
        this.root = null;
        this.current = null;
        this.currentScope = 0;
        this.scopeCount = 0;
        this.outputScope = 0;
        this.STErrors = 0;
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

        //close scope after processing block
        if(node.name.equals("Block")) {
            closeScope();
        }
    }

    //ST -- open scope -- opens a new scope within the symbol table
    private void openScope(){
        System.out.println("openscope");
        //if there is no root then create 1. Otherwise increment the pointer and create a childnode
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
        System.out.println("clpse scope");
        //if the current scope is not the root node then move the pointer to its parent
        if(current.scope != 0 && current.parent != null){
            current = current.parent;
            currentScope--;
        }
    }

    //ST -- var decl -- add symbol to the current nodes hash table
    private void STVarDecl(ASTNode currentNode){
        System.out.println("var decl");
        String varType = currentNode.children.get(0).name;
        String varID = currentNode.children.get(1).name;
        String line = currentNode.children.get(0).token.line;
        addSymbol(varID, varType, line);
    }

    //ST -- assignment statement -- lookup the symbol and check types
    private void STAssignmentStatement(ASTNode currentNode){
        System.out.println("assignment statement");
        Symbol firstChild = lookupSymbol(currentNode.children.get(0).name); //grab the left child nodes symbol
        ASTNode secondChild = currentNode.children.get(1); //grab the right child node

        //check if the first child symbol was created
        if(firstChild == null){
            //throw error because this variable doesnt exist
            symbolTableLog("ERROR! ATTEMPT TO USE UNDECLARED VARIABLE: " + currentNode.children.get(0).name);
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
                    else if(secondChild.name.matches("[a-z]")){ //right child is a variable/id
                        Symbol temp = lookupSymbol(secondChild.name);
                        if(temp == null){   //does this variable exist?
                            symbolTableLog("ERROR! ATTEMPT TO USE UNDECLARED VARIABLE: " + secondChild.name);
                            STErrors++;
                            return;
                        }
                        else{   //if the variable exists check its type
                            if(!temp.type.equals("int")){
                                symbolTableLog("ERROR! TYPE MISMATCH: " + secondChild.name);
                                STErrors++;
                                return;
                            }
                        }
                    }
                    else if((secondChild.name.matches("(true|false)")) || (secondChild.name.length() >= 2 && secondChild.name.matches("[a-z]+"))){    //check if second child is not a digit
                        symbolTableLog("ERROR! TYPE MISMATCH: " + secondChild.name);
                        STErrors++;
                        return;
                    }
                    else if(!secondChild.name.matches("[0-9]")){
                        symbolTableLog("ERROR! INVALID SYNTAX IN ASSIGNMENT STATEMENT: " + secondChild.name);
                        STErrors++;
                        return;
                    }
                    break;

                //type string
                case "string":
                    if(secondChild.name.length() == 1 && secondChild.name.matches("[a-z]")){    //second child is an id/variable
                        Symbol temp = lookupSymbol(secondChild.name);
                        if(temp == null){   //scope checking
                            symbolTableLog("ERROR! ATTEMPT TO USE UNDECLARED VARIABLE: " + secondChild.name);
                            STErrors++;
                            return;
                        }
                        else{   //type checking
                            if(!temp.type.equals("string")){
                                symbolTableLog("ERROR! TYPE MISMATCH: " + secondChild.name);
                                STErrors++;
                                return;
                            }
                        }
                    }
                    else if(secondChild.name.matches("[0-9]") || secondChild.name.matches("(true|false)")){ //if its a digit throw type mismatch -- is this useless?
                        symbolTableLog("ERROR! TYPE MISMATCH: " + secondChild.name);
                        STErrors++;
                        return;
                    }
                    else if(!(secondChild.name.length() >= 2 && secondChild.name.matches("[a-z]+"))){
                        symbolTableLog("ERROR! INVALID SYNTAX IN ASSIGNMENT STATEMENT");
                        STErrors++;
                        return;
                    }
                    break;
                
                //type boolean
                case "boolean":
                    if(secondChild.name.matches("(==|!=)")){    //right child is a boolop
                        //STBoolOP(secondChild);
                    }
                    else if(secondChild.name.matches("[a-z]")){ //its an id/variable
                        Symbol boolId = lookupSymbol(secondChild.name);
                        if(boolId == null){ //check scope
                            symbolTableLog("ERROR! ATTEMPT TO USE UNDECLARED VARIABLE: " + secondChild.name);
                            STErrors++;
                            return;
                        }
                        else{   //check type
                            if(!boolId.type.equals("boolean")){
                                symbolTableLog("ERROR! TYPE MISMATCH: " + secondChild.name);
                                STErrors++;
                                return;
                            }
                        }
                    }
                    else if(secondChild.name.matches("[0-9]") || ((secondChild.name.length() >= 2 && secondChild.name.matches("[a-z]+") && !secondChild.name.matches("(true|false)")))){
                        symbolTableLog("ERROR! TYPE MISMATCH: " + secondChild.name);
                        STErrors++;
                        return;
                    }
                    else if(!secondChild.name.matches("(true|false)")){  //boolval
                        symbolTableLog("ERROR! INVALID SYNTAX IN ASSIGNMENT STATEMENT");
                        STErrors++;
                        return;
                    }
                    break;
                default:
                        symbolTableLog("ERROR! INVALID ASSIGNMENT STATEMENT");
                        STErrors++;
                    break;
            }
        }
        firstChild.isINIT = true;
    }

    //ST -- print statement -- lookup symbol to check scope
    private void STPrintStatement(ASTNode currentNode){
        System.out.println("print statement");
        //grab the child node
        ASTNode child = currentNode.children.get(0);
        Symbol temp = lookupSymbol(child.name);
        if(temp == null){   //scope check and throw error if it doesnt exist
            symbolTableLog("ATTEMPT TO USE UNDECLARED VARIABLE: " + child.name);
            STErrors++;
            return;
        }
        else if(!temp.isINIT){
            symbolTableLog("ATTEMPT TO USE UNINITIALIZED VARIABLE: " + child.name);
            STErrors++;
            return;
        }
    }

    //ST -- while statement -- lookup symbol to check scope
    private void STWhileStatement(ASTNode whileNode){
        System.out.println("while statement");
        ASTNode booleanExpr = whileNode.children.get(0);

        if (booleanExpr.name.matches("(==|!=)")){  //boolop
            STBoolOP(booleanExpr);
        }
        else if (!booleanExpr.name.matches("(true|false)")){   //boolval
            symbolTableLog("ERROR! INVALID BOOLEAN EXPRESSION: " + booleanExpr.name);
            STErrors++;
        }
    }

    //ST -- if statement -- lookup symbol to check scope
    private void STIfStatement(ASTNode ifNode){
        System.out.println("if statement");
        ASTNode booleanExpr = ifNode.children.get(0);

        if (booleanExpr.name.matches("(==|!=)")){  //boolop
            STBoolOP(booleanExpr);
        }
        else if (!booleanExpr.name.matches("(true|false)")){   //boolval
            symbolTableLog("ERROR! INVALID BOOLEAN EXPRESSION: " + booleanExpr.name);
            STErrors++;
        }
    }

    //ST -- Int Op -- lookup symbol to check scope
    private void STIntOP(ASTNode intopNode){
        System.out.println("int op");
        ASTNode leftNode = intopNode.children.get(0);  //always a digit
        ASTNode rightNode = intopNode.children.get(1); //digit, +, ID

        if(!leftNode.name.matches("[0-9]")){
            symbolTableLog("ERROR! INVALID INT EXPRESSION: " + leftNode.name);
            STErrors++;
            return;
        }

        if(rightNode.name.equals("+")){
            STIntOP(rightNode);
        }
        else{
            if(rightNode.name.matches("[a-z]")){
                Symbol symbol = lookupSymbol(rightNode.name);
                if (symbol == null){
                    symbolTableLog("ERROR! UNDECLARED IDENTIFIER: " + rightNode.name);
                    STErrors++;
                    return;
                } 
                else{
                    if(!symbol.type.equals("int")){
                        symbolTableLog("ERROR! TYPE MISMATCH: " + rightNode.name);
                        STErrors++;
                        return;
                    }
                }
            }
            else if((rightNode.name.matches("(true|false)")) || (rightNode.name.length() >= 2 && rightNode.name.matches("[a-z]+"))){    //check if second child is not a digit
                symbolTableLog("ERROR! TYPE MISMATCH: " + rightNode.name);
                STErrors++;
                return;
            }
            else if(!rightNode.name.matches("[0-9]")){
                symbolTableLog("ERROR! INVALID SYNTAX IN ASSIGNMENT STATEMENT: " + rightNode.name);
                STErrors++;
                return;
            }
        }
    }

    //ST -- Bool Op -- adds a symbol to the symbol table
    private void STBoolOP(ASTNode boolopNode){
        System.out.println("bool op");
        ASTNode leftNode = boolopNode.children.get(0); //expr
        ASTNode rightNode = boolopNode.children.get(1);    //expr

        String leftNodeType = "";
        String rightNodeType = "";

        if(leftNode.name.equals("+")){
            STIntOP(leftNode);
        }
        else if(leftNode.name.matches("(==|!=)")){
            STBoolOP(leftNode);
        }
        else{
            if(leftNode.name.matches("[a-z]")){
                Symbol symbol = lookupSymbol(leftNode.name);
                if (symbol == null){
                    symbolTableLog("ERROR! UNDECLARED IDENTIFIER: " + leftNode.name);
                    STErrors++;
                    return;
                } 
                else{
                    leftNodeType = symbol.type;
                }
            }
            else if(leftNode.name.matches("[0-9]")){
                leftNodeType = "int";
            }
            else if (leftNode.name.matches("(true|false)")){
                leftNodeType = "boolean";
            }
            else if((leftNode.name.length() >= 2 && leftNode.name.matches("[a-z]+"))){
                leftNodeType = "string";
            }
            else{
                symbolTableLog("ERROR! INVALID SYNTAX IN BOOLEAN OP STATEMENT: " + leftNode.name);
                STErrors++;
                return;
            }
        }

        if(rightNode.name.equals("+")){
            STIntOP(rightNode);
        }
        else if(rightNode.name.matches("(==|!=)")){
            STBoolOP(rightNode);
        }
        else{
            if(rightNode.name.matches("[a-z]")){
                Symbol symbol = lookupSymbol(rightNode.name);
                if (symbol == null){
                    symbolTableLog("ERROR! UNDECLARED IDENTIFIER: " + rightNode.name);
                    STErrors++;
                    return;
                } 
                else{
                    rightNodeType = symbol.type;
                }
            }
            else if(rightNode.name.matches("[0-9]")){
                rightNodeType = "int";
            }
            else if (rightNode.name.matches("(true|false)")){
                rightNodeType = "boolean";
            }
            else if((rightNode.name.length() >= 2 && rightNode.name.matches("[a-z]+"))){
                rightNodeType = "string";
            }
            else{
                symbolTableLog("ERROR! INVALID SYNTAX IN BOOLEAN OP STATEMENT: " + rightNode.name);
                STErrors++;
                return;
            }
        }

        if(!leftNodeType.equals(rightNodeType)){
            symbolTableLog("ERROR! TYPE MISMATCH: " + rightNode.name);
            STErrors++;
            return;
        }
    }

    //ST -- Add Symbol -- adds a symbol to the symbol table
    private void addSymbol(String id, String type, String line){
        if(current.symbols.contains(id)){
            return;
        }
        else{
            Symbol newSymbol = new Symbol(id, type, currentScope, line);
            current.symbols.put(newSymbol.name, newSymbol);
        }
    }

    //ST -- Lookup Symbol -- looks up a symbol within each ST/scope and returns it
    private Symbol lookupSymbol(String id){
        Symbol temp = null;
        SymbolTableNode tempNode = current;
        int tempPointer = currentScope;
        while(tempPointer != -1 && temp == null){
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
        for(Symbol symbol : node.symbols.values()){
            symbolTableLog(symbol.name + "\t" + symbol.type + "\t" + symbol.scope + "\t" + symbol.isINIT + "\t" + symbol.isUsed + "\t" + symbol.line);
        }
        for(SymbolTableNode child : node.children){
            STexpand(child);
        }
    }
}