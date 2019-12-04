package mirea.interpreter;

import mirea.lexer.Lexer;
import mirea.lexer.LexerToken;
import mirea.optimizer.Converter;
import mirea.optimizer.Optimizer;
import mirea.optimizer.Triad;
import mirea.parser.ParserToken;
import mirea.parser.Parser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ComplexTest {
    private String testFolder = "test_files/";

    @Test
    public void compTest() throws Exception {

        Lexer lexer = new Lexer(testFolder + "OptimTest");
        List<LexerToken> lexerTokenList = lexer.getAllTokens();
        Parser parser = new Parser(lexerTokenList);
        List<ParserToken> parserTokenList = (parser.lang());
        Converter conv = new Converter();
        List<Triad> triads = conv.reverseNot_toTriads(parserTokenList);

        System.err.println("\nTRIADS:\n");
        for (int i=0; i<triads.size(); i++) {
            Triad tr = triads.get(i);
            System.err.printf("%d: %s %s (%s %s, %s %s)\n", i, tr.getOp().getType(), tr.getOp().getValue(),
                    tr.getT1().getType(), tr.getT1().getValue(), tr.getT2().getType(), tr.getT2().getValue());
        }
        Optimizer opt = new Optimizer();
        System.err.println("\nOPTIMIZING:\n");
        try {
            triads = opt.findConstants(triads);
        } catch (Exception e) {};
        System.err.println("\nOPTIMIZED TRIADS:\n");
        for (int i=0; i<triads.size(); i++) {
            Triad tr = triads.get(i);
            System.err.printf("%d: %s %s (%s %s, %s %s)\n", i, tr.getOp().getType(), tr.getOp().getValue(),
                    tr.getT1().getType(), tr.getT1().getValue(), tr.getT2().getType(), tr.getT2().getValue());
        }
        parserTokenList = conv.triads_toReverseNot(triads);
        Interpreter interpreter = new Interpreter(testFolder + "ComplexTestResult.txt");
        interpreter.count(parserTokenList);
    }
}
