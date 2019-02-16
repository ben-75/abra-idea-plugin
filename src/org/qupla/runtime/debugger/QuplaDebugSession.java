package org.qupla.runtime.debugger;

import com.intellij.debugger.engine.DebugProcess;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;
import org.qupla.language.module.QuplaModule;
import org.qupla.runtime.debugger.ui.QuplaDebuggerManager;
import org.qupla.runtime.debugger.ui.QuplaDebuggerToolWindow;
import org.qupla.runtime.interpreter.QuplaInterpreterRunConfiguration;

import javax.swing.*;
import java.util.List;

public class QuplaDebugSession extends ProcessAdapter {

    private final DebugProcess debugProcess;
    private final QuplaInterpreterRunConfiguration runConfiguration;
    private Content content;
    private QuplaDebuggerToolWindow quplaDebuggerToolWindow;

    public QuplaDebugSession(DebugProcess debugProcess, QuplaInterpreterRunConfiguration runConfiguration) {
        this.debugProcess = debugProcess;
        this.runConfiguration = runConfiguration;
        debugProcess.getProcessHandler().addProcessListener(this);
    }

    @Override
    public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) {
        debugProcess.getProject().getComponent(QuplaDebuggerManager.class).forgetSession(debugProcess.getProcessHandler());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                quplaDebuggerToolWindow.applyCallStack(null);
                content.setDisplayName(content.getDisplayName()+" (DEAD)");
            }
        });
    }



    public void setContent(Content content) {
        this.content = content;
    }

    public void publishCallStack(List<QuplaCallStackItem> callStack) {
        quplaDebuggerToolWindow.applyCallStack(callStack);
    }

    public void setQuplaDebuggerToolWindow(QuplaDebuggerToolWindow quplaDebuggerToolWindow) {
        this.quplaDebuggerToolWindow = quplaDebuggerToolWindow;
    }

    public Project getProject() {
        return debugProcess.getProject();
    }

    public DebugProcess getDebugProcess() {
        return debugProcess;
    }

    public QuplaInterpreterRunConfiguration getRunConfiguration() {
        return runConfiguration;
    }
}
