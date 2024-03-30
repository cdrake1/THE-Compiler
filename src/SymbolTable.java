/*
    Symbol Table file
    Creates a symbol table or a tree of hashmaps
    To be used in Symantic Analysis
*/

//tree of hashtables?-------------

//Collin Drakes Symbol Table
public class SymbolTable {
    SymbolTableNode root;  //pointer to the root node
    SymbolTableNode current;   //pointer to the current node
    int currentScope;   //scope pointer
    String traversal;   //string to hold symbol table traversal
    int STErrors;

    //Symbol Table constructor -- initializes variables
    public SymbolTable(){
        this.root = null;
        this.current = null;
        this.currentScope = 0;
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
                //figure out when to close scope
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
                STWhileStatement();
                
                break;
            case "If statement":
                STIfStatement();
                break;
            default:
                //throw error?
                STErrors++;
                break;
        }

        //iterate through all children recursively
        for(Node child : node.children){
            inOrder(child);
        }
    }

    //opens a new scope within the symbol table
    private void openScope(){
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
        //if the current scope is not the root node then move the pointer to its parent
        if(current.scope != 0 && current.parent != null){
            current = current.parent;
            currentScope--;
        }
    }

    //add symbol to the current nodes hash table
    private void STVarDecl(Node currentNode){
        String varID = currentNode.children.get(0).name;
        String varType = currentNode.children.get(1).name;
        addSymbol(varID, varType);
    }

    //lookup the symbol and check types
    private void STAssignmentStatement(Node currentNode){
        Symbol temp = lookupSymbol(currentNode.children.get(0).name);
        if(temp == null){
            //throw error
            STErrors++;
            return;
        }
        else{
            switch (temp.type) {
                case "int":
                    //if to check what 2nd child node is ( digit, + or id)
                    break;
                case "string":
                    // just check 2nd child node for " or chars "
                    break;
                case "boolean":
                    //if to check what 2nd child node is (boolval and expr)
                    break;
                default:
                    //throw error
                    break;
            }
            //switch case for symbol type?
            //eval expression
        }
        temp.isINIT = true;
    }

    //lookup symbol to check scope
    private void STPrintStatement(Node currentNode){
        Symbol temp = lookupSymbol(currentNode.children.get(0).name);
        if(temp == null){
            //throw error
            STErrors++;
            return;
        }
    }


    private void STWhileStatement(){}
    private void STIfStatement(){}
    private void STBoolOP(){}
    private void STIntOP(Node nextNode){
        //recursive to?? -- a = 1+1+1+1 etc
    }

    //looks up a symbol to check if it was ever used
    private boolean checkUsed(){
        //lookup symbol and check if used
        //return true or false
    }

    //looks up a symbol to check if it was ever initialized
    private boolean checkINIT(){
        //lookup symbol and check if init
        //return true or false
    }

    //adds a symbol to the symbol table
    private void addSymbol(String id, String type){
        Symbol newSymbol = new Symbol(id, type, currentScope);
        current.symbols.put(newSymbol.name, newSymbol);
    }

    //looks up a symbol within each ST/scope and returns it
    private Symbol lookupSymbol(String id){
        Symbol temp = null;
        SymbolTableNode tempNode = current;
        int tempPointer = currentScope;
        while(tempPointer != -1 && temp == null){
            temp = tempNode.symbols.get(id);
            tempPointer--;
            tempNode = tempNode.parent;
        }
        return temp;
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


    //-------------------------------------------------------------
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
}