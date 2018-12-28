package org.qupla.ide.ui.action;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiDirectory;
import org.qupla.ide.ui.QuplaIcons;
import org.qupla.language.module.QuplaModuleManager;

public class MarkAsQuplaSourceRoot extends AnAction {


    public MarkAsQuplaSourceRoot() {
        super(QuplaIcons.QUPLA_MODULE_FOLDER);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Navigatable focused = e.getData(CommonDataKeys.NAVIGATABLE);
        if(focused instanceof PsiDirectory && e.getProject()!=null){
            String fullPath = ((PsiDirectory)focused).getVirtualFile().getPath();
            String projectPath = e.getProject().getBasePath();
            if(projectPath!=null) {
                String subPath = fullPath.substring(projectPath.length() + 1);
                e.getProject().getComponent(QuplaModuleManager.class).setQuplaSourceRootPath(subPath);
                ProjectView.getInstance(e.getProject()).refresh();
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        Navigatable focused = e.getData(CommonDataKeys.NAVIGATABLE);
        e.getPresentation().setEnabledAndVisible(focused instanceof PsiDirectory);
    }
}
