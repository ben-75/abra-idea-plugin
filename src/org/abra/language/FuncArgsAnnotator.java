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
                    int  argsCount = funcExpr.getPostfixExprList().size();
                    int   startOffset = funcExpr.getPostfixExprList().get(0).getTextRange().getStartOffset();
                    int  endOffset = funcExpr.getPostfixExprList().get(argsCount - 1).getTextRange().getEndOffset();

                    int expectedArgsCount = funcDefinition.getFuncParameterList().size();

                    if (expectedArgsCount != argsCount) {
                        TextRange range = new TextRange(startOffset, endOffset);
                        holder.createErrorAnnotation(range, "Unexpected arguments count. Expecting : " + expectedArgsCount + " args, but found " + argsCount);
                    } else if (!funcExpr.isInTemplateStatement()) {
                        for (int i = 0; i < funcDefinition.getFuncParameterList().size(); i++) {
                            try {
                                int expectedSize = funcDefinition.getFuncParameterList().get(i).getTypeSize().getResolvedSize();
                                int effectiveSize = funcExpr.getPostfixExprList().get(i).getResolvedSize();
                                if (expectedSize != effectiveSize && funcExpr.getPostfixExprList().get(i).getConcatTerm().getInteger()==null) {
                                    TextRange range = funcExpr.getPostfixExprList().get(i).getTextRange();
                                    holder.createErrorAnnotation(range, "Unexpected argument size. Expecting : " + expectedSize + "trits, but found " + effectiveSize + " trits");
                                }
                            }catch (UnresolvableTokenException e){
                                TextRange range = funcExpr.getPostfixExprList().get(i).getTextRange();
                                holder.createErrorAnnotation(range, "Trit vector size cannot be computed");
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
