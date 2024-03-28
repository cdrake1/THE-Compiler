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

    //Symbol Table constructor -- initializes variables
    public SymbolTable(){
        this.root = null;
        this.current = null;
        this.currentScope = 0;
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
                //open scope
                openScope();
                break;
            case "Variable declaration":

                //hard code look ahead?? ickyyy
            
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
                break;
        }
        
        //iterate through children and call inOrder
        for(int i = 0; i < node.children.size(); i++){
            inOrder(node.children.get(i));
        }
    }

    //adds a node to the symbol table
    public void addNodeSymbolTable(int scope){

        //scope - where is it located within the program
        //kind - what kind of node is it root, branch, leaf?

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
        // if block node and not root move current scope to parent
        if(current.scope != 0){
            current = current.parent;
            currentScope--;
        }
    }

    //adds a symbol to the symbol table
    private void addSymbol(String id, String type){
        Symbol newSymbol = new Symbol(id, type, currentScope);
        current.symbols.put(newSymbol.name, newSymbol);
    }

    //looks up a symbol within the symbol table and returns it
    private Symbol lookupSymbol(String id){
        return current.symbols.get(id);
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