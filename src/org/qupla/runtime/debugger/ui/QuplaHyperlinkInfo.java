package org.qupla.runtime.debugger.ui;

import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.qupla.language.module.QuplaModuleManager;

public class QuplaHyperlinkInfo implements HyperlinkInfo {

    String modulePath;
    int line;
    int col;

    public QuplaHyperlinkInfo(String modulePath, int line, int col) {
        this.modulePath = modulePath;
        this.line = line;
        this.col = col;
    }

    @Override
    public void navigate(Project project) {
        OpenFileDescriptor openFileDescriptor = makeOpenFileDescriptor(project);
        if(openFileDescriptor!=null){
            FileEditorManager.getInstance(project).navigateToTextEditor(openFileDescriptor, true);
        }
    }

    private OpenFileDescriptor makeOpenFileDescriptor(Project project){
        QuplaModuleManager moduleManager = project.getComponent(QuplaModuleManager.class);
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(moduleManager.getFullQuplaSourceRootPath()+"/"+modulePath);
        if(virtualFile!=null)
            return new OpenFileDescriptor(project, virtualFile, line-1, col-1);
        return null;
    }
    @Override
    public boolean includeInOccurenceNavigation() {
        return false;
    }
}
