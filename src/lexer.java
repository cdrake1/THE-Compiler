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
    boolean inComment; //are win in a comment?
    boolean endOfProgram; //determines if $ is used
    int programCounter; //determines what program we are on
    int warningCount; //counts total errors
    int errorCount; //counts total warnings

    //creates a lexer and initializes everything. We are prepared to start lexing!
    public Lexer(){
        this.sourceCode = new ArrayList<>();
        this.tokenStream = new ArrayList<>();
        this.lineNumber = 1; //start at line 1 position 1
        this.position = 1;
        this.inQuotes = false;
        this.inComment = false;
        this.endOfProgram = false;
        this.programCounter = 1; //do I need this?????
        this.errorCount = 0;
        this.warningCount = 0;
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
        String keywords = "(print|while|if|int|string|boolean|true|false)";
        //Identifiers: a-z (can only be characters)
        String ids = "[a-z]";
        //symbols: {, }, (, ), ", =, +, !=, ==
        String symbols = "(\\{|\\}|\\(|\\)|\"|\\=|\\+|\\!=|\\==|\\$)";
        //digits: 0-9
        String digits = "[0-9]";
        //characters: a-z (same as identifiers) (in quotes)
        String characters = "[a-z]";
        //whitespace and comments
        String whitespace = "\s";
        String comments = "/\\*|\\*/";
        //undefined characters
        String undefined = ".";
        //add whitespace to characters? or gets its own?

        //regular expression union
        String allTypes = keywords + "|" + ids + "|" + symbols + "|" + digits + "|" + characters + "|" + whitespace + "|" + comments;
        Pattern pattern = Pattern.compile(allTypes);

        //iterate through source code
        for(int line = 0; line < sourceCode.size(); line++){
            String buffer = sourceCode.get(line);
            Matcher match = pattern.matcher(buffer);

            //keep going while matches are found
            while (match.find()) {
                position = match.end();
                //use if statements to divide keywords, ids, symbols, digits, chars, etc
                if(match.group().matches(keywords) && !inComment && !inQuotes){
                    String type = "";
                    String keyword = match.group();

                    switch (keyword) {
                        case "print":
                            type = "PRINT";
                            break;
                        case "while":
                            type = "WHILE";
                            break;
                        case "if":
                            type = "IF";
                            break;
                        case "int":
                            type = "TYPE_INT";
                            break;
                        case "string":
                            type = "TYPE_STRING";
                            break;
                        case "boolean":
                            type = "TYPE_BOOLEAN";
                            break;
                        case "true":
                            type = "BOOL_TRUE";
                            break;
                        case "false":
                            type = "BOOL_FALSE";
                            break;
                    }
                    Token newToken = new Token(type, keyword, Integer.toString(lineNumber), Integer.toString(position));
                    tokenStream.add(newToken);
                    lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);
                }
                else if(match.group().matches(ids) && !inQuotes && !inComment){
                    String type = "ID";
                    String id = match.group();
                    Token newToken = new Token(type, id, Integer.toString(lineNumber), Integer.toString(position));
                    tokenStream.add(newToken);
                    lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);
                }
                else if(match.group().matches(symbols)){
                    String type = "";
                    String symbol = match.group();

                    switch (symbol) {
                        case "{":
                            type = "OPENING_BRACE";
                            //code block
                            //create token
                            // pass to lex output function
                            break;
                        case "}":
                            type = "CLOSING_BRACE";
                            //code block
                            break;
                        case "(":
                            type = "OPENING_PARENTHESIS";
                            break;
                        case ")":
                            type = "CLOSING_PARENTHESIS";
                            break;
                        case "\"":
                            type = "QUOTE";
                            if(inQuotes){
                                inQuotes = false;
                            }
                            else{
                                inQuotes = true;
                            }
                            break;
                        case "=":
                            type = "ASSIGN";
                            break;
                        case "+":
                            type = "ADD";
                            break;
                        case "==":
                            type = "EQUALITY_OP";
                            break;
                        case "!=":
                            type = "INEQUALITY_OP";
                            break;
                        case "$":
                            type = "EOP";
                            endOfProgram = true;
                            programCounter++;
                    }
                    Token newToken = new Token(type, symbol, Integer.toString(lineNumber), Integer.toString(position));
                    tokenStream.add(newToken);
                    lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);
                }
                else if(match.group().matches(digits) && !inComment && !inQuotes){
                    String type = "DIGIT";
                    String digit = match.group();
                    Token newToken = new Token(type, digit, Integer.toString(lineNumber), Integer.toString(position));
                    tokenStream.add(newToken);
                    lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);
                }
                else if(match.group().matches(characters) && inQuotes && !inComment){
                    String type = "CHAR";
                    String character = match.group();
                    Token newToken = new Token(type, character, Integer.toString(lineNumber), Integer.toString(position));
                    tokenStream.add(newToken);
                    lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);
                }
                else if(match.group().matches(comments)){
                    String comment = match.group();
                    switch (comment) {
                        case "/*":
                            inComment = true;
                            break;
                    
                        case "*/":
                            inComment = false;
                            break;
                    }
                }
                else if(match.group().matches(whitespace) && inQuotes){
                    String type = "WHITESPACE";
                    String whitespaces = match.group();
                    Token newToken = new Token(type, whitespaces, Integer.toString(lineNumber), Integer.toString(position));
                    tokenStream.add(newToken);
                    lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);   
                }
                else if(match.group().matches(undefined)){
                    // thow warning for undefined
                    // add to warning count
                    String warning = match.group();
                    lexerLog("WARNING! UNRECOGNIZED TOKEN [ " + warning + " ]");
                    warningCount++;

                }
            }
            //increase line number
            lineNumber++;
        }
        lexerLog("Lexical Analysis Complete... " + "Warnings: " + warningCount + " Errors: " + errorCount);
    }
}
