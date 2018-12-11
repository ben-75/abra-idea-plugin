package org.abra.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.abra.language.psi.AbraNamedElement;
import org.jetbrains.annotations.NotNull;

public abstract class AbraNamedElementImpl extends ASTWrapperPsiElement implements AbraNamedElement {

    public AbraNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }

}