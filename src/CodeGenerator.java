/*
    Code Generation file
    Creates a 6502 op codes
*/

//Collin Drakes Code Generator
public class CodeGenerator {
    String[] opCodes;

    //Code Generator constructor -- initializes variables
    public CodeGenerator(){
        opCodes = new String[256];

    }

    //outputs the processes completed within the Code Generator
    public void codeGeneratorLog(String output){
        System.out.println("Code Generator - " + output);
    }

    //function for the depth first in order traversal of the AST
    public void inOrder(ASTNode node){
        //if the node is null return to avoid errors
        if(node == null){
            return;
        }

        //iterate through all of the nodes children recursively
        for(ASTNode child : node.children){
            inOrder(child);
        }

        System.out.println(node.name);
    }
    
}
