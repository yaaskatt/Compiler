package mirea.lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Lexer {
    private StringBuilder input = new StringBuilder();
    private TokenType type;
    private String value;
    private boolean exhausted = false;
    private String errorMessage = "";
    private Set<Character> blankChars = new HashSet<>();

    /**
     * Converts program on specified language to tokens
     *
     * @param filePath location of file with input code
     */
    public Lexer(String filePath) {
        try (Stream<String> st = Files.lines(Paths.get(filePath))) {
            st.forEach(input::append);
        } catch (IOException ex) {
            exhausted = true;
            errorMessage = "Could not read file: " + filePath;
            return;
        }
        /* Initializing blank chars array */
        blankChars.add('\r');
        blankChars.add('\n');
        blankChars.add((char) 8 );
        blankChars.add((char) 9);
        blankChars.add((char) 11);
        blankChars.add((char) 12);
        blankChars.add((char) 32);

        moveAhead();
    }

    private void moveAhead() {
        if (exhausted) {
            return;
        }

        if (input.length() == 0) {
            exhausted = true;
            return;
        }

        ignoreWhiteSpaces();

        if (findNextToken()) {
            return;
        }

        exhausted = true;

        if (input.length() > 0) {
            errorMessage = "Unexpected symbol: '" + input.charAt(0) + "'";
        }
    }

    private void ignoreWhiteSpaces() {
        int charsToDelete = 0;

        while (blankChars.contains(input.charAt(charsToDelete))) {
            charsToDelete++;
        }

        if (charsToDelete > 0) {
            input.delete(0, charsToDelete);
        }
    }

    private boolean findNextToken() {
        for (TokenType t : TokenType.values()) {
            int end = t.endOfMatch(input.toString());

            if (end != -1) {
                type = t;
                value = input.substring(0, end);
                input.delete(0, end);
                return true;
            }
        }

        return false;
    }

    private TokenType currentType() {
        return type;
    }

    private String currentValue() {
        return value;
    }

    private boolean isSuccessful() {
        return errorMessage.length() == 0;
    }

    private String errorMessage() {
        return errorMessage;
    }

    private boolean isExhausted() {
        return exhausted;
    }

    public List<Token> getAllTokens() {
        List<Token> allTokens = new ArrayList<>();
        while (!isExhausted()){
            allTokens.add(new Token(currentType(), currentValue()));
            moveAhead();
        }
        if (!isSuccessful()) try {
            throw new Exception(errorMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allTokens;
    }
}
