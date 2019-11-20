package mirea.triad_optimisation;

import mirea.lexer.Lexer;
import mirea.lexer.LexerToken;
import mirea.parser.ParserToken;
import mirea.parser.Parser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class OptTest {
    private String testFolder = "test_files/";

    @Test
    public void findConstants() {
        Lexer lexer = new Lexer(testFolder + "ConvTest");
        List<LexerToken> lexerTokenList = lexer.getAllTokens();
        assertFalse(lexerTokenList.isEmpty());
        for (LexerToken lexerToken : lexerTokenList) {
            System.out.printf("tokenType: %s, lexema: %s\n", lexerToken.getType(), lexerToken.getValue());
        }
        Parser parser = new Parser(lexerTokenList);
        List<ParserToken> parserTokenList = (parser.lang());
        Conv conv = new Conv();
        List<Triad> triads = conv.reverseNot_toTriads(parserTokenList);

        for (int i=0; i<triads.size(); i++) {
            Triad tr = triads.get(i);
            System.out.printf("%d: %s %s (%s %s, %s %s)\n", i, tr.getOp().getType(), tr.getOp().getValue(),
                    tr.getT1().getType(), tr.getT1().getValue(), tr.getT2().getType(), tr.getT2().getValue());
        }
        Opt opt = new Opt();
        try {
            triads = opt.findConstants(triads);
        } catch (Exception e) {};
        for (int i=0; i<triads.size(); i++) {
            Triad tr = triads.get(i);
            System.out.printf("%d: %s %s (%s %s, %s %s)\n", i, tr.getOp().getType(), tr.getOp().getValue(),
                    tr.getT1().getType(), tr.getT1().getValue(), tr.getT2().getType(), tr.getT2().getValue());
        }
        parserTokenList = conv.triads_toReverseNot(triads);
        for (int i = 0; i< parserTokenList.size(); i++) {
            System.out.printf("%d: type: %s, value: %s\n", i, parserTokenList.get(i).getType(), parserTokenList.get(i).getValue());
        }
    }

}