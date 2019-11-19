package mirea.parser;

import mirea.token.AbstractToken;
import mirea.lexer.LexerToken;


public class ParserToken extends AbstractToken {

    public ParserToken(String type, String value) {
        super(type, value);
    }

    public ParserToken(LexerToken lexerToken) {
        super(lexerToken.getType(), lexerToken.getValue());
    }

    public ParserToken() {
        super("", "");
    }

    public boolean notBlank() {
        if (super.getType().equals("") && super.getValue().equals("")) return false;
    return true;
    }
}
