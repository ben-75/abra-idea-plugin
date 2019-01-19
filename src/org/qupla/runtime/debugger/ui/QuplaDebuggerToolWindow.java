package org.qupla.runtime.debugger.ui;

import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.jdi.StackFrameProxy;
import com.intellij.debugger.jdi.LocalVariableProxyImpl;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.GroupedElementsRenderer;
import com.intellij.ui.treeStructure.Tree;
import com.jgoodies.common.collect.ArrayListModel;
import org.jetbrains.debugger.Variable;
import org.qupla.ide.ui.QuplaIcons;
import org.qupla.runtime.debugger.QuplaCallStackItem;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
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
                    TreeNode root = callStackItem.getRootNode();
                    if(root!=null){
                        TreeModel treeModel = new DefaultTreeModel(root);

                        variables.setModel(treeModel);
                    }
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

    private void createUIComponents() {
        variables = new Tree();
        TreeCellRenderer renderer = new DefaultTreeCellRenderer(){
            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                                                          Object value, boolean selected, boolean expanded,
                                                          boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, selected,expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) value;
                if (tree.getModel().getRoot().equals(nodo)) {
                    setIcon(QuplaIcons.QUPLA_VALUES);
                } else if (nodo.getChildCount() > 0) {
                    setIcon(null);
                } else {
                    setIcon(QuplaIcons.VECTOR);
                }
                return this;
            }
        };
        variables.setCellRenderer(renderer);

    }
}
