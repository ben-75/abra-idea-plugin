package org.abra.language;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;

public class MultiDeclarationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
//        AbraFuncNameRef functionReference = null;
//        if (element instanceof AbraFuncStmt) {
//            functionReference = AbraElementFactory.createAbraFunctionReference(element.getProject(),((AbraFuncStmt)element).getFunctionIdentifier().getText());
//        }else if(element instanceof AbraFunctionInstanciation){
//            functionReference = AbraElementFactory.createAbraFunctionReference(element.getProject(),((AbraFunctionInstanciation)element).getExpandedName());
//        }
//        if(functionReference!=null){
//            AbraFunctionReferencePsiReferenceImpl dummy = new AbraFunctionReferencePsiReferenceImpl(functionReference);
//            PsiElement resolved = dummy.resolveFunctionReference(element.getContainingFile(), true);
//            if(resolved!=null){
//                if (element instanceof AbraFuncStmt) {
//                    if(resolved!=((AbraFuncStmt)element).getFunctionIdentifier()){
//                        TextRange range = new TextRange(element.getTextRange().getStartOffset(),element.getTextRange().getEndOffset());
//                        holder.createErrorAnnotation(range, "Duplicate declaration");
//                    }
//                }else if(element instanceof AbraFunctionInstanciation){
//                    if(resolved!=element){
//                        TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
//                        holder.createErrorAnnotation(range, "Duplicate declaration");
//                    }
//                }
//            }
//        }
    }
}
