package org.qupla.runtime.debugger;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.qupla.language.psi.*;

public class QuplaDebuggerUtil {

    public static PsiElement getQuplaPsiElement(PsiElement element) {
        while (element instanceof PsiWhiteSpace) element = element.getNextSibling();
        if (element == null) return null;
        if (element instanceof QuplaFuncBody){
            if (((QuplaFuncBody) element).getAssignExprList().size() == 0) {
                return getQuplaPsiElement(((QuplaFuncBody) element).getReturnExpr());
            }else{
                return getQuplaPsiElement(((QuplaFuncBody) element).getAssignExprList().get(0));
            }
        }
        if(element instanceof QuplaFuncStmt){
            return getQuplaPsiElement(((QuplaFuncStmt) element).getFuncBody());
        }
        if(element instanceof QuplaCondExpr){
            return getQuplaPsiElement(((QuplaCondExpr)element).getMergeExprList().get(0));
        }
        while (!(element instanceof QuplaFuncExpr) && !(element instanceof QuplaConcatExpr)
                && !(element instanceof QuplaAssignExpr) && !(element instanceof QuplaLutExpr)
                && !(element instanceof QuplaMergeExpr) && !(element instanceof QuplaSliceExpr)
                && !(element instanceof QuplaStateExpr) && !(element instanceof QuplaReturnExpr) && !(element instanceof QuplaFile)
                && !(element instanceof QuplaFuncStmt)) {
            element = element.getParent();
        }
        return element;
    }

    public static PsiElement findEvaluable(PsiElement element) {
        if (element instanceof QuplaReturnExpr) {
            element = ((QuplaReturnExpr) element).getCondExpr();
            while (element.getChildren().length == 1 && QuplaDebuggerUtil.isEvaluable(element.getChildren()[0])) {
                element = element.getChildren()[0];
            }
        }
        if (element instanceof QuplaFuncStmt) {
            if (((QuplaFuncStmt) element).getFuncBody().getAssignExprList().size() == 0) {
                return findEvaluable(((QuplaFuncStmt) element).getFuncBody().getReturnExpr());
            }
        }
        return element;
    }

    public static boolean isEvaluable(PsiElement element) {
        return (element instanceof QuplaFuncExpr) || (element instanceof QuplaConcatExpr)
                || (element instanceof QuplaAssignExpr) || (element instanceof QuplaLutExpr)
                || (element instanceof QuplaMergeExpr) || (element instanceof QuplaSliceExpr)
                || (element instanceof QuplaStateExpr) || (element instanceof QuplaReturnExpr) || (element instanceof QuplaPostfixExpr);
    }

    public static PsiElement findEvaluableNearElement(PsiElement element) {
        return findEvaluable(QuplaDebuggerUtil.getQuplaPsiElement(element));
    }
}
