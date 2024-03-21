/*
    Abstract Syntax tree file
    Creates ASTS to be used in Symantic Analysis
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
    
}
