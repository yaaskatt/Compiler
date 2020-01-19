package mirea.table;

import mirea.parser.ParserToken;

import java.util.ArrayList;
import java.util.List;

public class FuncHolder {
    String name = "";
    List<ParserToken> args;
    List<ParserToken> body = new ArrayList<>();
    ParserToken returnValue;

    FuncHolder(String name, List<ParserToken> args, ParserToken returnValue) {
        this.name = name;
        this.args = args;
        this.returnValue = returnValue;
    }

    public void setBody(List<ParserToken> body) {
        this.body = body;
    }

    public List<ParserToken> getBody() {
        return body;
    }
}
