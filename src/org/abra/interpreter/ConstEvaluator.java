package org.abra.interpreter;


import org.abra.language.psi.AbraConstExpr;
import org.abra.language.psi.AbraConstOperator;

//constExpr       ::= constTerm (constOperator constExpr)?

public class ConstEvaluator {

    public static int eval(AbraConstExpr expr, AbraEvaluationContext context){
        int termValue = ConstTermEvaluator.eval(expr.getConstTerm(), context);
        if(expr.getConstOperator()==null)return termValue;
        AbraConstOperator operator = expr.getConstOperator();
        int term2Value = ConstEvaluator.eval(expr.getConstExpr(),context);
        if(operator.getText().equals("+")){
            return termValue+term2Value;
        }else{
            return termValue-term2Value;
        }
    }
}
