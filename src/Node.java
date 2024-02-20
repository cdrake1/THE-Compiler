/*
    Node file
    Creates Nodes to be used in the Parsers CST
*/

import java.util.ArrayList; //import arraylist

//The Node class!
public class Node {
    String name;    //The name of the node
    Node parent;    //Pointer to the nodes parent
    ArrayList<Node> children;   //list of pointers to child nodes

    //creates a node and initializes its variables
    public Node(String label){
        this.name = label;
        this.parent = null;
        this.children = new ArrayList<>();
    }   
}