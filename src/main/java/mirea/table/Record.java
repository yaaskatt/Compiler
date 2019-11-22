package mirea.table;

import mirea.parser.ParserTokenType;

public class Record {
    private String name;
    private Object value;
    private ParserTokenType type;

    public Record(String name, Object value, ParserTokenType type){
        this.name = name;
        this.value = value;
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ParserTokenType getType() {
        return type;
    }
}
