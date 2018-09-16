package org.abra.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.tree.TokenSet;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbraVarOrParamPsiReferenceImpl extends PsiReferenceBase implements PsiReference {


    public AbraVarOrParamPsiReferenceImpl(@NotNull PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
        final int parent = 0;
        return new TextRange(parent, myElement.getTextLength() + parent);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return resolveLocally();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    private PsiElement resolveLocally(){
        PsiElement funcBody = myElement;
        PsiElement myExpression = null;
        while(!(funcBody instanceof AbraFuncBody)){
            if(funcBody.getParent() instanceof AbraFuncBody){
                myExpression = funcBody;
            }
            funcBody = funcBody.getParent();
        }
        //look in local vars
        if(((AbraFuncBody)funcBody).getAssignExprList()!=null) {
            for (AbraAssignExpr assignExpr : ((AbraFuncBody) funcBody).getAssignExprList()){
                if(assignExpr.equals(myExpression))break;
                if(assignExpr.getVarName().getText().equals(myElement.getText())){
                    return assignExpr.getVarName();
                }
            }
        }
        //look in function parameters
        while(!(funcBody instanceof AbraFuncName)){
            funcBody = funcBody.getPrevSibling();
            if(funcBody instanceof AbraFuncParameter){
                AbraFuncParameter p = (AbraFuncParameter)funcBody;
                if(p.getParamName().getText().equals(myElement.getText())){
                    return p.getParamName();
                }
            }
        }
        return null;
    }

}
