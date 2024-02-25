/*
    Token Constructor
    Creates tokens for the Lexer
*/

public class Token {
    //each token has a type, lexeme, position, and line
    String tokenType;
    String lexeme;
    String position;
    String line;
    
    //Token constructor -- creating tokens!
    public Token(String tType, String sCode, String line,String position){
        this.tokenType = tType;
        this.lexeme = sCode;
        this.line = line;
        this.position = position;
    }
}