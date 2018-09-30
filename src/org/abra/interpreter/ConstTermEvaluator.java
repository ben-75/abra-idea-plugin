package org.abra.interpreter;

import org.abra.language.psi.AbraConstTerm;

public class ConstTermEvaluator {
    //constTerm       ::= constPrimary (termOperator constTerm )?
    public static int eval(AbraConstTerm expr, AbraEvaluationContext context){
        int primaryValue = ConstPrimaryEvaluator.eval(expr.getConstPrimary(), context);
        if(expr.getTermOperator()==null) return primaryValue;
        int value2 = eval(expr.getConstTerm(), context);
        if(expr.getTermOperator().getText().equals("*"))return primaryValue*value2;
        if(expr.getTermOperator().getText().equals("/"))return primaryValue/value2;
        return primaryValue%value2;
    }
}
