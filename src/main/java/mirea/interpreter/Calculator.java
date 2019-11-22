package mirea.interpreter;

import mirea.parser.ParserToken;
import mirea.parser.ParserTokenType;
import mirea.structures.CustomList;
import mirea.structures.CustomSet;
import mirea.table.Record;
import mirea.table.SymbolTable;

import java.util.logging.Logger;

public class Calculator {
    private SymbolTable symbolTable;
    private Logger logger;

    public Calculator(SymbolTable symbolTable, Logger logger) {
        this.symbolTable = symbolTable;
        this.logger = logger;
    }

    public ParserToken sum(ParserToken arg1, ParserToken arg2) {
        ParserTokenType type = getTypeForSimpleOp(arg1, arg2);
        switch(type) {
            case INT:
                return new ParserToken(type, (Integer.parseInt(arg1.getValue()) + Integer.parseInt(arg2.getValue())) + "");
            case DOUBLE:
                return new ParserToken(type, (Double.parseDouble(arg1.getValue()) + Double.parseDouble(arg2.getValue())) + "");
        }
        return null;
    }

    public ParserToken dif(ParserToken arg2, ParserToken arg1) {
        ParserTokenType type = getTypeForSimpleOp(arg1, arg2);
        switch(type) {
            case INT:
                return new ParserToken(type,(Integer.parseInt(arg1.getValue()) - Integer.parseInt(arg2.getValue())) + "");
            case DOUBLE:
                return new ParserToken(type, (Double.parseDouble(arg1.getValue()) - Double.parseDouble(arg2.getValue())) + "");
        }
        return null;
    }

    public ParserToken mult(ParserToken arg2, ParserToken arg1) {
        ParserTokenType type = getTypeForSimpleOp(arg1, arg2);
        switch(type) {
            case INT:
                return new ParserToken(type,(Integer.parseInt(arg1.getValue()) * Integer.parseInt(arg2.getValue())) + "");
            case DOUBLE:
                return new ParserToken(type, (Double.parseDouble(arg1.getValue()) * Double.parseDouble(arg2.getValue())) + "");
        }
        return null;
    }

    public ParserToken div(ParserToken arg2, ParserToken arg1) {
        ParserTokenType type = getTypeForSimpleOp(arg1, arg2);
        switch(type) {
            case INT:
                return new ParserToken(type,(Integer.parseInt(arg1.getValue()) / Integer.parseInt(arg2.getValue())) + "");
            case DOUBLE:
                return new ParserToken(type, (Double.parseDouble(arg1.getValue()) / Double.parseDouble(arg2.getValue())) + "");
        }
        return null;
    }

    private ParserTokenType getTypeForSimpleOp(ParserToken arg2, ParserToken arg1) {
        if (arg1.getType() != arg2.getType())
            logger.warning("Types mismatch(" + arg1.getType() + "-" + arg2.getType() +
                    "): consider reviewing your code");
        return arg1.getType();
    }

    public ParserToken disj(ParserToken arg2, ParserToken arg1) {
        return new ParserToken(ParserTokenType.INT, boolToInt(intToBool(Integer.parseInt(arg1.getValue())) ||
                intToBool(Integer.parseInt(arg2.getValue()))).toString());
    }

    public ParserToken conj(ParserToken arg2, ParserToken arg1) {
        return new ParserToken(ParserTokenType.INT, boolToInt(intToBool(Integer.parseInt(arg1.getValue())) &&
                intToBool(Integer.parseInt(arg2.getValue()))).toString());
    }

    public ParserToken isNotEq(ParserToken arg2, ParserToken arg1)
            throws Exception {
        return new ParserToken(ParserTokenType.INT, boolToInt(compareToken(arg1, arg2) != 0).toString());
    }

    public ParserToken isEq(ParserToken arg2, ParserToken arg1)
            throws Exception {
        return new ParserToken(ParserTokenType.INT, boolToInt(compareToken(arg1, arg2) == 0).toString());
    }

    public ParserToken isLessOrEq(ParserToken arg2, ParserToken arg1)
            throws Exception {
        return new ParserToken(ParserTokenType.INT, boolToInt(compareToken(arg1, arg2) <= 0).toString());
    }

    public ParserToken isBiggerOrEq(ParserToken arg2, ParserToken arg1)
            throws Exception {
        return new ParserToken(ParserTokenType.INT, boolToInt(compareToken(arg1, arg2) >= 0).toString());
    }

    public ParserToken isBigger(ParserToken arg2, ParserToken arg1)
            throws Exception {
        return new ParserToken(ParserTokenType.INT, boolToInt(compareToken(arg1, arg2) > 0).toString());
    }

    public ParserToken isLess(ParserToken arg2, ParserToken arg1)
            throws Exception {
        return new ParserToken(ParserTokenType.INT, boolToInt(compareToken(arg1, arg2) < 0).toString());
    }

    public Integer boolToInt(Boolean b) {
        return b ? 0 : 1;
    }

    public Boolean intToBool(Integer b) {
        return b == 0;
    }

    private int compareToken(ParserToken arg2, ParserToken arg1) throws Exception {
        getTypeForSimpleOp(arg1, arg2);
        switch (arg1.getType()) {
            case INT:
                return Integer.parseInt(arg2.getValue()) - Integer.parseInt(arg1.getValue());
            case DOUBLE:
                double res = Double.parseDouble(arg2.getValue()) - Double.parseDouble(arg1.getValue());
                if (res > 0) return 1;
                else if (res == 0) return 0;
                else return -1;
            case STRING:
                return arg2.getValue().compareTo(arg1.getValue());
            default:
                logger.severe("Can not compare type " + arg1.getType());
                throw new Exception("Comparison of type " + arg1.getType() +
                        " is not supported");
        }
    }

    public ParserToken contains(ParserToken el, ParserToken var) throws Exception {
        Record record = symbolTable.lookup(var.getValue());

        if (el.getType() != ParserTokenType.INT) {
            throw new Exception("Type mismatch: " + el.getType() + ", required: INT");
        }

        switch (record.getType()) {
            case LIST:
                @SuppressWarnings("unchecked") CustomList<Integer> list = (CustomList<Integer>) record.getValue();
                return new ParserToken(ParserTokenType.INT, boolToInt(list.contains(Integer.parseInt(el.getValue()))).toString());
            case SET:
                @SuppressWarnings("unchecked") CustomSet<Integer> set = (CustomSet<Integer>) record.getValue();
                return new ParserToken(ParserTokenType.INT, boolToInt(set.contains(Integer.parseInt(el.getValue()))).toString());
            default: throw new Exception("Trying yo put to type " + record.getType());
        }
    }

    public ParserToken get(ParserToken index, ParserToken var) throws Exception {
        Record record = symbolTable.lookup(var.getValue());
        Integer val;
        if (record == null) throw new Exception("Variable " + var.getValue() + " is not defined" +
                " in this scope");

        switch (record.getType()) {
            case LIST:
                @SuppressWarnings("unchecked") CustomList<Integer> list = (CustomList<Integer>) record.getValue();
                val = list.get(Integer.parseInt(index.getValue()));
                logger.fine("Got " + var.getValue() + "[" + index + "]=" + val);
                break;
            default:
                throw new Exception("Calling get on " + record.getType() + " variable.");
        }
        if (val != null) return new ParserToken(ParserTokenType.INT, val.toString());
        return null;
    }

    public void add(ParserToken el, ParserToken var)
            throws Exception {
        Record record = symbolTable.lookup(var.getValue());

        if (el.getType() != ParserTokenType.INT) {
            throw new Exception("Type mismatch: " + el.getType() + ", required: INT");
        }

        switch (record.getType()) {
            case LIST:
                CustomList<Integer> list = (CustomList<Integer>) record.getValue();
                list.add(Integer.parseInt(el.getValue()));
                break;
            case SET:
                CustomSet<Integer> set = (CustomSet<Integer>) record.getValue();
                set.add(Integer.parseInt(el.getValue()));
                break;
            default: throw new Exception("Trying yo put to type " + record.getType());
        }
        logger.fine("Put to " + var.getValue() + " " + el);
    }

}
