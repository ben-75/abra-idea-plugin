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
                AbraFuncDefinition funcDefinition = resolved instanceof AbraFuncName ? (AbraFuncDefinition) resolved.getParent() : ((AbraTemplateStmt)((AbraTemplateNameRef)resolved).getReference().resolve().getParent()).getFuncDefinition();
                if(resolved instanceof AbraTemplateNameRef){
                    AbraPsiImplUtil.ContextStack.INSTANCE.push(AbraPsiImplUtil.getTemplateContextMap((AbraUseStmt)resolved.getParent()));
                }
                try {
                    int argsCount = 0;
                    int startOffset = 0;
                    int endOffset = 0;
                    if (funcExpr.getConcatExpr() != null) {
                        argsCount = 1;
                        startOffset = funcExpr.getConcatExpr().getTextRange().getStartOffset();
                        endOffset = funcExpr.getConcatExpr().getTextRange().getEndOffset();
                    } else {
                        argsCount = funcExpr.getPostfixExprList().size();
                        startOffset = funcExpr.getPostfixExprList().get(0).getTextRange().getStartOffset();
                        endOffset = funcExpr.getPostfixExprList().get(argsCount - 1).getTextRange().getEndOffset();
                    }
                    int expectedArgsCount = funcDefinition.getFuncParameterList().size();

                    if (expectedArgsCount != argsCount) {
                        TextRange range = new TextRange(startOffset, endOffset);
                        holder.createErrorAnnotation(range, "Unexpected arguments count. Expecting : " + expectedArgsCount + " args, but found " + argsCount);
                    } else if (!funcExpr.isInTemplateStatement()) {
                        for (int i = 0; i < funcDefinition.getFuncParameterList().size(); i++) {
                            int expectedSize = funcDefinition.getFuncParameterList().get(i).getTypeSize().getResolvedSize();
                            int effectiveSize = funcExpr.getConcatExpr() != null ? funcExpr.getConcatExpr().getResolvedSize() : funcExpr.getPostfixExprList().get(i).getResolvedSize();
                            if (expectedSize != effectiveSize && (funcExpr.getConcatExpr()!=null || (funcExpr.getPostfixExprList().get(i).getInteger()==null))) {
                                TextRange range = funcExpr.getConcatExpr() != null ? funcExpr.getConcatExpr().getTextRange() : funcExpr.getPostfixExprList().get(i).getTextRange();
                                holder.createErrorAnnotation(range, "Unexpected argument size. Expecting : " + expectedSize + "trits, but found " + effectiveSize + " trits");
                            }
                        }
                    }
                }finally {
                    if(resolved instanceof AbraTemplateNameRef){
                        AbraPsiImplUtil.ContextStack.INSTANCE.pop();
                    }
                }
            }
        }
    }
}
