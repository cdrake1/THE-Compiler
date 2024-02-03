/*
    Token object
    Creates tokens for the Lexer
*/

public class Token {
    String tokenType;
    String lexeme;
    String position;
    String line;

    public Token(String tType, String sCode, String line,String position){
        this.tokenType = tType;
        this.lexeme = sCode;
        this.line = line;
        this.position = position;
    }
}