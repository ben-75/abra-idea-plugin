package org.abra.language.psi;

import com.intellij.psi.tree.IElementType;
import org.abra.language.AbraLanguage;
import org.jetbrains.annotations.*;

public class AbraElementType extends IElementType {

    public AbraElementType(@NotNull @NonNls String debugName) {
        super(debugName, AbraLanguage.INSTANCE);
    }

}
