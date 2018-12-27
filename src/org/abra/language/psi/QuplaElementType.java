package org.abra.language.psi;

import com.intellij.psi.tree.IElementType;
import org.abra.language.QuplaLanguage;
import org.jetbrains.annotations.*;

public class QuplaElementType extends IElementType {
    public QuplaElementType(@NotNull @NonNls String debugName) {
        super(debugName, QuplaLanguage.INSTANCE);
    }
}
