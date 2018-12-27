package org.qupla.ide.highlighter;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.qupla.language.psi.*;
import org.jetbrains.annotations.NotNull;

public class DeclarationAnnotator  implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {

        if (element instanceof QuplaTypeName) {
            if(element.getParent().getParent().getParent() instanceof QuplaFieldName){
                highlight(element,holder, QuplaSyntaxHighlighter.QUPLA_FIELD_DECLARATION);
            }else {
                highlight(element,holder, QuplaSyntaxHighlighter.QUPLA_TYPE_DECLARATION);
            }
        } else if (element instanceof QuplaFuncName || element instanceof QuplaFuncNameRef) {
            highlight(element,holder, QuplaSyntaxHighlighter.QUPLA_FCT_DECLARATION);
        } else if (element instanceof QuplaVarName ||
                   element instanceof QuplaParamName ||
                   element instanceof QuplaParamOrVarNameRef ||
                  (element instanceof QuplaLutOrParamOrVarNameRef && !(element.getReference().resolve() instanceof QuplaLutName)) ) {
            if((element instanceof QuplaVarName && (element.getParent() instanceof QuplaStateExpr || element.getReference().resolve()!=null)) ||
                    (element instanceof QuplaParamOrVarNameRef && element.getReference().resolve()!=null && element.getReference().resolve().getParent() instanceof QuplaStateExpr)){
                highlight(element,holder, QuplaSyntaxHighlighter.QUPLA_STATE_VAR_REFERENCE);
            }else {
                highlight(element,holder, QuplaSyntaxHighlighter.QUPLA_LOCAL_VAR);
            }
        } else if (element instanceof QuplaLutName || element instanceof QuplaLutNameRef ||
                (element instanceof QuplaLutOrParamOrVarNameRef && (element.getReference().resolve() instanceof QuplaLutName)) ) {
            highlight(element,holder, QuplaSyntaxHighlighter.QUPLA_LUT_DECLARATION);
        } else if (element instanceof QuplaFieldName ||
                (element instanceof QuplaFieldNameRef) ) {
            highlight(element,holder, QuplaSyntaxHighlighter.QUPLA_FIELD_DECLARATION);
        } else if (element instanceof QuplaTemplateName ||
                (element instanceof QuplaTemplateNameRef) ) {
            highlight(element,holder, QuplaSyntaxHighlighter.QUPLA_TEMPLATE_DECLARATION);
        }  else if (element instanceof QuplaEnvironmentName) {
            highlight(element,holder, QuplaSyntaxHighlighter.ENV_NAME);
        } else if (element instanceof QuplaEnvValue) {
            highlight(element,holder, QuplaSyntaxHighlighter.ENV_VALUE);
        } else if (element instanceof QuplaTypeNameRef || (element instanceof QuplaTypeOrPlaceHolderNameRef && element.getReference().resolve() instanceof QuplaTypeName)) {
            highlight(element,holder, QuplaSyntaxHighlighter.QUPLA_TYPE_REFERENCE);
        } else if (element.getParent() instanceof QuplaTritList && (element.getNode().getElementType()== QuplaTypes.MINUS ||
                element.getNode().getElementType()== QuplaTypes.ZERO ||
                element.getNode().getElementType()== QuplaTypes.ONE)){
            highlight(element,holder, QuplaSyntaxHighlighter.QUPLA_TRIT);
        }


    }

    private void highlight(final PsiElement element, @NotNull AnnotationHolder holder, TextAttributesKey attr){
        TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
        Annotation annotation = holder.createInfoAnnotation(range,null);
        annotation.setTextAttributes(attr);
    }
}

