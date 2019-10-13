package mirea.parser;

import mirea.interpreter.ElementInterface;
import mirea.lexer.Token;

public class Element implements ElementInterface {
    private String type;
    private String value;

    Element(String type, String value) {
        this.type = type;
        this.value = value;
    }

    Element(Token token) {
        this.type = token.getTokenType().name();
        this.value = token.getValue();
    }

    Element() {
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
