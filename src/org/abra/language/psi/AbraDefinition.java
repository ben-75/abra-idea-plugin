package org.abra.language.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import org.jetbrains.annotations.NotNull;

public interface AbraDefinition extends NavigatablePsiElement {

    @NotNull
    @Override
    ItemPresentation getPresentation();
}
