/*
    Lexical Analysis
*/
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer 
{
    public Lexer(){}

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
