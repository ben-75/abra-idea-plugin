package org.qupla.language.psi;

import com.intellij.psi.tree.IElementType;
import org.qupla.language.QuplaLanguage;
import org.jetbrains.annotations.*;

public class QuplaElementType extends IElementType {
    public QuplaElementType(@NotNull @NonNls String debugName) {
        super(debugName, QuplaLanguage.INSTANCE);
    }
}
