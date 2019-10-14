package mirea.parser;

import mirea.lexer.Token;

public class Triad {
    Token op;
    Element el1;
    Element el2;

    public Triad() {
    }

    public Triad(Token op) {
        this.op = op;
    }

    public Triad(Element el1) {
        this.el1 = el1;
    }

    public Triad(Token op, Element el1, Element el2) {
        this.op = op;
        this.el1 = el1;
        this.el2 = el2;
    }

    public void setOp(Token op) {
        this.op = op;
    }

    public void setEl1(Element el1) {
        this.el1 = el1;
    }

    public void setEl2(Element el2) {
        this.el2 = el2;
    }

    public void setUnknownElement(Element el) {
        if (el1 == null) el1 = el;
        else if (el2 == null) el2 = el;
    }
}

