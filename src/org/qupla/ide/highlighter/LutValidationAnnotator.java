package org.qupla.ide.highlighter;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.qupla.language.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class LutValidationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof QuplaLutStmt) {
            QuplaLutStmt lutStmt = (QuplaLutStmt) element;
            HashSet entrySet = new HashSet(lutStmt.getLutEntryList().size());
            if (lutStmt.getLutEntryList().size() > 0) {
                int left_size = lutStmt.getLutEntryList().get(0).getInputLength();
                if(left_size>3){
                    TextRange range = new TextRange(lutStmt.getTextRange().getStartOffset(),lutStmt.getTextRange().getEndOffset());
                    holder.createErrorAnnotation(range, "LUT cannot have more than 3 input trits");
                }
                int right_size = lutStmt.getLutEntryList().get(0).getOutputLength();
                for (QuplaLutEntry entry : lutStmt.getLutEntryList()) {
                    QuplaTritList lhs = entry.getTritListList().get(0);
                    QuplaTritList rhs = entry.getTritListList().get(1);
                    String compact = lhs.getText().replaceAll(" ", "");
                    if (!entrySet.add(compact)) {
                        TextRange range = new TextRange(lhs.getTextRange().getStartOffset(),lhs.getTextRange().getEndOffset());
                        holder.createErrorAnnotation(range, "Duplicate entry");
                    }
                    if (lhs.getLength() != left_size) {
                        TextRange range = new TextRange(lhs.getTextRange().getStartOffset(),lhs.getTextRange().getEndOffset());
                        holder.createErrorAnnotation(range, "Unexpected trit count");
                    }
                    if (rhs.getLength() != right_size) {
                        TextRange range = new TextRange(rhs.getTextRange().getStartOffset(),rhs.getTextRange().getEndOffset());
                        holder.createErrorAnnotation(range, "Unexpected trit count");
                    }
                }
            }
        }
    }
}