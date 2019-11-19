package mirea.lexer;

import mirea.token.AbstractToken;

public class LexerToken extends AbstractToken {
    LexerToken(TokenType tokenType, String value){
        super(tokenType.name(), value);
    }
}
