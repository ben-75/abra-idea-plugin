package org.abra.language;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.abra.language.psi.*;
import org.abra.language.psi.impl.AbraFuncPsiReferenceImpl;
import org.jetbrains.annotations.NotNull;

public class MultiDeclarationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        AbraFuncNameRef functionReference = null;
//        AbraConstExpr constExpr = null;
//        if (element instanceof AbraFuncStmt) {
//            functionReference = AbraElementFactory.createAbraFunctionReference(element.getProject(),((AbraFuncStmt)element).getFuncSignature().getFuncName().getText(),(AbraFile) element.getContainingFile().getContainingFile());
//            if(((AbraFuncStmt)element).getFuncSignature().getTypeOrPlaceHolderNameRef()!=null){
//                PsiElement resolved = ((AbraFuncStmt)element).getFuncSignature().getTypeOrPlaceHolderNameRef().getReference().resolve();
//                if(resolved instanceof AbraTypeName){
//                    AbraTypeStmt typeStmt = (AbraTypeStmt)resolved.getParent();
//                    if(typeStmt.getTypeSize()!=null) {
//                        constExpr = AbraElementFactory.createAbraConstExpr(element.getProject(), "" + typeStmt.getTypeSize().getResolvedSize());
//                    }else{
//                        constExpr = AbraElementFactory.createAbraConstExpr(element.getProject(), "" + typeStmt.getFieldSpecList().get(typeStmt.getFieldSpecList().size()-1).getTypeSize().getResolvedSize());
//                    }
//                }
//            }
//
//        }else if(element instanceof AbraUseStmt){
//
//            AbraTemplateName templateName = (AbraTemplateName) ((AbraUseStmt)element).getTemplateNameRef().getReference().resolve();
//            if(templateName==null)return;
//            String expandedFuncName = "TODO";
//            String funcName = expandedFuncName.substring(0,expandedFuncName.indexOf("<"));
//            functionReference = AbraElementFactory.createAbraFunctionReference(element.getProject(),funcName,(AbraFile) element.getContainingFile().getContainingFile());
//            String constLiteral = expandedFuncName.substring(expandedFuncName.indexOf("<")+1,expandedFuncName.length()-1);
//            constExpr = AbraElementFactory.createAbraConstExpr(element.getProject(),constLiteral);
//        }
//        if(functionReference!=null){
//            AbraFuncPsiReferenceImpl dummy = new AbraFuncPsiReferenceImpl(functionReference);
//            try{
//                PsiElement resolved = dummy.resolveInFile(element.getContainingFile(), constExpr);
//                if(resolved!=null){
//                    if (element instanceof AbraFuncStmt) {
//                        if(resolved!=((AbraFuncStmt)element).getFuncSignature().getFuncName()){
//                            TextRange range = new TextRange(element.getTextRange().getStartOffset(),element.getTextRange().getEndOffset());
//                            holder.createErrorAnnotation(range, "Duplicate declaration");
//
//                            TextRange range2 = new TextRange(resolved.getTextRange().getStartOffset(),resolved.getTextRange().getEndOffset());
//                            holder.createErrorAnnotation(range2, "Duplicate declaration");
//                        }
//                    }else if(element instanceof AbraUseStmt){
//                        if(resolved!=((AbraUseStmt)element).getTemplateNameRef()){
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
