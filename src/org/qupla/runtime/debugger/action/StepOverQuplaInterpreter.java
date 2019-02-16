package org.qupla.runtime.debugger.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.qupla.runtime.debugger.QuplaDebugSession;

import javax.swing.*;

public class StepOverQuplaInterpreter extends QuplaDebugAction {

    public StepOverQuplaInterpreter() {
        this(null);
    }

    public StepOverQuplaInterpreter(QuplaDebugSession session) {
        super(session,AllIcons.Actions.TraceOver);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        if(session!=null){
            session.registerStepOverBreakpoint();
        }
        super.actionPerformed(e);
    }

}
