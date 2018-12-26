package org.abra.ide.highlighter;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;

public class DeclarationAnnotator  implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {

        if (element instanceof AbraTypeName) {
            if(element.getParent().getParent().getParent() instanceof AbraFieldName){
                highlight(element,holder, QuplaSyntaxHighlighter.ABRA_FIELD_DECLARATION);
            }else {
                highlight(element,holder, QuplaSyntaxHighlighter.ABRA_TYPE_DECLARATION);
            }
        } else if (element instanceof AbraFuncName || element instanceof AbraFuncNameRef) {
            highlight(element,holder, QuplaSyntaxHighlighter.ABRA_FCT_DECLARATION);
        } else if (element instanceof AbraVarName ||
                   element instanceof AbraParamName ||
                   element instanceof AbraParamOrVarNameRef ||
                  (element instanceof AbraLutOrParamOrVarNameRef && !(element.getReference().resolve() instanceof AbraLutName)) ) {
            if((element instanceof AbraVarName && (element.getParent() instanceof AbraStateExpr || element.getReference().resolve()!=null)) ||
                    (element instanceof AbraParamOrVarNameRef && element.getReference().resolve()!=null && element.getReference().resolve().getParent() instanceof AbraStateExpr)){
                highlight(element,holder, QuplaSyntaxHighlighter.ABRA_STATE_VAR_REFERENCE);
            }else {
                highlight(element,holder, QuplaSyntaxHighlighter.ABRA_LOCAL_VAR);
            }
        } else if (element instanceof AbraLutName || element instanceof AbraLutNameRef ||
                (element instanceof AbraLutOrParamOrVarNameRef && (element.getReference().resolve() instanceof AbraLutName)) ) {
            highlight(element,holder, QuplaSyntaxHighlighter.ABRA_LUT_DECLARATION);
        } else if (element instanceof AbraFieldName ||
                (element instanceof AbraFieldNameRef) ) {
            highlight(element,holder, QuplaSyntaxHighlighter.ABRA_FIELD_DECLARATION);
        } else if (element instanceof AbraTemplateName ||
                (element instanceof AbraTemplateNameRef) ) {
            highlight(element,holder, QuplaSyntaxHighlighter.ABRA_TEMPLATE_DECLARATION);
        }  else if (element instanceof AbraEnvironmentName ) {
            highlight(element,holder, QuplaSyntaxHighlighter.ENV_NAME);
        } else if (element instanceof AbraEnvValue ) {
            highlight(element,holder, QuplaSyntaxHighlighter.ENV_VALUE);
        } else if (element instanceof AbraTypeNameRef || (element instanceof AbraTypeOrPlaceHolderNameRef && element.getReference().resolve() instanceof AbraTypeName)) {
            highlight(element,holder, QuplaSyntaxHighlighter.ABRA_TYPE_REFERENCE);
        } else if (element.getParent() instanceof AbraTritList && (element.getNode().getElementType()== AbraTypes.MINUS ||
                element.getNode().getElementType()== AbraTypes.ZERO ||
                element.getNode().getElementType()== AbraTypes.ONE)){
            highlight(element,holder, QuplaSyntaxHighlighter.ABRA_TRIT);
        }


    }

    private void highlight(final PsiElement element, @NotNull AnnotationHolder holder, TextAttributesKey attr){
        TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
        Annotation annotation = holder.createInfoAnnotation(range,null);
        annotation.setTextAttributes(attr);
    }
}

