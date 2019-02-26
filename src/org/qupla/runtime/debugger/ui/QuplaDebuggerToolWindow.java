package org.qupla.runtime.debugger.ui;

import com.intellij.debugger.SourcePosition;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.ActionPanel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.xdebugger.impl.actions.ResumeAction;
import com.jgoodies.common.collect.ArrayListModel;
import org.qupla.ide.ui.QuplaIcons;
import org.qupla.runtime.debugger.QuplaCallStackItem;
import org.qupla.runtime.debugger.QuplaDebugSession;
import org.qupla.runtime.debugger.action.ResumeQuplaInterpreter;
import org.qupla.runtime.debugger.action.StepIntoQuplaInterpreter;
import org.qupla.runtime.debugger.action.StepOutQuplaInterpreter;
import org.qupla.runtime.debugger.action.StepOverQuplaInterpreter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.util.List;

import static com.intellij.execution.ui.ConsoleViewContentType.LOG_INFO_OUTPUT;
import static com.intellij.execution.ui.ConsoleViewContentType.NORMAL_OUTPUT;

public class QuplaDebuggerToolWindow {
    public static final String ID = "Qupla Debugger";
    private JPanel myToolWindowContent;
    private JList callstackItems;
    private JTree variables;
    private JSplitPane jSplitpane;
    private QuplaRunnerTabs myTabs;
    private QuplaDebugSession mySession;

    public QuplaDebuggerToolWindow(Project project, QuplaDebugSession session) {
        this.mySession = session;
        myTabs = new QuplaRunnerTabs(project);
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


        TabInfo debuggerTabInfo = new TabInfo(jSplitpane);
        debuggerTabInfo.setText("Debugger");
        //debuggerTabInfo.setActions()
        ActionGroup actionGroup = new DefaultActionGroup();
        ((DefaultActionGroup) actionGroup).add(new StepOverQuplaInterpreter(mySession));
        ((DefaultActionGroup) actionGroup).add(new StepIntoQuplaInterpreter(mySession));
        ((DefaultActionGroup) actionGroup).add(new StepOutQuplaInterpreter(mySession));
        debuggerTabInfo.setActions(actionGroup,null);
        myTabs.addTab(debuggerTabInfo);

        ConsoleViewImpl consoleView = new ConsoleViewImpl(project,true);
        TabInfo consoleTabInfo = new TabInfo(consoleView.getComponent());
        consoleTabInfo.setText("Console");
        consoleTabInfo.setIcon(AllIcons.Debugger.Console);
        consoleTabInfo.setActions(actionGroup,null);
        myTabs.addTab(consoleTabInfo);
        mySession.registerConsoleView(consoleView);

        myToolWindowContent = new JPanel(new BorderLayout());
        myToolWindowContent.add(myTabs,BorderLayout.CENTER);

        DefaultActionGroup globalActionGroup = new DefaultActionGroup();
        ResumeAction resumeAction = new ResumeAction();
        resumeAction.getTemplatePresentation().setIcon(AllIcons.Actions.Resume);
        globalActionGroup.add(resumeAction);
        ActionToolbarImpl globalActionPanel = (ActionToolbarImpl) ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN,globalActionGroup,false);
        globalActionPanel.setBorder(BorderFactory.createLineBorder(JBColor.LIGHT_GRAY, 1));
        myToolWindowContent.add(globalActionPanel,BorderLayout.WEST);
        jSplitpane.setDividerLocation(300);

    }

    public void hideDebugTab(){
        myTabs.remove(0);
    }


    public JComponent getContent() {
        return myToolWindowContent;
    }

    public void applyCallStack(List<QuplaCallStackItem> callstack){
        if(callstack==null){
            callstackItems.setModel(new ArrayListModel());
            variables.setModel(null);
        }else {
            callstackItems.setModel(new ArrayListModel(callstack));
            callstackItems.setSelectedIndex(0);
        }
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
