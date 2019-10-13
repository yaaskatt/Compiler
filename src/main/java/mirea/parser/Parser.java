package mirea.parser;

import mirea.lexer.Token;
import mirea.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {
    private List<Token> inp;
    private List<Element> out = new ArrayList<>();
    private Stack<Token> s = new Stack<>();
    private Token curToken;
    private int lastNumInPrefix = -1;
    int num = -1;

    public Parser(List<Token> inp) {
        this.inp = inp;
    }

    public List<Element> lang() {
        while (expr()) {
        }
        return out;
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
            condBeg = out.size();
            if (condition_stmt() && DO() && L_CB()) {
                condEndRef = out.size() - 3;
                while (expr()) {
                }
                if (R_CB()) {
                    out.add(new Element("INT", condBeg + ""));
                    out.add(new Element("TRANS", "!"));
                    out.set(condEndRef, new Element("INT", out.size() + ""));
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
            ifEndRef = out.size() - 3;
            while (expr()) {
            }
            int cycleNum = num;
            if (R_CB() && ELSE() && L_CB()) {
                thenEndRef = out.size() - 3;
                elseBeg = out.size()-1;
                while (expr()) {
                }
            }
            else num = cycleNum;
            if (R_CB()) {
                if (elseBeg != -1) {
                    out.set(ifEndRef, new Element("INT", elseBeg + ""));
                    out.set(thenEndRef, new Element("INT", out.size() + ""));
                } else {
                    out.set(ifEndRef, new Element("INT", out.size() + ""));
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

    private boolean checkToken(String reqTokenTypeName) {
        if (num == inp.size() - 1) return false;
        int begNum = num;
        match();
        if (curToken.getTokenType().name().equals(reqTokenTypeName)) {
            if (num > lastNumInPrefix) {
                toReverseNot(curToken);
                lastNumInPrefix++;
            }
            return true;
        }
        num = begNum;
        return false;
    }

    private void match() {
        curToken = inp.get(++num);
    }

    private Element toElement(Token token) {
        switch (token.getTokenType().name()) {
            case "ASSIGN_OP":
            case "ADD":
            case "GET":
            case "PUT":
            case "CONTAINS":
            case "LOG_OP":
            case "COMP_OP":
            case "PRINT":
                return new Element("OP", token.getValue());
            case "TYPE":
                return new Element("DEF", token.getValue());
            case "STRING":
                return new Element("STRING", token.getValue().substring(1, token.getValue().length()-1));
            default:
                return new Element(token);
        }
    }

    private void toReverseNot(Token token) {
        switch (token.getTokenType().name()) {
            case "R_RB":
                while (!s.peek().getTokenType().equals(TokenType.L_RB)) {
                    out.add(toElement(s.pop()));
                }
                s.pop();
                break;
            case "L_CB":
                out.add(toElement(curToken));
                break;
            case "L_RB":
                s.add(curToken);
                break;
            case "OP":
                String prevTokenLex = inp.get(num - 1).getValue();
                if ((prevTokenLex.equals("(") || prevTokenLex.equals("=")) && token.getValue().equals("-")) {
                    out.add(new Element("INT", "0"));
                }
            case "COMP_OP":
            case "LOG_OP":
                if (!s.isEmpty()) {
                    while (!s.isEmpty() && priority(inp.get(num)) <= priority(s.peek())) {
                        out.add(toElement(s.pop()));
                    }
                }
                s.add(curToken);
                break;
            case "ASSIGN_OP":
                out.set(out.size() - 1, new Element("ADR", out.get(out.size() - 1).getValue()));
                if (!s.isEmpty()) {
                    while (!s.isEmpty() && priority(inp.get(num)) <= priority(s.peek())) {
                        out.add(toElement(s.pop()));
                    }
                }
                s.add(curToken);
                break;
            case "GET":
            case "ADD":
            case "PUT":
            case "CONTAINS":
                out.set(out.size() - 1, new Element("ADR", out.get(out.size() - 1).getValue()));
            case "TYPE":
            case "PRINT":
                s.add(curToken);
                break;
            case "DOUBLE":
            case "INT":
            case "STRING":
            case "VAR":
                out.add(toElement(curToken));
                break;
            case "SEMI":
                while (!s.isEmpty()) {
                    if (s.peek().getTokenType().name().equals("TYPE") )
                        out.set(out.size() -1, new Element("ADR", out.get(out.size()-1).getValue()));
                    out.add(toElement(s.pop()));
                }
                break;
            case "COMMA":
                while (!s.peek().getTokenType().name().equals("PUT")) {
                    out.add(toElement(s.pop()));
                }
                break;
            case "THEN":
            case "DO":
                while (!s.isEmpty()) {
                    out.add(toElement(s.pop()));
                }
                out.add(new Element());
                out.add(new Element("TRANS", "!F"));
                break;
            case "ELSE":
                out.add(new Element());
                out.add(new Element("TRANS", "!"));
                break;
            case "R_CB":
                out.add(toElement(curToken));
                break;
        }
    }

    private static int priority(Token token) {
        if (token.getValue().equals("="))
            return 1;
        if (token.getValue().equals("||")) return 2;
        if (token.getValue().equals("&&")) return 3;
        if (token.getTokenType().equals(TokenType.COMP_OP))
            return 4;
        if (token.getValue().equals("-") || token.getValue().equals("+"))
            return 5;
        if (token.getValue().equals("*") || token.getValue().equals("/"))
            return 6;
        return 0;
    }
}
