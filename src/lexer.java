/*
    Lexer/scanner/lexical analyzer file
    Takes a txt file as an input and returns an ordered stream of tokens
    Performs lexical analysis on input program/s and returns an ordered stream of tokens
*/

//import regEx for pattern matching, and a scanner to read the input into an arraylist
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.w3c.dom.events.EventException;

import java.util.regex.Matcher;

// Collin Drakes Lexer
public class Lexer {
    ArrayList<String> sourceCode; //stores the values/"programs" from the input file
    ArrayList<String> tokenStream; //stores the sourceCode as tokens. Final result of Lexical Analysis
    int lineNumber; //keeps track of what line we are on during lexical analysis
    int position; //keeps track of the position we are at during lexical analysis
    boolean inQuotes; //are we in quotes????

    //creates a lexer and initializes everything. We are prepared to start lexing!
    public Lexer(){
        this.sourceCode = new ArrayList<>();
        this.tokenStream = new ArrayList<>();
        this.lineNumber = 1; //start at line 1 position 1
        this.position = 1;
        this.inQuotes = false;
    }

    //reads the input file into an arraylist called sourceCode. Prepare for Lexical Analysis
    public void readInput(String textFile){
        //tries to read the values from the input file into an arraylist
        try
        {
            File file = new File(textFile);
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                sourceCode.add(scanner.nextLine());
            }
            //close the scanner so Java can clean it up
            scanner.close();
            System.out.println("The input file was read successfully");
        }
        //if it fails it throws an error
        catch(IOException exception)
        {
            System.out.println("Something went wrong when trying to read the file");
        }

        /*
        outputs the input to test
        for(int i = 0; i < sourceCode.size(); i++){
            System.out.println(sourceCode.get(i));
        }
        */
    }

    //outputs the results of the lexer. Also outputs warnings and errors
    private void lexerLog(String output){
        System.out.println(output);
    }

    //performs the lexical analysis of the source code and creates a stream of tokens
    public void scanner(){
        lexerLog("LEXER - Starting lexical analysis");

        //keywords: print, while, if, int, string, boolean, true, false
        String keywords = "\\b(print|while|if|int|string|boolean|true|false)\\b";
        //Identifiers: a-z (can only be characters)
        String ids = "[a-z]";
        //symbols: {, }, (, ), ", =, +, !=, ==
        String symbols = "(\\{|\\}|\\(|\\)|\"|\\=|\\+|\\!=|\\==)";
        //digits: 0-9
        String digits = "[0-9]";
        //characters: a-z (same as identifiers)
        String characters = "[a-z]";
        //whitespace and comments
        String whitespace = "\s";
        String comments = "/\\*";


        //regular expression union
        String allTypes = keywords + "|" + ids;
        Pattern pattern = Pattern.compile(allTypes);


        //how to break regex into groups?

        for(String line: sourceCode){
            Matcher match = pattern.matcher(line);
            while(match.find()){
                /*
                if(match.group(1)!= null){
                    String keyword = match.group(1);
                    System.out.println(keyword);
                }
                
                else if(match.group(2)!= null){
                    String id = match.group(1);
                    System.out.println(id);

                }
                */
            }
        }

        /*
        //iterate through source code
        for(int line = 0; line < sourceCode.size(); line++){
            //pointers and buffer declaration
            String buffer = sourceCode.get(line);
            int lexemeBegin = 0; //marks beginning of lexeme
            int forward = 0; //scans ahead until pattern match is found

        }
        
        /* 
        read through source code and call a tokenizer to check reg expresions 
        and then call token class to create tokens 
        then add them to token string
        */

        //return token stream to compiler
    }
}
