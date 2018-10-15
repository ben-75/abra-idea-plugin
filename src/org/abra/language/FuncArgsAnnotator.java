package org.abra.language;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;

public class FuncArgsAnnotator  implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if(element instanceof AbraFuncExpr){
            AbraFuncExpr funcExpr = (AbraFuncExpr) element;
            PsiElement resolved =  funcExpr.getFuncNameRef().getReference().resolve();
            if(resolved!=null){
                AbraFuncSignature funcSignature = resolved instanceof AbraFuncName ? (AbraFuncSignature) resolved.getParent() : getFuncSignatureForTemplateNameRef(funcExpr.getFuncNameRef().getText(),(AbraTemplateNameRef)resolved);
//                if(resolved instanceof AbraTemplateNameRef){
//                    AbraPsiImplUtil.ContextStack.INSTANCE.push(AbraPsiImplUtil.getTemplateContextMap((AbraUseStmt)resolved.getParent(), funcExpr.getConstExpr().getResolvedSize()));
//                }
                try {
                    int  argsCount = funcExpr.getMergeExprList().size();
                    int   startOffset = funcExpr.getMergeExprList().get(0).getTextRange().getStartOffset();
                    int  endOffset = funcExpr.getMergeExprList().get(argsCount - 1).getTextRange().getEndOffset();

                    int expectedArgsCount = funcSignature.getFuncParameterList().size();

                    if (expectedArgsCount != argsCount) {
                        TextRange range = new TextRange(startOffset, endOffset);
                        holder.createErrorAnnotation(range, "Unexpected arguments count. Expecting : " + expectedArgsCount + " args, but found " + argsCount);
                    } else if (((AbraFuncStmt)funcExpr.getStatment()).getFuncSignature().getConstExprList().size()==0) {
//                        for (int i = 0; i < funcSignature.getFuncParameterList().size(); i++) {
//                            try {
//                                int expectedSize = funcSignature.getFuncParameterList().get(i).getTypeSize().getResolvedSize();
//                                int effectiveSize = funcExpr.getMergeExprList().get(i).getResolvedSize();
//
//                                try{
//                                    Integer.parseInt(funcExpr.getMergeExprList().get(i).getText());
//                                    effectiveSize=-1;
//                                }catch(NumberFormatException e){
//
//                                }
//                                if (expectedSize != effectiveSize && effectiveSize>0) {
//                                    int result = funcExpr.getMergeExprList().get(i).getResolvedSize(); //side effect throw UnresolvableTokenException
//                                    TextRange range = funcExpr.getMergeExprList().get(i).getTextRange();
//                                    holder.createErrorAnnotation(range, "Unexpected argument size. Expecting : " + expectedSize + "trits, but found " + effectiveSize + " trits");
//                                }
//                            }catch (UnresolvableTokenException e){
//                                TextRange range = funcExpr.getMergeExprList().get(i).getTextRange();
//                                holder.createErrorAnnotation(range, "Trit vector size cannot be computed");
//                            }
//                        }
                    }
                }finally {
                    if(resolved instanceof AbraTemplateNameRef){
                        AbraPsiImplUtil.ContextStack.INSTANCE.pop();
                    }
                }
            }
        }
    }

    private AbraFuncSignature getFuncSignatureForTemplateNameRef(String funcName, AbraTemplateNameRef templateNameRef) {
        AbraTemplateName templateName = (AbraTemplateName) templateNameRef.getReference().resolve();
        if(templateName!=null) {
            //((AbraTemplateStmt)useStmt.getTemplateNameRef().getReference().resolve().getParent()).getFuncStmtList().get(0).getFuncSignature().getFuncName().getText()
            AbraFuncStmt funcStmt = AbraPsiImplUtil.getFuncWithNameInTemplate(funcName, (AbraTemplateStmt) templateName.getParent());
            if (funcStmt!=null) {
                return funcStmt.getFuncSignature();
            }
        }
        return null;
    }
}
