package org.qupla.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.qupla.language.psi.QuplaNamedElement;
import org.jetbrains.annotations.NotNull;

public abstract class QuplaNamedElementImpl extends ASTWrapperPsiElement implements QuplaNamedElement {
    public QuplaNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}