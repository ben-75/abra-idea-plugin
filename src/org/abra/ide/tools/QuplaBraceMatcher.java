package org.abra.ide.tools;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.abra.language.psi.QuplaTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuplaBraceMatcher implements PairedBraceMatcher {

    private static final BracePair[] PAIRS = {
            new BracePair(QuplaTypes.OPEN_BRACE, QuplaTypes.CLOSE_BRACE, true),
            new BracePair(QuplaTypes.OPEN_BRACKET, QuplaTypes.CLOSE_BRACKET, true),
            new BracePair(QuplaTypes.OPEN_PAR, QuplaTypes.CLOSE_PAR, true),
    };

    @NotNull
    @Override
    public BracePair[] getPairs() {
        return PAIRS;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
