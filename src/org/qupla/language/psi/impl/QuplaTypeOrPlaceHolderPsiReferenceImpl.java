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

public class QuplaTypeOrPlaceHolderPsiReferenceImpl extends PsiReferenceBase implements PsiReference {

    public QuplaTypeOrPlaceHolderPsiReferenceImpl(@NotNull QuplaTypeOrPlaceHolderNameRef typeOrPlaceHolderNameRef) {
        super(typeOrPlaceHolderNameRef);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
        final int parent = 0;
        return new TextRange(parent, myElement.getTextLength() + parent);
    }

    @Override
    public QuplaTypeOrPlaceHolderNameRef handleElementRename(String newElementName) throws IncorrectOperationException {
        QuplaTypeOrPlaceHolderNameRef ref = QuplaElementFactory.createQuplaTypeOrPlaceHolderNameRef(myElement.getProject(), newElementName);
        ASTNode newKeyNode = ref.getFirstChild().getNode();
        myElement.getNode().replaceChild(myElement.getFirstChild().getNode(), newKeyNode);
        return ref;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement templateStmt = myElement;
        while(!(templateStmt instanceof QuplaTemplateStmt) && !(templateStmt instanceof QuplaFile))templateStmt = templateStmt.getParent();
        if(templateStmt instanceof QuplaTemplateStmt){
            for(QuplaPlaceHolderTypeName phn:((QuplaTemplateStmt)templateStmt).getPlaceHolderTypeNameList()){
                if(phn.getText().equals(myElement.getText()))return phn;
            }
            for(QuplaTypeStmt typeStmt:((QuplaTemplateStmt)templateStmt).getTypeStmtList()){
                QuplaTypeName typeName = typeStmt.getTypeName();
                if(typeName!=null && typeName.getText().equals(myElement.getText()))return typeStmt.getTypeName();
            }
        }
        PsiElement resolved = resolveInFile(myElement.getContainingFile());
        if(resolved==null){
            resolved = resolveFromImports(myElement.getContainingFile());
        }
        return resolved;
    }

    private QuplaTypeName resolveInFile(PsiFile aFile){
        for(ASTNode stmt:aFile.getNode().getChildren(TokenSet.create(QuplaTypes.TYPE_STMT))){
            QuplaTypeName typeName = ((QuplaTypeStmt)stmt.getPsi()).getTypeName();
            if(typeName!=null && typeName.getText().equals(myElement.getText())){
                return ((QuplaTypeStmt)stmt.getPsi()).getTypeName();
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
    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
