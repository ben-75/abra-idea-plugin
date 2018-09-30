package org.abra.interpreter;

import com.intellij.psi.PsiElement;
import org.abra.language.psi.AbraConstPrimary;
import org.abra.language.psi.AbraPlaceHolderName;
import org.abra.language.psi.AbraTypeName;
import org.abra.language.psi.AbraTypeStmt;

public class ConstPrimaryEvaluator {

    //constPrimary    ::= trit | integer | typeOrPlaceHolderNameRef | (OPEN_PAR mergeExpr CLOSE_PAR)
    public static int eval(AbraConstPrimary expr, AbraEvaluationContext context){
        if(expr.getInteger()!=null)return Integer.valueOf(expr.getInteger().getText());
        if(expr.getTrit()!=null){
            if(expr.getTrit().getText().equals("-"))return -1;
            if(expr.getTrit().getText().equals("0"))return 0;
            return 1;
        }
        if(expr.getTypeOrPlaceHolderNameRef()!=null){
            PsiElement resolved = expr.getTypeOrPlaceHolderNameRef().getReference().resolve();
            if(resolved==null){
                throw new AbraSyntaxError("Cannot resolve symbol "+expr.getTypeOrPlaceHolderNameRef().getText()+" at "+InterpreterUtils.getErrorLocationString(expr.getTypeOrPlaceHolderNameRef()));
            }
            if(resolved instanceof AbraPlaceHolderName){
                return context.getType((AbraPlaceHolderName)resolved);
            }
            if(resolved instanceof AbraTypeName){
                return ((AbraTypeStmt)resolved.getParent()).getResolvedSize();
            }
        }
        throw new RuntimeException("hum... something wrong with the bnf. We shouldn't have a merge expression here:"+expr.getText()+ " at "+InterpreterUtils.getErrorLocationString(expr));
    }
}
