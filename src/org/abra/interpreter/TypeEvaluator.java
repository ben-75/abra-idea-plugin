package org.abra.interpreter;

import org.abra.language.psi.AbraPostfixExpr;
import org.abra.language.psi.AbraReturnExpr;
import org.abra.language.psi.AbraTypeExpr;
import org.abra.utils.TRIT;

import java.util.ArrayList;
import java.util.Arrays;

public class TypeEvaluator {

    //typeExpr        ::= typeNameRef OPEN_BRACE (fieldNameRef ASSIGN returnExpr SEMICOLON)+ CLOSE_BRACE

    public static TRIT[] eval(AbraTypeExpr expr, AbraEvaluationContext context){
        ArrayList<TRIT> resp = new ArrayList<>();
        for(AbraReturnExpr returnExpr:expr.getReturnExprList()){
            resp.addAll(Arrays.asList(ReturnEvaluator.eval(returnExpr, context)));
        }
        return resp.toArray(new TRIT[resp.size()]);
    }
}
