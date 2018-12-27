package org.abra.ide.tools.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.abra.language.module.QuplaModuleManager;
import org.abra.language.psi.AbraFile;
import org.abra.language.psi.AbraPlaceHolderTypeName;
import org.abra.language.psi.AbraTemplateStmt;
import org.abra.language.psi.AbraTypeStmt;
import org.jetbrains.annotations.NotNull;

public class QuplaTypeNameCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement element = parameters.getPosition();
        while(!(element instanceof AbraFile)){
            if(element instanceof AbraTemplateStmt){
                for(AbraPlaceHolderTypeName placeHolderTypeName : ((AbraTemplateStmt)element).getPlaceHolderTypeNameList()){
                    result.addElement(LookupElementBuilder.createWithIcon(placeHolderTypeName));
                }
                for(AbraTypeStmt typeStmt:((AbraTemplateStmt)element).getTypeStmtList()){
                    if(typeStmt.getTypeName()!=null)
                        result.addElement(LookupElementBuilder.createWithIcon(typeStmt.getTypeName()));
                }
            }
            element = element.getParent();
        }
        for(AbraFile f:element.getProject().getComponent(QuplaModuleManager.class).getAllVisibleFiles((AbraFile) ((AbraFile) element).getOriginalFile())){
            for(AbraTypeStmt typeStmt:f.getAllTypeStmts()){
                if(typeStmt.getTypeName()!=null)
                    result.addElement(LookupElementBuilder.createWithIcon(typeStmt.getTypeName()));
            }
        }
    }
}
