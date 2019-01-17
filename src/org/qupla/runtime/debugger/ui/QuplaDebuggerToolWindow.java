package org.qupla.runtime.debugger.ui;

import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

public class QuplaDebuggerToolWindow {
    public static final String ID = "Qupla Debugger:";
    private JPanel myToolWindowContent;

    public QuplaDebuggerToolWindow(ToolWindow toolWindow) {
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }
}
