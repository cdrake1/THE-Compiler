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
    int outputScope;    //pointer for output function
    String traversal;   //string to hold symbol table traversal
    int STErrors;   //counts total errors

    //Symbol Table constructor -- initializes variables
    public SymbolTable(){
        this.root = null;
        this.current = null;
        this.currentScope = 0;
        this.outputScope = 0;
        this.STErrors = 0;
        this.traversal = "";
    }   

    //outputs the processes completed within the symbol table
    public void symbolTableLog(String output){
        System.out.println("Symantic Analyzer - Symbol Table - " + output);
    }

    //function for depth first in order traversal of AST
    public void inOrder(Node node){
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
                //STWhileStatement(node);
                break;
            case "If statement":
                //STIfStatement(node);
                break;
            default:
                //do nothing
                break;
        }

        //iterate through all children recursively
        for(Node child : node.children){
            inOrder(child);
        }
    }

    //opens a new scope within the symbol table
    private void openScope(){
        System.out.println("openscope");
        //if there is no root then create 1. Otherwise increment the pointer and create a childnode
        if(root == null){
            addNodeSymbolTable(currentScope);
        }
        else{
            currentScope++;
            addNodeSymbolTable(currentScope);
        }
    }

    //closes the most recently opened scope
    private void closeScope(){
        System.out.println("clpse scope");
        //if the current scope is not the root node then move the pointer to its parent
        if(current.scope != 0 && current.parent != null){
            current = current.parent;
            currentScope--;
        }
    }

    //add symbol to the current nodes hash table
    private void STVarDecl(Node currentNode){
        System.out.println("var decl");
        String varType = currentNode.children.get(0).name;
        String varID = currentNode.children.get(1).name;
        addSymbol(varID, varType);
    }

    //lookup the symbol and check types
    private void STAssignmentStatement(Node currentNode){
        System.out.println("assignment statement");
        Symbol firstChild = lookupSymbol(currentNode.children.get(0).name); //grab the left child nodes symbol
        Node secondChild = currentNode.children.get(1); //grab the right child node

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
                        //STIntOP(currentNode.children.get(1));
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

    //lookup symbol to check scope
    private void STPrintStatement(Node currentNode){
        //grab the child node
        Node child = currentNode.children.get(0);
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

    //adds a symbol to the symbol table
    private void addSymbol(String id, String type){
        if(current.symbols.contains(id)){
            return;
        }
        else{
            Symbol newSymbol = new Symbol(id, type, currentScope);
            current.symbols.put(newSymbol.name, newSymbol);
        }
    }

    //looks up a symbol within each ST/scope and returns it
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

    //adds a node to the symbol table
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


    //--------------------Symbol Table Output Functions-----------------------------------------

    //----------outputs the symbol table as a tree--------------
    public void testScopes(){
        expand(root, 0);
        System.out.print("\n");
        symbolTableLog("\n" + traversal);
    }

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
    public void printSymbolTable(){
        symbolTableLog("--------------------------------------");
        symbolTableLog("Name\tType\tScope\tisINIT\tisUsed");
        symbolTableLog("--------------------------------------");
        STexpand(root);
    }

    public void STexpand(SymbolTableNode node){
        for(Symbol symbol : node.symbols.values()){
            symbolTableLog(symbol.name + "\t" + symbol.type + "\t" + symbol.scope + "\t" + symbol.isINIT + "\t" + symbol.isUsed);
        }
        for(SymbolTableNode child : node.children){
            STexpand(child);
        }
    }
}