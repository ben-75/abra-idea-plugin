package org.qupla.ide.tools;

import com.intellij.codeInsight.editorActions.wordSelection.AbstractWordSelectioner;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.NotNull;
import org.qupla.language.psi.QuplaTypes;

import java.util.Collections;
import java.util.List;

public class TritLiteralSelectioner extends AbstractWordSelectioner {

    public TritLiteralSelectioner() {
        super();
    }

    @Override
    public boolean canSelect(@NotNull PsiElement e) {
        return e instanceof LeafPsiElement && ((LeafPsiElement) e).getElementType() == QuplaTypes.TRIT_LIT;
    }

    @Override
    public List<TextRange> select(@NotNull PsiElement e, @NotNull CharSequence editorText, int cursorOffset, @NotNull Editor editor) {
        return Collections.singletonList(e.getTextRange());
    }

    @Override
    public int getMinimalTextRangeLength(@NotNull PsiElement element, @NotNull CharSequence text, int cursorOffset) {
        return element.getTextRange().getLength();
    }
}
