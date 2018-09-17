package org.abra.language.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import org.abra.language.psi.AbraImportStmt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbraFileReferencePsiReferenceImpl extends PsiReferenceBase implements PsiReference {

    private AbraImportStmt importFile;
    private VirtualFile virtualFile;

    public AbraFileReferencePsiReferenceImpl(AbraImportStmt abraImportFile, VirtualFile virtualFile) {
        super(abraImportFile, false);
        this.importFile = abraImportFile;
        this.virtualFile = virtualFile;
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
        return PsiManager.getInstance(importFile.getProject()).findFile(virtualFile);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
