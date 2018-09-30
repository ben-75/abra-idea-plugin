package org.abra.interpreter.action;

import org.abra.interpreter.*;
import org.abra.language.psi.AbraConcatTerm;
import org.abra.language.psi.AbraPostfixExpr;
import org.abra.utils.TRIT;

public class ConcatTermEvaluator {

    public static TRIT[] eval(AbraConcatTerm expr, AbraEvaluationContext context){
        if(expr.getTypeExpr()!=null)return TypeEvaluator.eval(expr.getTypeExpr(), context);
        if(expr.getLutExpr()!=null)return LutEvaluator.eval(expr.getLutExpr(), context);
        if(expr.getConcatExpr()!=null)return ConcatEvaluator.eval(expr.getConcatExpr(), context);
        if(expr.getFuncExpr()!=null)return FuncEvaluator.eval(expr.getFuncExpr(), context);
        if(expr.getSliceExpr()!=null)return SliceEvaluator.eval(expr.getSliceExpr(), context);
        if(expr.getInteger()!=null)return IntegerEvaluator.eval(expr.getInteger());
        if(expr.getMergeExpr()!=null)return MergeEvaluator.eval(expr.getMergeExpr(), context);
        throw new AbraSyntaxError(InterpreterUtils.getErrorLocationString(expr));
    }
}
