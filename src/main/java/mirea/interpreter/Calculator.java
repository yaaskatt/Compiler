package mirea.interpreter;

import mirea.parser.ParserToken;
import mirea.structures.CustomList;
import mirea.structures.CustomSet;
import mirea.table.Record;
import mirea.table.SymbolTable;
import mirea.token.Name;

import java.util.logging.Logger;

public class Calculator {
    private SymbolTable symbolTable;
    private Logger logger;

    public Calculator(SymbolTable symbolTable, Logger logger) {
        this.symbolTable = symbolTable;
        this.logger = logger;
    }

    public ParserToken sum(ParserToken arg1, ParserToken arg2) {
        String type = getTypeForSimpleOp(arg1, arg2);
        switch(type) {
            case Name.INT:
                return new ParserToken(type, (Integer.parseInt(arg1.getValue()) + Integer.parseInt(arg2.getValue())) + "");
            case Name.DOUBLE:
                return new ParserToken(type, (Double.parseDouble(arg1.getValue()) + Double.parseDouble(arg2.getValue())) + "");
        }
        return null;
    }

    public ParserToken dif(ParserToken arg1, ParserToken arg2) {
        String type = getTypeForSimpleOp(arg1, arg2);
        switch(type) {
            case Name.INT:
                return new ParserToken(type,(Integer.parseInt(arg1.getValue()) - Integer.parseInt(arg2.getValue())) + "");
            case Name.DOUBLE:
                return new ParserToken(type, (Double.parseDouble(arg1.getValue()) - Double.parseDouble(arg2.getValue())) + "");
        }
        return null;
    }

    public ParserToken mult(ParserToken arg1, ParserToken arg2) {
        String type = getTypeForSimpleOp(arg1, arg2);
        switch(type) {
            case Name.INT:
                return new ParserToken(type,(Integer.parseInt(arg1.getValue()) * Integer.parseInt(arg2.getValue())) + "");
            case Name.DOUBLE:
                return new ParserToken(type, (Double.parseDouble(arg1.getValue()) * Double.parseDouble(arg2.getValue())) + "");
        }
        return null;
    }

    public ParserToken div(ParserToken arg1, ParserToken arg2) {
        String type = getTypeForSimpleOp(arg1, arg2);
        switch(type) {
            case Name.INT:
                return new ParserToken(type,(Integer.parseInt(arg1.getValue()) / Integer.parseInt(arg2.getValue())) + "");
            case Name.DOUBLE:
                return new ParserToken(type, (Double.parseDouble(arg1.getValue()) / Double.parseDouble(arg2.getValue())) + "");
        }
        return null;
    }

    private String getTypeForSimpleOp(ParserToken arg1, ParserToken arg2) {
        if (!arg1.getType().equals(arg2.getType()))
            logger.warning("Types mismatch(" + arg1.getType() + "-" + arg2.getType() +
                    "): consider reviewing your code");
        return arg1.getType();
    }

    public ParserToken disj(ParserToken arg2, ParserToken arg1) {
        return new ParserToken(Name.INT, boolToInt(intToBool(Integer.parseInt(arg1.getValue())) ||
                intToBool(Integer.parseInt(arg2.getValue()))).toString());
    }

    public ParserToken conj(ParserToken arg2, ParserToken arg1) {
        return new ParserToken(Name.INT, boolToInt(intToBool(Integer.parseInt(arg1.getValue())) &&
                intToBool(Integer.parseInt(arg2.getValue()))).toString());
    }

    public ParserToken isNotEq(ParserToken arg2, ParserToken arg1)
            throws InterpreterException {
        return new ParserToken(Name.INT, boolToInt(compareToken(arg1, arg2) != 0).toString());
    }

    public ParserToken isEq(ParserToken arg2, ParserToken arg1)
            throws InterpreterException {
        return new ParserToken(Name.INT, boolToInt(compareToken(arg1, arg2) == 0).toString());
    }

    public ParserToken isLessOrEq(ParserToken arg2, ParserToken arg1)
            throws InterpreterException {
        return new ParserToken(Name.INT, boolToInt(compareToken(arg1, arg2) <= 0).toString());
    }

    public ParserToken isBiggerOrEq(ParserToken arg2, ParserToken arg1)
            throws InterpreterException {
        return new ParserToken(Name.INT, boolToInt(compareToken(arg1, arg2) >= 0).toString());
    }

    public ParserToken isBigger(ParserToken arg2, ParserToken arg1)
            throws InterpreterException {
        return new ParserToken(Name.INT, boolToInt(compareToken(arg1, arg2) > 0).toString());
    }

    public ParserToken isLess(ParserToken arg2, ParserToken arg1)
            throws InterpreterException {
        return new ParserToken(Name.INT, boolToInt(compareToken(arg1, arg2) < 0).toString());
    }

    public Integer boolToInt(Boolean b) {
        return b? 0:1;
    }

    public Boolean intToBool(Integer b) {
        return b == 0;
    }

    private int compareToken(ParserToken arg1, ParserToken arg2) throws InterpreterException {
        if (!arg1.getType().equals(arg2.getType()))
            logger.warning("Types mismatch(" + arg1.getType() + "+" + arg2.getType() +
                    "): consider reviewing your code");
        switch (arg1.getType()) {
            case Name.INT:
                return Integer.parseInt(arg1.getValue()) - Integer.parseInt(arg2.getValue());
            case Name.DOUBLE:
                double res = Double.parseDouble(arg1.getValue()) - Double.parseDouble(arg2.getValue());
                if (res > 0) return 1;
                else if (res == 0) return 0;
                else return -1;
            case Name.STRING:
                return arg1.getValue().compareTo(arg2.getValue());
            default:
                logger.severe("Can not compare type " + arg1.getType());
                throw new InterpreterException("Comparison of type " + arg1.getType() +
                        " is not supported");
        }
    }

    public ParserToken contains(ParserToken el, ParserToken var) throws InterpreterException {
        Record record = symbolTable.lookup(var.getValue());

        if (!el.getType().equals(Name.INT)) {
            throw new InterpreterException("Type mismatch: " + el.getType() + ", required: INT");
        }

        switch (record.getType()) {
            case Name.LIST:
                @SuppressWarnings("unchecked") CustomList<Integer> list = (CustomList<Integer>) record.getValue();
                return new ParserToken(Name.INT, boolToInt(list.contains(Integer.parseInt(el.getValue()))).toString());
            case Name.SET:
                @SuppressWarnings("unchecked") CustomSet<Integer> set = (CustomSet<Integer>) record.getValue();
                return new ParserToken(Name.INT, boolToInt(set.contains(Integer.parseInt(el.getValue()))).toString());
            default: throw new InterpreterException("Trying yo put to type " + record.getType());
        }
    }

    public ParserToken get(ParserToken index, ParserToken var) throws InterpreterException {
        Record record = symbolTable.lookup(var.getValue());
        Integer val;
        if (record == null) throw new InterpreterException("Variable " + var.getValue() + " is not defined" +
                " in this scope");

        switch (record.getType()) {
            case Name.LIST:
                @SuppressWarnings("unchecked") CustomList<Integer> list = (CustomList<Integer>) record.getValue();
                val = list.get(Integer.parseInt(index.getValue()));
                logger.fine("Got " + var.getValue() + "[" + index + "]=" + val);
                break;
            default:
                throw new InterpreterException("Calling get on " + record.getType() + " variable.");
        }
        if (val != null) return new ParserToken(Name.INT, val.toString());
        return null;
    }

}
