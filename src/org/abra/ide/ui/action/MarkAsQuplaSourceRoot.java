package org.abra.ide.ui.action;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiDirectory;
import com.intellij.tasks.context.ProjectViewContextProvider;
import org.abra.ide.ui.AbraIcons;
import org.abra.language.module.QuplaModuleManager;

public class MarkAsQuplaSourceRoot extends AnAction {


    public MarkAsQuplaSourceRoot() {
        super(AbraIcons.QUPLA_MODULE_FOLDER);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Navigatable focused = e.getData(CommonDataKeys.NAVIGATABLE);
        if(focused instanceof PsiDirectory){
            String fullPath = ((PsiDirectory)focused).getVirtualFile().getPath();
            String projectPath = e.getProject().getBasePath();
            String subPath = fullPath.substring(projectPath.length()+1);
            e.getProject().getComponent(QuplaModuleManager.class).setQuplaSourceRootPath(subPath);
            ProjectView.getInstance(e.getProject()).refresh();
        }
    }

    @Override
    public void update(AnActionEvent e) {
        Navigatable focused = e.getData(CommonDataKeys.NAVIGATABLE);
        e.getPresentation().setEnabledAndVisible(focused instanceof PsiDirectory);
    }
}
