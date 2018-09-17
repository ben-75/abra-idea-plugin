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

public class AbraTypePsiReferenceImpl  extends PsiReferenceBase implements PsiReference {


    public AbraTypePsiReferenceImpl(@NotNull PsiElement element) {
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
        AbraTypeNameRef ref = AbraElementFactory.createAbraTypeNameRef(myElement.getProject(), newElementName);
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

    private AbraTypeName resolveInFile(PsiFile aFile){
        for(ASTNode stmt:aFile.getNode().getChildren(TokenSet.create(AbraTypes.TYPE_STMT))){
            if(((AbraTypeStmt)stmt.getPsi()).getTypeName().getText().equals(myElement.getText())){
                return ((AbraTypeStmt)stmt.getPsi()).getTypeName();
            }
        }
        return null;
    }

    private AbraTypeName resolveFromImports(PsiFile startingFile){
        for(ASTNode stmt:startingFile.getNode().getChildren(TokenSet.create(AbraTypes.IMPORT_STMT))){
            PsiReference[] importedFiles = AbraPsiImplUtil.getReferences((AbraImportStmt) stmt.getPsi());
            if(importedFiles!=null) {
                for (PsiReference psiRef : importedFiles) {
                    PsiElement anAbraFile = psiRef.resolve();
                    if(anAbraFile!=null){
                        AbraTypeName resolved = resolveInFile((PsiFile) anAbraFile);
                        if(resolved!=null)return resolved;
                    }
                }
            }
        }
        return null;
    }
}
