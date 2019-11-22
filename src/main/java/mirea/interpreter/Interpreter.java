package mirea.interpreter;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Logger;

import mirea.parser.ParserToken;
import mirea.parser.ParserTokenType;
import mirea.structures.CustomList;
import mirea.structures.CustomSet;
import mirea.table.Record;
import mirea.table.SymbolTable;

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
                 case OP:           processOp(token.getValue()); break;
                 case ENTER_SCOPE:  symbolTable.enterScope(); break;
                 case EXIT_SCOPE:   symbolTable.exitScope(); break;
                 case INT:
                 case DOUBLE:
                 case STRING:
                 case ADR:     s.push(token); break;
                 case VAR:     s.push(getSymData(token)); break;
                 case DEF:     insertSym(s.pop().getValue(), token.getValue()); break;
                 case TRANS:
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

            case "add":         calculator.add(s.pop(), s.pop()); break;
            case "get":         s.push(calculator.get(s.pop(), s.pop())); break;
            case "contains":    s.push(calculator.contains(s.pop(), s.pop())); break;
            
            case "print":   System.out.println("" + s.pop().getValue()); break;

            default:        logger.severe("Operator " + value + " not supported");
        }
    }

    private void assignVal(ParserToken match, ParserToken var)
            throws Exception {
        if (var.getType() != ParserTokenType.ADR){
            throw new Exception("Assigning var  \"" + var.getValue() +
                    "\" must be address.");
        }
        Record destRec = symbolTable.lookup(var.getValue());
        if (destRec == null) {
            throw new Exception("Variable " + var.getValue()
                    + " is not defined in this scope.");
        }
        if (destRec.getType() != match.getType()){
            logger.severe("Type mismatch [" + destRec.getType() + ", " + match.getType() + "]");
            throw new Exception("Assigning type " + match.getType() + " to "
                    + destRec.getType());
        }
        destRec.setValue(match.getValue());
    }


    private boolean isTrue(ParserToken pop) throws Exception {
        if (pop.getType() != ParserTokenType.INT){
            throw new Exception("Condition type " + pop.getType() + "not supported");
        }
        return Integer.parseInt(pop.getValue()) == 0;
    }


    private void insertSym(String name, String type) throws Exception {
        if (symbolTable.localLookup(name) != null) {
            throw new Exception("Variable " + name + " is already defined in this scope.");
        }
        Object value = null;
        logger.fine("Symbol table insert symbol " + name + " type " + type);
        if (ParserTokenType.valueOf(type.toUpperCase()) == ParserTokenType.LIST) value = new CustomList<Integer>(); // int list
        else if (ParserTokenType.valueOf(type.toUpperCase()) == ParserTokenType.SET) value = new CustomSet<Integer>(); // int list
        symbolTable.insertSymbol(new Record(name, value, ParserTokenType.valueOf(type.toUpperCase())));
    }


    private ParserToken getSymData(ParserToken token) throws Exception {

        Record rec = symbolTable.lookup(token.getValue());
        if (rec == null) {
            throw new Exception("Variable " + token.getValue() +
                    " not defined in this scope.");
        }
        logger.fine("Got value for " + token.getType() + " " + token.getValue() +
                ": " + rec.getType() + " " + rec.getValue());
        return new ParserToken(rec.getType(), rec.getValue().toString());
    }
}