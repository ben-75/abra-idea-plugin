package org.abra.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.abra.language.psi.AbraDefinition;
import org.jetbrains.annotations.NotNull;

public abstract class AbraDefinitionImpl extends ASTWrapperPsiElement implements AbraDefinition {

    public AbraDefinitionImpl(@NotNull ASTNode node) {
        super(node);
    }
}
