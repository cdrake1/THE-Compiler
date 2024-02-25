/*
    Node file
    Creates Nodes to be used in the Parsers CST
*/

//import arraylist
import java.util.ArrayList;

//The Node class!
public class Node {
    String name;    //the name of the node
    Node parent;    //pointer to the nodes parent
    ArrayList<Node> children;   //list of pointers to child nodes

    //Node constructor -- creates a node and initializes its variables
    public Node(String label){
        this.name = label;
        this.parent = null;
        this.children = new ArrayList<>();
    }   
}