package org.qupla.ide.highlighter;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.qupla.ide.ui.action.RefreshImportsIntentionAction;
import org.qupla.language.module.QuplaModuleManager;
import org.qupla.language.psi.QuplaImportStmt;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ImportAnnotator  implements Annotator {

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if(element instanceof QuplaImportStmt){
            if(element.getProject().getComponent(QuplaModuleManager.class).getModule(((QuplaImportStmt)element).getModuleName().getText())==null){
                int endOffset = element.getTextRange().getEndOffset();
                TextRange range = new TextRange(element.getTextRange().getStartOffset(), endOffset);
                holder.createErrorAnnotation(range, "Cannot resolve import").registerFix(new RefreshImportsIntentionAction());
            }
        }
    }
}
