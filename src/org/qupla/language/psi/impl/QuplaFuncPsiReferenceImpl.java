package org.qupla.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.qupla.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class QuplaFuncPsiReferenceImpl extends PsiReferenceBase implements PsiPolyVariantReference {


    public QuplaFuncPsiReferenceImpl(@NotNull QuplaFuncNameRef element) {
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
        QuplaFuncNameRef ref = QuplaElementFactory.createAbraFunctionReference(myElement.getProject(), newElementName);
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
        QuplaFile startingFile = (QuplaFile) myElement.getContainingFile();
        List<QuplaFile> files = startingFile.getAbraFileScope();
        List<QuplaFuncName> allRefs = QuplaPsiImplUtil.findAllFuncName(myElement.getProject(), null, files.size() == 1 ? null : files);
        return allRefs.toArray();
    }

    public List<ResolveResult> resolveInFile(PsiFile aFile){
        List<ResolveResult> resolveResults = new ArrayList<>();
        List<QuplaFuncName> all = ((QuplaFile)aFile).findAllFuncName(myElement.getText());
        for(QuplaFuncName funcName:all){
            resolveResults.add(buildResolvedResult(funcName));
        }
        return resolveResults;
    }

    private ResolveResult buildResolvedResult(final QuplaFuncName psi) {
        return new PsiElementResolveResult(psi,true);
    }

    private List<ResolveResult> resolveFromImports(PsiFile startingFile){
        List<QuplaFile> importsTree = (((QuplaFile)startingFile).getImportTree());
       return resolveFromImportTree(importsTree);
    }

    public List<ResolveResult> resolveFromImportTree(List<QuplaFile> scope){
        List<ResolveResult> resolveResults = new ArrayList<>();
        if(scope.size()>0){
            for(PsiFile f:scope){
                resolveResults.addAll(resolveInFile(f));
            }
        }
        return resolveResults;
    }
}
