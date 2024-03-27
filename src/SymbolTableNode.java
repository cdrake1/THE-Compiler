/*
    Symbol table node file
    Creates nodes for the symbol table
*/

//import arraylist and hashtable
import java.util.ArrayList;
import java.util.Hashtable;

//The symbol table node class!
public class SymbolTableNode {
    int scope;
    SymbolTableNode parent; //pointer to parent node
    Hashtable<String, Symbol> symbols;    //hashtable of symbols
    ArrayList<SymbolTableNode> children;   //list of pointers to child nodes

    //Symbol table node constructor -- initializes all variables
    public SymbolTableNode(int scope){
        this.scope = scope;
        this.parent = null;
        this.symbols = new Hashtable<>();
        this.children = new ArrayList<>();
    }
}
