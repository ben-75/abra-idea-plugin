package org.abra.language;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.abra.language.AbraSyntaxHighlighter;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class DeclarationAnnotator  implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof AbraFuncName) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            annotation.setTextAttributes(AbraSyntaxHighlighter.FCT_DECLARATION);
        } else if (element instanceof AbraTypeName) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            if(element.getParent().getParent().getParent() instanceof AbraFieldName){
                annotation.setTextAttributes(AbraSyntaxHighlighter.FIELD_DECLARATION);
            }else {
                annotation.setTextAttributes(AbraSyntaxHighlighter.TYPE_DECLARATION);
            }
        } else if (element instanceof AbraLutName) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            annotation.setTextAttributes(AbraSyntaxHighlighter.LUT_DECLARATION);
        } else if (element instanceof AbraTypeSize) {
//            TextRange range = new TextRange(element.getTextRange().getStartOffset()+1, element.getTextRange().getEndOffset()-1);
//            Annotation annotation = holder.createInfoAnnotation(range,
//                    null);
//            annotation.setTextAttributes(AbraSyntaxHighlighter.SIZE_DEF);
        }
    }
}