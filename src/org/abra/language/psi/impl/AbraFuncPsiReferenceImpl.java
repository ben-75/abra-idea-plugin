package org.abra.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    public PsiElement resolveInFile(PsiFile aFile, AbraConstExpr constExpr){

        if(constExpr==null) {
            //this reference a standard function in this file or in another
            for (ASTNode stmt : aFile.getNode().getChildren(TokenSet.create(AbraTypes.FUNC_STMT))) {
                if (((AbraFuncStmt) stmt.getPsi()).getFuncSignature().getFuncName().getText().equals(myElement.getText())) {
                    return ((AbraFuncStmt) stmt.getPsi()).getFuncSignature().getFuncName();
                }
            }
        }else{
            AbraFuncExpr funcExpr = (AbraFuncExpr) myElement.getParent();
            //recursive call
            if(funcExpr.isInFuncStatement() && ((AbraFuncStmt)funcExpr.getStatment()).getFuncSignature().getFuncName().getText().equals(myElement.getText())){
                return ((AbraFuncStmt)funcExpr.getStatment()).getFuncSignature().getFuncName();
            }
            //override in module
            for (ASTNode stmt : aFile.getNode().getChildren(TokenSet.create(AbraTypes.FUNC_STMT))) {
                if (((AbraFuncStmt) stmt.getPsi()).getFuncSignature().getFuncName().getText().equals(myElement.getText()) && ((AbraFuncStmt) stmt.getPsi()).getFuncSignature().getConstExpr()!=null) {
                    if(funcExpr.getConstExpr().getResolvedSize()==((AbraFuncStmt) stmt.getPsi()).getFuncSignature().getConstExpr().getResolvedSize())
                        return ((AbraFuncStmt) stmt.getPsi()).getFuncSignature().getFuncName();
                }
            }
            //use statement
            boolean referenceATemplate = true;
            if(constExpr.isTypeOrPlaceHolderNameRef()){
                AbraTypeOrPlaceHolderNameRef typeOrPlaceHolderNameRef = constExpr.getTypeOrPlaceHolderNameRef();
                PsiElement resolved = typeOrPlaceHolderNameRef.getReference().resolve();
                if(resolved instanceof AbraTypeName){
                    referenceATemplate = resolved.getParent().getParent() instanceof AbraTemplateStmt;
                }
            }
            if(referenceATemplate){
                for (ASTNode stmt : aFile.getNode().getChildren(TokenSet.create(AbraTypes.TEMPLATE_STMT))) {
                    AbraTemplateStmt templateStmt = (AbraTemplateStmt) stmt.getPsi();
                    for(AbraFuncStmt funcStmt:templateStmt.getFuncStmtList()){
                        if(funcStmt.getFuncSignature().getFuncName().getText().equals(myElement.getText())){
                            return funcStmt.getFuncSignature().getFuncName();
                        }
                    }
                }
            }else{
                for (ASTNode stmt : aFile.getNode().getChildren(TokenSet.create(AbraTypes.USE_STMT))) {
                    AbraUseStmt useStmt = (AbraUseStmt) stmt.getPsi();
                    AbraTemplateName templateName = (AbraTemplateName) useStmt.getTemplateNameRef().getReference().resolve();
                    if(templateName!=null) {
                        //((AbraTemplateStmt)useStmt.getTemplateNameRef().getReference().resolve().getParent()).getFuncStmtList().get(0).getFuncSignature().getFuncName().getText()
                        AbraFuncStmt funcStmt = AbraPsiImplUtil.getFuncWithNameInTemplate(myElement.getText(), (AbraTemplateStmt) templateName.getParent());
                        if (funcStmt!=null) {
                            return useStmt.getTemplateNameRef();
                        }
                    }
                }
            }
        }
        return null;//resolveFromImports(aFile, constExpr);
    }

//    private int sizeOfNamedParam(AbraUseStmt useStmt, AbraTemplateStmt templateStmt, String typeRefName){
//        int i = 0;
//        for (int j = 0; j < templateStmt.getPlaceHolderNameList().size(); j++) {
//            if (typeRefName.equals(templateStmt.getPlaceHolderNameList().get(j).getText())) {
//                break;
//            }
//            i++;
//        }
//        //i is the index of the suffix.
//        if(i<useStmt.getTypeNameRefList().size()) {
//            AbraTypeNameRef typeNameRef = useStmt.getTypeNameRefList().get(i);
//            PsiElement resolvedTypeName = typeNameRef.getReference().resolve();
//            if(resolvedTypeName!=null)
//                return ((AbraTypeStmt) typeNameRef.getReference().resolve().getParent()).getResolvedSize();
//        }
//        return -2;
//    }

    private PsiElement resolveFromImports(PsiFile startingFile, AbraConstExpr typeOrPlaceHolderNameRef){
        List<AbraFile> importsTree = (((AbraFile)startingFile).getImportTree(new ArrayList<>()));
       return resolveFromImportTree(importsTree, typeOrPlaceHolderNameRef);
    }

    public PsiElement resolveFromImportTree(List<AbraFile> scope, AbraConstExpr typeOrPlaceHolderNameRef){
        if(scope.size()>0){
            for(PsiFile f:scope){
                PsiElement resolved = resolveInFile(f, typeOrPlaceHolderNameRef);
                if(resolved!=null)return resolved;
            }
        }
        return null;
    }
}
