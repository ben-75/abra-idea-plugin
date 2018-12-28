package org.qupla.ide.tools.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.qupla.language.module.QuplaModuleManager;
import org.qupla.language.psi.*;
import org.jetbrains.annotations.NotNull;

public class QuplaTypeNameCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement element = parameters.getPosition();
        while(!(element instanceof QuplaFile)){
            if(element instanceof QuplaTemplateStmt){
                for(QuplaPlaceHolderTypeName placeHolderTypeName : ((QuplaTemplateStmt)element).getPlaceHolderTypeNameList()){
                    result.addElement(LookupElementBuilder.createWithIcon(placeHolderTypeName));
                }
                for(QuplaTypeStmt typeStmt:((QuplaTemplateStmt)element).getTypeStmtList()){
                    if(typeStmt.getTypeName()!=null)
                        result.addElement(LookupElementBuilder.createWithIcon(typeStmt.getTypeName()));
                }
            }
            element = element.getParent();
        }
        for(QuplaFile f:element.getProject().getComponent(QuplaModuleManager.class).getAllVisibleFiles((QuplaFile) ((QuplaFile) element).getOriginalFile())){
            for(QuplaTypeStmt typeStmt:f.getAllTypeStmts()){
                if(typeStmt.getTypeName()!=null)
                    result.addElement(LookupElementBuilder.createWithIcon(typeStmt.getTypeName()));
            }
        }
    }
}
