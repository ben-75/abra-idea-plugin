package org.abra.language;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class LutValidationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
//        if (element instanceof AbraLutStmt) {
//            AbraLutStmt lutStmt = (AbraLutStmt) element;
//            HashSet entrySet = new HashSet(lutStmt.getTritListList().size());
//            if (lutStmt.getTritListList().size() > 0) {
//                int left_size = lutStmt.getTritListList().get(0).getTritList().size();
//                int right_size = lutStmt.getTritListList().get(1).getTritList().size();
//                for (AbraTritList entry : lutStmt.getTritListList()) {
//                    AbraTritlist lhs = entry.getTritList().get(0);
//                    AbraTritlist rhs = entry.getTritList().get(1);
//                    String compact = lhs.getText().replaceAll(" ", "");
//                    if (!entrySet.add(compact)) {
//                        TextRange range = new TextRange(lhs.getTextRange().getStartOffset(),lhs.getTextRange().getEndOffset());
//                        holder.createErrorAnnotation(range, "Duplicate entry");
//                    }
//                    if (lhs.getLength() != left_size) {
//                        TextRange range = new TextRange(lhs.getTextRange().getStartOffset(),lhs.getTextRange().getEndOffset());
//                        holder.createErrorAnnotation(range, "Unexpected trit count");
//                    }
//                    if (rhs.getLength() != right_size) {
//                        TextRange range = new TextRange(rhs.getTextRange().getStartOffset(),rhs.getTextRange().getEndOffset());
//                        holder.createErrorAnnotation(range, "Unexpected trit count");
//                    }
//                }
//            }
//        }
    }
}