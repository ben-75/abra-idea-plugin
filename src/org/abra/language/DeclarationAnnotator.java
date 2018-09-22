package org.abra.language;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.abra.language.AbraSyntaxHighlighter;
import org.abra.language.psi.*;
import org.abra.language.psi.impl.AbraLutOrVarOrParamPsiReferenceImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class DeclarationAnnotator  implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof AbraTypeName) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            if(element.getParent().getParent().getParent() instanceof AbraFieldName){
                annotation.setTextAttributes(AbraSyntaxHighlighter.ABRA_FIELD_DECLARATION);
            }else {
                annotation.setTextAttributes(AbraSyntaxHighlighter.ABRA_TYPE_DECLARATION);
            }
        } else if (element instanceof AbraFuncName || element instanceof AbraFuncNameRef) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            annotation.setTextAttributes(AbraSyntaxHighlighter.ABRA_FCT_DECLARATION);
        } else if (element instanceof AbraVarName ||
                   element instanceof AbraParamName ||
                   element instanceof AbraParamOrVarNameRef ||
                  (element instanceof AbraLutOrParamOrVarNameRef && !(element.getReference().resolve() instanceof AbraLutName)) ) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            annotation.setTextAttributes(AbraSyntaxHighlighter.ABRA_LOCAL_VAR);
        } else if (element instanceof AbraLutName ||
                (element instanceof AbraLutOrParamOrVarNameRef && (element.getReference().resolve() instanceof AbraLutName)) ) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            annotation.setTextAttributes(AbraSyntaxHighlighter.ABRA_LUT_DECLARATION);
        } else if (element instanceof AbraFieldName ||
                (element instanceof AbraFieldNameRef) ) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            annotation.setTextAttributes(AbraSyntaxHighlighter.ABRA_FIELD_DECLARATION);
        } else if (element instanceof AbraTemplateName ||
                (element instanceof AbraTemplateNameRef) ) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            annotation.setTextAttributes(AbraSyntaxHighlighter.ABRA_TEMPLATE_DECLARATION);
        } else if (element instanceof AbraTypeSize) {
//            TextRange range = new TextRange(element.getTextRange().getStartOffset()+1, element.getTextRange().getEndOffset()-1);
//            Annotation annotation = holder.createInfoAnnotation(range,
//                    null);
//            annotation.setTextAttributes(AbraSyntaxHighlighter.SIZE_DEF);
        }
    }
}