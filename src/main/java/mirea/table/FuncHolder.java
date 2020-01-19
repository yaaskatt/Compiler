package mirea.table;

import mirea.parser.ParserToken;
import mirea.parser.ParserTokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FuncHolder {
    String name;
    ArrayList<Record> args;
    List<ParserToken> body;
    ParserToken returnValue;

    public FuncHolder(String name, ArrayList<Record> args, ParserToken returnValue, List<ParserToken> body) {
        this.name = name;
        this.args = args;
        this.returnValue = returnValue;
        this.body = body;
    }

    public ArrayList<Record> getArgs() {
        return args;
    }

    public void setArgs(ArrayList<Record> args) {
        this.args = args;
    }

    public void setBody(List<ParserToken> body) {
        this.body = body;
    }

    public List<ParserToken> getBody() {
        return body;
    }

    public ParserToken getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(ParserToken returnValue) {
        this.returnValue = returnValue;
    }
}
