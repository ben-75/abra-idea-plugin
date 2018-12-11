package org.abra.ide.tools;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import org.abra.language.psi.AbraFuncName;
import org.abra.language.psi.AbraFuncNameRef;
import org.abra.language.psi.AbraFuncStmt;
import org.abra.language.psi.AbraPsiImplUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Extension to handle function renaming.
 * TODO : is it still useful ?
 */
public class RenameFuncNameProcessor extends RenamePsiElementProcessor {

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return (element instanceof AbraFuncName) || (element instanceof AbraFuncNameRef);
    }

    @Override
    public void prepareRenaming(@NotNull PsiElement element, @NotNull String newName, @NotNull Map<PsiElement, String> allRenames) {
        for(AbraFuncStmt funcStmt: AbraPsiImplUtil.findAllFuncStmt(element.getProject(),element.getText())){
            allRenames.put(funcStmt.getFuncSignature().getFuncName(),newName);
        }
        super.prepareRenaming(element,newName,allRenames);
    }

    @NotNull
    @Override
    public Collection<PsiReference> findReferences(@NotNull PsiElement element) {
        ArrayList<PsiReference> allRefs = new ArrayList<>();
        for(AbraFuncNameRef funcNameRef: AbraPsiImplUtil.findAllFuncNameRef(element.getProject(),element.getText())){
            allRefs.add(funcNameRef.getReference());
        }
        return allRefs;
    }
}
