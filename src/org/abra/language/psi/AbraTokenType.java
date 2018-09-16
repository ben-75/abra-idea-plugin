package org.abra.language.psi;

import com.intellij.psi.tree.IElementType;
import org.abra.language.AbraLanguage;
import org.jetbrains.annotations.*;

public class AbraTokenType extends IElementType {
    public AbraTokenType(@NotNull @NonNls String debugName) {
        super(debugName, AbraLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "AbraTokenType." + super.toString();
    }
}