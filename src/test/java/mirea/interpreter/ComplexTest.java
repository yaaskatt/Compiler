package mirea.interpreter;

import mirea.lexer.Lexer;
import mirea.lexer.Token;
import mirea.parser.Element;
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
            List<Token> tokenList = lexer.getAllTokens();
            assertFalse(tokenList.isEmpty());

            Parser parser = new Parser(tokenList);
            List<Element> out = (parser.lang());

            Interpreter interpreter = new Interpreter();
            interpreter.count(out);
    }

    @Test
    public void compTest1() throws Exception {
        Lexer lexer = new Lexer(testFolder + "langTest");
        List<Token> tokenList = lexer.getAllTokens();
        assertFalse(tokenList.isEmpty());

        Parser parser = new Parser(tokenList);
        List<Element> out = (parser.lang());

        Interpreter interpreter = new Interpreter();
        interpreter.count(out);
    }*/

    @Test
    public void compTest2() throws Exception {
        Lexer lexer = new Lexer(testFolder + "ComplexTest.txt");
        List<Token> tokenList = lexer.getAllTokens();
        assertFalse(tokenList.isEmpty());
        Parser parser = new Parser(tokenList);
        List<Element> out = (parser.lang());
        Interpreter interpreter = new Interpreter(testFolder + "ComplexTestResult.txt");
        interpreter.count(out);
    }
}
