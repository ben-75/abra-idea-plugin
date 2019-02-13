package org.qupla.ide.ui.action;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.FileContentUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import org.qupla.language.QuplaFileType;
import org.qupla.language.module.QuplaModuleManager;

public class RefreshImportsIntentionAction extends BaseIntentionAction {

    @NotNull
    @Override
    public String getText() {
        return "Refresh modules";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Refresh";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull final Project project, final Editor editor, PsiFile file) throws
            IncorrectOperationException {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                project.getComponent(QuplaModuleManager.class).invalidate();
                FileContentUtil.reparseOpenedFiles();
            }
        });
    }
}
