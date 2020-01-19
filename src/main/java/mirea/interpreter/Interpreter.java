package mirea.interpreter;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import mirea.parser.ParserToken;
import mirea.parser.ParserTokenType;
import static mirea.parser.ParserTokenType.*;

import mirea.structures.CustomList;
import mirea.structures.CustomSet;
import mirea.table.FuncHolder;
import mirea.table.Record;
import mirea.table.SymbolTable;

class Interpreter {

    private LinkedList<ParserToken> stack = new LinkedList<>();
    private SymbolTable symbolTable = new SymbolTable();
    private HashMap<String, FuncHolder> functions = new HashMap<>();
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
                 case ARG:
                 case ADR:     stack.push(token); break;
                 case VAR:     stack.push(getSymData(token)); break;
                 case DEF:     insertSym(stack.pop().getValue(), token.getValue()); break;
                 case TRANS:
                     /* Безусловный переход */
                     if (token.getValue().equals("!")) i = Integer.parseInt(stack.pop().getValue()) - 1;
                     /* Переход по лжи */
                     else {
                         int index = Integer.parseInt(stack.pop().getValue()) - 1;
                         if (!isTrue(stack.pop())) i = index;
                     }
                     break;
                 case RETURN: return Integer.parseInt(stack.pop().getValue());
                 case FUNC: i = makeFunc(i, parserTokenList); break;
                 case EXEC: execFunc(stack.pop().getValue()); break;
                 default: throw new Exception("Unsupported type: " + token.getType());
             }
         }
         return 0;
     }

    private void execFunc(String name) throws Exception {
        FuncHolder funcHolder = functions.get(name);
        if (funcHolder == null) throw new Exception("no function definition '" + name + "' found");
        for (Record arg : funcHolder.getArgs()) {
            arg.setValue(stack.pop().getValue()); //TODO: add type checks
        }
        Interpreter funcInterpreter = new Interpreter();
        funcInterpreter.addToSymbolTable(funcHolder.getArgs());
        int result = funcInterpreter.count(funcHolder.getBody());
        if (funcHolder.getReturnValue() != null) stack.push(new ParserToken(INT, "" + result));
    }

    // return position after body in input RPN
    private int makeFunc(int pos, List<ParserToken> parserTokenList) throws Exception {
        int argc = Integer.parseInt(stack.pop().getValue());
        ArrayList<Record> args = new ArrayList<>();
        for (int i = 0; i < argc; i++) {
            String type = stack.pop().getValue();
            String name = stack.pop().getValue();
            args.add(new Record(name, null, stringToType(type)));
        }
        String name = stack.pop().getValue();
        if (!parserTokenList.get(++pos).getType().equals(ENTER_SCOPE)) {
            throw new Exception("Could not find body of function " + name);
        }
        List<ParserToken> body = new ArrayList<>();
        ParserToken returnVal = null;
        for (ParserToken current; !((current = parserTokenList.get(++pos)).getType().equals(EXIT_SCOPE));) {
            if (current.getType().equals(RETURN)) returnVal = current;
            body.add(current);
        }
        functions.put(name, new FuncHolder(name, args, returnVal, body));
        symbolTable.insertSymbol(new Record("func", name, FUNC));
        return pos;
    }

    private ParserTokenType stringToType(String type) {
        return ParserTokenType.valueOf(type.toUpperCase());
    }

    private void processOp(String value) throws Exception {
        switch (value){
            case "+":       stack.push(calculator.sum(stack.pop(), stack.pop())); break;
            case "-":       stack.push(calculator.dif(stack.pop(), stack.pop())); break;
            case "*":       stack.push(calculator.mult(stack.pop(), stack.pop())); break;
            case "/":       stack.push(calculator.div(stack.pop(), stack.pop())); break;

            case "<":       stack.push(calculator.isLess(stack.pop(), stack.pop())); break;
            case ">":       stack.push(calculator.isBigger(stack.pop(), stack.pop())); break;
            case ">=":      stack.push(calculator.isBiggerOrEq(stack.pop(), stack.pop())); break;
            case "<=":      stack.push(calculator.isLessOrEq(stack.pop(), stack.pop())); break;
            case "==":      stack.push(calculator.isEq(stack.pop(), stack.pop())); break;
            case "!=":      stack.push(calculator.isNotEq(stack.pop(), stack.pop())); break;

            case "&&":      stack.push(calculator.conj(stack.pop(), stack.pop())); break;
            case "||":      stack.push(calculator.disj(stack.pop(), stack.pop())); break;

            case "=":       assignVal(stack.pop(), stack.pop()); break;

            case "add":         calculator.add(stack.pop(), stack.pop()); break;
            case "get":         stack.push(calculator.get(stack.pop(), stack.pop())); break;
            case "contains":    stack.push(calculator.contains(stack.pop(), stack.pop())); break;
            
            case "print":   System.out.println("" + stack.pop().getValue()); break;

            default:        logger.severe("Operator " + value + " not supported");
        }
    }

    private void assignVal(ParserToken match, ParserToken var)
            throws Exception {
        if (var.getType() != ADR){
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
        if (pop.getType() != INT){
            throw new Exception("Condition type " + pop.getType() + "not supported");
        }
        return Integer.parseInt(pop.getValue()) == 0;
    }


    private void insertSym(String name, String type) throws Exception {
        if (symbolTable.localLookup(name) != null) {
            throw new Exception("Variable " + name + " is already defined in this scope.");
        }
        Object value = null;
        logger.fine("Symbol table insert symbol: '" + name + "', type: '" + type + "'");
        if (ParserTokenType.valueOf(type.toUpperCase()) == LIST) value = new CustomList<Integer>(); // int list
        else if (ParserTokenType.valueOf(type.toUpperCase()) == SET) value = new CustomSet<Integer>(); // int list
        symbolTable.insertSymbol(new Record(name, value, ParserTokenType.valueOf(type.toUpperCase())));
    }

    public void addToSymbolTable (ArrayList<Record> records){
        logger.log(Level.INFO, "adding to new interpreter symbol table " + records);
        this.symbolTable.insertAll(records);
    }

    private ParserToken getSymData(ParserToken token) throws Exception {

        Record rec = symbolTable.lookup(token.getValue());
        if (rec == null) {
            throw new Exception("Variable '" + token.getValue() +
                    "' not defined in this scope.");
        }
        logger.fine("Got value for " + token.getType() + " " + token.getValue() +
                ": " + rec.getType() + " " + rec.getValue());
        return new ParserToken(rec.getType(), rec.getValue().toString());
    }
}