//creates nodes for symbol table. pointer to parent and children. each node has a hashmap for symbols??

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTableNode {
    SymbolTableNode parent; //pointer to parent node
    HashMap<String, Symbol> symbols;    //hashmap of symbols
    ArrayList<SymbolTableNode> children;   //list of pointers to child nodes

    public SymbolTableNode(){}
}
