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

    //performs the lexical analysis of the source code and creates a stream of tokens
    public void scanner(){
        //Define grammar through RegEx
        Pattern BeginBlock = Pattern.compile(null);
        Pattern EndBlock = Pattern.compile(null);
        Pattern LParen = Pattern.compile(null);
        Pattern RParen = Pattern.compile(null);
        Pattern Test4Equality = Pattern.compile(null);
        Pattern Assignment = Pattern.compile(null);
        Pattern IntOp = Pattern.compile(null);
        Pattern BoolOp = Pattern.compile(null);
        Pattern Digit = Pattern.compile(null);
        Pattern Char = Pattern.compile(null);
        Pattern FalseBool = Pattern.compile(null);
        Pattern TrueBool = Pattern.compile(null);
        Pattern WhiteSpace = Pattern.compile(null);
        Pattern Quote = Pattern.compile(null);
        Pattern Id = Pattern.compile(null);
        Pattern IntType = Pattern.compile(null);
        Pattern StringType = Pattern.compile(null);
        Pattern BoolType = Pattern.compile(null);
        Pattern Print = Pattern.compile(null);
        Pattern While = Pattern.compile(null);
        Pattern If = Pattern.compile(null);
        Pattern EOP = Pattern.compile(null);

        /*
         * //RegEx.... does this go within the loop with the other regex maching?
        String grammar = "";
        Pattern pattern = Pattern.compile(grammar);
        
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

    //reads the input file into an arraylist called sourceCode. Prepare for Lexical Analysis
    public void readInput(String textFile){
        //tries to read the values from the input file into an arraylist
        try
        {
            Scanner scanner = new Scanner(new File(textFile));
            while(scanner.hasNextLine()){
                sourceCode.add(scanner.nextLine());
            }
            //close the scanner so Java can clean it up
            scanner.close();
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
    private void lexerLog(){}
}
