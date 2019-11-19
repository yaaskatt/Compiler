package mirea.triad_optimisation;
import mirea.parser.ParserToken;

public class Triad {
    private ParserToken op;
    private ParserToken el1;
    private ParserToken el2;

    public Triad() {
    }

    public Triad(ParserToken op) {
        this.op = op;
    }

    public Triad(ParserToken op, ParserToken el1) {
        this.op = op;
        this.el1 = el1;
    }

    public Triad(ParserToken op, ParserToken el1, ParserToken el2) {
        this.op = op;
        this.el1 = el1;
        this.el2 = el2;
    }

    public ParserToken getOp() {
        return this.op;
    }

    public ParserToken getEl1() {
        return el1;
    }

    public ParserToken getEl2() {
        return el2;
    }

    public void setOp(ParserToken op) {
        this.op = op;
    }

    public void setEl1(ParserToken el1) {
        this.el1 = el1;
    }

    public void setEl2(ParserToken el2) {
        this.el2 = el2;
    }

    public void setUnknownElement(ParserToken el) {
        if (el1 == null) el1 = el;
        else if (el2 == null) el2 = el;
    }

    @Override
    public boolean equals(Object obj) {
        Triad t = (Triad)obj;
        if (this.getOp().equals(t.getOp()) &&
                this.getEl1().equals(t.getEl1()) &&
                this.getEl2().equals(t.getEl2())) return true;
        return false;
    }
}