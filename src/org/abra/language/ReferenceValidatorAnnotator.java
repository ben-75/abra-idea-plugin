package org.abra.language;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.TokenSet;
import org.abra.language.psi.*;
import org.abra.language.psi.impl.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ReferenceValidatorAnnotator implements Annotator {
    private static final Logger log = Logger.getInstance(ReferenceValidatorAnnotator.class);

    private HashSet<AbraFile> usedImports = new HashSet<>();
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if(element instanceof AbraFile){
            log.info("FILE :"+this+"   "+((AbraFile)element).getName());
            for(ASTNode node:element.getNode().getChildren(TokenSet.create(AbraTypes.IMPORT_STMT))){
                AbraImportStmt importStmt = (AbraImportStmt) node.getPsi();
                boolean importNotUsed = true;
                for(PsiReference psiReference:importStmt.getReferences()){
                    if(usedImports.contains(psiReference.resolve())){
                        importNotUsed = false;
                    }
                }
                if(importNotUsed){
                    int endOffset = importStmt.getTextRange().getEndOffset();
                    TextRange range = new TextRange(importStmt.getTextRange().getStartOffset(), endOffset);
                    holder.createWarningAnnotation(range, "Unused import");
                }
            }
        }
        if(element instanceof AbraResolvable){
            log.info("ELEMENT :"+this+"   "+((AbraFile)element.getContainingFile()).getName());
            if(element.getReference()==null)return;
            PsiReference psiReference = element.getReference();
            PsiElement resolved = null;
            try{
                resolved = psiReference.resolve();
            }catch (UnresolvableTokenException e){
                //ignore
            }
            if (resolved == null) {
                int endOffset = element.getTextRange().getEndOffset();
                TextRange range = new TextRange(element.getTextRange().getStartOffset(), endOffset);
                holder.createErrorAnnotation(range, "Unresolved");
            }else{
                if(!resolved.getContainingFile().equals(element.getContainingFile())){
                    //was resolved from import
                    usedImports.add((AbraFile) resolved.getContainingFile());

                    List<AbraFile> importTree = ((AbraFile)element.getContainingFile()).getImportTree(new ArrayList<>());
                    importTree.remove(resolved.getContainingFile());
                    PsiElement resolved2 = null;
                    if(psiReference instanceof AbraLutOrVarOrParamPsiReferenceImpl){
                        resolved2 = ((AbraLutOrVarOrParamPsiReferenceImpl)psiReference).resolveFromImportTree(importTree);
                    }else if(resolved instanceof AbraFieldPsiReferenceImpl){
                        resolved2 = ((AbraFieldPsiReferenceImpl)psiReference).resolveFromImportTree(importTree);
                    }else if(resolved instanceof AbraTemplatePsiReferenceImpl){
                        resolved2 = ((AbraTemplatePsiReferenceImpl)element.getReference()).resolveFromImportTree(importTree);
                    }else if(resolved instanceof AbraTypeOrPlaceHolderPsiReferenceImpl){
                        resolved2 = ((AbraTypeOrPlaceHolderPsiReferenceImpl)element.getReference()).resolveFromImportTree(importTree);
                    }else if(resolved instanceof AbraTypeOrPlaceHolderPsiReferenceImpl){
                        resolved2 = ((AbraTypeOrPlaceHolderPsiReferenceImpl)element.getReference()).resolveFromImportTree(importTree);
                    }else if(resolved instanceof AbraFuncPsiReferenceImpl){
                        resolved2 = ((AbraFuncPsiReferenceImpl)element.getReference()).resolveFromImportTree(importTree,null, null);
                    }

                    if(resolved2!=null){
                        makeAmbiguousAnnotation(element, holder, resolved, resolved2);
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
