package org.abra.runtime.debugger;

import com.intellij.debugger.SourcePosition;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class AbraSourcePosition extends SourcePosition {

    private PsiElement element;
    private int line;

    public AbraSourcePosition(PsiElement element, int line){
        this.element = element;
        this.line = line;
    }
    @NotNull
    @Override
    public PsiFile getFile() {
        return element.getContainingFile();
    }

    @Override
    public PsiElement getElementAt() {
        return element;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getOffset() {
        return element.getTextOffset();
    }

    @Override
    public boolean canNavigate() {
        return getFile().isValid();
    }

    @Override
    public boolean canNavigateToSource() {
        return canNavigate();
    }

    @Override
    public void navigate(final boolean requestFocus) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (!canNavigate()) {
                return;
            }
            openEditor(requestFocus);
        });
    }

    @Override
    public Editor openEditor(final boolean requestFocus) {
        final PsiFile psiFile = getFile();
        final Project project = psiFile.getProject();
        if (project.isDisposed()) {
            return null;
        }
        final VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null || !virtualFile.isValid()) {
            return null;
        }
        final int offset = getOffset();
        if (offset < 0) {
            return null;
        }
        return FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, virtualFile, offset), requestFocus);
    }
}
