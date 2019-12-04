package mirea.optimizer;

import mirea.interpreter.Calculator;
import mirea.parser.ParserToken;
import mirea.parser.ParserTokenType;
import mirea.structures.CustomList;
import mirea.structures.CustomSet;
import mirea.table.Record;
import mirea.table.SymbolTable;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Optimizer {

    private Logger logger = Logger.getLogger(Optimizer.class.getName());
    SymbolTable constantsTable = new SymbolTable();
    private Calculator calculator = new Calculator(constantsTable, logger);

    public Optimizer() {
    }

    private ParserToken replaceVarWithValue(ParserToken el) {
        Record rec = constantsTable.localLookup(el.getValue());
        if (rec != null) {
            return new ParserToken(rec.getType(), rec.getValue() + "");

        }
        return el;
    }

    private ParserToken replaceRefWithValue(ParserToken el, List<Triad> triads) {
        int index = Integer.parseInt(el.getValue());
        if (triads.get(index).getOp().getType() == ParserTokenType.CONST) {
            return triads.get(index).getT1();
        }
        return el;
    }

    private boolean isConstant(Triad triad) {
        if (isConstant(triad.getT1().getType()) && isConstant(triad.getT2().getType())) {
            return true;
        }
        return false;
    }

    private boolean isConstant(ParserTokenType type) {
        if (type == ParserTokenType.INT || type == ParserTokenType.DOUBLE || type == ParserTokenType.STRING) {
            return true;
        }
        return false;
    }

    public List<Triad> findConstants(List<Triad> triadList) throws Exception{
        List <Triad> origTempList = new ArrayList<>();
        boolean condition = false;
        for (int i=0; i<triadList.size(); i++) {

            Record rec;
            Triad curTriad = triadList.get(i);
            System.err.print("CURRENT TRIAD\t" + triadStr(curTriad) + "\n");
            switch(curTriad.getOp().getType()) {
                case ENTER_SCOPE:
                    constantsTable.enterScope();
                    continue;
                case EXIT_SCOPE:
                    constantsTable.exitScope();
                    continue;
                case DEF:
                    Object value = null;
                    if (constantsTable.lookup(curTriad.getT1().getValue()) == null) {
                        switch(ParserTokenType.valueOf(curTriad.getOp().getValue().toUpperCase())) {
                            case LIST:  value = new CustomList<Integer>(); break;
                            case SET:   value = new CustomSet<Integer>(); break;


                        }
                        if (constantsTable.lookup(curTriad.getT1().getValue()) == null) {
                            constantsTable.insertSymbol(new Record(curTriad.getT1().getValue(), value,
                                    ParserTokenType.valueOf(curTriad.getOp().getValue().toUpperCase())));
                        }
                    }
                    continue;

                case TRANS:
                    List<Triad> toReplace = triadList.subList(i - origTempList.size(), i);
                    triadList.removeAll(toReplace);
                    triadList.addAll(i - origTempList.size(), origTempList);
                    logger.info(origTempList.size() + " VALUE(S) RESTORED" + "\n");
                    origTempList.clear();
                    continue;
            }

            origTempList.add(new Triad(curTriad));

            if (curTriad.getT1().getType() == ParserTokenType.VAR) {
                ParserToken replToken = replaceVarWithValue(curTriad.getT1());
                if (replToken != curTriad.getT1()) {
                    curTriad.setT1(replaceVarWithValue(curTriad.getT1()));
                    logger.info("ARG1 REPLACED WITH VALUE\t" + triadStr(curTriad) + "\n");
                }
            }
            else if (curTriad.getT1().getType().equals(ParserTokenType.REF)) {
                ParserToken replToken = replaceRefWithValue(curTriad.getT1(), triadList);
                if (replToken != curTriad.getT1()) {
                    curTriad.setT1(replaceRefWithValue(curTriad.getT1(), triadList));
                    logger.info("ARG1 REPLACED WITH VALUE\t" + triadStr(curTriad) + "\n");
                }
            }

            if (curTriad.getT2().getType() == ParserTokenType.VAR) {
                ParserToken replToken = replaceVarWithValue(curTriad.getT2());
                if (replToken != curTriad.getT2()) {
                    curTriad.setT2(replaceVarWithValue(curTriad.getT2()));
                    logger.info("ARG2 REPLACED WITH VALUE\t" + triadStr(curTriad) + "\n");
                }
            }
            else if (curTriad.getT2().getType().equals(ParserTokenType.REF)) {
                ParserToken replToken = replaceRefWithValue(curTriad.getT2(), triadList);
                if (replToken != curTriad.getT2()) {
                    curTriad.setT2(replaceRefWithValue(curTriad.getT2(), triadList));
                    logger.info("ARG2 REPLACED WITH VALUE\t" + triadStr(curTriad) + "\n");
                }
            }

            if (curTriad.getOp().getValue().equals("=")) {
                origTempList.clear();
                rec = constantsTable.localLookup(curTriad.getT1().getValue());
                switch (curTriad.getT2().getType()) {
                    case INT:
                    case DOUBLE:
                    case STRING:
                    case CONST:
                        rec.setValue(curTriad.getT2().getValue());
                        break;
                    default:
                        constantsTable.deleteSymbol(curTriad.getT1().getValue());
                }

                continue;
            }

            if (curTriad.getOp().getValue().equals("print")) {
                origTempList.clear();
                continue;
            }

            if (curTriad.getOp().getType() == ParserTokenType.OP) {
                boolean flag = true;
                if (isConstant(curTriad)) {
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
                            if (!condition) curTriad.setT1(calculator.isLess(curTriad.getT2(), curTriad.getT1()));
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
                    }

                }
                else if (curTriad.getT1().getType() == ParserTokenType.ADR &&
                        constantsTable.localLookup(curTriad.getT1().getValue()) != null &&
                        isConstant(curTriad.getT2().getType())){
                    switch (curTriad.getOp().getValue()) {
                        case "get":
                            curTriad.setT1(calculator.get(curTriad.getT2(), curTriad.getT1()));
                            break;
                        case "contains":
                            curTriad.setT1(calculator.contains(curTriad.getT2(), curTriad.getT1()));
                            break;
                        case "add":
                            if (constantsTable.localLookup(curTriad.getT1().getValue()) != null)
                            calculator.add(curTriad.getT2(), curTriad.getT1());
                            else constantsTable.deleteSymbol(curTriad.getT1().getValue());
                            flag = false;
                            break;
                    }

                }
                else {
                    if (curTriad.getOp().getValue().equals("add"))
                        constantsTable.deleteSymbol(curTriad.getT1().getValue());
                    flag = false;
                }
                if (flag) {
                    curTriad.setOp(new ParserToken(ParserTokenType.CONST, "const"));
                    curTriad.setT2(new ParserToken());
                    logger.info("OPERATION PROCESSED\t" + triadStr(curTriad) + "\n");
                }
            }
        }
        return triadList;
    }

    public String triadStr(Triad triad) {
        return triad.getOp().getType() + " " + triad.getOp().getValue() + " (" +
                triad.getT1().getType() + " " + triad.getT1().getValue() + ", " +
                triad.getT2().getType() + " " + triad.getT2().getValue() + ")";
    }
}
