/*
    Lexical Analysis
*/
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Lexer 
{
    public ArrayList lexicalAnalysis(ArrayList sourceCode) {
        //final token stream
        ArrayList<Token> tokenStream = new ArrayList<>();
        boolean inQuotes = false;

        //RegEx.... does this go within the loop with the other regex maching?
        String grammar = "";
        Pattern pattern = Pattern.compile(grammar);
        
        //iterate through source code
        for(int i = 0; i < sourceCode.length; i++){

        }
        
        /* 
        read through source code and call a tokenizer to check reg expresions 
        and then call token class to create tokens 
        then add them to token string
        */

        //return token stream to compiler
        return tokenStream;
    }

    // reads input file into a string
    public void readFile(String inputFile)
    {
        //stores the values from the input file
        ArrayList<String> textFile = new ArrayList<>();
        //tries to read the values from the input file into an arraylist, but if it fails it throws an error
        try
        {
            Scanner scanner = new Scanner(new File(inputFile));
            while(scanner.hasNextLine()){
                textFile.add(scanner.nextLine());
            }
            //close the scanner so Java can clean it up
            scanner.close();
        }
        catch(IOException exception)
        {
            System.out.println("Something went wrong when trying to read the file");
        }

        for(int i = 0; i < textFile.size(); i++){
            System.out.println(textFile.get(i));
        }
    }
}
