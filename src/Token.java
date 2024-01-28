import java.util.regex.Pattern;
/*
    Holds all of the grammar/identifiers and defines tokens
*/

public class Token {
    String type;
    String code;
    String position;

    public Token(String tokenType, String sCode, String position){
        this.type = tokenType;
        this.code = sCode;
        this.position = position;
    }

    class TokenType {
        // <keywords>
        // <identifiers>
        // <symbols>
        // <digits>
        // <characters>
    }
}