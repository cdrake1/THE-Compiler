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

    //Symbol Table constructor -- initializes variables
    public SymbolTable(){
        this.root = null;
        this.current = null;
        this.currentScope = 0;
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

        //output node
        System.out.println(node.name + "\n");
        
        //iterate through children and call inOrder
        for(int i = 0; i < node.children.size(); i++){
            inOrder(node.children.get(i));
        }
    }

    //adds a node to the symbol table
    public void addNodeSymbolTable(int scope, String kind){

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

        //if the new node is not a leaf node make it the current
        if(!kind.equals("leaf")){
            current = newNode;
        }
    }

    //opens a new scope within the symbol table
    private void openScope(){}

    //closes the most recently opened scope
    private void closeScope(){}

    //adds a symbol to the symbol table
    private void addSymbol(){}

    //looks up a symbol within the symbol table
    private void lookupSymbol(){}
}