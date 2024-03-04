/*
  THECompiler entry point
  Builds the parts of a compiler, connects them, and calls them
*/

public class Compiler {
    public static void main(String[] args) 
    {
        //create the parts of a compiler
        Lexer lexer = new Lexer();

        //introductory output
        System.out.println("Welcome to THECompiler by Collin Drake!");

        //check for command line arguments
        if(args.length > 0)
        {
            //file handling and lexer initialization. Keep user updated on what happens and keeping naming obvious
            String textFile = args[0];
            compilerLog("Processing file: " + textFile);
            /*
            output to user
            call readfile to read the input text file into a string
            call scanner to the lex the input file
            */
            compilerLog("Powering on the LEXER...");
            compilerLog("Powering on the PARSER...\n");
            lexer.readInput(textFile);
            lexer.scanner();
        }
        else{
            //output error if no argument is provided
            compilerLog("No program found. Please provide a command line argument to the compiler.");
        }
    }

        //log function -- outputs errors and information
        static void compilerLog(String output){
            System.out.println("COMPILER - " + output);
        }
}