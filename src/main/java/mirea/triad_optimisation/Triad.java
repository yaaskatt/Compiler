package mirea.triad_optimisation;
import mirea.parser.ParserToken;

public class Triad {
    private ParserToken op;
    private ParserToken t1;
    private ParserToken t2;

    public Triad() {
    }

    public Triad(ParserToken op) {
        this.op = op;
    }

    public Triad(ParserToken op, ParserToken t1) {
        this.op = op;
        this.t1 = t1;
    }

    public Triad(ParserToken op, ParserToken t1, ParserToken t2) {
        this.op = op;
        this.t1 = t1;
        this.t2 = t2;
    }

    public ParserToken getOp() {
        return this.op;
    }

    public ParserToken getT1() {
        return t1;
    }

    public ParserToken getT2() {
        return t2;
    }

    public void setOp(ParserToken op) {
        this.op = op;
    }

    public void setT1(ParserToken t1) {
        this.t1 = new ParserToken(t1.getType(), t1.getValue());
    }

    public void setT2(ParserToken t2) {
        this.t2 = new ParserToken(t2.getType(), t2.getValue());;
    }

    public void setUnknownElement(ParserToken el) {
        if (t1 == null) t1 = el;
        else if (t2 == null) t2 = el;
    }

    @Override
    public boolean equals(Object obj) {
        Triad t = (Triad)obj;
        if (this.getOp().equals(t.getOp()) &&
                this.getT1().equals(t.getT1()) &&
                this.getT2().equals(t.getT2())) return true;
        return false;
    }
}