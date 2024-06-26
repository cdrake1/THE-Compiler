/*
    AST Node file
    Creates AST Nodes to be used in the Semantic Analyzer and Symbol Table
    These nodes contain the tokens created by the lexer
*/

//import arraylist
import java.util.ArrayList;

//The AST Node class!
public class ASTNode {
    String name;    //the name of the node
    Token token;    //if the node contains a token...
    ASTNode parent;    //pointer to the nodes parent
    ArrayList<ASTNode> children;   //list of pointers to child nodes
    boolean processed;  //for code gen to keep track of what nodes have been processed already

    //AST Node constructor -- creates a node and initializes its variables
    public ASTNode(String label, Token token){
        this.name = label;
        this.token = token;
        this.parent = null;
        this.children = new ArrayList<>();
        this.processed = false;
    }   
}