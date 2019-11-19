package mirea.lexer;

import org.junit.Test;

import java.util.List;

public class LexerTest {
    private String testFolder = "test_files/";


    @Test
    public void getAllTokens() {
        Lexer lexer = new Lexer(testFolder + "typesTest.txt");
        List<LexerToken> tokenList = lexer.getAllTokens();
        for (LexerToken token : tokenList) {
            System.out.printf("tokenType: %s, value: %s\n", token.getType(), token.getValue());
        }
    }
}