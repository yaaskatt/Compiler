package mirea.interpreter;

import mirea.lexer.Lexer;
import mirea.lexer.LexerToken;
import mirea.parser.Parser;
import mirea.parser.ParserToken;
import mirea.parser.ParserTokenType;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;

public class InterpreterTest {

    private String testFolder = "test_files/";

    @Test
    public void funcTest() throws Exception {
        ArrayList<ParserToken> input = new ArrayList<>();
        input.add(new ParserToken(ParserTokenType.STRING, "foo"));  //
        input.add(new ParserToken(ParserTokenType.INT, "a"));       // argv...
        input.add(new ParserToken(ParserTokenType.INT, "b"));       //
        input.add(new ParserToken(ParserTokenType.INT, "2"));       // argc
        input.add(new ParserToken(ParserTokenType.FUNC, "func"));
        input.add(new ParserToken(ParserTokenType.ENTER_SCOPE, "{"));
        input.add(new ParserToken(ParserTokenType.VAR, "a"));
        input.add(new ParserToken(ParserTokenType.VAR, "b"));
        input.add(new ParserToken(ParserTokenType.OP, "+"));
        input.add(new ParserToken(ParserTokenType.RETURN, "return"));
        input.add(new ParserToken(ParserTokenType.EXIT_SCOPE, "}"));
        input.add(new ParserToken(ParserTokenType.INT, "2"));
        input.add(new ParserToken(ParserTokenType.INT, "4"));
        input.add(new ParserToken(ParserTokenType.STRING, "foo"));
        input.add(new ParserToken(ParserTokenType.EXEC, "exec"));
        input.add(new ParserToken(ParserTokenType.RETURN, "return"));

        Interpreter interpreter = new Interpreter();
        Assert.assertEquals(6, interpreter.count(input));
    }

    @Test
    public void returnTest() throws Exception {
        ArrayList<ParserToken> input = new ArrayList<>();
        input.add(new ParserToken(ParserTokenType.INT, "1"));
        input.add(new ParserToken(ParserTokenType.RETURN, "1"));

        Interpreter interpreter = new Interpreter();
        Assert.assertEquals(1, interpreter.count(input));
    }

    @Test
    public void noReturnTest() throws Exception {
        ArrayList<ParserToken> input = new ArrayList<>();
        input.add(new ParserToken(ParserTokenType.INT, "1"));

        Interpreter interpreter = new Interpreter();
        Assert.assertEquals(0, interpreter.count(input));
    }

    @Test
    public void funcWithParserTest() throws Exception {
        Lexer lexer = new Lexer(testFolder + "tempTest.txt");
        List<LexerToken> tokenList = lexer.getAllTokens();
        assertFalse(tokenList.isEmpty());
        for (LexerToken lexerToken : tokenList) {
            System.out.printf("tokenType: %s, lexema: %s\n", lexerToken.getType(), lexerToken.getValue());
        }
        Parser parser = new Parser(tokenList);
        List<ParserToken> out = (parser.lang());
        for (int i=0; i<out.size(); i++) {
            System.out.printf("%d: type: %s, value: %s\n", i, out.get(i).getType(), out.get(i).getValue());
        }
        Interpreter interpreter = new Interpreter();
        Assert.assertEquals(0, interpreter.count(out));
    }
}