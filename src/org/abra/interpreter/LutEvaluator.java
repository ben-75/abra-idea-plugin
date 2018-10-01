package org.abra.interpreter;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.abra.language.psi.*;
import org.abra.utils.TRIT;

import java.util.Arrays;

import static org.abra.interpreter.InterpreterUtils.getErrorLocationString;

public class LutEvaluator {

    public static TRIT[] eval(AbraLutExpr expr, AbraEvaluationContext context){
        System.out.println("LUT  :"+expr.getText());
        PsiElement resolved = expr.getLutOrParamOrVarNameRef().getReference().resolve();
//        if(!(resolved instanceof AbraLutName)){
//            throw new AbraSyntaxError("Cannot resolve "+expr.getLutOrParamOrVarNameRef().getText()+" in "+InterpreterUtils.getErrorLocationString(expr.getLutOrParamOrVarNameRef()));
//        }
        if(resolved instanceof AbraLutName) {
            AbraLutStmt lut = (AbraLutStmt) expr.getLutOrParamOrVarNameRef().getReference().resolve().getParent();
            TRIT[] input = new TRIT[expr.getPostfixExprList().size()];
            for (int i = 0; i < expr.getPostfixExprList().size(); i++) {
                TRIT[] evalation = PostFixEvaluator.eval(expr.getPostfixExprList().get(i), context);
                if (evalation.length != 1) {
                    throw new AbraSyntaxError(expr.getPostfixExprList().get(i).getText() + "should evalute to exactly one trit. At " + InterpreterUtils.getErrorLocationString(expr.getPostfixExprList().get(i)));
                }
                input[i] = evalation[0];
            }
            return eval(input, lut);
        }
        if(resolved instanceof AbraParamName){
            TRIT[] trits = context.get((AbraNamedElement) resolved);
            AbraInteger integerExpr = expr.getPostfixExprList().get(0).getConcatTerm().getInteger();
            if(integerExpr==null){
                throw new RuntimeException("Hum... something to review in the bnf.");
            }
            return new TRIT[]{trits[Integer.parseInt(integerExpr.getText())]};
        }
        if(resolved instanceof AbraVarName){
            TRIT[] trits = context.get((AbraNamedElement) resolved);
            AbraInteger integerExpr = expr.getPostfixExprList().get(0).getConcatTerm().getInteger();
            if(integerExpr!=null){
                return new TRIT[]{trits[Integer.parseInt(integerExpr.getText())]};
            }

            RangeEvaluator.apply(expr.getPostfixExprList().get(0).getConcatTerm().getSliceExpr().getRangeExpr(),trits,context);

            throw new RuntimeException("Hum... something to review in the bnf.");
        }
        return null;
    }

    private static TRIT[] eval(TRIT[] input, AbraLutStmt lut){
        int expectedInputSize = lut.getLutEntryList().get(0).getInputLength();
        int inputSize = input.length;
        if(inputSize!=expectedInputSize){
            throw new IllegalArgumentException("Expecting "+expectedInputSize+" input trits, but got "+inputSize+" (LUT: "+lut.getLutName().getText()+")");
        }

        for(AbraLutEntry entry:lut.getLutEntryList()){
            AbraTritList pattern = entry.getTritListList().get(0);
            if(match(fromTritList(pattern),input))return fromTritList(entry.getTritListList().get(1));
        }
        return null;
    }

    private static boolean match(TRIT[] pattern, TRIT[] input){
        return Arrays.equals(pattern,input);
    }

    private static TRIT[] fromTritList(AbraTritList lst){
        TRIT[] resp = new TRIT[lst.getLength()];
        for(int i=0;i<lst.getLength();i++){
            switch (lst.getTritList().get(i).getText()) {
                case "1" : resp[i]=TRIT.O;break;
                case "0" : resp[i]=TRIT.Z;break;
                case "-" : resp[i]=TRIT.M;break;
                default: throw new AbraSyntaxError(getErrorLocationString(lst.getTritList().get(i)));
            }
        }
        return resp;
    }


}
