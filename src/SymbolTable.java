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
        if(node == null){
            return;
        }
        //recursively call with child node on the left -- if it exists
        if(!node.children.isEmpty()){
            inOrder(node.children.get(0));
        }
        //output the value of each node
        System.out.println(node.name + "\n");
        //recursively call with child node on the right -- if it exists
        if(node.children.size() > 1){
            inOrder(node.children.get(1));
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