package mirea.triad_optimisation;

import mirea.parser.ParserToken;
import mirea.token.Name;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.String.valueOf;

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
        int start = inp.size();
        HashMap<Integer, Integer> corr = new HashMap<>();
        List<ParserToken> trRef = new ArrayList<>();
        for (int i=0; i<inp.size(); i++) {
            int difference = start - inp.size();
            switch(inp.get(i).getType()) {
                case "OP":
                    switch(inp.get(i).getValue()) {
                        case "print":
                        case "println":
                            triads = singleOp(triads, inp, corr, i, difference);
                            i--;
                            break;
                        default:
                            triads = doubleOp(triads, inp, corr, i, difference);
                            i -= 2;
                    }
                    break;
                case "DEF":
                    triads = singleOp(triads, inp, corr, i, difference);
                    i--;
                    break;
                case Name.TRANS:
                    switch(inp.get(i).getValue()) {
                        case "!":
                            singleOp(triads, inp, corr, i, difference);
                            i--;
                            trRef.add(triads.get(triads.size()-1).getT1());
                            break;
                        case "!F":
                            doubleOp(triads, inp, corr, i, difference);
                            i -= 2;
                            trRef.add(triads.get(triads.size()-1).getT2());
                            break;
                    }
                    break;
                case Name.ENTER_SCOPE:
                case Name.EXIT_SCOPE:
                    triads.add(new Triad(inp.get(i),new ParserToken(), new ParserToken()));
                    break;
            }
        }
        for (ParserToken token : trRef) {
            token.setValue(valueOf(corr.get(Integer.parseInt(token.getValue()))));
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
            if (curTriad.getT1().notBlank()) {
                revNot.add(curTriad.getT1());
            }
            revNot.add(curTriad.getOp());
        }
        return revNot;
    }

    private List<Triad> singleOp(List<Triad> t, List<ParserToken> el, HashMap<Integer, Integer> corr, int i, int dif) {
        t.add(new Triad(el.get(i), el.get(i-1), new ParserToken()));
        el.remove(i);
        el.set(i-1, new ParserToken("REF", t.size()-1 + ""));
        for (int j=i+dif; j>=i+dif-1; j--) {
            if (corr.containsKey(j)) continue;
            corr.put(j, i);
        }
        return t;
    }

    private List<Triad> doubleOp(List<Triad> t, List<ParserToken> el, HashMap<Integer, Integer> corr, int i, int dif) {
        t.add(new Triad(el.get(i), el.get(i - 2), el.get(i - 1)));
        el.remove(i);
        el.remove(i-1);
        el.set(i-2, new ParserToken("REF", t.size()-1 + ""));
        for (int j=i+dif; j>=i+dif-2; j--) {
            if (corr.containsKey(j)) continue;
            corr.put(j, i);
        }
        return t;
    }

}

