package org.abra.ide.highlighter;

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
                int  argsCount = funcExpr.getCondExprList().size();
                int   startOffset = funcExpr.getCondExprList().get(0).getTextRange().getStartOffset();
                int  endOffset = funcExpr.getCondExprList().get(argsCount - 1).getTextRange().getEndOffset();

                int expectedArgsCount = funcSignature.getFuncParameterList().size();

                if (expectedArgsCount != argsCount) {
                    TextRange range = new TextRange(startOffset, endOffset);
                    holder.createErrorAnnotation(range, "Unexpected arguments count. Expecting : " + expectedArgsCount + " args, but found " + argsCount);
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
