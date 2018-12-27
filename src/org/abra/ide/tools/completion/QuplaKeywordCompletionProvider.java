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

public class QuplaKeywordCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        if(parameters.getOriginalPosition()!=null)
            addCompletionsForOriginalParent(parameters.getOriginalPosition().getParent(), resultSet);
    }

    void addCompletionsForOriginalParent(@NotNull PsiElement originalParent, @NotNull CompletionResultSet resultSet) {
        if(originalParent instanceof AbraFile){
            resultSet.addElement(LookupElementBuilder.create("func "));
            resultSet.addElement(LookupElementBuilder.create("type "));
            resultSet.addElement(LookupElementBuilder.create("import "));
            resultSet.addElement(LookupElementBuilder.create("use "));
            resultSet.addElement(LookupElementBuilder.create("template "));
            resultSet.addElement(LookupElementBuilder.create("eval "));
            resultSet.addElement(LookupElementBuilder.create("test "));
        }else if(originalParent instanceof AbraTemplateStmt){
            resultSet.addElement(LookupElementBuilder.create("func "));
            resultSet.addElement(LookupElementBuilder.create("type "));
        }
    }
}
