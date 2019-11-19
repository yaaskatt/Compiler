package mirea.triad_optimisation;

import mirea.parser.ParserToken;
import mirea.structures.CustomList;
import mirea.structures.CustomSet;
import mirea.table.Record;
import mirea.table.SymbolTable;
import mirea.token.Name;

import java.util.List;

public class Opt {
    private Conv conv = new Conv();
    SymbolTable table = new SymbolTable();

    public Opt () {
    }

    private ParserToken replaceVarWithValue(ParserToken el) {
        Record rec = table.lookup(el.getValue());
        if (rec != null) {
            return new ParserToken(rec.getType(), rec.getValue() + "");
        }
        return el;
    }

    private ParserToken replaceRefWithValue(ParserToken el, List<Triad> triads) {
        int index = Integer.parseInt(el.getValue());
        if (triads.get(index).getOp().getType().equals("CONST")) {
            return triads.get(index).getEl1();
        }
        return el;
    }

    private boolean isConstant(Triad triad) {
        if (isConstant(triad.getEl1().getType().toUpperCase()) && isConstant(triad.getEl2().getType().toUpperCase())) {
            return true;
        }
        return false;
    }

    private boolean isConstant(String type) {
        if (type.equals(Name.INT) || type.equals(Name.DOUBLE) || type.equals(Name.STRING)) {
            return true;
        }
        return false;
    }

    private String sum(String type, ParserToken el1, ParserToken el2) {
        switch(type) {
            case Name.INT:
                return (Integer.parseInt(el1.getValue()) + Integer.parseInt(el2.getValue())) + "";
            case Name.DOUBLE:
                return (Double.parseDouble(el1.getValue()) + Double.parseDouble(el2.getValue())) + "";
        }
        return "";
    }

    private String dif(String type, ParserToken el1, ParserToken el2) {
        switch(type.toUpperCase()) {
            case Name.INT:
                return (Integer.parseInt(el1.getValue()) - Integer.parseInt(el2.getValue())) + "";
            case Name.DOUBLE:
                return (Double.parseDouble(el1.getValue()) - Double.parseDouble(el2.getValue())) + "";
        }
        return "";
    }

    private String mult(String type, ParserToken el1, ParserToken el2) {
        switch(type) {
            case Name.INT:
                return (Integer.parseInt(el1.getValue()) * Integer.parseInt(el2.getValue())) + "";
            case Name.DOUBLE:
                return (Double.parseDouble(el1.getValue()) * Double.parseDouble(el2.getValue())) + "";
        }
        return "";
    }

    private String div(String type, ParserToken el1, ParserToken el2) {
        switch(type) {
            case Name.INT:
                return (Integer.parseInt(el1.getValue()) / Integer.parseInt(el2.getValue())) + "";
            case Name.DOUBLE:
                return (Double.parseDouble(el1.getValue()) / Double.parseDouble(el2.getValue())) + "";
        }
        return "";
    }




    public List<Triad> findConstants(List<Triad> triads) {
        for (int i=0; i<triads.size(); i++) {
            Record rec;
            Triad curTriad = triads.get(i);

            if (curTriad.getOp().getType() == Name.DEF) {
                Object value = null;
                if (curTriad.getOp().getType().equals(Name.LIST)) value = new CustomList<Integer>();
                else if (curTriad.getOp().getType().equals(Name.SET)) value = new CustomSet<Integer>();
                table.insertSymbol(new Record(curTriad.getEl1().getValue(), value, curTriad.getOp().getValue()));
                continue;
            }

            if (curTriad.getEl1().getType().equals(Name.VAR)) {
                curTriad.setEl1(replaceVarWithValue(curTriad.getEl1()));
            }
            else if (curTriad.getEl1().getType().equals(Name.REF)) {
                curTriad.setEl1(replaceRefWithValue(curTriad.getEl1(), triads));
            }

            if (curTriad.getEl2().getType().equals(Name.VAR)) {
                curTriad.setEl2(replaceVarWithValue(curTriad.getEl2()));
            }
            else if (curTriad.getEl2().getType().equals(Name.REF)) {
                    curTriad.setEl2(replaceRefWithValue(curTriad.getEl2(), triads));
            }

            if (curTriad.getOp().getType().equals(Name.OP) && isConstant(curTriad)) {
                String type = curTriad.getEl1().getType().toUpperCase();
                boolean flag = false;
                switch (curTriad.getOp().getValue()) {
                    case "+":
                        curTriad.setEl1(new ParserToken(type, sum(type, curTriad.getEl1(), curTriad.getEl2())));
                        flag = true;
                        break;
                    case "-":
                        curTriad.setEl1(new ParserToken(type, dif(type, curTriad.getEl1(), curTriad.getEl2())));
                        flag = true;
                        break;
                    case "*":
                        curTriad.setEl1(new ParserToken(type, mult(type, curTriad.getEl1(), curTriad.getEl2())));
                        flag = true;
                        break;
                    case "/":
                        curTriad.setEl1(new ParserToken(type, div(type, curTriad.getEl1(), curTriad.getEl2())));
                        flag = true;
                }
                if (flag) {
                    curTriad.setOp(new ParserToken("CONST", type));
                    curTriad.setEl2(new ParserToken());
                }

            }

            else if (curTriad.getOp().getValue().equals("=")) {
                rec = table.lookup(curTriad.getEl1().getValue());
                if (rec != null) {
                    switch (curTriad.getEl2().getType().toUpperCase()) {
                        case Name.INT:
                        case Name.DOUBLE:
                        case Name.STRING:
                        case Name.CONST:
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
