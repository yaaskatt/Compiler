package mirea.interpreter;

import mirea.parser.ParserToken;
import static mirea.parser.ParserTokenType.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class InterpreterTest {

    private String testFolder = "test_files/";

    @Test
    public void funcTest() throws Exception {
        ArrayList<ParserToken> input = new ArrayList<>();
        input.add(new ParserToken(STRING, "foo"));  //
        input.add(new ParserToken(INT, "a"));       // argv...
        input.add(new ParserToken(INT, "b"));       //
        input.add(new ParserToken(INT, "2"));       // argc
        input.add(new ParserToken(FUNC, "func"));
        input.add(new ParserToken(ENTER_SCOPE, "{"));
        input.add(new ParserToken(VAR, "a"));
        input.add(new ParserToken(VAR, "b"));
        input.add(new ParserToken(OP, "+"));
        input.add(new ParserToken(RETURN, "return"));
        input.add(new ParserToken(EXIT_SCOPE, "}"));
        input.add(new ParserToken(STRING, "foo"));
        input.add(new ParserToken(INT, "2"));
        input.add(new ParserToken(INT, "4"));
        input.add(new ParserToken(INT, "2"));
        input.add(new ParserToken(EXEC, "exec"));
        input.add(new ParserToken(RETURN, "return"));
        Interpreter interpreter = new Interpreter();
        Assert.assertEquals(6, interpreter.count(input));
    }

    @Test
    public void returnTest() throws Exception {
        ArrayList<ParserToken> input = new ArrayList<>();
        input.add(new ParserToken(INT, "1"));
        input.add(new ParserToken(RETURN, "1"));

        Interpreter interpreter = new Interpreter();
        Assert.assertEquals(1, interpreter.count(input));
    }

    @Test
    public void noReturnTest() throws Exception {
        ArrayList<ParserToken> input = new ArrayList<>();
        input.add(new ParserToken(INT, "1"));

        Interpreter interpreter = new Interpreter();
        Assert.assertEquals(0, interpreter.count(input));
    }

    @Test
    public void globalVarTest() throws Exception {
        ArrayList<ParserToken> input = new ArrayList<>();
        input.add(new ParserToken(STRING, "foo"));  //
        input.add(new ParserToken(INT, "a"));       // argv...
        input.add(new ParserToken(INT, "b"));       //
        input.add(new ParserToken(INT, "2"));       // argc
        input.add(new ParserToken(FUNC, "func"));
        input.add(new ParserToken(ENTER_SCOPE, "{"));
        input.add(new ParserToken(VAR, "a"));
        input.add(new ParserToken(VAR, "b"));
        input.add(new ParserToken(VAR, "c"));
        input.add(new ParserToken(OP, "+"));
        input.add(new ParserToken(OP, "+"));
        input.add(new ParserToken(RETURN, "return"));
        input.add(new ParserToken(EXIT_SCOPE, "}"));
        input.add(new ParserToken(INT, "c"));
        input.add(new ParserToken(DEF, "int"));
        input.add(new ParserToken(ADR, "c"));
        input.add(new ParserToken(INT, "10"));
        input.add(new ParserToken(OP, "="));
        input.add(new ParserToken(STRING, "foo"));
        input.add(new ParserToken(INT, "2"));
        input.add(new ParserToken(INT, "4"));
        input.add(new ParserToken(INT, "2"));
        input.add(new ParserToken(EXEC, "exec"));
        input.add(new ParserToken(RETURN, "return"));


        Interpreter interpreter = new Interpreter();
        Assert.assertEquals(16, interpreter.count(input));
    }

    @Test
    public void threadTest() throws Exception {
        ArrayList<ParserToken> input = new ArrayList<>();
        input.add(new ParserToken(STRING, "foo"));  //
        input.add(new ParserToken(INT, "0"));       // argc
        input.add(new ParserToken(FUNC, "func"));
        input.add(new ParserToken(ENTER_SCOPE, "{"));
        input.add(new ParserToken(STRING, "Thread started"));
        input.add(new ParserToken(OP, "print"));
        input.add(new ParserToken(EXIT_SCOPE, "}"));
        input.add(new ParserToken(STRING, "foo"));
        input.add(new ParserToken(INT, "0"));
        input.add(new ParserToken(THREAD, "thread"));
        input.add(new ParserToken(STRING, "foo"));
        input.add(new ParserToken(INT, "0"));
        input.add(new ParserToken(THREAD, "thread"));

        Interpreter interpreter = new Interpreter();
        interpreter.count(input);

    }
}