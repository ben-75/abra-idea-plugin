package org.qupla.runtime.debugger.ui;

import com.intellij.debugger.SourcePosition;
import com.intellij.openapi.wm.ToolWindow;
import com.jgoodies.common.collect.ArrayListModel;
import org.qupla.runtime.debugger.QuplaCallStackItem;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.List;
import java.util.Stack;

public class QuplaDebuggerToolWindow {
    public static final String ID = "Qupla Debugger:";
    private JPanel myToolWindowContent;
    private JList callstackItems;
    private JTree variables;
    private JSplitPane jSplitpane;

    public QuplaDebuggerToolWindow(ToolWindow toolWindow) {
        jSplitpane.setContinuousLayout(true);
        callstackItems.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        callstackItems.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                QuplaCallStackItem callStackItem = (QuplaCallStackItem) callstackItems.getSelectedValue();
                if(callStackItem!=null) {
                    SourcePosition sourcePosition = callStackItem.getSourcePosition();
                    if (sourcePosition != null) sourcePosition.navigate(true);
                }
            }
        });
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

    public void applyCallStack(List<QuplaCallStackItem> callstack){
        callstackItems.setModel(new ArrayListModel(callstack));
        callstackItems.setSelectedIndex(0);
    }
}
