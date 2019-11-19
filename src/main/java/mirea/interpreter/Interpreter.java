package mirea.interpreter;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Logger;

import mirea.parser.ParserToken;
import mirea.structures.CustomList;
import mirea.structures.CustomSet;
import mirea.table.Record;
import mirea.table.SymbolTable;
import mirea.token.Name;

class Interpreter {

    private LinkedList<ParserToken> s = new LinkedList<>();
    private SymbolTable symbolTable = new SymbolTable();
    private Logger logger = Logger.getLogger(Interpreter.class.getName());
    private Calculator calculator = new Calculator(symbolTable, logger);

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

    public int count(List<ParserToken> parserTokenList) throws Exception {
        for (int i = 0; i < parserTokenList.size(); i++) {
            ParserToken token = parserTokenList.get(i);
             switch (token.getType()) {
                 /* Обработка операторов(вычисления) */
                 case Name.OP:      processOp(token.getValue()); break;
                 /* Области видимости */
                 case Name.ENTER_SCOPE:     symbolTable.enterScope(); break;
                 case Name.EXIT_SCOPE:      symbolTable.exitScope(); break;
                 /* Обработка операндов(положить в стек) */
                 case Name.INT:
                 case Name.DOUBLE:
                 case Name.STRING:
                 case Name.ADR:     s.push(token); break;
                 case Name.VAR:     s.push(getSymData(token)); break;
                 /* Объявления переменных */
                 case Name.DEF:     insertSym(s.pop().getValue(), token.getValue()); break;
                 /* Переходы */
                 case Name.TRANS:
                     /* Безусловный переход */
                     if (token.getValue().equals("!")) i = Integer.parseInt(s.pop().getValue()) - 1;
                     /* Переход по лжи */
                     else {
                         int index = Integer.parseInt(s.pop().getValue()) - 1;
                         if (!isTrue(s.pop())) i = index;
                     }
                     break;
                 default: logger.severe("Unsupported type: " + token.getType());
             }
         }
         return 0;
     }

    private void processOp(String value) throws Exception {
        switch (value){
            case "+":       s.push(calculator.sum(s.pop(), s.pop())); break;
            case "-":       s.push(calculator.dif(s.pop(), s.pop())); break;
            case "*":       s.push(calculator.mult(s.pop(), s.pop())); break;
            case "/":       s.push(calculator.div(s.pop(), s.pop())); break;

            case "<":       s.push(calculator.isLess(s.pop(), s.pop())); break;
            case ">":       s.push(calculator.isBigger(s.pop(), s.pop())); break;
            case ">=":      s.push(calculator.isBiggerOrEq(s.pop(), s.pop())); break;
            case "<=":      s.push(calculator.isLessOrEq(s.pop(), s.pop())); break;
            case "==":      s.push(calculator.isEq(s.pop(), s.pop())); break;
            case "!=":      s.push(calculator.isNotEq(s.pop(), s.pop())); break;

            case "&&":      s.push(calculator.conj(s.pop(), s.pop())); break;
            case "||":      s.push(calculator.disj(s.pop(), s.pop())); break;

            case "=":       assignVal(s.pop(), s.pop()); break;

            case "add":     addEl(s.pop(), s.pop()); break;
            case "get":     s.push(calculator.get(s.pop(), s.pop())); break;
            case "contains":
                s.push(calculator.contains(s.pop(), s.pop())); break;
            case "print":   System.out.println("" + s.pop().getValue()); break;
            case "println": System.out.println("" + s.pop().getValue()); break;
            default:        logger.severe("Operator " + value + " not supported");
        }
    }

    private void assignVal(ParserToken token, ParserToken destination)
            throws InterpreterException {
        if (!destination.getType().equals(Name.ADR)){
            throw new InterpreterException("Assigning destination  \"" + destination.getValue() +
                    "\" must be address.");
        }
        Record destRec = symbolTable.lookup(destination.getValue());
        if (destRec == null) {
            throw new InterpreterException("Variable " + destination.getValue()
                    + " is not defined in this scope.");
        }
        if (!destRec.getType().toUpperCase().equals(token.getType())){
            logger.severe("Type mismatch [" + destRec.getType() + ", " + token.getType() + "]");
            throw new InterpreterException("Assigning type " + token.getType() + " to "
                    + destRec.getType());
        }
        destRec.setValue(token.getValue());
    }

    private void addEl(ParserToken token, ParserToken destination)
            throws InterpreterException {
        Record record = symbolTable.lookup(destination.getValue());

        if (!token.getType().equals(Name.INT)) {
            throw new InterpreterException("Type mismatch: " + token.getType() + ", required: INT");
        }

        switch (record.getType()) {
            case Name.LIST:
                @SuppressWarnings("unchecked") CustomList<Integer> list = (CustomList<Integer>) record.getValue();
                list.add(Integer.parseInt(token.getValue()));
                break;
            case Name.SET:
                @SuppressWarnings("unchecked") CustomSet<Integer> set = (CustomSet<Integer>) record.getValue();
                set.add(Integer.parseInt(token.getValue()));
                break;
            default: throw new InterpreterException("Trying yo put to type " + record.getType());
        }
        logger.fine("Put to " + destination.getValue() + " " + token);
    }

    private boolean isTrue(ParserToken pop) throws InterpreterException {
        if (!pop.getType().equals(Name.INT)){
            throw new InterpreterException("Condition type " + pop.getType() + "not supported");
        }
        return Integer.parseInt(pop.getValue()) == 0;
    }


    private void insertSym(String name, String type) throws InterpreterException {
        if (symbolTable.localLookup(name) != null) {
            throw new InterpreterException("Variable " + name + " is already defined in this scope.");
        }
        Object value = null;
        logger.fine("Symbol table insert symbol " + name + " type " + type);
        if (type.equals(Name.LIST)) value = new CustomList<Integer>(); // int list
        else if (type.equals(Name.SET)) value = new CustomSet<Integer>(); // int list
        symbolTable.insertSymbol(new Record(name, value, type));
    }


    private ParserToken getSymData(ParserToken token) throws InterpreterException {
        Record rec = symbolTable.lookup(token.getValue());
        if (rec == null) {
            throw new InterpreterException("Variable " + token.getValue() +
                    " not defined in this scope.");
        }
        logger.fine("Got value for " + token.getType() + " " + token.getValue() +
                ": " + rec.getType() + " " + rec.getValue());
        return new ParserToken(rec.getType(), rec.getValue().toString());
    }


    private String strVal(LinkedList<ParserToken> stack) {
        StringBuilder stringBuilder = new StringBuilder("[ ");
        for (ParserToken inp: stack) {
            stringBuilder.append(inp.getValue()).append(" ");
        }
        return stringBuilder.append("]").toString();
    }
}