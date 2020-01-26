package mirea.optimizer;

import mirea.parser.ParserToken;
import static mirea.parser.ParserTokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.String.valueOf;

public class Converter {

    public static List<Triad> reverseNot_toTriads(List<ParserToken> tokenList) {

        List<Triad> triadList = new ArrayList<>();
        int start = tokenList.size();
        HashMap<Integer, Integer> corr = new HashMap<>();
        List<ParserToken> trans_ref = new ArrayList<>();

        for (int i=0; i<tokenList.size(); i++) {
            int difference = start - tokenList.size();
            switch(tokenList.get(i).getType()) {
                case OP:
                    switch(tokenList.get(i).getValue()) {
                        case "print":
                            triadList = singleOp(triadList, tokenList, corr, i, difference);
                            i--;
                            break;
                        default:
                            triadList = doubleOp(triadList, tokenList, corr, i, difference);
                            i -= 2;
                    }
                    break;
                case DEF:
                    triadList = singleOp(triadList, tokenList, corr, i, difference);
                    i--;
                    break;
                case TRANS:
                    switch(tokenList.get(i).getValue()) {
                        case "!":
                            singleOp(triadList, tokenList, corr, i, difference);
                            i--;
                            trans_ref.add(triadList.get(triadList.size()-1).getT1());
                            break;
                        case "!F":
                            doubleOp(triadList, tokenList, corr, i, difference);
                            i -= 2;
                            trans_ref.add(triadList.get(triadList.size()-1).getT2());
                            break;
                    }
                    break;
                case FUNC:
                    func(triadList, tokenList, corr, i, difference);
                    break;
                case ENTER_SCOPE:
                case EXIT_SCOPE:
                    triadList.add(new Triad(tokenList.get(i),new ParserToken(), new ParserToken()));
                    break;
            }
        }
        for (ParserToken token : trans_ref) {
            if (!corr.containsKey(Integer.parseInt(token.getValue()))) {
                token.setValue(triadList.size() + "");
            }
            else token.setValue(valueOf(corr.get(Integer.parseInt(token.getValue()))));
        }
        return triadList;
    }

    public static void replaceRef(Triad ref, List<ParserToken> tokenList) {
        int index1 = Integer.parseInt(ref.getT1().getValue());
        int index2 = Integer.parseInt(ref.getT2().getValue());
        for (int j = index1; index1 != index2; index2--) {
            tokenList.add(tokenList.get(j));
            tokenList.remove(j);
        }

    }

    public static List<ParserToken> triads_toReverseNot(List<Triad> triadList) {
        List<ParserToken> tokenList = new ArrayList<>();
        for (int i = 0; i < triadList.size(); i++) {
            Triad curTriad = triadList.get(i);
            if (curTriad.getOp().getType() == CONST) continue;

            int begin = tokenList.size();

            if (curTriad.getT1().getType() == REF) {
                Triad ref = triadList.get(Integer.parseInt(curTriad.getT1().getValue()));
                begin = Integer.parseInt(ref.getT1().getValue());
                replaceRef(ref, tokenList);
            }
            else if (curTriad.getT1().notBlank()) {
                tokenList.add(curTriad.getT1());
            }

            if (curTriad.getT2().getType() == REF) {
                Triad ref = triadList.get(Integer.parseInt(curTriad.getT2().getValue()));
                if (curTriad.getT1().getType() != REF) {
                    begin = Integer.parseInt(ref.getT1().getValue());
                }
                replaceRef(ref, tokenList);
            }
            else if (curTriad.getT2().notBlank()) {
                tokenList.add(curTriad.getT2());
            }

            tokenList.add(curTriad.getOp());
            int end = tokenList.size();

            triadList.set(i, new Triad(new ParserToken(REF, ""),
                    new ParserToken(INT, valueOf(begin)),
                    new ParserToken(INT, valueOf(end))));

        }

        for (int i=0; i<tokenList.size(); i++) {
            if (tokenList.get(i).getType() == TRANS) {
                int index = Integer.parseInt(tokenList.get(i-1).getValue());
                if (index < triadList.size())
                    tokenList.get(i-1).setValue(triadList.get(index).getT1().getValue());
                else {
                    tokenList.get(i-1).setValue(tokenList.size() + "");
                }
            }
        }

        return tokenList;
    }



    private static List<Triad> singleOp(List<Triad> triadList, List<ParserToken> token, HashMap<Integer, Integer> corr, int i, int dif) {
        triadList.add(new Triad(token.get(i), token.get(i-1), new ParserToken()));
        token.remove(i);
        token.set(i-1, new ParserToken(REF, triadList.size()-1 + ""));
        for (int j=i+dif; j>=i+dif-1; j--) {
            if (corr.containsKey(j)) continue;
            corr.put(j, triadList.size()-1);
        }
        return triadList;
    }

    private static List<Triad> doubleOp(List<Triad> triadList, List<ParserToken> token, HashMap<Integer, Integer> corr, int i, int dif) {
        triadList.add(new Triad(token.get(i), token.get(i - 2), token.get(i - 1)));
        token.remove(i);
        token.remove(i-1);
        token.set(i-2, new ParserToken(REF, triadList.size()-1 + ""));
        for (int j=i+dif; j>=i+dif-2; j--) {
            if (corr.containsKey(j)) continue;
            corr.put(j, triadList.size()-1);
        }
        return triadList;
    }

    private static List<Triad> func(List<Triad> triadList, List<ParserToken> token, HashMap<Integer, Integer> corr, int i, int dif) {
        int argNum = Integer.parseInt(token.get(i-1).getValue());
        for (int j = i - argNum - 2; j < i; j++) {
            triadList.add(new Triad(new ParserToken(CONST, "const"), token.get(j), new ParserToken()));
            token.set(j, new ParserToken(REF, triadList.size()-1 + ""));
        }
        triadList.add(new Triad(token.get(i), new ParserToken(), new ParserToken()));
        token.set(i, new ParserToken(REF, triadList.size()-1 + ""));
        corr.put(i + dif, triadList.size()-1);
        return triadList;
    }
}

