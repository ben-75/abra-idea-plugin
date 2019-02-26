package org.qupla.runtime.debugger.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.qupla.runtime.debugger.QuplaDebugSession;

import javax.swing.*;

public class StepOutQuplaInterpreter extends QuplaDebugAction {

    public StepOutQuplaInterpreter() {
        this(null);
    }

    public StepOutQuplaInterpreter(QuplaDebugSession session) {
        super(session,AllIcons.Actions.StepOut);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        if(session!=null){
            session.registerStepOutBreakpoint();
        }
        super.actionPerformed(e);
    }

    @Override
    protected boolean isEnabled(AnActionEvent e) {
        return session!=null && session.isActive();
    }

}
