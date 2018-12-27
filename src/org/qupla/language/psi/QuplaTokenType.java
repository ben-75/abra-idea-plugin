package org.qupla.language.psi;

import com.intellij.psi.tree.IElementType;
import org.qupla.language.QuplaLanguage;
import org.jetbrains.annotations.*;

public class QuplaTokenType extends IElementType {
    public QuplaTokenType(@NotNull @NonNls String debugName) {
        super(debugName, QuplaLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "QuplaTokenType." + super.toString();
    }
}