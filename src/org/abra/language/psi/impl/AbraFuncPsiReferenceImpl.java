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

public class AbraFuncPsiReferenceImpl extends PsiReferenceBase implements PsiPolyVariantReference {


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

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> resolved = resolveInFile(myElement.getContainingFile());
        resolved.addAll(resolveFromImports(myElement.getContainingFile()));
        ResolveResult[] arr = new ResolveResult[resolved.size()];
        resolved.toArray(arr);
        return arr;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        List<ResolveResult> resolved = resolveInFile(myElement.getContainingFile());
        if(resolved.size()==1){
            return resolved.get(0).getElement();
        }
        return null;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return super.getCanonicalText();
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return super.bindToElement(element);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    public List<ResolveResult> resolveInFile(PsiFile aFile){
        List<ResolveResult> resolveResults = new ArrayList<>();
        AbraFuncExpr funcExpr = (AbraFuncExpr) myElement.getParent();
        for (ASTNode stmt : aFile.getNode().getChildren(TokenSet.create(AbraTypes.FUNC_STMT, AbraTypes.TEMPLATE_STMT))) {
            if(stmt.getElementType()==AbraTypes.FUNC_STMT) {
                if (AbraPsiImplUtil.match(funcExpr, (AbraFuncStmt) stmt.getPsi()) != null) {
                    resolveResults.add(buildResolvedResult(((AbraFuncStmt) stmt.getPsi()).getFuncSignature().getFuncName()));
                }
            }else{
                for (ASTNode f_stmt : stmt.getChildren(TokenSet.create(AbraTypes.FUNC_STMT))) {
                    if (AbraPsiImplUtil.match(funcExpr, (AbraFuncStmt) f_stmt.getPsi()) != null) {
                        resolveResults.add(buildResolvedResult(((AbraFuncStmt) f_stmt.getPsi()).getFuncSignature().getFuncName()));
                    }
                }
            }
        }
        return resolveResults;
    }

    private ResolveResult buildResolvedResult(final AbraFuncName psi) {
        return new PsiElementResolveResult(psi,true);
    }

    private List<ResolveResult> resolveFromImports(PsiFile startingFile){
        List<AbraFile> importsTree = (((AbraFile)startingFile).getImportTree(new ArrayList<>()));
       return resolveFromImportTree(importsTree);
    }

    public List<ResolveResult> resolveFromImportTree(List<AbraFile> scope){
        List<ResolveResult> resolveResults = new ArrayList<>();
        if(scope.size()>0){
            for(PsiFile f:scope){
                resolveResults.addAll(resolveInFile(f));
            }
        }
        return resolveResults;
    }
}
