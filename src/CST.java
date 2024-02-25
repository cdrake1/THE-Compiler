/*
    Concrete Syntax tree file
    Creates CSTs to be used in Parse
    Created with help from Alan G. Labouseur, Michael Ardizzone, and Tim Smith
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

        //if the new node is not a leaf node make it the current
        if(!kind.equals("leaf")){
            current = newNode;
        }
        else{
            
            //output that a node was added to the tree
            CSTLog("Added " + label + " node");
        }
    }

    //traverses up the tree
    public void moveUp(){

        //move up to parent node if possible
        if(this.current.parent != null && this.current.parent.name != null){
            this.current = this.current.parent;
        }
        else{

            // error logging
            CSTLog("ERROR! There was an error when trying to move up the tree...");
        }
    }

    //outputs the current programs CST if it parsed successfully
    public void outputCST(){
        
        //initialize the CST output string
        String traversal = "";
    }
}
