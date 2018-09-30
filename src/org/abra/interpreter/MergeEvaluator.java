package org.abra.interpreter;

import org.abra.interpreter.action.ConcatTermEvaluator;
import org.abra.language.psi.AbraConcatTerm;
import org.abra.language.psi.AbraMergeExpr;
import org.abra.language.psi.AbraPostfixExpr;
import org.abra.utils.TRIT;
import org.abra.utils.TritUtils;

public class MergeEvaluator {

    public static TRIT[] eval(AbraMergeExpr expr, AbraEvaluationContext context){
        TRIT[] resp = null;
        AbraConcatTerm current = null;
        for(AbraConcatTerm postFixExpr:expr.getConcatTermList()){
            TRIT[] res = ConcatTermEvaluator.eval(postFixExpr, context);
            if(res!=null && resp!=null){
                throw new RuntimeException("Multiple not null branch in merge expr : \n\t1:"
                        +current.getText()+"="+ TritUtils.trit2String(resp)+"\n\t2:"
                        +postFixExpr.getText()+"="+TritUtils.trit2String(res)+"\n in "+InterpreterUtils.getErrorLocationString(expr));
            }
            if(res!=null){
                current = postFixExpr;
                resp = res;
            }
        }
        return resp;
    }
}
