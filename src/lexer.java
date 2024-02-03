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
    ArrayList<Token> tokenStream; //stores the sourceCode as tokens. Final result of Lexical Analysis
    int lineNumber; //keeps track of what line we are on during lexical analysis
    int position; //keeps track of the position we are at during lexical analysis
    boolean inQuotes; //are we in quotes????
    boolean endOfProgram; //determines if $ is used
    int programCounter; //determines what program we are on
    boolean inBrackets; //are brackets used?

    //creates a lexer and initializes everything. We are prepared to start lexing!
    public Lexer(){
        this.sourceCode = new ArrayList<>();
        this.tokenStream = new ArrayList<>();
        this.lineNumber = 1; //start at line 1 position 1
        this.position = 1;
        this.inQuotes = false;
        this.endOfProgram = false;
        this.programCounter = 1;
        this.inBrackets = false;
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
    }

    //outputs the results of the lexer. Also outputs warnings and errors
    private void lexerLog(String output){
        System.out.println("LEXER - " + output);
    }

    //performs the lexical analysis of the source code and creates a stream of tokens
    public void scanner(){
        lexerLog("Starting lexical analysis");

        //keywords: print, while, if, int, string, boolean, true, false
        String keywords = "\\b(print|while|if|int|string|boolean|true|false)\\b";
        //Identifiers: a-z (can only be characters)
        String ids = "[a-z]";
        //symbols: {, }, (, ), ", =, +, !=, ==
        String symbols = "(\\{|\\}|\\(|\\)|\"|\\=|\\+|\\!=|\\==)";
        //digits: 0-9
        String digits = "[0-9]";
        //characters: a-z (same as identifiers) (in quotes)
        String characters = "[a-z]";
        //whitespace and comments
        String whitespace = "\s";
        String comments = "/\\*|\\*/|//";

        /*
            iterate line by line
            on each line execute pattern matcher
            for each match found
            use if statements to determine what grammar it is
            use switch statements to find out specific token
            create token
            add to token stream


            then
            figure out whitespace, comments, quotes
            line and position
        */

        //regular expression union
        String allTypes = keywords;
        Pattern pattern = Pattern.compile(allTypes);

        //iterate through source code
        for(int line = 0; line < sourceCode.size(); line++){
            String buffer = sourceCode.get(line);
            Matcher match = pattern.matcher(buffer);

            //keep going while matches are found
            while (match.find()) {
                //use if statements to divide keywords, ids, symbols, digits, chars, etc
                if(match.group().matches(keywords)){
                    String type = "";
                    String keyword = match.group();
                    System.out.println(keyword);

                    switch (keyword) {
                        case "print":
                            type = "Print";
                            break;
                        case "while":
                            type = "While";
                            break;
                        case "if":
                            type = "If";
                            break;
                        case "int":
                            type = "Type_Int";
                            break;
                        case "string":
                            type = "Type_String";
                            break;
                        case "boolean":
                            type = "Type_Boolean";
                            break;
                        case "true":
                            type = "Bool_True";
                            break;
                        case "false":
                            type = "Bool_False";
                            break;
                    }
                    Token newToken = new Token(type, keyword, Integer.toString(lineNumber), Integer.toString(position));
                    tokenStream.add(newToken);
                    lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);
                }
                else if(match.group().matches(ids) && !inQuotes){
                    String type = "Id";
                    String id = match.group();
                    System.out.println(id);

                    //dont need any information/switch statements. Just create tokens
                }
                else if(match.group().matches(symbols)){
                    String type = "";
                    String symbol = match.group();
                    System.out.println(symbol);

                    switch (symbol) {
                        case "{":
                            type = "Opening_Brace";
                            //code block
                            //create token
                            // pass to lex output function
                            break;
                        case "}":
                            type = "Closing_Brace";
                            //code block
                            break;
                        case "(":
                            type = "Opening_Parenthesis";
                            break;
                        case ")":
                            type = "Closing_Parenthesis";
                            break;
                        case "\"":
                            type = "Quote";
                            break;
                        case "=":
                            type = "Assign";
                            break;
                        case "+":
                            type = "Add";
                            break;
                        case "==":
                            type = "Equality_Op";
                            break;
                        case "!=":
                            type = "Inequality_OP";
                            break;
                    }
                }
                else if(match.group().matches(digits)){
                    String type = "Digit";
                    String digit = match.group();
                    System.out.println(digit);

                    //dont need any information/switch statements. Just create tokens
                }
                else if(match.group().matches(characters) && inQuotes){
                    String type = "Char";
                    String character = match.group();
                    System.out.println(character);

                    //dont need any information/switch statements. Just create tokens
                    //have to check to make sure inside quotes
                }
                
            }
            //increase line number
            lineNumber++;
        }
    }
}
