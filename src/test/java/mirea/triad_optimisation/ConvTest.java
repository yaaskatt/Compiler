package mirea.triad_optimisation;

import mirea.lexer.Lexer;
import mirea.lexer.Token;
import mirea.parser.Element;
import mirea.parser.Parser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ConvTest {
    private String testFolder = "test_files/";

    @Test
    public void convTest() {
        Lexer lexer = new Lexer(testFolder + "test4");
        List<Token> tokenList = lexer.getAllTokens();
        assertFalse(tokenList.isEmpty());
        for (Token aTokenList : tokenList) {
            System.out.printf("tokenType: %s, lexema: %s\n", aTokenList.getTokenType(), aTokenList.getValue());
        }
        Parser parser = new Parser(tokenList);
        List<Element> elements = (parser.lang());
        Conv conv = new Conv();
        List<Triad> triads = conv.reverseNot_toTriads(elements);

        for (int i=0; i<triads.size(); i++) {
            Triad tr = triads.get(i);
            System.out.printf("%d: %s %s (%s %s, %s %s)\n", i, tr.getOp().getType(), tr.getOp().getValue(),
                    tr. getEl1().getType(), tr.getEl1().getValue(), tr.getEl2().getType(), tr.getEl2().getValue());
        }
    }

}