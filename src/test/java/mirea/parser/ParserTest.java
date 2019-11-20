package mirea.parser;

import mirea.lexer.Lexer;
import mirea.lexer.LexerToken;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ParserTest {
    private String testFolder = "test_files/";

    @Test
    public void expr() {
    }

    @Test
    public void lang() {
        Lexer lexer = new Lexer(testFolder + "ConvTest");
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
    }
}