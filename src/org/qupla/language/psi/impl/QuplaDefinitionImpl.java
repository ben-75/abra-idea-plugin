package org.qupla.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.qupla.language.psi.QuplaDefinition;
import org.jetbrains.annotations.NotNull;

public abstract class QuplaDefinitionImpl extends ASTWrapperPsiElement implements QuplaDefinition {

    public QuplaDefinitionImpl(@NotNull ASTNode node) {
        super(node);
    }
}
