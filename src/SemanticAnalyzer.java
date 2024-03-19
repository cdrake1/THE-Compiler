/*
    Semantic Analysis file
    Takes a stream of tokens as input and creates an AST (intermediate representation)
*/


//import arraylist, list, and arrays
import java.util.ArrayList;

//Collin Drakes Semantic Analyzer
public class SemanticAnalyzer {

    //Semantic Analysis constructor -- initializes all variables
    public SemanticAnalyzer(){}

    //outputs the results of Semantic Analysis. Also outputs errors...
    private void SemanticAnalyzerLog(String output){
        System.out.println("SEMANTIC ANALYZER - " + output);
    }

    public void test(){
        SemanticAnalyzerLog("passed!");
    }
}