/*
    Parser file
    Takes a stream of tokens as an input and creates and Concrete Syntax Tree (Parse tree)
    "Takes tokens and groups them into phrases according to the syntax specification"
    Uses recursive descent
*/

//import necessary packages
import java.util.ArrayList;

//Collin Drakes Parser
public class Parser{

    //creates a parser and initializes all variables. We are prepared to start parsing!
    public Parser(){}

    //outputs the results of the parser. Also outputs warnings and errors...
    private void parserLog(String output){
        System.out.println("PARSER - " + output);
    }

    public void parseProgram(ArrayList<Token> programTokenStream){
        for(int i = 0; i < programTokenStream.size(); i++){
            parserLog(programTokenStream.get(i).tokenType);
        }
    }
}