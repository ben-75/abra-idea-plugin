package org.abra.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class QuplaStateVarPsiReferenceImpl extends PsiReferenceBase implements PsiReference {


    public QuplaStateVarPsiReferenceImpl(@NotNull PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
        final int parent = 0;
        return new TextRange(parent, myElement.getTextLength() + parent);
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        QuplaVarName stateVarRef = QuplaElementFactory.createAbraVarName(myElement.getProject(), newElementName);
        ASTNode newKeyNode = stateVarRef.getFirstChild().getNode();
        myElement.getNode().replaceChild(myElement.getFirstChild().getNode(), newKeyNode);
        return stateVarRef;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement funcBody = myElement;
        while(!(funcBody instanceof QuplaFuncBody)){
            funcBody = funcBody.getParent();
        }
        QuplaFuncBody body = (QuplaFuncBody)funcBody;
        for(QuplaStateExpr stateExpr:body.getStateExprList()){
            if(stateExpr.getVarName()!=myElement && stateExpr.getVarName().getText().equals(myElement.getText())){
                return stateExpr.getVarName();
            }
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

}
