package org.qupla.language.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import org.jetbrains.annotations.NotNull;

public interface QuplaDefinition extends NavigatablePsiElement {

    @NotNull
    @Override
    ItemPresentation getPresentation();
}
