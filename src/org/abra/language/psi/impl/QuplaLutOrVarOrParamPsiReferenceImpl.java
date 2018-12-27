package org.abra.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuplaLutOrVarOrParamPsiReferenceImpl extends PsiReferenceBase implements PsiReference {


    public QuplaLutOrVarOrParamPsiReferenceImpl(@NotNull QuplaLutOrParamOrVarNameRef element) {
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
        QuplaLutOrParamOrVarNameRef ref = QuplaElementFactory.createAbraLutOrParamOrVarRef(myElement.getProject(), newElementName);
        ASTNode newKeyNode = ref.getFirstChild().getNode();
        myElement.getNode().replaceChild(myElement.getFirstChild().getNode(), newKeyNode);
        return ref;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement resolved = resolveLocally();
        if(resolved!=null)return resolved;
        resolved = resolveInFile(myElement.getContainingFile());
        if(resolved==null){
            resolved = resolveFromImports(myElement.getContainingFile());
        }
        return resolved;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
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
        //look in local vars
        for (QuplaAssignExpr assignExpr : ((QuplaFuncBody) funcBody).getAssignExprList()){
            if(assignExpr.equals(myExpression))break;
            if(assignExpr.getVarName().getText().equals(myElement.getText())){
                return assignExpr.getVarName();
            }
        }

        //look in function parameters
        for(QuplaFuncParameter p:((QuplaFuncBody)funcBody).getFuncSignature().getFuncParameterList()){
            if(p.getParamName().getText().equals(myElement.getText())){
                return p.getParamName();
            }
        }
        return null;
    }


    private QuplaLutName resolveInFile(PsiFile aFile){
        for(ASTNode stmt:aFile.getNode().getChildren(TokenSet.create(QuplaTypes.LUT_STMT))){
            if(((QuplaLutStmt)stmt.getPsi()).getLutName().getText().equals(myElement.getText())){
                return ((QuplaLutStmt)stmt.getPsi()).getLutName();
            }
        }
        return null;
    }

//    private QuplaLutName resolveFromImports(PsiFile startingFile){
//        for(ASTNode stmt:startingFile.getNode().getChildren(TokenSet.create(QuplaTypes.IMPORT_STMT))){
//            PsiReference[] importedFiles = QuplaPsiImplUtil.getReferences((QuplaImportStmt) stmt.getPsi());
//            if(importedFiles!=null) {
//                for (PsiReference psiRef : importedFiles) {
//                    PsiElement anAbraFile = psiRef.resolve();
//                    if(anAbraFile!=null){
//                        QuplaLutName resolved = resolveInFile((PsiFile) anAbraFile);
//                        if(resolved!=null)return resolved;
//                    }
//                }
//            }
//        }
//        return null;
//    }

    private PsiElement resolveFromImports(PsiFile startingFile){
        List<QuplaFile> importsTree = (((QuplaFile)startingFile).getImportTree());
        return resolveFromImportTree(importsTree);
    }

    public PsiElement resolveFromImportTree(List<QuplaFile> scope){
        if(scope.size()>0){
            for(PsiFile f:scope){
                PsiElement resolved = resolveInFile(f);
                if(resolved!=null)return resolved;
            }
        }
        return null;
    }
}
