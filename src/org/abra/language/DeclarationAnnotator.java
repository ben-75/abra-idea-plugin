package org.abra.language;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.abra.language.AbraSyntaxHighlighter;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;

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
            if((element instanceof AbraVarName && (element.getParent() instanceof AbraStateExpr || element.getReference().resolve()!=null)) ||
                    (element instanceof AbraParamOrVarNameRef && element.getReference().resolve()!=null && element.getReference().resolve().getParent() instanceof AbraStateExpr)){
                annotation.setTextAttributes(AbraSyntaxHighlighter.ABRA_STATE_VAR_REFERENCE);
            }else {
                annotation.setTextAttributes(AbraSyntaxHighlighter.ABRA_LOCAL_VAR);
            }
        } else if (element instanceof AbraLutName || element instanceof AbraLutNameRef ||
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
        }  else if (element instanceof AbraEnvironmentName ) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            annotation.setTextAttributes(AbraSyntaxHighlighter.ENV_NAME);
        } else if (element instanceof AbraEnvValue ) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            annotation.setTextAttributes(AbraSyntaxHighlighter.ENV_VALUE);
        } else if (element instanceof AbraTypeNameRef || (element instanceof AbraTypeOrPlaceHolderNameRef && element.getReference().resolve() instanceof AbraTypeName)) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            annotation.setTextAttributes(AbraSyntaxHighlighter.ABRA_TYPE_REFERENCE);
        } else if (element.getParent() instanceof AbraTritList && (element.getNode().getElementType()== AbraTypes.MINUS ||
                element.getNode().getElementType()== AbraTypes.ZERO ||
                element.getNode().getElementType()== AbraTypes.ONE)){

            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
            Annotation annotation = holder.createInfoAnnotation(range,
                    null);
            annotation.setTextAttributes(AbraSyntaxHighlighter.ABRA_TRIT);
        }
    }
}