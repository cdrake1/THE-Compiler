import java.util.regex.Pattern;
/*
    Token object. Creates tokens for the Lexer.
*/

public class Token {
    String type;
    String code;
    String position;
    String line;

    public Token(String tokenType, String sCode, String line,String position){
        this.type = tokenType;
        this.code = sCode;
        this.line = line;
        this.position = position;
    }
}