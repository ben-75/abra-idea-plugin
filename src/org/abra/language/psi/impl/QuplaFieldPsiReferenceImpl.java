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

public class QuplaFieldPsiReferenceImpl extends PsiReferenceBase implements PsiReference {


    public QuplaFieldPsiReferenceImpl(@NotNull PsiElement element) {
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
        QuplaFieldNameRef ref = QuplaElementFactory.createAbraFieldNameReference(myElement.getProject(), newElementName);
        ASTNode newKeyNode = ref.getFirstChild().getNode();
        myElement.getNode().replaceChild(myElement.getFirstChild().getNode(), newKeyNode);
        return ref;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement resolved = resolveInFile(myElement.getContainingFile());
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

    private PsiElement resolveInFile(PsiFile aFile){
        PsiElement resolved = resolveInTemplate();
        if(resolved!=null)return resolved;
        for(ASTNode stmt:aFile.getNode().getChildren(TokenSet.create(QuplaTypes.TYPE_STMT))){
            for(QuplaFieldSpec fieldSpec:((QuplaTypeStmt)stmt.getPsi()).getFieldSpecList()){
                if(fieldSpec.getFieldName().getText().equals(myElement.getText())){
                    return fieldSpec.getFieldName();
                }
            }
        }
        return null;
    }

    private PsiElement resolveInTemplate(){
        PsiElement templateStmt = myElement;
        while(!(templateStmt instanceof QuplaFile) && !(templateStmt instanceof QuplaTemplateStmt)){
            templateStmt = templateStmt.getParent();
        }
        if(templateStmt instanceof QuplaTemplateStmt){
            for(QuplaTypeStmt localTypeStmt:((QuplaTemplateStmt)templateStmt).getTypeStmtList()){
                if(localTypeStmt.getFieldSpecList().size()>0){
                    for(QuplaFieldSpec fs:localTypeStmt.getFieldSpecList()){
                        if(fs.getFieldName().getText().equals(myElement.getText()))return fs.getFieldName();
                    }
                }
            }
        }
        return null;
    }

//    private PsiElement resolveFromImports(PsiFile startingFile){
//        for(ASTNode stmt:startingFile.getNode().getChildren(TokenSet.create(QuplaTypes.IMPORT_STMT))){
//            PsiReference[] importedFiles = QuplaPsiImplUtil.getReferences((QuplaImportStmt) stmt.getPsi());
//            if(importedFiles!=null) {
//                for (PsiReference psiRef : importedFiles) {
//                    PsiElement anAbraFile = psiRef.resolve();
//                    if(anAbraFile!=null){
//                        PsiElement resolved = resolveInFile((PsiFile) anAbraFile);
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
