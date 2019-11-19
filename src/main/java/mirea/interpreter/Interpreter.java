package mirea.interpreter;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Logger;

import mirea.structures.CustomList;
import mirea.structures.CustomSet;
import mirea.table.Record;
import mirea.table.SymbolTable;

class Interpreter {

    private LinkedList<ElementInterface> stack = new LinkedList<>();
    private SymbolTable symbolTable = new SymbolTable();
    private Logger logger = Logger.getLogger(Interpreter.class.getName());

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

    /* Перенаправляет стандартный поток вывода в filename */
    Interpreter(String filename){
        try {
            System.setOut(new PrintStream(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    Interpreter(){
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

    public int count(List<? extends ElementInterface> elements) throws Exception {
        for (int i = 0; i < elements.size(); i++) {
             ElementInterface element = elements.get(i);
             /*logger.info("On element " + i + ", type: " + element.getType() +
                     ", value: " + element.getValue());*/
             switch (element.getType()) {
                 /* Обработка операторов(вычисления) */
                 case OP_TYPE:      processOp(element.getValue()); break;
                 /* Области видимости */
                 case LB_TYPE: symbolTable.enterScope(); break;
                 case RB_TYPE:  symbolTable.exitScope(); break;
                 /* Обработка операндов(положить в стек) */
                 case INT_TYPE:     stack.push(element); break;
                 case DOUBLE_TYPE:  stack.push(element); break;
                 case STRING_TYPE:  stack.push(element); break;
                 case ADR_TYPE:     stack.push(element); break;
                 case VAR_TYPE:     stack.push(getSymData(element)); break;
                 /* Объявления переменных */
                 case DEF_TYPE:     insertSym(stack.pop().getValue(), element.getValue()); break;
                 /* Переходы */
                 case TR_TYPE:
                     /* Безусловный переход */
                     if (element.getValue().equals("!")) i = intVal(stack.pop().getValue()) - 1;
                     /* Переход по лжи */
                     else {
                         int index = intVal(stack.pop().getValue()) - 1;
                         if (!isTrue(stack.pop())) i = index;
                     }
                     break;
                 default: logger.severe("Unsupported type: " + element.getType());
             }
             //logger.info("Stack: " + strVal(stack));
         }
         return 0;
     }

    private void processOp(String value) throws Exception {
        switch (value){
            case "+":       stack.push( sum(stack.pop(), stack.pop())); break;
            case "-":       stack.push( dif(stack.pop(), stack.pop())); break;
            case "*":       stack.push( mul(stack.pop(), stack.pop())); break;
            case "/":       stack.push( div(stack.pop(), stack.pop())); break;

            case "<":       stack.push(isLess(stack.pop(), stack.pop())); break;
            case ">":       stack.push(isBigger(stack.pop(), stack.pop())); break;
            case ">=":      stack.push(isBiggerOrEq(stack.pop(), stack.pop())); break;
            case "<=":      stack.push(isLessOrEq(stack.pop(), stack.pop())); break;
            case "==":      stack.push(isEq(stack.pop(), stack.pop())); break;
            case "!=":      stack.push(isNotEq(stack.pop(), stack.pop())); break;

            case "&&":      stack.push(conj(stack.pop(), stack.pop())); break;
            case "||":      stack.push(disj(stack.pop(), stack.pop())); break;

            case "=":       assignVal( stack.pop(), stack.pop()); break;

            case "add":     addEl( stack.pop(), stack.pop()); break;
            case "get":     getEl( stack.pop(), stack.pop()); break;
            case "contains": containsEl( stack.pop(), stack.pop()); break;

            case "print":   System.out.println("" + stack.pop().getValue()); break;
            case "println": System.out.println("" + stack.pop().getValue()); break;
            default:        logger.severe("Operator " + value + " not supported");
        }
    }

    private ElementInterface disj(ElementInterface arg2, ElementInterface arg1) {
        return mkElement(INT_TYPE, bToI(
                iToB(intVal(arg1.getValue())) || iToB(intVal(arg2.getValue()))).toString());
    }

    private ElementInterface conj(ElementInterface arg2, ElementInterface arg1) {
        return mkElement(INT_TYPE, bToI(
                iToB(intVal(arg1.getValue())) && iToB(intVal(arg2.getValue()))).toString());
    }

    private ElementInterface isNotEq(ElementInterface arg2, ElementInterface arg1)
            throws InterpreterException {
        return mkElement(INT_TYPE, bToI(compareEl(arg1, arg2) != 0).toString());
    }

    private ElementInterface isEq(ElementInterface arg2, ElementInterface arg1)
            throws InterpreterException {
        return mkElement(INT_TYPE, bToI(compareEl(arg1, arg2) == 0).toString());
    }

    private ElementInterface isLessOrEq(ElementInterface arg2, ElementInterface arg1)
            throws InterpreterException {
        return mkElement(INT_TYPE, bToI(compareEl(arg1, arg2) <= 0).toString());
    }

    private ElementInterface isBiggerOrEq(ElementInterface arg2, ElementInterface arg1)
            throws InterpreterException {
        return mkElement(INT_TYPE, bToI(compareEl(arg1, arg2) >= 0).toString());
    }

    private ElementInterface isBigger(ElementInterface arg2, ElementInterface arg1)
            throws InterpreterException {
        return mkElement(INT_TYPE, bToI(compareEl(arg1, arg2) > 0).toString());
    }

    private ElementInterface isLess(ElementInterface arg2, ElementInterface arg1)
            throws InterpreterException {
        return mkElement(INT_TYPE, bToI(compareEl(arg1, arg2) < 0).toString());
    }

    private Integer bToI(Boolean b) {
        return b? 0:1;
    }

    private Boolean iToB(Integer b) {
        return b == 0;
    }

    private int compareEl(ElementInterface arg1, ElementInterface arg2) throws InterpreterException {
        if (!arg1.getType().equals(arg2.getType()))
            logger.warning("Types mismatch(" + arg1.getType() + "+" + arg2.getType() +
                    "): consider reviewing your code");
        switch (arg1.getType()) {
            case INT_TYPE:
                return intVal(arg1.getValue()) - intVal(arg2.getValue());
            case DOUBLE_TYPE:
                double res = doubleVal(arg1.getValue()) - doubleVal(arg2.getValue());
                if (res > 0) return 1;
                else if (res == 0) return 0;
                else return -1;
            case STRING_TYPE:
                return arg1.getValue().compareTo(arg2.getValue());
            default:
                logger.severe("Can not compare type " + arg1.getType());
                throw new InterpreterException("Comparison of type " + arg1.getType() +
                        " is not supported");
        }
    }

    private ElementInterface sum(ElementInterface arg2, ElementInterface arg1)
            throws InterpreterException{
        if (!arg1.getType().equals(arg2.getType()))
            logger.warning("Types mismatch(" + arg1.getType() + "+" + arg2.getType() +
                    "): consider reviewing your code");
        switch (arg1.getType()) {
            case INT_TYPE:
                return mkElement(arg1.getType(), String.valueOf(
                        intVal(arg1.getValue()) + intVal(arg2.getValue())));
            case DOUBLE_TYPE:
                return mkElement(arg1.getType(), String.valueOf(
                        doubleVal(arg1.getValue()) + doubleVal(arg2.getValue())));
            default:
                logger.severe("Can not sum type " + arg1.getType());
                throw new InterpreterException("SUM OF TYPE " + arg1.getType() + " IS NOT SUPPORTED");
        }
    }

    /**
     * Counts multiplication of arg1 and arg2. Result type is defined by arg1 type.
     * @param arg2 multiplier 2
     * @param arg1 multiplier 1
     * @throws InterpreterException when arg1 type is not double or int
     */
    private ElementInterface mul(ElementInterface arg2, ElementInterface arg1)
            throws InterpreterException{
        if (!arg1.getType().equals(arg2.getType()))
            logger.warning("Types mismatch(" + arg1.getType() + "*" + arg2.getType() +
                    "): consider reviewing your code");
        switch (arg1.getType()) {
            case INT_TYPE:
                return mkElement(arg1.getType(), String.valueOf(
                        intVal(arg1.getValue()) * intVal(arg2.getValue())));
            case DOUBLE_TYPE:
                return mkElement(arg1.getType(), String.valueOf(
                        doubleVal(arg1.getValue()) * doubleVal(arg2.getValue())));
            default:
                logger.severe("Can not multiply type " + arg1.getType());
                throw new InterpreterException("MULTIPLY OF TYPE " + arg1.getType() +
                        " IS NOT SUPPORTED");
        }
    }

    private ElementInterface dif(ElementInterface arg2, ElementInterface arg1)
            throws InterpreterException{
        if (!arg1.getType().equals(arg2.getType()))
            logger.warning("Types mismatch(" + arg1.getType() + "-" + arg2.getType() +
                    "): consider reviewing your code");
        switch (arg1.getType()) {
            case INT_TYPE:
                return mkElement(arg1.getType(), String.valueOf(
                        intVal(arg1.getValue()) - intVal(arg2.getValue())));
            case DOUBLE_TYPE:
                return mkElement(arg1.getType(), String.valueOf(
                        doubleVal(arg1.getValue()) - doubleVal(arg2.getValue())));
            default:
                logger.severe("Can not subtract type " + arg1.getType());
                throw new InterpreterException("DIFFERENCE OF TYPE " + arg1.getType() +
                        " IS NOT SUPPORTED");
        }
    }

    private ElementInterface div(ElementInterface arg2, ElementInterface arg1)
            throws InterpreterException {
        if (!arg1.getType().equals(arg2.getType()))
            logger.warning("Types mismatch(" + arg1.getType() + "/" + arg2.getType() +
                    "): consider reviewing your code");
        switch (arg1.getType()) {
            case INT_TYPE:
                return mkElement(arg1.getType(), String.valueOf(
                        intVal(arg1.getValue()) / intVal(arg2.getValue())));
            case DOUBLE_TYPE:
                return mkElement(arg1.getType(), String.valueOf(
                        doubleVal(arg1.getValue()) / doubleVal(arg2.getValue())));
            default:
                logger.severe("Can not divide type " + arg1.getType());
                throw new InterpreterException("DIVISION OF TYPE " + arg1.getType() +
                        " IS NOT SUPPORTED");
        }
    }

    private void assignVal(ElementInterface element, ElementInterface destination)
            throws InterpreterException {
        if (!destination.getType().equals(ADR_TYPE)){
            throw new InterpreterException("Assigning destination  \"" + destination.getValue() +
                    "\" must be address.");
        }
        Record destRec = symbolTable.lookup(destination.getValue());
        if (destRec == null) {
            throw new InterpreterException("Variable " + destination.getValue()
                    + " is not defined in this scope.");
        }
        if (!destRec.getType().toUpperCase().equals(element.getType())){
            logger.severe("Type mismatch [" + destRec.getType() + ", " + element.getType() + "]");
            throw new InterpreterException("Assigning type " + element.getType() + " to "
                    + destRec.getType());
        }
        destRec.setValue(element.getValue());
    }

    private void addEl(ElementInterface element, ElementInterface destination)
            throws InterpreterException {
        Record record = symbolTable.lookup(destination.getValue());

        if (!element.getType().equals(INT_TYPE)) {
            throw new InterpreterException("Type mismatch: " + element.getType() + ", required: INT");
        }

        switch (record.getType()) {
            case LIST_TYPE:
                @SuppressWarnings("unchecked") CustomList<Integer> list = (CustomList<Integer>) record.getValue();
                list.add(intVal(element.getValue()));
                break;
            case SET_TYPE:
                @SuppressWarnings("unchecked") CustomSet<Integer> set = (CustomSet<Integer>) record.getValue();
                set.add(intVal(element.getValue()));
                break;
            default: throw new InterpreterException("Trying yo put to type " + record.getType());
        }
        logger.fine("Put to " + destination.getValue() + " " + element);
    }

    private void containsEl(ElementInterface element, ElementInterface destination)
            throws InterpreterException {
        Record record = symbolTable.lookup(destination.getValue());

        if (!element.getType().equals(INT_TYPE)) {
            throw new InterpreterException("Type mismatch: " + element.getType() + ", required: INT");
        }

        switch (record.getType()) {
            case LIST_TYPE:
                @SuppressWarnings("unchecked") CustomList<Integer> list = (CustomList<Integer>) record.getValue();
                stack.push(mkElement(INT_TYPE, bToI(list.contains(intVal(element.getValue()))).toString()));
                break;
            case SET_TYPE:
                @SuppressWarnings("unchecked") CustomSet<Integer> set = (CustomSet<Integer>) record.getValue();
                stack.push(mkElement(INT_TYPE, bToI(set.contains(intVal(element.getValue()))).toString()));
                break;
            default: throw new InterpreterException("Trying yo put to type " + record.getType());
        }
    }

    private void getEl(ElementInterface index, ElementInterface inp) throws InterpreterException {
        Record record = symbolTable.lookup(inp.getValue());
        Integer val;
        if (record == null) throw new InterpreterException("Variable " + inp.getValue() + " is not defined" +
                " in this scope");
        switch (record.getType()) {
            case LIST_TYPE:
                @SuppressWarnings("unchecked") CustomList<Integer> list = (CustomList<Integer>) record.getValue();
                val = list.get(intVal(index.getValue()));
                logger.fine("Got " + inp.getValue() + "[" + index + "]=" + val);
                break;
            default:
                throw new InterpreterException("Calling get on " + record.getType() + " variable.");
        }
        if (val != null) stack.push(mkElement(INT_TYPE, val.toString()));
    }

    private boolean isTrue(ElementInterface pop) throws InterpreterException {
        if (!pop.getType().equals(INT_TYPE)){
            throw new InterpreterException("Condition type " + pop.getType() + "not supported");
        }
        return intVal(pop.getValue()) == 0;
    }

    private int intVal(String s){
        return Integer.parseInt(s);
    }

    private double doubleVal(String s){
        return Double.parseDouble(s);
    }

    private void insertSym(String name, String type) throws InterpreterException {
        if (symbolTable.localLookup(name) != null) {
            throw new InterpreterException("Variable " + name + " is already defined in this scope.");
        }
        Object value = null;
        logger.fine("Symbol table insert symbol " + name + " type " + type);
        if (type.equals(LIST_TYPE)) value = new CustomList<Integer>(); // int list
        else if (type.equals(SET_TYPE)) value = new CustomSet<Integer>(); // int list
        symbolTable.insertSymbol(new Record(name, value, type));
    }


    private ElementInterface getSymData(ElementInterface element) throws InterpreterException {
        Record rec = symbolTable.lookup(element.getValue());
        if (rec == null) {
            throw new InterpreterException("Variable " + element.getValue() +
                    " not defined in this scope.");
        }
        logger.fine("Got value for " + element.getType() + " " + element.getValue() +
                ": " + rec.getType() + " " + rec.getValue());
        return mkElement(rec.getType(), rec.getValue().toString());
    }

    private ElementInterface mkElement(String type, String value){
         return new ElementInterface() {
             @Override
             public String getType() {
                 return type.toUpperCase();
             }

             @Override
             public String getValue() {
                 return value;
             }
         };
    }

    private String strVal(LinkedList<ElementInterface> stack) {
        StringBuilder stringBuilder = new StringBuilder("[ ");
        for (ElementInterface inp: stack) {
            stringBuilder.append(inp.getValue()).append(" ");
        }
        return stringBuilder.append("]").toString();
    }
}