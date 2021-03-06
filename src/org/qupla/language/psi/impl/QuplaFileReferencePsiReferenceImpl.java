package org.qupla.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import org.qupla.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuplaFileReferencePsiReferenceImpl extends PsiReferenceBase implements PsiReference {

    private final VirtualFile virtualFile;

    public QuplaFileReferencePsiReferenceImpl(QuplaImportStmt quplaImportStmt, VirtualFile virtualFile) {
        super(quplaImportStmt, false);
        this.virtualFile = virtualFile;
    }
    @NotNull
    @Override
    public TextRange getRangeInElement() {
        final int parent = 0;
        return new TextRange(parent, myElement.getTextLength() + parent);
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        QuplaModuleName moduleName = QuplaElementFactory.createQuplaModuleName(myElement.getProject(), newElementName);
        ASTNode newKeyNode = moduleName.getFirstChild().getNode();
        myElement.getNode().replaceChild(myElement.getLastChild().getNode(), newKeyNode);
        return moduleName;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return PsiManager.getInstance(myElement.getProject()).findFile(virtualFile);
    }


    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }


}
