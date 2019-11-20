package mirea.triad_optimisation;

import mirea.interpreter.Calculator;
import mirea.parser.ParserToken;
import mirea.structures.CustomList;
import mirea.structures.CustomSet;
import mirea.table.Record;
import mirea.table.SymbolTable;
import mirea.token.Name;

import java.util.List;
import java.util.logging.Logger;

public class Opt {
    private Conv conv = new Conv();
    private Logger logger = Logger.getLogger(Opt.class.getName());
    SymbolTable table = new SymbolTable();
    private Calculator calculator = new Calculator(table, logger);

    public Opt () {
    }

    private ParserToken replaceVarWithValue(ParserToken el) {
        Record rec = table.localLookup(el.getValue());
        if (rec != null) {
            return new ParserToken(rec.getType(), rec.getValue() + "");
        }
        return el;
    }

    private ParserToken replaceRefWithValue(ParserToken el, List<Triad> triads) {
        int index = Integer.parseInt(el.getValue());
        if (triads.get(index).getOp().getType().equals("CONST")) {
            return triads.get(index).getT1();
        }
        return el;
    }

    private boolean isConstant(Triad triad) {
        if (isConstant(triad.getT1().getType().toUpperCase()) && isConstant(triad.getT2().getType().toUpperCase())) {
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

    public List<Triad> findConstants(List<Triad> triads) throws Exception{
        for (int i=0; i<triads.size(); i++) {
            Record rec;
            Triad curTriad = triads.get(i);

            if (curTriad.getOp().getType().equals(Name.ENTER_SCOPE)) {
                table.enterScope();
                continue;
            }
            if (curTriad.getOp().getType().equals(Name.EXIT_SCOPE)) {
                table.exitScope();
                continue;
            }
            if (curTriad.getOp().getType() == Name.DEF) {
                Object value = null;
                if (curTriad.getOp().getType().equals(Name.LIST)) value = new CustomList<Integer>();
                else if (curTriad.getOp().getType().equals(Name.SET)) value = new CustomSet<Integer>();
                if (table.lookup(curTriad.getT1().getValue()) == null) {
                    table.insertSymbol(new Record(curTriad.getT1().getValue(), value, curTriad.getOp().getValue()));
                }
                continue;
            }

            if (curTriad.getOp().getType() == "TRANS") {
                continue;
            }

            if (curTriad.getT1().getType().equals(Name.VAR)) {
                curTriad.setT1(replaceVarWithValue(curTriad.getT1()));
            }
            else if (curTriad.getT1().getType().equals(Name.REF)) {
                curTriad.setT1(replaceRefWithValue(curTriad.getT1(), triads));
            }

            if (curTriad.getT2().getType().equals(Name.VAR)) {
                curTriad.setT2(replaceVarWithValue(curTriad.getT2()));
            }
            else if (curTriad.getT2().getType().equals(Name.REF)) {
                    curTriad.setT2(replaceRefWithValue(curTriad.getT2(), triads));
            }

            if (curTriad.getOp().getType().equals(Name.OP) && isConstant(curTriad)) {
                String type = curTriad.getT1().getType().toUpperCase();
                boolean flag = true;
                switch (curTriad.getOp().getValue()) {
                    case "+":
                        curTriad.setT1(calculator.sum(curTriad.getT2(), curTriad.getT1()));
                        break;
                    case "-":
                        curTriad.setT1(calculator.dif(curTriad.getT2(), curTriad.getT1()));;
                        break;
                    case "*":
                        curTriad.setT1(calculator.mult(curTriad.getT1(), curTriad.getT2()));
                        break;
                    case "/":
                        curTriad.setT1(calculator.div(curTriad.getT2(), curTriad.getT1()));
                    case "<":
                        curTriad.setT1(calculator.isLess(curTriad.getT2(), curTriad.getT1()));
                        break;
                    case ">":
                        curTriad.setT1(calculator.isBigger(curTriad.getT2(), curTriad.getT1()));
                        break;
                    case ">=":
                        curTriad.setT1(calculator.isBiggerOrEq(curTriad.getT2(), curTriad.getT1()));
                        break;
                    case "<=":
                        curTriad.setT1(calculator.isLessOrEq(curTriad.getT2(), curTriad.getT1()));
                        break;
                    case "==":
                        curTriad.setT1(calculator.isEq(curTriad.getT2(), curTriad.getT1()));
                        break;
                    case "!=":
                        curTriad.setT1(calculator.isNotEq(curTriad.getT2(), curTriad.getT1()));
                        break;
                    case "&&":
                        curTriad.setT1(calculator.conj(curTriad.getT2(), curTriad.getT1()));
                        break;
                    case "||":
                        curTriad.setT1(calculator.disj(curTriad.getT2(), curTriad.getT1()));
                        break;
                    case "get":
                        curTriad.setT1(calculator.get(curTriad.getT2(), curTriad.getT1()));
                        break;
                    case "contains":
                        curTriad.setT1(calculator.contains(curTriad.getT2(), curTriad.getT1()));
                        break;
                    default:
                        flag = false;

                }
                if (flag) {
                    curTriad.setOp(new ParserToken("CONST", type));
                    curTriad.setT2(new ParserToken());
                }
            continue;
            }

            if (curTriad.getOp().getValue().equals("=")) {
                rec = table.localLookup(curTriad.getT1().getValue());
                if (rec != null) {
                    switch (curTriad.getT2().getType().toUpperCase()) {
                        case Name.INT:
                        case Name.DOUBLE:
                        case Name.STRING:
                        case Name.CONST:
                            rec.setValue(curTriad.getT2().getValue());
                            break;
                        default:
                            table.deleteSymbol(rec);
                    }
                }
                continue;
            }
            if (curTriad.getOp().getValue().equals("add")) {
                rec = table.localLookup(curTriad.getT1().getValue());
                if (rec != null) {
                    switch (curTriad.getT2().getType()) {
                        case Name.INT:
                        case Name.DOUBLE:
                        case Name.STRING:
                        case Name.CONST:
                            switch(curTriad.getT1().getType()) {
                                case Name.LIST:
                                    CustomList<Integer> list = (CustomList<Integer>) rec.getValue();
                                    list.add(Integer.parseInt(curTriad.getT2().getValue()));
                                    break;
                                case Name.SET:
                                    CustomSet<Integer> set = (CustomSet<Integer>) rec.getValue();
                                    set.add(Integer.parseInt(curTriad.getT2().getValue()));
                                    break;
                            }
                        default:
                            table.deleteSymbol(rec);
                    }
                }
            }
        }
        return triads;
    }
}