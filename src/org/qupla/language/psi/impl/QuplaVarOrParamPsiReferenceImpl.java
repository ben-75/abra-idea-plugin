package org.qupla.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import org.qupla.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class QuplaVarOrParamPsiReferenceImpl extends PsiReferenceBase implements PsiReference {


    public QuplaVarOrParamPsiReferenceImpl(@NotNull PsiElement element) {
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
        QuplaParamOrVarNameRef ref = QuplaElementFactory.createAbraVarOrParamNameRef(myElement.getProject(), newElementName);
        ASTNode newKeyNode = ref.getFirstChild().getNode();
        myElement.getNode().replaceChild(myElement.getFirstChild().getNode(), newKeyNode);
        return ref;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return resolveLocally();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        PsiElement funcBody = myElement;
        PsiElement myExpression = null;
        while(!(funcBody instanceof QuplaFuncBody)){
            if(funcBody.getParent() instanceof QuplaFuncBody){
                myExpression = funcBody;
            }
            funcBody = funcBody.getParent();
        }
        List<PsiElement> allRefs = new ArrayList<>();

        //look in state vars
        if(((QuplaFuncBody)funcBody).getStateExprList()!=null) {
            for (QuplaStateExpr stateExpr : ((QuplaFuncBody) funcBody).getStateExprList()){
                if(stateExpr.equals(myExpression))break;
                allRefs.add(stateExpr.getVarName());
            }
        }

        //look in local vars
        if(((QuplaFuncBody)funcBody).getAssignExprList()!=null) {
            for (QuplaAssignExpr assignExpr : ((QuplaFuncBody) funcBody).getAssignExprList()){
                if(assignExpr.equals(myExpression))break;
                allRefs.add(assignExpr.getVarName());

            }
        }
        //look in function parameters
        for(QuplaFuncParameter p:((QuplaFuncBody)funcBody).getFuncSignature().getFuncParameterList()){
            allRefs.add(p.getParamName());
        }
        return allRefs.toArray();
    }

    private PsiElement resolveLocally(){
        PsiElement funcBody = myElement;
        PsiElement myExpression = null;
        while(!(funcBody instanceof QuplaFuncBody)){
            if(funcBody.getParent() instanceof QuplaFuncBody){
                myExpression = funcBody;
            }
            funcBody = funcBody.getParent();
        }

        //look in state vars
        if(((QuplaFuncBody)funcBody).getStateExprList()!=null) {
            for (QuplaStateExpr stateExpr : ((QuplaFuncBody) funcBody).getStateExprList()){
                if(stateExpr.equals(myExpression))break;
                if(stateExpr.getVarName().getText().equals(myElement.getText())){
                    return stateExpr.getVarName();
                }
            }
        }

        //look in local vars
        if(((QuplaFuncBody)funcBody).getAssignExprList()!=null) {
            for (QuplaAssignExpr assignExpr : ((QuplaFuncBody) funcBody).getAssignExprList()){
                if(assignExpr.equals(myExpression))break;
                if(assignExpr.getVarName().getText().equals(myElement.getText())){
                    return assignExpr.getVarName();
                }
            }
        }
        //look in function parameters
        for(QuplaFuncParameter p:((QuplaFuncBody)funcBody).getFuncSignature().getFuncParameterList()){
            if(p.getParamName().getText().equals(myElement.getText())){
                return p.getParamName();
            }
        }

        //look in template types
        if(funcBody.getParent().getParent() instanceof QuplaTemplateStmt){
            QuplaTemplateStmt templateStmt = (QuplaTemplateStmt)funcBody.getParent().getParent();
            for(QuplaPlaceHolderTypeName phn:templateStmt.getPlaceHolderTypeNameList()){
                if(phn.getText().equals(myElement.getText())){
                    return phn;
                }
            }
            for(QuplaTypeStmt phn:templateStmt.getTypeStmtList()){
                if(phn.getTypeName().getText().equals(myElement.getText())){
                    return phn;
                }
            }
        }
        return null;
    }

}
