package org.qupla.runtime.debugger.ui;

import com.intellij.execution.ui.layout.impl.JBRunnerTabs;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import org.jetbrains.annotations.NotNull;

public class QuplaRunnerTabs extends JBRunnerTabs {

    public QuplaRunnerTabs(@NotNull Project project) {
        super(project, ActionManager.getInstance(), IdeFocusManager.findInstance(), project);
    }

}
