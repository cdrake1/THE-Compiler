/*
    Abstract Syntax tree file
    Creates AST's to be used in Symantic Analysis
    Created with help from Alan G. Labouseur, Michael Ardizzone, and Tim Smith
    Uses the same functions as the CST file except it uses specific AST nodes
*/

//The AST class!
public class AST {
    ASTNode root;  //pointer to the root node
    ASTNode current;   //pointer to the current node
    String traversal;   //string to hold AST traversal


    //AST constructor -- creates an AST and initializes all variables
    public AST(){
        this.root = null;
        this.current = null;
        this.traversal = "";
    }

    //outputs the processes completed within the AST
    public void ASTLog(String output){
        System.out.println("Symantic Analyzer - AST - " + output);
    }

    //adds a node to the AST
    public void addNodeAST(String kind, String label, Token token){

        //kind - what kind of node is it?
        //label - what is the parse function?
        //token -- the token associated with the node

        //create a new node to be added to the AST
        ASTNode newNode = new ASTNode(label, token);

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
    }

     //traverses up the tree
     public void moveUpAST(){
        //move up to parent node if possible
        if(current.parent != null){
            current = current.parent;
        }
        else{   //check if the current node is block and if its the root node so there are no errors thrown when trying to move up
            if(current.name.equals("Block") && current.parent == null){
                //do nothing
                //epsilon
            }
            else{
                // error logging
                ASTLog("ERROR! There was an error when trying to move up the tree...");
            }
        }
    }

    //outputs the current programs CST if it parsed successfully
    public void outputAST(){
        expand(root, 0);
        System.out.print("\n");
        ASTLog("\n" + traversal);
    }

    public void expand(ASTNode node, int depth){
        //space nodes out based off of the current depth
        for(int i = 0; i < depth; i++){
            traversal += "-";
        }

        //if this node is a leaf node output the name
        if(node.children.size() == 0){
            traversal += "[" + node.name + "]";
            traversal += "\n";
        }
        else{
            //this node is not a leaf node
            traversal += "<" + node.name + "> \n";

            //recursion!!! -- call the next child and increment the depth
            for(int j = 0; j < node.children.size(); j++){
                expand(node.children.get(j), depth + 1);
            }
        }
    }
}