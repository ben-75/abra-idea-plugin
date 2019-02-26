package org.qupla.runtime.debugger.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.impl.actions.ResumeAction;

public class ResumeQuplaInterpreter extends AnAction {

    public ResumeQuplaInterpreter() {
        super("Resume","Resume Qupla Interpreter",AllIcons.Actions.Resume);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println("resume...");
    }


}
