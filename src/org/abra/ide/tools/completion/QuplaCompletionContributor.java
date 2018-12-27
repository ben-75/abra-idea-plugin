package org.abra.ide.tools.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.ProcessingContext;
import org.abra.language.AbraLanguage;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class QuplaCompletionContributor extends CompletionContributor {

    static final PatternCondition NO_ERROR = new PatternCondition("no error") {
        @Override
        public boolean accepts(@NotNull Object o, ProcessingContext context) {
            return o instanceof PsiElement && noError(((PsiElement) o).getChildren());
        }
        boolean noError(PsiElement[] elements){
            for(PsiElement e:elements){
                if(e instanceof PsiErrorElement)return false;
            }
            return true;
        }
    };
    static final ElementPattern<PsiElement> AFTER_FUNC_KEYWORD = quplaElement().afterLeaf("func");
    static final ElementPattern<PsiElement> TYPE_REFERENCE = quplaElement().withParents(AbraTypeOrPlaceHolderNameRef.class,AbraTypeNameRef.class);
    static final ElementPattern<PsiElement> PSI_ERROR_ELEMENT = quplaElement().withParent(PsiErrorElement.class);

    static final ElementPattern<PsiElement> VALID_STMT = quplaElement().withElementType(TokenSet.create(AbraTypes.FUNC_STMT, AbraTypes.TYPE_STMT, AbraTypes.IMPORT_STMT, AbraTypes.USE_STMT, AbraTypes.TEMPLATE_STMT)).with(NO_ERROR);
    static final ElementPattern<PsiElement> AFTER_VALID_STMT = quplaElement().afterSiblingSkipping(quplaElement().whitespace(),VALID_STMT);


    static PsiElementPattern quplaElement(){
        return psiElement().withLanguage(AbraLanguage.INSTANCE);
    }
    public QuplaCompletionContributor() {
        extend(CompletionType.BASIC, PSI_ERROR_ELEMENT,new QuplaKeywordCompletionProvider());
        extend(CompletionType.BASIC, AFTER_FUNC_KEYWORD,new QuplaTypeNameCompletionProvider());
        extend(CompletionType.BASIC, TYPE_REFERENCE,new QuplaTypeNameCompletionProvider());
    }

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context) {
        super.beforeCompletion(context);
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet resultSet) {
        super.fillCompletionVariants(parameters, resultSet);
    }

}
