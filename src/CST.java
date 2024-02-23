/*
    Concrete Syntax tree file
    Creates CSTs to be used in Parse
*/

//The CST class!
public class CST {
    Node root;  //pointer to the root node
    Node current;   //pointer to the current node

    //CST constructor -- creates a CST and initializes all variables
    public CST(){
        this.root = null;
        this.current = null;
    }

    //outputs the processes completed within the CST
    public void CSTLog(String output){
        System.out.println("PARSER - CST - " + output);
    }

    //adds a node to the CST
    public void addNode(String kind, String label){

        //kind - what kind of node is it?
        //label - what is the parse function?

        //create a new node to be added to the CST
        Node newNode = new Node(label);

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

        //output that a node was added to the tree
        CSTLog("Added " + label + "node");

        //if the new node is not a leaf node make it the current
        if(!kind.equals("leaf")){
            current = newNode;
        }
    }

    //traverses up the tree
    public void moveUp(){}
}
