package org.abra.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AbraTypeOrPlaceHolderPsiReferenceImpl extends PsiReferenceBase implements PsiReference {

    public AbraTypeOrPlaceHolderPsiReferenceImpl(@NotNull AbraTypeOrPlaceHolderNameRef typeOrPlaceHolderNameRef) {
        super(typeOrPlaceHolderNameRef);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
        final int parent = 0;
        return new TextRange(parent, myElement.getTextLength() + parent);
    }

    @Override
    public AbraTypeOrPlaceHolderNameRef handleElementRename(String newElementName) throws IncorrectOperationException {
        AbraTypeOrPlaceHolderNameRef ref = AbraElementFactory.createAbraTypeOrPlaceHolderNameRef(myElement.getProject(), newElementName);
        ASTNode newKeyNode = ref.getFirstChild().getNode();
        myElement.getNode().replaceChild(myElement.getFirstChild().getNode(), newKeyNode);
        return ref;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement templateStmt = myElement;
        while(!(templateStmt instanceof AbraTemplateStmt) && !(templateStmt instanceof AbraFile))templateStmt = templateStmt.getParent();
        if(templateStmt instanceof AbraTemplateStmt){
            for(AbraPlaceHolderTypeName phn:((AbraTemplateStmt)templateStmt).getPlaceHolderTypeNameList()){
                if(phn.getText().equals(myElement.getText()))return phn;
            }
            for(AbraTypeStmt typeStmt:((AbraTemplateStmt)templateStmt).getTypeStmtList()){
                if(typeStmt.getTypeName().getText().equals(myElement.getText()))return typeStmt.getTypeName();
            }
        }
        PsiElement resolved = resolveInFile(myElement.getContainingFile());
        if(resolved==null){
            resolved = resolveFromImports(myElement.getContainingFile());
        }
        return resolved;
    }

    private AbraTypeName resolveInFile(PsiFile aFile){
        for(ASTNode stmt:aFile.getNode().getChildren(TokenSet.create(AbraTypes.TYPE_STMT))){
            if(((AbraTypeStmt)stmt.getPsi()).getTypeName().getText().equals(myElement.getText())){
                return ((AbraTypeStmt)stmt.getPsi()).getTypeName();
            }
        }
        return null;
    }

//    private AbraTypeName resolveFromImports(PsiFile startingFile){
//        for(ASTNode stmt:startingFile.getNode().getChildren(TokenSet.create(AbraTypes.IMPORT_STMT))){
//            PsiReference[] importedFiles = AbraPsiImplUtil.getReferences((AbraImportStmt) stmt.getPsi());
//            if(importedFiles!=null) {
//                for (PsiReference psiRef : importedFiles) {
//                    PsiElement anAbraFile = psiRef.resolve();
//                    if(anAbraFile!=null){
//                        AbraTypeName resolved = resolveInFile((PsiFile) anAbraFile);
//                        if(resolved!=null)return resolved;
//                    }
//                }
//            }
//        }
//        return null;
//    }

    private PsiElement resolveFromImports(PsiFile startingFile){
        List<AbraFile> importsTree = (((AbraFile)startingFile).getImportTree());
        return resolveFromImportTree(importsTree);
    }

    public PsiElement resolveFromImportTree(List<AbraFile> scope){
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
