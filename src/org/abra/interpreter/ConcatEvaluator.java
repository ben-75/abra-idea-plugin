package org.abra.interpreter;

import org.abra.interpreter.action.ConcatTermEvaluator;
import org.abra.language.psi.AbraConcatExpr;
import org.abra.language.psi.AbraConcatTerm;
import org.abra.language.psi.AbraConstTerm;
import org.abra.language.psi.AbraPostfixExpr;
import org.abra.utils.TRIT;

import java.util.ArrayList;
import java.util.Arrays;

public class ConcatEvaluator {

    //concatExpr ::= postfixExpr (AMP postfixExpr)+
    public static TRIT[] eval(AbraConcatExpr expr, AbraEvaluationContext context){
        ArrayList<TRIT> resp = new ArrayList<>();
        for(AbraConcatTerm postfixExpr:expr.getConcatTermList()){
            resp.addAll(Arrays.asList(ConcatTermEvaluator.eval(postfixExpr, context)));
        }
        return resp.toArray(new TRIT[resp.size()]);
    }
}
