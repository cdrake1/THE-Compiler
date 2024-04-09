/*
    Code Generation file
    Creates a 6502 op codes
*/

//Collin Drakes Code Generator
public class CodeGenerator {
    String[] opCodes;
    int programCounter;
    int codeGenErrors;
    AST ast;
    SymbolTable symbolTable;

    //Code Generator constructor -- initializes variables
    public CodeGenerator(AST ast, SymbolTable ST, int programCounter){
        opCodes = new String[256];
        this.programCounter = programCounter;
        this.codeGenErrors = 0;
        this.ast = ast;
        this.symbolTable = ST;
    }

    //outputs the processes completed within the Code Generator
    public void codeGeneratorLog(String output){
        System.out.println("Code Generator - " + output);
    }

    //starts the code generation process
    public void startCodeGen(){
        inOrder(ast.root);

        if(codeGenErrors == 0){
            codeGeneratorLog("Code Generation Complete... Errors: " + codeGenErrors);
        }
        else{
            codeGeneratorLog("Code Generation Failed... Errors: " + codeGenErrors);
        }
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