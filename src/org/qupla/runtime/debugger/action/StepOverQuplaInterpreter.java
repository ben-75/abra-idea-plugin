package org.qupla.runtime.debugger.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class StepOverQuplaInterpreter extends AnAction {

    public StepOverQuplaInterpreter() {
        super("Step Over","Step over", AllIcons.Actions.TraceOver);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println("resume...");
    }


}
