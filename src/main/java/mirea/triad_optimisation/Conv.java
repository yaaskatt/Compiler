package mirea.triad_optimisation;

import mirea.parser.ParserToken;

import java.util.ArrayList;
import java.util.List;

public class Conv {

    private final String INT_TYPE = "INT";
    private final String DOUBLE_TYPE = "DOUBLE";
    private final String STRING_TYPE = "STRING";
    private final String LIST_TYPE = "List";
    private final String SET_TYPE = "Set";
    private final String MAP_TYPE = "Map";
    private final String OP_TYPE = "OP";
    private final String ADR_TYPE = "ADR";
    private final String VAR_TYPE = "VAR";
    private final String DEF_TYPE = "DEF";
    private final String TR_TYPE = "TRANS";
    private final String LB_TYPE = "L_CB";
    private final String RB_TYPE = "R_CB";

    public Conv() {
    }

    public List<Triad> reverseNot_toTriads(List<ParserToken> inp) {
        List<Triad> triads = new ArrayList<Triad>();
        for (int i=0; i<inp.size(); i++) {
            switch(inp.get(i).getType()) {
                case "OP":
                    switch(inp.get(i).getValue()) {
                        case "print":
                        case "println":
                            triads = singleOp(triads, inp, i);
                            i--;
                            break;
                        default:
                            triads = doubleOp(triads, inp, i);
                            i -= 2;
                    }
                    break;
                case "DEF":
                    triads = singleOp(triads, inp, i);
                    i--;
                    break;
            }
        }
        return triads;
    }

    public List<ParserToken> triads_toReverseNot(List<Triad> inp) {
        List<ParserToken> revNot = new ArrayList<>();
        for (int i=0; i<inp.size(); i++) {
            Triad curTriad = inp.get(i);
            if (curTriad.getOp().getType().equals("CONST")) continue;
            if (curTriad.getT2().notBlank())
                revNot.add(curTriad.getT2());
            revNot.add(curTriad.getT1());
            revNot.add(curTriad.getOp());
        }
        return revNot;
    }

    private List<Triad> singleOp(List<Triad> t, List<ParserToken> el, int i) {
        t.add(new Triad(el.get(i), el.get(i-1), new ParserToken()));
        el.remove(i);
        el.set(i-1, new ParserToken("REF", t.size()-1 + ""));
        return t;
    }

    private List<Triad> doubleOp(List<Triad> t, List<ParserToken> el, int i) {
        t.add(new Triad(el.get(i), el.get(i - 2), el.get(i - 1)));
        el.remove(i);
        el.remove(i-1);
        el.set(i-2, new ParserToken("REF", t.size()-1 + ""));
        return t;
    }

}

