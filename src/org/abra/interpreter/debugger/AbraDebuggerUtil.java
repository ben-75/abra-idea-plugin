package org.abra.interpreter.debugger;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import org.abra.language.psi.*;

public class AbraDebuggerUtil {


    public static boolean isEvaluable(PsiElement element) {
        return (element instanceof AbraFuncExpr) || (element instanceof AbraConcatExpr)
                || (element instanceof AbraAssignExpr) || (element instanceof AbraLutExpr)
                || (element instanceof AbraMergeExpr) || (element instanceof AbraSliceExpr)
                || (element instanceof AbraStateExpr) || (element instanceof AbraReturnExpr) || (element instanceof AbraPostfixExpr);
    }

    public static PsiElement findEvaluable(PsiElement element) {
        if (element instanceof AbraReturnExpr) {
            element = ((AbraReturnExpr) element).getCondExpr();
            while (element.getChildren().length == 1 && AbraDebuggerUtil.isEvaluable(element.getChildren()[0])) {
                element = element.getChildren()[0];
            }
        }
        if (element instanceof AbraFuncStmt) {
            if (((AbraFuncStmt) element).getFuncBody().getAssignExprList().size() == 0) {
                return findEvaluable(((AbraFuncStmt) element).getFuncBody().getReturnExpr());
            }
        }
        return element;
    }


    public static PsiElement getAbraPsiElement(PsiFile file, int offset) {
        return getAbraPsiElement(file.findElementAt(offset));
    }

    public static PsiElement getAbraPsiElement(PsiElement element) {
        while (element instanceof PsiWhiteSpace) element = element.getNextSibling();
        if (element == null) return null;
        if (element instanceof AbraFuncBody && ((AbraFuncBody) element).getCondExpr() != null)
            return getAbraPsiElement(((AbraFuncBody) element).getCondExpr());
        while (!(element instanceof AbraFuncExpr) && !(element instanceof AbraConcatExpr)
                && !(element instanceof AbraAssignExpr) && !(element instanceof AbraLutExpr)
                && !(element instanceof AbraMergeExpr) && !(element instanceof AbraSliceExpr)
                && !(element instanceof AbraStateExpr) && !(element instanceof AbraReturnExpr) && !(element instanceof AbraFile)
                && !(element instanceof AbraFuncStmt)) {
            element = element.getParent();
        }
        if (element instanceof AbraFile)
            return null;
        if (element instanceof AbraFuncStmt) {
            if (((AbraFuncStmt) element).getFuncBody().getCondExpr() != null) {
                AbraCondExpr condExpr = ((AbraFuncStmt) element).getFuncBody().getCondExpr();
                if (condExpr.getChildren().length == 1) return getAbraPsiElement(condExpr.getFirstChild());
                return condExpr;
            }
        }
        return element;
    }
}
