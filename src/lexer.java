/*
    Lexical Analysis
*/
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.*;

public class Lexer 
{
    public String lexer(String sourceCode) {
        String tokens = "";
        
        /* 
        read through source code and call a tokenizer to check reg expresions 
        and then call token class to create tokens 
        then add them to token string
        */

        return tokens;
    }

    // reads input file into a string
    public void readFile(String inputFile)
    {
        // all input file as a string
        String sourceCode = "";
        // tries to read the file line by line into a string builder
        try
        {
            StringBuilder input = new StringBuilder();
            BufferedReader buffReader = new BufferedReader(new FileReader(inputFile));
            String line = buffReader.readLine();
            while(line != null){
                input.append(line);
                input.append("\n");
                line = buffReader.readLine();
            }
            sourceCode = input.toString();
        }
        // catches any errors
        catch(IOException exception)
        {
            System.out.println("Something went wrong when trying to read the file");
        }
        // test output
        System.out.println(sourceCode);
    }
}
