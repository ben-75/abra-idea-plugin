package org.qupla.runtime.debugger.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class StepOutQuplaInterpreter extends AnAction {

    public StepOutQuplaInterpreter() {
        super("Step Out","Step out", AllIcons.Actions.StepOut);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        System.out.println("step out...");
    }


}
