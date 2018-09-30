package org.abra.interpreter;

import org.abra.interpreter.action.ConcatTermEvaluator;
import org.abra.language.psi.AbraPostfixExpr;
import org.abra.language.psi.AbraReturnExpr;
import org.abra.utils.TRIT;

import java.util.ArrayList;
import java.util.Arrays;

public class ReturnEvaluator {

    //returnExpr      ::=  concatExpr | mergeExpr | funcExpr | typeExpr | lutExpr | sliceExpr | integer

    public static TRIT[] eval(AbraReturnExpr expr, AbraEvaluationContext context){
        if(expr.getConcatExpr()!=null)return ConcatEvaluator.eval(expr.getConcatExpr(), context);
        if(expr.getConcatTerm()!=null)return ConcatTermEvaluator.eval(expr.getConcatTerm(),context);
        if(expr.getMergeExpr()!=null)return MergeEvaluator.eval(expr.getMergeExpr(), context);
        throw new AbraSyntaxError(InterpreterUtils.getErrorLocationString(expr));
    }
}
