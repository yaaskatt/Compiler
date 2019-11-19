package mirea.triad_optimisation;

import mirea.parser.Element;
import mirea.structures.CustomList;
import mirea.structures.CustomSet;
import mirea.table.Record;
import mirea.table.SymbolTable;

import java.util.List;

public class Opt {
    private Conv conv = new Conv();
    SymbolTable table = new SymbolTable();

    private final String INT_TYPE = "INT";
    private final String DOUBLE_TYPE = "DOUBLE";
    private final String STRING_TYPE = "STRING";
    private final String LIST_TYPE = "List";
    private final String SET_TYPE = "Set";
    private final String OP_TYPE = "OP";
    private final String ADR_TYPE = "ADR";
    private final String VAR_TYPE = "VAR";
    private final String DEF_TYPE = "DEF";
    private final String TR_TYPE = "TRANS";
    private final String LB_TYPE = "L_CB";
    private final String RB_TYPE = "R_CB";
    private final String REF_TYPE = "REF";
    private final String CONST_TYPE = "CONST";

    public Opt () {
    }

    private Element replaceVarWithValue(Element el) {
        Record rec = table.lookup(el.getValue());
        if (rec != null) {
            return new Element(rec.getType(), rec.getValue() + "");
        }
        return el;
    }

    private Element replaceRefWithValue(Element el, List<Triad> triads) {
        int index = Integer.parseInt(el.getValue());
        if (triads.get(index).getOp().getType().equals("CONST")) {
            return triads.get(index).getEl1();
        }
        return el;
    }

    private Element replaceWithConstant(Element el) {
        return el;
    }

    private boolean isConstant(Triad triad) {
        if (isConstant(triad.getEl1().getType().toUpperCase()) && isConstant(triad.getEl2().getType().toUpperCase())) {
            return true;
        }
        return false;
    }

    private boolean isConstant(String type) {
        if (type.equals(INT_TYPE) || type.equals(DOUBLE_TYPE) || type.equals(STRING_TYPE)) {
            return true;
        }
        return false;
    }

    private String sum(String type, Element el1, Element el2) {
        switch(type) {
            case INT_TYPE:
                return (Integer.parseInt(el1.getValue()) + Integer.parseInt(el2.getValue())) + "";
            case DOUBLE_TYPE:
                return (Double.parseDouble(el1.getValue()) + Double.parseDouble(el2.getValue())) + "";
        }
        return "";
    }

    private String dif(String type, Element el1, Element el2) {
        switch(type.toUpperCase()) {
            case INT_TYPE:
                return (Integer.parseInt(el1.getValue()) - Integer.parseInt(el2.getValue())) + "";
            case DOUBLE_TYPE:
                return (Double.parseDouble(el1.getValue()) - Double.parseDouble(el2.getValue())) + "";
        }
        return "";
    }

    private String mult(String type, Element el1, Element el2) {
        switch(type) {
            case INT_TYPE:
                return (Integer.parseInt(el1.getValue()) * Integer.parseInt(el2.getValue())) + "";
            case DOUBLE_TYPE:
                return (Double.parseDouble(el1.getValue()) * Double.parseDouble(el2.getValue())) + "";
        }
        return "";
    }

    private String div(String type, Element el1, Element el2) {
        switch(type) {
            case INT_TYPE:
                return (Integer.parseInt(el1.getValue()) / Integer.parseInt(el2.getValue())) + "";
            case DOUBLE_TYPE:
                return (Double.parseDouble(el1.getValue()) / Double.parseDouble(el2.getValue())) + "";
        }
        return "";
    }




    public List<Triad> findConstants(List<Triad> triads) {
        for (int i=0; i<triads.size(); i++) {
            Record rec;
            Triad curTriad = triads.get(i);

            if (curTriad.getOp().getType() == DEF_TYPE) {
                Object value = null;
                if (curTriad.getOp().getType().equals(LIST_TYPE)) value = new CustomList<Integer>(); // int list
                else if (curTriad.getOp().getType().equals(SET_TYPE)) value = new CustomSet<Integer>(); // int list
                table.insertSymbol(new Record(curTriad.getEl1().getValue(), value, curTriad.getOp().getValue()));
                continue;
            }

            if (curTriad.getEl1().getType().equals(VAR_TYPE)) {
                curTriad.setEl1(replaceVarWithValue(curTriad.getEl1()));
            }
            else if (curTriad.getEl1().getType().equals(REF_TYPE)) {
                curTriad.setEl1(replaceRefWithValue(curTriad.getEl1(), triads));
            }

            if (curTriad.getEl2().getType().equals(VAR_TYPE)) {
                curTriad.setEl2(replaceVarWithValue(curTriad.getEl2()));
            }
            else if (curTriad.getEl2().getType().equals(REF_TYPE)) {
                    curTriad.setEl2(replaceRefWithValue(curTriad.getEl2(), triads));
            }

            if (curTriad.getOp().getType().equals(OP_TYPE) && isConstant(curTriad)) {
                String type = curTriad.getEl1().getType().toUpperCase();
                boolean flag = false;
                switch (curTriad.getOp().getValue()) {
                    case "+":
                        curTriad.setEl1(new Element(type, sum(type, curTriad.getEl1(), curTriad.getEl2())));
                        flag = true;
                        break;
                    case "-":
                        curTriad.setEl1(new Element(type, dif(type, curTriad.getEl1(), curTriad.getEl2())));
                        flag = true;
                        break;
                    case "*":
                        curTriad.setEl1(new Element(type, mult(type, curTriad.getEl1(), curTriad.getEl2())));
                        flag = true;
                        break;
                    case "/":
                        curTriad.setEl1(new Element(type, div(type, curTriad.getEl1(), curTriad.getEl2())));
                        flag = true;
                        break;
                }
                if (flag) {
                    curTriad.setOp(new Element("CONST", type));
                    curTriad.setEl2(new Element());
                }

            }

            else if (curTriad.getOp().getValue().equals("=")) {
                rec = table.lookup(curTriad.getEl1().getValue());
                if (rec != null) {
                    switch (curTriad.getEl2().getType().toUpperCase()) {
                        case INT_TYPE:
                        case DOUBLE_TYPE:
                        case STRING_TYPE:
                        case CONST_TYPE:
                            rec.setValue(curTriad.getEl2().getValue());
                            break;
                        default:
                            table.deleteSymbol(rec);
                    }
                }
            }
        }
        return triads;
    }
}
