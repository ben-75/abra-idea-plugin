package org.abra.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbraFuncPsiReferenceImpl  extends PsiReferenceBase implements PsiReference {


    public AbraFuncPsiReferenceImpl(@NotNull AbraFuncNameRef element) {
        super(element);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
        final int parent = 0;
        return new TextRange(parent, myElement.getTextLength() + parent);
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        AbraFuncNameRef ref = AbraElementFactory.createAbraFunctionReference(myElement.getProject(), newElementName);
        ASTNode newKeyNode = ref.getFirstChild().getNode();
        myElement.getNode().replaceChild(myElement.getFirstChild().getNode(), newKeyNode);
        return ref;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        AbraConstExpr constExpr = null;
        PsiElement it = myElement.getNextSibling();
        while(it.getNode().getElementType()== TokenType.WHITE_SPACE) it = it.getNextSibling();
        boolean isTemplateRef = it.getNode().getElementType() == AbraTypes.OPEN_TAG;
        if(isTemplateRef) {
            it = it.getNextSibling();
            while (it.getNode().getElementType() == TokenType.WHITE_SPACE) it = it.getNextSibling();
            //at this point it is the type name ref
            constExpr = (AbraConstExpr)it;
        }
        PsiElement resolved = resolveInFile(myElement.getContainingFile(), constExpr);
        if(resolved==null){
            resolved = resolveFromImports(myElement.getContainingFile(), constExpr);
        }
        return resolved;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    private PsiElement resolveInFile(PsiFile aFile, AbraConstExpr constExpr){

        if(constExpr==null){
            //this reference a standard function in this file or in another
            for (ASTNode stmt : aFile.getNode().getChildren(TokenSet.create(AbraTypes.FUNC_STMT))) {
                if (((AbraFuncStmt) stmt.getPsi()).getFuncDefinition().getFuncName().getText().equals(myElement.getText())) {
                    return ((AbraFuncStmt) stmt.getPsi()).getFuncDefinition().getFuncName();
                }
            }
        }else{
            int sizeOfMyElement = constExpr.getResolvedSize();
            for (ASTNode stmt : aFile.getNode().getChildren(TokenSet.create(AbraTypes.USE_STMT))) {
                AbraUseStmt useStmt = (AbraUseStmt) stmt.getPsi();
                AbraTemplateName referencedTemplateName = (AbraTemplateName) useStmt.getTemplateNameRef().getReference().resolve();
                if (referencedTemplateName != null) {
                    AbraTemplateStmt abraTemplateStmt = (AbraTemplateStmt) referencedTemplateName.getParent();
                    if (abraTemplateStmt.getFuncDefinition().getFuncName().getText().equals(myElement.getText())) {
                        int sizeOfTheUseStatement = sizeOfNamedParam(useStmt,abraTemplateStmt,abraTemplateStmt.getFuncDefinition().getTypeOrPlaceHolderNameRef().getText());
                        if(sizeOfMyElement==sizeOfTheUseStatement)return useStmt.getTemplateNameRef();
                    }
                }
            }
        }

        //now the use case of a template call from a template
        for (ASTNode stmt : aFile.getNode().getChildren(TokenSet.create(AbraTypes.TEMPLATE_STMT))) {
            AbraTemplateStmt templateStmt = (AbraTemplateStmt) stmt.getPsi();
            if (templateStmt.getFuncDefinition().getFuncName().getText().equals(myElement.getText())) {
                return templateStmt.getFuncDefinition().getFuncName();
            }
        }

        return null;//resolveFromImports(aFile, constExpr);
    }

    private int sizeOfNamedParam(AbraUseStmt useStmt, AbraTemplateStmt templateStmt, String typeRefName){
        int i = 0;
        for (int j = 0; j < templateStmt.getPlaceHolderNameList().size(); j++) {
            if (typeRefName.equals(templateStmt.getPlaceHolderNameList().get(j).getText())) {
                break;
            }
            i++;
        }
        //i is the index of the suffix.
        if(i<useStmt.getTypeNameRefList().size()) {
            AbraTypeNameRef typeNameRef = useStmt.getTypeNameRefList().get(i);
            return ((AbraTypeStmt) typeNameRef.getReference().resolve().getParent()).getResolvedSize();
        }
        return -2;
    }
    private PsiElement resolveFromImports(PsiFile startingFile, AbraConstExpr typeOrPlaceHolderNameRef){
        for(ASTNode stmt:startingFile.getNode().getChildren(TokenSet.create(AbraTypes.IMPORT_STMT))){
            PsiReference[] importedFiles = AbraPsiImplUtil.getReferences((AbraImportStmt) stmt.getPsi());
            if(importedFiles!=null) {
                for (PsiReference psiRef : importedFiles) {
                    PsiElement anAbraFile = psiRef.resolve();
                    if(anAbraFile!=null){
                        PsiElement resolved = resolveInFile((PsiFile) anAbraFile, typeOrPlaceHolderNameRef);
                        if(resolved!=null)return resolved;
                    }
                }
            }
        }
        return null;
    }
}
