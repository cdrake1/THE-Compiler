/*
    Lexer/scanner/lexical analyzer file
    Takes a txt file as an input and returns an ordered stream of tokens
    Performs lexical analysis on input program/s and returns an ordered stream of tokens
*/

//import RegEx for pattern matching, and a scanner to read the input into an arraylist
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

//Collin Drakes Lexer
public class Lexer {
    ArrayList<String> sourceCode;   //stores the values/programs from the input file
    ArrayList<Token> tokenStream;   //stores the sourceCode as tokens. Final result of Lexical Analysis
    int lineNumber; //keeps track of what line we are on during lexical analysis
    int position;   //keeps track of the position we are at during lexical analysis
    boolean inQuotes;   //are we in quotes????
    boolean inComment;  //are we in in a comment?
    boolean endOfProgram;   //determines if $ is used
    int programCounter; //determines what program we are on
    int warningCount;   //counts total errors
    int errorCount; //counts total warnings
    boolean multiLineQuoteError;    //flag to check if multi line quote/string error was thrown

    //creates a lexer and initializes everything. We are prepared to start lexing!
    public Lexer(){
        this.sourceCode = new ArrayList<>();
        this.tokenStream = new ArrayList<>();
        this.lineNumber = 1;
        this.position = 1;
        this.inQuotes = false;
        this.inComment = false;
        this.endOfProgram = false;
        this.programCounter = 1;
        this.errorCount = 0;
        this.warningCount = 0;
        this.multiLineQuoteError = false;
    }

    //resets the global variables for the next program
    public void resetLexer(){
        this.tokenStream.clear();
        this.position = 1;
        this.inQuotes = false;
        this.inComment = false;
        this.endOfProgram = false;
        this.errorCount = 0;
        this.warningCount = 0;
        this.multiLineQuoteError = false;
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
            scanner.close();    //close the scanner so Java can clean it up
            System.out.println("The input file was read successfully");
        }

        //if it fails it throws an error and exits the program
        catch(IOException exception)
        {
            System.out.println("Something went wrong when trying to read the file");
            System.exit(0);
        }
    }

    //when called passes the token stream of the current program to the parser.
    private void callParse(){
        Parser parser = new Parser();
        parser.parseTokenStream(tokenStream);
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
        //symbols: {, }, (, ), =, +, !=, ==
        String symbols = "(\\{|\\}|\\(|\\)|\\==|\\+|\\!=|\\=|\\$)";
        //digits: 0-9
        String digits = "[0-9]";
        //characters: a-z (same as identifiers) (in quotes)
        String characters = "[a-z]+";
        //whitespace
        String whitespace = "\s";
        //comments
        String comments = "/\\*|\\*/";
        //quotes
        String quotes = "\"";
        //undefined characters
        String undefined = "[A-Z!$@#%^&*;:<>?-_/~`|\\\\]";

        //regular expression union and compilation
        String allTypes = keywords + "|" + ids + "|" + symbols + "|" + digits + "|" + characters + "|" + whitespace + "|" + comments + "|" + quotes + "|" + undefined;
        Pattern pattern = Pattern.compile(allTypes);

        //iterate through source code
        lexerLog("Lexing program 1");
        for(int line = 0; line < sourceCode.size(); line++){
            String buffer = sourceCode.get(line);
            Matcher match = pattern.matcher(buffer);

            //throws error if quote/string spans multiple lines
            if(inQuotes && !multiLineQuoteError){
                lexerLog("ERROR! UNCLOSED QUOTE SPANNING MULTIPLE LINES");
                errorCount++;
                multiLineQuoteError = true;
            }

            //keep going while matches are found
            while (match.find()) {
                position = match.end();

                //use switch cases to divide keywords, ids, symbols, digits, chars, etc
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

                    //create tokens for keywords
                    Token newToken = new Token(type, keyword, Integer.toString(lineNumber), Integer.toString(position));
                    tokenStream.add(newToken);
                    lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);
                }
                else if(match.group().matches(ids) && !inQuotes && !inComment){
                    String type = "ID";
                    String id = match.group();

                    //create tokens for ids
                    Token newToken = new Token(type, id, Integer.toString(lineNumber), Integer.toString(position));
                    tokenStream.add(newToken);
                    lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);
                }
                else if(match.group().matches(symbols) && !inComment && !inQuotes){
                    String type = "";
                    String symbol = match.group();
                    switch (symbol) {
                        case "{":
                            type = "OPENING_BRACE";
                            break;
                        case "}":
                            type = "CLOSING_BRACE";
                            break;
                        case "(":
                            type = "OPENING_PARENTHESIS";
                            break;
                        case ")":
                            type = "CLOSING_PARENTHESIS";
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

                            //creates an EOP token and adds it to the token stream before checking to see if the program is done
                            Token newToken = new Token(type, symbol, Integer.toString(lineNumber), Integer.toString(position));
                            tokenStream.add(newToken);
                            lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);

                            //mark that the program is over and increment the program counter
                            endOfProgram = true;
                            programCounter++;

                            //check for more warnings. Missing end of comment or quote before the end of a program
                            if(inComment || inQuotes){
                                warningCount++;
                                if(inComment){
                                    lexerLog("WARNING! MISSING END COMMENT [ */ ]");
                                }
                                else{
                                    lexerLog("WARNING! MISSING END QUOTE [ \" ]");
                                }
                            }

                            //if statement to check if lexing was successful
                            if(errorCount == 0 && endOfProgram){
                                lexerLog("Lexical Analysis Complete... " + "Warnings: " + warningCount + " Errors: " + errorCount);
                                System.out.print("\n");
                                callParse();    //call parse if lex passes successfully
                            }
                            else{
                                lexerLog("Lexical Analysis Failed... " + "Warnings: " + warningCount + " Errors: " + errorCount);
                            }

                            //reset lexer and global variables. Output next program intro if there is more code
                            if(line < sourceCode.size() - 1 || position < sourceCode.get(line).length()-1){
                                System.out.println("\n");
                                lexerLog("Lexing program " + programCounter);
                                resetLexer();
                            }
                            continue;   //breaks 1 iteration
                    }

                    //create tokens for symbols
                    Token newToken = new Token(type, symbol, Integer.toString(lineNumber), Integer.toString(position));
                    tokenStream.add(newToken);
                    lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);
                }
                else if(match.group().matches(digits) && !inComment){
                    String type = "DIGIT";
                    String digit = match.group();

                    //Thow an error for a digit within quotes
                    if(inQuotes){
                        errorCount++;
                        lexerLog("ERROR! UNRECOGNIZED TOKEN [ " + digit + " ] at line " + lineNumber + " position " + position);
                    }
                    else{

                        //create tokens for digits
                        Token newToken = new Token(type, digit, Integer.toString(lineNumber), Integer.toString(position));
                        tokenStream.add(newToken);
                        lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);
                    }
                }
                else if(match.group().matches(characters) && inQuotes && !inComment){
                    String type = "CHAR";
                    String character = match.group();

                    //if a keyword is in a string do this. Otherwise just make a token. Deals with keywords in strings
                    if(match.group().matches(characters) && match.group().matches(keywords)){

                        //break the keyword into a char array and create tokens for each letter
                        char[] word = character.toCharArray();
                        position = position - (character.length() - 1); //set position correctly
                        for(char c: word){

                            //create tokens for characters
                            Token newToken = new Token(type, String.valueOf(c), Integer.toString(lineNumber), Integer.toString(position));
                            tokenStream.add(newToken);
                            lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);
                            position++;
                        }
                    }
                    else{

                        //create tokens for characters
                        Token newToken = new Token(type, character, Integer.toString(lineNumber), Integer.toString(position));
                        tokenStream.add(newToken);
                        lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);
                    }
                }
                else if(match.group().matches(comments)){

                    //flag to determine if we are inside a comment
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
                else if(match.group().matches(quotes)){
                    String type = "QUOTE";
                    String quote = match.group();
                    
                    //flag to determine if we are inside quotes
                    if(inQuotes){
                        inQuotes = false;
                    }
                    else{
                        inQuotes = true;
                    }

                    //create tokens for quotes
                    Token newToken = new Token(type, quote, Integer.toString(lineNumber), Integer.toString(position));
                    tokenStream.add(newToken);
                    lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);
                }
                else if(match.group().matches(whitespace) && inQuotes && !inComment){
                    String type = "WHITESPACE";
                    String whitespaces = match.group();
                    
                    //create tokens for whitespace
                    Token newToken = new Token(type, whitespaces, Integer.toString(lineNumber), Integer.toString(position));
                    tokenStream.add(newToken);
                    lexerLog(newToken.tokenType + " [ " + newToken.lexeme + " ] on line " + newToken.line + " position " + newToken.position);   
                }
                else if(match.group().matches(undefined) && !inComment){

                    //throw warning for undefined grammar and increment the warning count
                    String error = match.group();
                    lexerLog("ERROR! UNRECOGNIZED TOKEN [ " + error + " ] at line " + lineNumber + " position " + position);
                    errorCount++;
                }
            }

            //increase line number
            lineNumber++;
        }
        
        //check for error (Missing EOP)
        if (!endOfProgram) {

            //check for more warnings (Missing end comment or quote)
            if(inComment || inQuotes){
                warningCount++; //increment warning count
                if(inComment){
                    lexerLog("WARNING! MISSING END COMMENT [ */ ]");
                }
                else{
                    lexerLog("WARNING! MISSING END QUOTE [ \" ]");
                }
            }
            errorCount++;   //increment error count
            lexerLog("ERROR! REACHED END OF FILE. MISSING EOP OPERATOR [ $ ]");
            lexerLog("Lexical Analysis Failed... " + "Warnings: " + warningCount + " Errors: " + errorCount);
        }
    }
}