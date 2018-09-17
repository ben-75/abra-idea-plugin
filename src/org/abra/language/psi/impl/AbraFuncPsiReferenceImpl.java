package org.abra.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.tree.TokenSet;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbraFuncPsiReferenceImpl  extends PsiReferenceBase implements PsiReference {


    public AbraFuncPsiReferenceImpl(@NotNull PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
        final int parent = 0;
        return new TextRange(parent, myElement.getTextLength() + parent);
    }
    //TODO : handleRename

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

    private PsiElement resolveInFile(PsiFile aFile){
        for(ASTNode stmt:aFile.getNode().getChildren(TokenSet.create(AbraTypes.FUNC_STMT))){
            if(((AbraFuncStmt)stmt.getPsi()).getFuncDefinition().getFuncName().getText().equals(myElement.getText())){
                return ((AbraFuncStmt)stmt.getPsi()).getFuncDefinition().getFuncName();
            }
        }
        for(ASTNode stmt:aFile.getNode().getChildren(TokenSet.create(AbraTypes.USE_STMT))){

            AbraUseStmt useStmt = (AbraUseStmt)stmt.getPsi();

            AbraTemplateName referencedTemplateName = (AbraTemplateName) useStmt.getTemplateNameRef().getReference().resolve();
            if(referencedTemplateName!=null){
                AbraTemplateStmt abraTemplateStmt = (AbraTemplateStmt) referencedTemplateName.getParent();
                String funcNameSuffixPlaceHolder = abraTemplateStmt.getFuncDefinition().getTypeOrPlaceHolderNameRef().getText();
                int i=0;
                for(int j=0;j<abraTemplateStmt.getPlaceHolderNameList().size();j++){
                    if(funcNameSuffixPlaceHolder.equals(abraTemplateStmt.getPlaceHolderNameList().get(j).getText())){
                        break;
                    }
                    i++;
                }
                String funcNameSuffix = useStmt.getTypeNameRefList().get(i).getText();
                if((((AbraTemplateStmtImpl) referencedTemplateName.getParent()).getFuncDefinition().getFuncName().getText()+funcNameSuffix).equals(myElement.getText())){
                    return useStmt.getTemplateNameRef();
                }
            }
        }
        //now the use case of a template call from a template
        for(ASTNode stmt:aFile.getNode().getChildren(TokenSet.create(AbraTypes.TEMPLATE_STMT))){
            AbraTemplateStmt templateStmt = (AbraTemplateStmt)stmt.getPsi();
            if(templateStmt.getFuncDefinition().getFuncName().getText().equals(myElement.getText())){
                return templateStmt.getFuncDefinition().getFuncName();
            }
        }
        return null;
    }

    private PsiElement resolveFromImports(PsiFile startingFile){
        for(ASTNode stmt:startingFile.getNode().getChildren(TokenSet.create(AbraTypes.IMPORT_STMT))){
            PsiReference[] importedFiles = AbraPsiImplUtil.getReferences((AbraImportStmt) stmt.getPsi());
            if(importedFiles!=null) {
                for (PsiReference psiRef : importedFiles) {
                    PsiElement anAbraFile = psiRef.resolve();
                    if(anAbraFile!=null){
                        PsiElement resolved = resolveInFile((PsiFile) anAbraFile);
                        if(resolved!=null)return resolved;
                    }
                }
            }
        }
        return null;
    }
}
