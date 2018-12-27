package org.abra.ide.highlighter;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;

public class MultiDeclarationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        QuplaFuncNameRef functionReference = null;
//        QuplaConstExpr constExpr = null;
//        if (element instanceof QuplaFuncStmt) {
//            functionReference = QuplaElementFactory.createAbraFunctionReference(element.getProject(),((QuplaFuncStmt)element).getFuncSignature().getFuncName().getText(),(QuplaFile) element.getContainingFile().getContainingFile());
//            if(((QuplaFuncStmt)element).getFuncSignature().getTypeOrPlaceHolderNameRef()!=null){
//                PsiElement resolved = ((QuplaFuncStmt)element).getFuncSignature().getTypeOrPlaceHolderNameRef().getReference().resolve();
//                if(resolved instanceof QuplaTypeName){
//                    QuplaTypeStmt typeStmt = (QuplaTypeStmt)resolved.getParent();
//                    if(typeStmt.getTypeSize()!=null) {
//                        constExpr = QuplaElementFactory.createAbraConstExpr(element.getProject(), "" + typeStmt.getTypeSize().getResolvedSize());
//                    }else{
//                        constExpr = QuplaElementFactory.createAbraConstExpr(element.getProject(), "" + typeStmt.getFieldSpecList().get(typeStmt.getFieldSpecList().size()-1).getTypeSize().getResolvedSize());
//                    }
//                }
//            }
//
//        }else if(element instanceof QuplaUseStmt){
//
//            QuplaTemplateName templateName = (QuplaTemplateName) ((QuplaUseStmt)element).getTemplateNameRef().getReference().resolve();
//            if(templateName==null)return;
//            String expandedFuncName = "TODO";
//            String funcName = expandedFuncName.substring(0,expandedFuncName.indexOf("<"));
//            functionReference = QuplaElementFactory.createAbraFunctionReference(element.getProject(),funcName,(QuplaFile) element.getContainingFile().getContainingFile());
//            String constLiteral = expandedFuncName.substring(expandedFuncName.indexOf("<")+1,expandedFuncName.length()-1);
//            constExpr = QuplaElementFactory.createAbraConstExpr(element.getProject(),constLiteral);
//        }
//        if(functionReference!=null){
//            QuplaFuncPsiReferenceImpl dummy = new QuplaFuncPsiReferenceImpl(functionReference);
//            try{
//                PsiElement resolved = dummy.resolveInFile(element.getContainingFile(), constExpr);
//                if(resolved!=null){
//                    if (element instanceof QuplaFuncStmt) {
//                        if(resolved!=((QuplaFuncStmt)element).getFuncSignature().getFuncName()){
//                            TextRange range = new TextRange(element.getTextRange().getStartOffset(),element.getTextRange().getEndOffset());
//                            holder.createErrorAnnotation(range, "Duplicate declaration");
//
//                            TextRange range2 = new TextRange(resolved.getTextRange().getStartOffset(),resolved.getTextRange().getEndOffset());
//                            holder.createErrorAnnotation(range2, "Duplicate declaration");
//                        }
//                    }else if(element instanceof QuplaUseStmt){
//                        if(resolved!=((QuplaUseStmt)element).getTemplateNameRef()){
//                            TextRange range = new TextRange(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
//                            holder.createErrorAnnotation(range, "Duplicate declaration");
//
//                            TextRange range2 = new TextRange(resolved.getTextRange().getStartOffset(),resolved.getTextRange().getEndOffset());
//                            holder.createErrorAnnotation(range2, "Duplicate declaration");
//                        }
//                    }
//                }
//            }catch (UnresolvableTokenException e){
//                //ignore
//            }
//        }
    }
}
