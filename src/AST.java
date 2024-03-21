/*
    Abstract Syntax tree file
    Creates ASTS to be used in Symantic Analysis
    Created with help from Alan G. Labouseur, Michael Ardizzone, and Tim Smith
    Uses the same functions as the CST file
*/

//The AST class!
public class AST {
    Node root;  //pointer to the root node
    Node current;   //pointer to the current node

    //AST constructor -- creates an AST and initializes all variables
    public AST(){
        this.root = null;
        this.current = null;
    }

    //outputs the processes completed within the AST
    public void ASTLog(String output){
        System.out.println("Symantic Analyzer - AST - " + output);
    }

    //adds a node to the AST
    public void addNodeAST(String kind, String label){

        //kind - what kind of node is it?
        //label - what is the parse function?

        //create a new node to be added to the AST
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
            ASTLog("Added [ " + label + " ] node");
        }
    }

     //traverses up the tree
     public void moveUpAST(){
        //move up to parent node if possible
        if(current.parent != null){
            current = current.parent;
        }
        else{
            // error logging
            ASTLog("ERROR! There was an error when trying to move up the tree...");
        }
    }
    
}
