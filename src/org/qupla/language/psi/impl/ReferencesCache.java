package org.qupla.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.qupla.language.psi.QuplaImportStmt;
import org.qupla.language.psi.QuplaPsiImplUtil;
import org.jetbrains.annotations.NotNull;

@Deprecated
public abstract class ReferencesCache extends ASTWrapperPsiElement implements QuplaImportStmt {

    public ReferencesCache(@NotNull ASTNode node) {
        super(node);
    }

    private final Object stateLock = new Object();
    private PsiReference[] references;
    @NotNull
    public PsiReference[] getReferences() {
        if(references==null) {
            synchronized (stateLock){
                references = QuplaPsiImplUtil.getReferences(this);
            }
        }
        return references;
//    return QuplaPsiImplUtil.getReferences(this);
    }


}
