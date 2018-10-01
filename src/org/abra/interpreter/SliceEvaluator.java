package org.abra.interpreter;

import com.intellij.psi.PsiElement;
import org.abra.language.psi.*;
import org.abra.language.psi.impl.FieldRange;
import org.abra.utils.TRIT;

public class SliceEvaluator {

    //sliceExpr       ::= paramOrVarNameRef (DOT fieldNameRef)* (OPEN_BRACKET rangeExpr CLOSE_BRACKET)?
    public static TRIT[] eval(AbraSliceExpr expr, AbraEvaluationContext context){
        PsiElement resolved = expr.getParamOrVarNameRef().getReference().resolve();
        if(resolved==null){
            throw new AbraSyntaxError("Cannot resolve "+expr.getParamOrVarNameRef().getText()+ " in "+InterpreterUtils.getErrorLocationString(expr.getParamOrVarNameRef()));
        }

        TRIT[] value = context.get((AbraNamedElement) resolved);
        if(value==null){
            throw new AbraSyntaxError("Cannot evaluate "+resolved.getText()+ " in "+InterpreterUtils.getErrorLocationString(resolved));
        }
        if(expr.getFieldNameRefList().size()>0){
            int startOffset = 0;
            int length = 0;
            for(AbraFieldNameRef fieldNameRef:expr.getFieldNameRefList()){
                AbraFieldName fieldName = (AbraFieldName) fieldNameRef.getReference().resolve();
                if(fieldName==null){
                    throw new AbraSyntaxError("Cannot resolve field "+fieldNameRef.getText()+" at "+InterpreterUtils.getErrorLocationString(fieldNameRef));
                }
                FieldRange fieldRange = fieldName.getFieldRange();
                startOffset = startOffset+fieldRange.getStartOffset();
                length = fieldRange.getLength();
            }
            TRIT[] resp = new TRIT[length];
            if(startOffset+length>value.length){
                //feed with 0 if required
                TRIT[] expanded = new TRIT[startOffset+length];
                System.arraycopy(value,0,expanded,0,value.length);
                for(int j=value.length;j<startOffset+length;j++){
                    expanded[j]=TRIT.Z;
                }
                System.arraycopy(expanded, startOffset, resp, 0, length);
            }else {
                System.arraycopy(value, startOffset, resp, 0, length);
            }
            return resp;
        }

        if(expr.getRangeExpr()!=null){
            return RangeEvaluator.apply(expr.getRangeExpr(), value, context);
        }
        return value;
    }
}
