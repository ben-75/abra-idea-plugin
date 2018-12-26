package org.abra.ide.tools;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.abra.language.psi.AbraTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuplaBraceMatcher implements PairedBraceMatcher {

    private static final BracePair[] PAIRS = {
            new BracePair(AbraTypes.OPEN_BRACE, AbraTypes.CLOSE_BRACE, true),
            new BracePair(AbraTypes.OPEN_BRACKET, AbraTypes.CLOSE_BRACKET, true),
            new BracePair(AbraTypes.OPEN_PAR, AbraTypes.CLOSE_PAR, true),
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
