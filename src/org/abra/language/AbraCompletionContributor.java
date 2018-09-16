package org.abra.language;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.ProcessingContext;
import org.abra.language.psi.AbraDefinition;
import org.abra.language.psi.AbraTypes;
import org.jetbrains.annotations.NotNull;

public class AbraCompletionContributor extends CompletionContributor {
    public AbraCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(AbraTypes.IDENTIFIER).withLanguage(AbraLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        ASTNode[] nodes = parameters.getOriginalFile().getNode().getChildren(TokenSet.create(AbraTypes.TYPE_STMT,AbraTypes.LUT_STMT,AbraTypes.FUNC_STMT));
                        for(ASTNode n:nodes){
                            if(n.getPsi() instanceof AbraDefinition){
                                ItemPresentation itemPresentation = ((AbraDefinition)n.getPsi()).getPresentation();
                                resultSet.addElement(LookupElementBuilder.create(itemPresentation.getPresentableText()).withIcon(itemPresentation.getIcon(true)).appendTailText(itemPresentation.getLocationString(),true));
                            }
                        }

                    }
                }
        );
    }
}
