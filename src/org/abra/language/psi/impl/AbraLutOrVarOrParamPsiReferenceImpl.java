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

public class AbraLutOrVarOrParamPsiReferenceImpl extends PsiReferenceBase implements PsiReference {


    public AbraLutOrVarOrParamPsiReferenceImpl(@NotNull AbraLutOrParamOrVarNameRef element) {
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
        AbraLutOrParamOrVarNameRef ref = AbraElementFactory.createAbraLutOrParamOrVarRef(myElement.getProject(), newElementName);
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
        while(!(funcBody instanceof AbraFuncBody)){
            if(funcBody.getParent() instanceof AbraFuncBody){
                myExpression = funcBody;
            }
            funcBody = funcBody.getParent();
        }
        //look in local vars
        for (AbraAssignExpr assignExpr : ((AbraFuncBody) funcBody).getAssignExprList()){
            if(assignExpr.equals(myExpression))break;
            if(assignExpr.getVarName().getText().equals(myElement.getText())){
                return assignExpr.getVarName();
            }
        }

        //look in function parameters
        for(AbraFuncParameter p:((AbraFuncBody)funcBody).getFuncSignature().getFuncParameterList()){
            if(p.getParamName().getText().equals(myElement.getText())){
                return p.getParamName();
            }
        }
        return null;
    }


    private AbraLutName resolveInFile(PsiFile aFile){
        for(ASTNode stmt:aFile.getNode().getChildren(TokenSet.create(AbraTypes.LUT_STMT))){
            if(((AbraLutStmt)stmt.getPsi()).getLutName().getText().equals(myElement.getText())){
                return ((AbraLutStmt)stmt.getPsi()).getLutName();
            }
        }
        return null;
    }

//    private AbraLutName resolveFromImports(PsiFile startingFile){
//        for(ASTNode stmt:startingFile.getNode().getChildren(TokenSet.create(AbraTypes.IMPORT_STMT))){
//            PsiReference[] importedFiles = AbraPsiImplUtil.getReferences((AbraImportStmt) stmt.getPsi());
//            if(importedFiles!=null) {
//                for (PsiReference psiRef : importedFiles) {
//                    PsiElement anAbraFile = psiRef.resolve();
//                    if(anAbraFile!=null){
//                        AbraLutName resolved = resolveInFile((PsiFile) anAbraFile);
//                        if(resolved!=null)return resolved;
//                    }
//                }
//            }
//        }
//        return null;
//    }

    private PsiElement resolveFromImports(PsiFile startingFile){
        List<AbraFile> importsTree = (((AbraFile)startingFile).getImportTree());
        return resolveFromImportTree(importsTree);
    }

    public PsiElement resolveFromImportTree(List<AbraFile> scope){
        if(scope.size()>0){
            for(PsiFile f:scope){
                PsiElement resolved = resolveInFile(f);
                if(resolved!=null)return resolved;
            }
        }
        return null;
    }
}
