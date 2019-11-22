package mirea.parser;

import mirea.lexer.LexerToken;


public class ParserToken {
    ParserTokenType type;
    String value;

    public ParserToken(ParserTokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public ParserToken(LexerToken lexerToken) {
        this.type = ParserTokenType.valueOf(lexerToken.getType().name());
        this.value = lexerToken.getValue();
    }

    public ParserToken() {

        this.type = ParserTokenType.BLANK;
        this.value = "";
    }

    public ParserTokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setType(ParserTokenType type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean blank() {
        return type == ParserTokenType.BLANK && value == "";
    }

    public boolean notBlank() {
        return !blank();
    }

}
