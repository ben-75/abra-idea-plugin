package org.qupla.runtime.debugger.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class StepIntoQuplaInterpreter extends AnAction {

    public StepIntoQuplaInterpreter() {
        super("Step Into","Step into", AllIcons.Actions.TraceInto);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println("resume...");
    }


}
