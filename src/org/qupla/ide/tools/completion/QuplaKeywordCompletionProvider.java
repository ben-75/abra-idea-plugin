package org.qupla.ide.tools.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.qupla.language.psi.QuplaFile;
import org.qupla.language.psi.QuplaTemplateStmt;
import org.jetbrains.annotations.NotNull;

public class QuplaKeywordCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        if(parameters.getOriginalPosition()!=null)
            addCompletionsForOriginalParent(parameters.getOriginalPosition().getParent(), resultSet);
    }

    void addCompletionsForOriginalParent(@NotNull PsiElement originalParent, @NotNull CompletionResultSet resultSet) {
        if(originalParent instanceof QuplaFile){
            resultSet.addElement(LookupElementBuilder.create("func "));
            resultSet.addElement(LookupElementBuilder.create("type "));
            resultSet.addElement(LookupElementBuilder.create("import "));
            resultSet.addElement(LookupElementBuilder.create("use "));
            resultSet.addElement(LookupElementBuilder.create("template "));
            resultSet.addElement(LookupElementBuilder.create("eval "));
            resultSet.addElement(LookupElementBuilder.create("test "));
        }else if(originalParent instanceof QuplaTemplateStmt){
            resultSet.addElement(LookupElementBuilder.create("func "));
            resultSet.addElement(LookupElementBuilder.create("type "));
        }
    }
}
