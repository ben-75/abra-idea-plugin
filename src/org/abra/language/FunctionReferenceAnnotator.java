package org.abra.language;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;

public class FunctionReferenceAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if(element instanceof AbraResolvable){
            if(element.getReference()==null)return;
            PsiElement resolved = element.getReference().resolve();
            if (resolved == null) {
                int endOffset = element.getTextRange().getEndOffset();
//                if (element.getTemplatePrefixIdentifierReference() != null) {
//                    endOffset = element.getTextRange().getStartOffset() + abraFunctionReference.getText().indexOf("<");
//                }
                TextRange range = new TextRange(element.getTextRange().getStartOffset(), endOffset);
                holder.createErrorAnnotation(range, "Unresolved");
            }
        }
//        if (element instanceof AbraFunctionReference) {
//            AbraFunctionReference abraFunctionReference = (AbraFunctionReference) element;
//            PsiElement resolved = abraFunctionReference.getReference().resolve();
//            if (resolved==null) {
//                int endOffset = element.getTextRange().getEndOffset();
//                if(abraFunctionReference.getTemplatePrefixIdentifierReference()!=null){
//                    endOffset = element.getTextRange().getStartOffset()+abraFunctionReference.getText().indexOf("<");
//                }
//                TextRange range = new TextRange(element.getTextRange().getStartOffset(), endOffset);
//                holder.createErrorAnnotation(range, "Unresolved function");
//            }else{
//                if(resolved.getParent() instanceof AbraFuncStmt) {
//                    TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
//                    Annotation annotation = holder.createInfoAnnotation(range,
//                            ((AbraFuncStmt) resolved.getParent()).getFunctionIdentifier().getText() + "(" + ((AbraFuncStmt) resolved.getParent()).getFuncParameters().getText() + ")");
//                    annotation.setTextAttributes(DefaultLanguageHighlighterColors.FUNCTION_CALL);
//                }
//            }
//        }else if (element instanceof AbraSizeReference) {
//            AbraSizeReference sizeReference = (AbraSizeReference) element;
//            PsiElement resolved = sizeReference.getTypeReference().getReference().resolve();
//            if (resolved==null) {
//                TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
//                holder.createErrorAnnotation(range, "Unresolved type");
//            }else{
//                TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
//                holder.createInfoAnnotation(range, "["+sizeReference.getResolvedSize()+"]");
//            }
//        }else if (element instanceof AbraGenericReference) {
//            AbraGenericReference genericReference = (AbraGenericReference) element;
//            PsiElement resolved = genericReference.getReference().resolve();
//            if (resolved==null) {
//                TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
//                holder.createErrorAnnotation(range, "Unresolved reference");
//            }
//        }else if (element instanceof AbraFieldReference) {
//            AbraFieldReference fieldReference = (AbraFieldReference) element;
//            PsiElement resolved = fieldReference.getReference().resolve();
//            if (resolved==null) {
//                TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
//                holder.createErrorAnnotation(range, "Field is not defined");
//            }
//        }
    }
}
