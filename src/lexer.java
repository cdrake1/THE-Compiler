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

        ArrayList<String> textFile = new ArrayList<>();

        try
        {
            Scanner scanner = new Scanner(new File(inputFile));
            while(scanner.hasNextLine()){
                textFile.add(scanner.nextLine());
            }
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
