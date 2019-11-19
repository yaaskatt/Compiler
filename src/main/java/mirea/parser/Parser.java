package mirea.parser;

import mirea.token.AbstractToken;
import mirea.lexer.LexerToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {
    private List<LexerToken> lexerTokenList;
    private List<ParserToken> parserTokenList = new ArrayList<>();
    private Stack<LexerToken> s = new Stack<>();
    private LexerToken curLexerToken;
    private int lastNumInPrefix = -1;
    private int num = -1;

    public Parser(List<LexerToken> lexerTokenList) {
        this.lexerTokenList = lexerTokenList;
    }

    public List<ParserToken> lang() {
        while (expr()) {
        }
        return parserTokenList;
    }

    private boolean expr() {
        int begNum = num;
        if (declar_stmt() || assign_stmt() || while_stmt() || if_stmt() || objectOp_stmt() || print_stmt()) {
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean declar_stmt() {
        int begNum = num;
        if (TYPE() && VAR() && SEMI()) {
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean assign_stmt() {
        int begNum = num;
        if (VAR() && ASSIGN_OP() && (value_stmt() || STRING()) && SEMI()) {
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean while_stmt() {
        int begNum = num;
        int condEndRef = -1;
        int condBeg;
        if (WHILE()) {
            condBeg = parserTokenList.size();
            if (condition_stmt() && DO() && L_CB()) {
                condEndRef = parserTokenList.size() - 3;
                while (expr()) {
                }
                if (R_CB()) {
                    parserTokenList.add(new ParserToken("INT", condBeg + ""));
                    parserTokenList.add(new ParserToken("TRANS", "!"));
                    parserTokenList.set(condEndRef, new ParserToken("INT", parserTokenList.size() + ""));
                    return true;
                }
            }
        }
        num = begNum;
        return false;
    }

    private boolean if_stmt() {
        int elseBeg = -1;
        int ifEndRef = -1;
        int thenEndRef = -1;
        int begNum = num;
        if (IF() && condition_stmt() && THEN() && L_CB()) {
            ifEndRef = parserTokenList.size() - 3;
            while (expr()) {
            }
            int cycleNum = num;
            if (R_CB() && ELSE() && L_CB()) {
                thenEndRef = parserTokenList.size() - 3;
                elseBeg = parserTokenList.size()-1;
                while (expr()) {
                }
            }
            else num = cycleNum;
            if (R_CB()) {
                if (elseBeg != -1) {
                    parserTokenList.set(ifEndRef, new ParserToken("INT", elseBeg + ""));
                    parserTokenList.set(thenEndRef, new ParserToken("INT", parserTokenList.size() + ""));
                } else {
                    parserTokenList.set(ifEndRef, new ParserToken("INT", parserTokenList.size() + ""));
                }
                return true;
            }
        }
        num = begNum;
        return false;
    }

    private boolean objectOp_stmt() {
        int begNum = num;
        if ((objectAdd() || objectPut()) && SEMI()) {
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean print_stmt() {
        int begNum = num;
        if (PRINT() && (STRING() || value_stmt()) && SEMI()) {
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean value_stmt() {
        int begNum = num;
        if (stmt() || b_stmt()) {
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean stmt() {
        int begNum = num;
        if (objectMethodWithReturnValue() || value()) {
            while (OP() && value_stmt()) {
            }
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean b_stmt() {
        int begNum = num;
        if (L_RB() && value_stmt() && R_RB()) {
            while (OP() && value_stmt()) {
            }
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean value() {
        int begNum = num;
        if (VAR() || num()) {
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean num() {
        int begNum = num;
        if (DOUBLE() || INT()) {
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean condition_stmt() {
        int begNum = num;
        if ((condition()) || b_condition() || b_cond_stmt()) {
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean b_cond_stmt() {
        int begNum = num;
        if (L_RB() && condition_stmt() && R_RB()) {
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean b_condition() {
        int begNum = num;
        if (L_RB() && condition() && R_RB()) {
            while (LOG_OP() && condition_stmt()) {
            }
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean condition() {
        int begNum = num;
        int cycleNum;
        if (value_stmt() && COMP_OP() && value_stmt()) {
            cycleNum = num;
            while (LOG_OP() && condition_stmt()) {
            cycleNum = num;
            }
            num = cycleNum;
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean objectMethodWithReturnValue() {
        int begNum = num;
        if (VAR() && DOT() && (GET() || CONTAINS() || ADD() || PUT()) && value_stmt()) {
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean objectAdd() {
        int begNum = num;
        if (VAR() && DOT() && ADD() && value_stmt()) {
            return true;
        }
        num = begNum;
        return false;
    }


    private boolean objectPut() {
        int begNum = num;
        if (VAR() && DOT() && PUT() && value_stmt() && COMMA() && value_stmt()) {
            return true;
        }
        num = begNum;
        return false;
    }

    private boolean VAR() {
        return checkToken("VAR");
    }

    private boolean ASSIGN_OP() {
        return checkToken("ASSIGN_OP");
    }

    private boolean DOUBLE() {
        return checkToken("DOUBLE");
    }

    private boolean INT() {
        return checkToken("INT");
    }

    private boolean STRING() {
        return checkToken("STRING");
    }

    private boolean OP() {
        return checkToken("OP");
    }

    private boolean COMP_OP() {
        return checkToken("COMP_OP");
    }

    private boolean LOG_OP() {
        return checkToken("LOG_OP");
    }

    private boolean WHILE() {
        return checkToken(("WHILE"));
    }

    private boolean DO() {
        return checkToken("DO");
    }

    private boolean IF() {
        return checkToken("IF");
    }

    private boolean THEN() {
        return checkToken("THEN");
    }

    private boolean ELSE() {
        return checkToken("ELSE");
    }

    private boolean PRINT() {
        return checkToken("PRINT");
    }

    private boolean TYPE() {
        return checkToken("TYPE");
    }

    private boolean PUT() {
        return checkToken("PUT");
    }

    private boolean ADD() {
        return checkToken("ADD");
    }

    private boolean CONTAINS() { return checkToken("CONTAINS"); }

    private boolean GET() {
        return checkToken("GET");
    }

    private boolean DOT() {
        return checkToken("DOT");
    }

    private boolean COMMA() {
        return checkToken("COMMA");
    }

    private boolean SEMI() {
        return checkToken("SEMI");
    }

    private boolean L_CB() {
        return checkToken("L_CB");
    }

    private boolean R_CB() {
        return checkToken("R_CB");
    }

    private boolean L_RB() {
        return checkToken("L_RB");
    }

    private boolean R_RB() {
        return checkToken("R_RB");
    }

    private boolean checkToken(String reqTokenType) {
        if (num == lexerTokenList.size() - 1) return false;
        int begNum = num;
        match();
        if (curLexerToken.getType().equals(reqTokenType)) {
            if (num > lastNumInPrefix) {
                toReverseNot(curLexerToken);
                lastNumInPrefix++;
            }
            return true;
        }
        num = begNum;
        return false;
    }

    private void match() {
        curLexerToken = lexerTokenList.get(++num);
    }

    private ParserToken toParserToken(LexerToken lexerToken) {
        switch (lexerToken.getType()) {
            case "ASSIGN_OP":
            case "ADD":
            case "GET":
            case "PUT":
            case "CONTAINS":
            case "LOG_OP":
            case "COMP_OP":
            case "PRINT":
                return new ParserToken("OP", lexerToken.getValue());
            case "TYPE":
                return new ParserToken("DEF", lexerToken.getValue());
            case "STRING":
                return new ParserToken("STRING", lexerToken.getValue()
                        .substring(1, lexerToken.getValue().length()-1));
            default:
                return new ParserToken(lexerToken);
        }
    }

    private void toReverseNot(LexerToken lexerToken) {
        switch (lexerToken.getType()) {
            case "R_RB":
                while (!s.peek().getType().equals("L_RB")) {
                    parserTokenList.add(toParserToken(s.pop()));
                }
                s.pop();
                break;
            case "L_CB":
                parserTokenList.add(toParserToken(curLexerToken));
                break;
            case "L_RB":
                s.add(curLexerToken);
                break;
            case "OP":
                String prevTokenLex = lexerTokenList.get(num - 1).getValue();
                if ((prevTokenLex.equals("(") || prevTokenLex.equals("=")) && lexerToken.getValue().equals("-")) {
                    parserTokenList.add(new ParserToken("INT", "0"));
                }
            case "COMP_OP":
            case "LOG_OP":
                if (!s.isEmpty()) {
                    while (!s.isEmpty() && priority(lexerTokenList.get(num)) <= priority(s.peek())) {
                        parserTokenList.add(toParserToken(s.pop()));
                    }
                }
                s.add(curLexerToken);
                break;
            case "ASSIGN_OP":
                parserTokenList.set(parserTokenList.size() - 1, new ParserToken("ADR", parserTokenList.get(parserTokenList.size() - 1).getValue()));
                if (!s.isEmpty()) {
                    while (!s.isEmpty() && priority(lexerTokenList.get(num)) <= priority(s.peek())) {
                        parserTokenList.add(toParserToken(s.pop()));
                    }
                }
                s.add(curLexerToken);
                break;
            case "GET":
            case "ADD":
            case "PUT":
            case "CONTAINS":
                parserTokenList.set(parserTokenList.size() - 1, new ParserToken("ADR", parserTokenList.get(parserTokenList.size() - 1).getValue()));
            case "TYPE":
            case "PRINT":
                s.add(curLexerToken);
                break;
            case "DOUBLE":
            case "INT":
            case "STRING":
            case "VAR":
                parserTokenList.add(toParserToken(curLexerToken));
                break;
            case "SEMI":
                while (!s.isEmpty()) {
                    if (s.peek().getType().equals("TYPE") )
                        parserTokenList.set(parserTokenList.size() -1, new ParserToken("ADR", parserTokenList.get(parserTokenList.size()-1).getValue()));
                    parserTokenList.add(toParserToken(s.pop()));
                }
                break;
            case "COMMA":
                while (!s.peek().getType().equals("PUT")) {
                    parserTokenList.add(toParserToken(s.pop()));
                }
                break;
            case "THEN":
            case "DO":
                while (!s.isEmpty()) {
                    parserTokenList.add(toParserToken(s.pop()));
                }
                parserTokenList.add(new ParserToken());
                parserTokenList.add(new ParserToken("TRANS", "!F"));
                break;
            case "ELSE":
                parserTokenList.add(new ParserToken());
                parserTokenList.add(new ParserToken("TRANS", "!"));
                break;
            case "R_CB":
                parserTokenList.add(new ParserToken(curLexerToken));
                break;
        }
    }

    private static int priority(AbstractToken lexerToken) {
        if (lexerToken.getValue().equals("="))
            return 1;
        if (lexerToken.getValue().equals("||")) return 2;
        if (lexerToken.getValue().equals("&&")) return 3;
        if (lexerToken.getType().equals("COMP_OP"))
            return 4;
        if (lexerToken.getValue().equals("-") || lexerToken.getValue().equals("+"))
            return 5;
        if (lexerToken.getValue().equals("*") || lexerToken.getValue().equals("/"))
            return 6;
        return 0;
    }
}
