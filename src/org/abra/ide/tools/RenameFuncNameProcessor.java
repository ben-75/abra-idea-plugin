package org.abra.ide.tools;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import org.abra.language.psi.QuplaFuncName;
import org.abra.language.psi.QuplaFuncNameRef;
import org.abra.language.psi.QuplaFuncStmt;
import org.abra.language.psi.QuplaPsiImplUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class RenameFuncNameProcessor extends RenamePsiElementProcessor {

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return (element instanceof QuplaFuncName) || (element instanceof QuplaFuncNameRef);
    }

    @Override
    public void prepareRenaming(@NotNull PsiElement element, @NotNull String newName, @NotNull Map<PsiElement, String> allRenames) {
        for(QuplaFuncStmt funcStmt: QuplaPsiImplUtil.findAllFuncStmt(element.getProject(),element.getText())){
            allRenames.put(funcStmt.getFuncSignature().getFuncName(),newName);
        }
//        for(QuplaFuncNameRef funcNameRef: QuplaPsiImplUtil.findAllFuncNameRef(element.getProject(),element.getText())){
//            allRenames.put(funcNameRef,newName);
//        }
        super.prepareRenaming(element,newName,allRenames);
    }

    @NotNull
    @Override
    public Collection<PsiReference> findReferences(@NotNull PsiElement element) {
        ArrayList<PsiReference> allRefs = new ArrayList<>();
        for(QuplaFuncNameRef funcNameRef: QuplaPsiImplUtil.findAllFuncNameRef(element.getProject(),element.getText())){
            allRefs.add(funcNameRef.getReference());
        }
        return allRefs;
    }
}
