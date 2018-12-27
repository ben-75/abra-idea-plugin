package org.abra.ide.highlighter;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import org.abra.language.UnresolvableTokenException;
import org.abra.language.psi.*;
import org.abra.language.psi.impl.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReferenceValidatorAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if(element instanceof QuplaResolvable){
            if(element.getReference()==null)return;
            PsiReference psiReference = element.getReference();
            if(psiReference instanceof PsiPolyVariantReference){
                ResolveResult[] results = ((PsiPolyVariantReference)psiReference).multiResolve(false);
                if(results.length==0){
                    int endOffset = element.getTextRange().getEndOffset();
                    TextRange range = new TextRange(element.getTextRange().getStartOffset(), endOffset);
                    holder.createErrorAnnotation(range, "Unresolved");
                }
            }else {
                PsiElement resolved = null;
                try {
                    resolved = psiReference.resolve();
                } catch (UnresolvableTokenException e) {
                    //ignore
                }
                if (resolved == null) {
                    int endOffset = element.getTextRange().getEndOffset();
                    TextRange range = new TextRange(element.getTextRange().getStartOffset(), endOffset);
                    holder.createErrorAnnotation(range, "Unresolved");
                } else {
                    if (!resolved.getContainingFile().equals(element.getContainingFile())) {
                        //was resolved from import

                        List<QuplaFile> importTree = ((QuplaFile) element.getContainingFile()).getImportTree();
                        importTree.remove(resolved.getContainingFile());
                        PsiElement resolved2 = null;
                        if (psiReference instanceof QuplaLutOrVarOrParamPsiReferenceImpl) {
                            resolved2 = ((QuplaLutOrVarOrParamPsiReferenceImpl) psiReference).resolveFromImportTree(importTree);
                        } else if (resolved instanceof QuplaFieldPsiReferenceImpl) {
                            resolved2 = ((QuplaFieldPsiReferenceImpl) psiReference).resolveFromImportTree(importTree);
                        } else if (resolved instanceof QuplaTemplatePsiReferenceImpl) {
                            resolved2 = ((QuplaTemplatePsiReferenceImpl) element.getReference()).resolveFromImportTree(importTree);
                        } else if (resolved instanceof QuplaTypeOrPlaceHolderPsiReferenceImpl) {
                            resolved2 = ((QuplaTypeOrPlaceHolderPsiReferenceImpl) element.getReference()).resolveFromImportTree(importTree);
                        }
                        if (resolved2 != null) {
                            makeAmbiguousAnnotation(element, holder, resolved, resolved2);
                        }
                    }
                }
            }
        }
    }

    private void makeAmbiguousAnnotation(@NotNull PsiElement element, @NotNull AnnotationHolder holder, PsiElement resolved, PsiElement resolved2) {
        int endOffset = element.getTextRange().getEndOffset();
        TextRange range = new TextRange(element.getTextRange().getStartOffset(), endOffset);
        holder.createErrorAnnotation(range, "Ambiguous refernce. Declaration exists in '"+resolved.getContainingFile().getName()+"' and '"+resolved2.getContainingFile().getName()+"'");
    }
}
