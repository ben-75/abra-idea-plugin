package org.qupla.runtime.debugger.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.impl.actions.ResumeAction;
import org.qupla.runtime.debugger.QuplaDebugSession;

import javax.swing.*;

public class StepIntoQuplaInterpreter extends QuplaDebugAction {

    public StepIntoQuplaInterpreter() {
        this(null);
    }

    public StepIntoQuplaInterpreter(QuplaDebugSession session) {
        super(session,AllIcons.Actions.TraceInto);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        if(session!=null){
            session.registerStepIntoBreakpoint();
        }
        super.actionPerformed(e);
    }


}
