/*
  THECompiler entry point
*/
public class Compiler {
    public static void main(String[] args) 
    {
      boolean doLex = true;
      //create the parts of a compiler
      Lexer lexer = new Lexer();

      //introductory output
      System.out.println("Welcome to THECompiler by Collin Drake!");

      //check for command line arguments
      if(args.length > 0)
      {
        //file handling and lexer initialization. Keep user updated on what happens and keeping naming obvious
        String textFile = args[0];
        System.out.println("Processing file: " + textFile);
    
        if(doLex){
          //call readfile to read the input text file into a string
          lexer.readInput(textFile);
          System.out.println("Powering on the LEXER...");
          //call the scanner to start lexical analysis
          lexer.scanner();
        }
      }
      else{
        System.out.println("No arguments found. Please provide a command line argument to the compiler.");
      }
    }
}