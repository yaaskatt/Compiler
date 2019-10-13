package mirea.lexer;

public class Token {
    private TokenType tokenType;
    private String value;

    Token (TokenType tokenType, String value){
        this.tokenType = tokenType;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public TokenType getTokenType() {
        return tokenType;
    }
}
