package mirea.parser;

import mirea.interpreter.ElementInterface;
import mirea.lexer.Token;

public class Element implements ElementInterface {
    private String type;
    private String value;

    public Element(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public Element(Token token) {
        this.type = token.getTokenType().name();
        this.value = token.getValue();
    }

    public Element() {
        this.type = "";
        this.value = "";
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
