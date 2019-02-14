package org.qupla.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import org.qupla.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuplaFieldPsiReferenceImpl extends PsiReferenceBase implements PsiReference {


    public QuplaFieldPsiReferenceImpl(@NotNull PsiElement element) {
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
        QuplaFieldNameRef ref = QuplaElementFactory.createQuplaFieldNameReference(myElement.getProject(), newElementName);
        ASTNode newKeyNode = ref.getFirstChild().getNode();
        myElement.getNode().replaceChild(myElement.getFirstChild().getNode(), newKeyNode);
        return ref;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if(myElement.getParent() instanceof QuplaSliceExpr){
            QuplaSliceExpr sliceExpr = (QuplaSliceExpr)myElement.getParent();
            QuplaParamOrVarNameRef paramOrVarNameRef = sliceExpr.getParamOrVarNameRef();
            PsiElement paramOrVarName = paramOrVarNameRef.getReference().resolve();
            PsiElement typeNameOrPlaceHolderName = null;
            if(paramOrVarName instanceof QuplaParamName){
                QuplaFuncParameter funcParameter = (QuplaFuncParameter) paramOrVarName.getParent();
                if(funcParameter!=null){
                    typeNameOrPlaceHolderName = funcParameter.getTypeOrPlaceHolderNameRef().getReference().resolve();
                }
            }else if(paramOrVarName instanceof QuplaVarName){
                if(paramOrVarName.getParent() instanceof QuplaStateExpr){
                    QuplaStateExpr stateExpr = (QuplaStateExpr) paramOrVarName.getParent();
                    typeNameOrPlaceHolderName = stateExpr.getTypeOrPlaceHolderNameRef().getReference().resolve();
                }else if(paramOrVarName.getParent() instanceof QuplaAssignExpr){
                    QuplaAssignExpr assignExpr = (QuplaAssignExpr) paramOrVarName.getParent();
                    QuplaFuncExpr funcExpr = findFuncExpr(assignExpr.getCondExpr());
                    if(funcExpr!=null){
                        PsiReference[] refs = funcExpr.getFuncNameRef().getReferences();
                        if(refs.length==1){
                            PsiReference ref = refs[0];
                            ResolveResult[] resolveResults = ((QuplaFuncPsiReferenceImpl)ref).multiResolve(false);
                            if(resolveResults.length==1) {
                                ResolveResult resolveResult = resolveResults[0];
                                QuplaFuncName funcName = (QuplaFuncName) resolveResult.getElement();
                                if (funcName != null) {
                                    QuplaFuncSignature funcSignature = (QuplaFuncSignature) funcName.getParent();
                                    typeNameOrPlaceHolderName = funcSignature.getTypeOrPlaceHolderNameRefList().get(0).getReference().resolve();
                                }
                            }
                        }
                    }
                }
            }
            if(typeNameOrPlaceHolderName instanceof QuplaTypeName){
                QuplaTypeStmt typeStmt = (QuplaTypeStmt) typeNameOrPlaceHolderName.getParent();
                for(QuplaFieldSpec fieldSpec:typeStmt.getFieldSpecList()){
                    if(fieldSpec.getFieldName().getText().equals(myElement.getText())){
                        return fieldSpec.getFieldName();
                    }
                }
            }
        }


        PsiElement resolved = resolveInFile(myElement.getContainingFile());
        if(resolved==null){
            resolved = resolveFromImports(myElement.getContainingFile());
        }
        return resolved;
    }

    private QuplaFuncExpr findFuncExpr(QuplaCondExpr condExpr){
        if(condExpr.getMergeExprList().size()==1){
            QuplaMergeExpr mergeExpr =condExpr.getMergeExprList().get(0);
            if(mergeExpr.getConcatExprList().size()==1){
                QuplaConcatExpr concatExpr = mergeExpr.getConcatExprList().get(0);
                if(concatExpr.getPostfixExprList().size()==1){
                    QuplaPostfixExpr postfixExpr = concatExpr.getPostfixExprList().get(0);
                    return postfixExpr.getFuncExpr();
                }
            }
        }
        return null;
    }
    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    private PsiElement resolveInFile(PsiFile aFile){
        PsiElement resolved = resolveInTemplate();
        if(resolved!=null)return resolved;
        for(ASTNode stmt:aFile.getNode().getChildren(TokenSet.create(QuplaTypes.TYPE_STMT))){
            for(QuplaFieldSpec fieldSpec:((QuplaTypeStmt)stmt.getPsi()).getFieldSpecList()){
                if(fieldSpec.getFieldName().getText().equals(myElement.getText())){
                    return fieldSpec.getFieldName();
                }
            }
        }
        return null;
    }

    private PsiElement resolveInTemplate(){
        PsiElement templateStmt = myElement;
        while(!(templateStmt instanceof QuplaFile) && !(templateStmt instanceof QuplaTemplateStmt)){
            templateStmt = templateStmt.getParent();
        }
        if(templateStmt instanceof QuplaTemplateStmt){
            for(QuplaTypeStmt localTypeStmt:((QuplaTemplateStmt)templateStmt).getTypeStmtList()){
                if(localTypeStmt.getFieldSpecList().size()>0){
                    for(QuplaFieldSpec fs:localTypeStmt.getFieldSpecList()){
                        if(fs.getFieldName().getText().equals(myElement.getText()))return fs.getFieldName();
                    }
                }
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
