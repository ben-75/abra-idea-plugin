package org.qupla.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import org.qupla.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuplaTemplatePsiReferenceImpl extends PsiReferenceBase implements PsiReference {


    public QuplaTemplatePsiReferenceImpl(@NotNull QuplaTemplateNameRef element) {
        super(element);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
        final int parent = 0;
        return new TextRange(parent, myElement.getTextLength() + parent);
    }

    @Override
    public QuplaTemplateNameRef handleElementRename(String newElementName) throws IncorrectOperationException {
        QuplaTemplateNameRef ref = QuplaElementFactory.createQuplaTemplateNameRef(myElement.getProject(), newElementName);
        ASTNode newKeyNode = ref.getFirstChild().getNode();
        myElement.getNode().replaceChild(myElement.getFirstChild().getNode(), newKeyNode);
        return ref;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement resolved = resolveInFile(myElement.getContainingFile());
        if(resolved==null){
            resolved = resolveFromImports(myElement.getContainingFile());
        }
        return resolved;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    private QuplaTemplateName resolveInFile(PsiFile aFile){
        for(ASTNode stmt:aFile.getNode().getChildren(TokenSet.create(QuplaTypes.TEMPLATE_STMT))){
            if(((QuplaTemplateStmt)stmt.getPsi()).getTemplateName().getText().equals(myElement.getText())){
                return ((QuplaTemplateStmt)stmt.getPsi()).getTemplateName();
            }
        }
        return null;
    }

    private PsiElement resolveFromImports(PsiFile startingFile){
        List<QuplaFile> importsTree = (((QuplaFile)startingFile).getImportTree());
        return resolveFromImportTree(importsTree);
    }

    public PsiElement resolveFromImportTree(List<QuplaFile> scope){
        if(scope.size()>0){
            for(PsiFile f:scope){
                PsiElement resolved = resolveInFile(f);
                if(resolved!=null)return resolved;
            }
        }
        return null;
    }
}
