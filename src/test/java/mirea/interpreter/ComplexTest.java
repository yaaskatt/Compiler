package mirea.interpreter;

import mirea.lexer.Lexer;
import mirea.lexer.LexerToken;
import mirea.parser.ParserToken;
import mirea.parser.Parser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ComplexTest {
    private String testFolder = "test_files/";

    /*@Test
    public void compTest() throws Exception {
            Lexer lexer = new Lexer(testFolder + "compTest.txt");
            List<LexerToken> tokenList = lexer.getAllTokens();
            assertFalse(tokenList.isEmpty());

            Parser parser = new Parser(tokenList);
            List<ParserToken> out = (parser.lang());

            Interpreter interpreter = new Interpreter();
            interpreter.count(out);
    }

    @Test
    public void compTest1() throws Exception {
        Lexer lexer = new Lexer(testFolder + "langTest");
        List<LexerToken> tokenList = lexer.getAllTokens();
        assertFalse(tokenList.isEmpty());

        Parser parser = new Parser(tokenList);
        List<ParserToken> out = (parser.lang());

        Interpreter interpreter = new Interpreter();
        interpreter.count(out);
    }*/

    @Test
    public void compTest2() throws Exception {
        Lexer lexer = new Lexer(testFolder + "compTest.txt");
        List<LexerToken> lexerTokenList = lexer.getAllTokens();
        assertFalse(lexerTokenList.isEmpty());
        Parser parser = new Parser(lexerTokenList);
        List<ParserToken> out = (parser.lang());
        Interpreter interpreter = new Interpreter(testFolder + "ComplexTestResult.txt");
        interpreter.count(out);
    }
}
