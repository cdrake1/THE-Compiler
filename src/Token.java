import java.util.regex.Pattern;
/*
    Token object. Creates tokens for the Lexer.
*/

public class Token {
    String tokenName;
    String lexeme;
    String position;
    String line;

    public Token(String tokenType, String sCode, String line,String position){
        this.tokenName = tokenType;
        this.lexeme = sCode;
        this.line = line;
        this.position = position;
    }
}