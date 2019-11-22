package mirea.lexer;


public class LexerToken {
    LexerTokenType type;
    String value;

    LexerToken(LexerTokenType type, String value){
        this.type = type;
        this.value = value;
    }

    public LexerTokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}


