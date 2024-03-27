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

    //Symbol Table constructor -- initializes variables
    public SymbolTable(){
        this.root = null;
        this.current = null;
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