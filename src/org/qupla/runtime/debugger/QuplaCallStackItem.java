package org.qupla.runtime.debugger;

import com.intellij.debugger.DebuggerManager;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebuggerUtils;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.evaluation.EvaluationContext;
import com.intellij.debugger.engine.evaluation.EvaluationContextImpl;
import com.intellij.debugger.engine.jdi.StackFrameProxy;
import com.intellij.debugger.engine.managerThread.DebuggerCommand;
import com.intellij.debugger.engine.managerThread.DebuggerManagerThread;
import com.intellij.debugger.impl.DebuggerManagerImpl;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.sun.jdi.Field;
import com.sun.tools.jdi.ArrayReferenceImpl;
import com.sun.tools.jdi.IntegerValueImpl;
import com.sun.tools.jdi.ObjectReferenceImpl;
import org.qupla.language.module.QuplaModuleManager;
import org.qupla.runtime.debugger.ui.TritVectorNode;
import org.qupla.runtime.debugger.ui.TritVectorView;
import org.qupla.runtime.debugger.ui.VariablesNode;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public class QuplaCallStackItem {

    private String modulePath;
    private final int lineNumber;  //1 based index (end user friendly)
    private final int colNumber;   //1 based index (end user friendly)
    private final int stackFrameIndex;
    private String expr;
    private String operation;
    private Project project;
    private MutableTreeNode rootNode;

    private SourcePosition sourcePosition;

    public QuplaCallStackItem(Project project, String operation, String expr, int lineNumber, int colNumber, int stackFrameIndex, String modulePath) {
        this.expr = expr;
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
        this.modulePath = modulePath;
        this.operation = operation;
        this.project = project;
        this.stackFrameIndex = stackFrameIndex;
    }

    public SourcePosition getSourcePosition() {
        if(sourcePosition==null){
            PsiFile quplaFile = project.getComponent(QuplaModuleManager.class).findQuplaFile(modulePath);
            if(quplaFile!=null) {
                Document quplaDoc = PsiDocumentManager.getInstance(project).getDocument(quplaFile);
                int offset = quplaDoc.getLineStartOffset(lineNumber - 1)+colNumber-1;
                sourcePosition = SourcePosition.createFromOffset(quplaFile, offset);
            }
        }
        return sourcePosition;
    }

    @Override
    public String toString() {
        return modulePath==null?expr:"<html>"+expr + "<font color=#808080>&nbsp;&nbsp;("+lineNumber+", " + modulePath.substring(modulePath.lastIndexOf("/")+1) + ")</font></html>";
    }

    public int getStackFrameIndex() {
        return stackFrameIndex;
    }

    public MutableTreeNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(MutableTreeNode rootNode) {
        this.rootNode = rootNode;
    }


}
