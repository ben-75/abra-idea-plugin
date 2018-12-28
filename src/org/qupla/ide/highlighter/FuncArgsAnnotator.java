package org.qupla.ide.highlighter;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.qupla.language.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FuncArgsAnnotator  implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if(element instanceof QuplaFuncExpr){
            QuplaFuncExpr funcExpr = (QuplaFuncExpr) element;
            PsiElement resolved =  funcExpr.getFuncNameRef().getReference().resolve();
            if(resolved!=null){
                QuplaFuncSignature funcSignature = resolved instanceof QuplaFuncName ? (QuplaFuncSignature) resolved.getParent() : getFuncSignatureForTemplateNameRef(funcExpr.getFuncNameRef().getText(),(QuplaTemplateNameRef)resolved);
                int  argsCount = funcExpr.getCondExprList().size();
                int   startOffset = funcExpr.getCondExprList().get(0).getTextRange().getStartOffset();
                int  endOffset = funcExpr.getCondExprList().get(argsCount - 1).getTextRange().getEndOffset();

                assert funcSignature != null;
                int expectedArgsCount = funcSignature.getFuncParameterList().size();

                if (expectedArgsCount != argsCount) {
                    TextRange range = new TextRange(startOffset, endOffset);
                    holder.createErrorAnnotation(range, "Unexpected arguments count. Expecting : " + expectedArgsCount + " args, but found " + argsCount);
                }
            }
        }
    }

    private QuplaFuncSignature getFuncSignatureForTemplateNameRef(String funcName, QuplaTemplateNameRef templateNameRef) {
        QuplaTemplateName templateName = (QuplaTemplateName) templateNameRef.getReference().resolve();
        if(templateName!=null) {
            //((QuplaTemplateStmt)useStmt.getTemplateNameRef().getReference().resolve().getParent()).getFuncStmtList().get(0).getFuncSignature().getFuncName().getText()
            QuplaFuncStmt funcStmt = QuplaPsiImplUtil.getFuncWithNameInTemplate(funcName, (QuplaTemplateStmt) templateName.getParent());
            if (funcStmt!=null) {
                return funcStmt.getFuncSignature();
            }
        }
        return null;
    }
}
