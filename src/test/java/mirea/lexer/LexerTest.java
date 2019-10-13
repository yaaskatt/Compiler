package mirea.lexer;

import org.junit.Test;

import java.util.List;

public class LexerTest {
    private String testFolder = "test_files/";


    @Test
    public void getAllTokens() {
        Lexer lexer = new Lexer(testFolder + "typesTest.txt");
        List<Token> tokenList = lexer.getAllTokens();
        for (Token aTokenList : tokenList) {
            System.out.printf("tokenType: %s, value: %s\n", aTokenList.getTokenType(), aTokenList.getValue());
        }
    }
}