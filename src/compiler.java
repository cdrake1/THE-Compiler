/*
  THECompiler entry point
*/
public class Compiler {
    public static void main(String[] args) 
    {
      //introductory output
      System.out.println("Welcome to THECompiler by Collin Drake!");

      //check for command line arguments
      if(args.length > 0)
      {
        //file handling and lexer initialization. Keep user updated on what happens and keeping naming obvious
        String textFile = args[0];
        System.out.println("Processing file: " + textFile);
        Lexer lexer = new Lexer();
        //call readfile to read the input text file into an arraylist. Arraylist keeps track of line numbers
        lexer.readFile(textFile);
      }
      else{
        System.out.println("No arguments found. Please provide a command line argument to the compiler.");
      }
    }
}