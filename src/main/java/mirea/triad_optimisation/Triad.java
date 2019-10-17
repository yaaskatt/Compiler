package mirea.triad_optimisation;
import mirea.parser.Element;

public class Triad {
    private Element op;
    private Element el1;
    private Element el2;

    public Triad() {
    }

    public Triad(Element op) {
        this.op = op;
    }

    public Triad(Element op, Element el1) {
        this.op = op;
        this.el1 = el1;
    }

    public Triad(Element op, Element el1, Element el2) {
        this.op = op;
        this.el1 = el1;
        this.el2 = el2;
    }

    public Element getOp() {
        return this.op;
    }

    public Element getEl1() {
        return el1;
    }

    public Element getEl2() {
        return el2;
    }

    public void setOp(Element op) {
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

    @Override
    public boolean equals(Object obj) {
        Triad t = (Triad)obj;
        if (this.getOp().equals(t.getOp()) &&
                this.getEl1().equals(t.getEl1()) &&
                this.getEl2().equals(t.getEl2())) return true;
        return false;
    }
}