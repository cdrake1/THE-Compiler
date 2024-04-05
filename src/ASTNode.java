/*
    Node file
    Creates Nodes to be used in the Parsers CST
*/

//import arraylist
import java.util.ArrayList;

//The Node class!
public class ASTNode {
    String name;    //the name of the node
    Token token;
    ASTNode parent;    //pointer to the nodes parent
    ArrayList<ASTNode> children;   //list of pointers to child nodes

    //Node constructor -- creates a node and initializes its variables
    public ASTNode(String label, Token token){
        this.name = label;
        this.token = token;
        this.parent = null;
        this.children = new ArrayList<>();
    }   
}