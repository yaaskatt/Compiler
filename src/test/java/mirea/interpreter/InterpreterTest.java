package mirea.interpreter;

import mirea.parser.ParserToken;
import mirea.parser.ParserTokenType;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class InterpreterTest {

    @Test
    public void funcTest() throws Exception {
        ArrayList<ParserToken> input = new ArrayList<>();
        input.add(new ParserToken(ParserTokenType.STRING, "foo"));  //
        input.add(new ParserToken(ParserTokenType.INT, "a"));       // argv...
        input.add(new ParserToken(ParserTokenType.INT, "b"));       //
        input.add(new ParserToken(ParserTokenType.INT, "2"));       // argc
        input.add(new ParserToken(ParserTokenType.FUNC, "func"));
        input.add(new ParserToken(ParserTokenType.ENTER_SCOPE, "{"));
        input.add(new ParserToken(ParserTokenType.INT, "a"));
        input.add(new ParserToken(ParserTokenType.INT, "b"));
        input.add(new ParserToken(ParserTokenType.OP, "+"));
        input.add(new ParserToken(ParserTokenType.RETURN, "return"));
        input.add(new ParserToken(ParserTokenType.EXIT_SCOPE, "}"));
        input.add(new ParserToken(ParserTokenType.INT, "2"));
        input.add(new ParserToken(ParserTokenType.INT, "4"));
        input.add(new ParserToken(ParserTokenType.STRING, "a"));
        input.add(new ParserToken(ParserTokenType.OP, "print"));

        Interpreter interpreter = new Interpreter();
        Assert.assertEquals(6, interpreter.count(input));
    }
}