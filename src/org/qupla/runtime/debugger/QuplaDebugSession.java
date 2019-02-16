package org.qupla.runtime.debugger;

import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.evaluation.EvaluationContext;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.tools.jdi.ArrayReferenceImpl;
import com.sun.tools.jdi.IntegerValueImpl;
import com.sun.tools.jdi.ObjectReferenceImpl;
import org.jetbrains.annotations.NotNull;
import org.qupla.language.module.QuplaModule;
import org.qupla.runtime.debugger.ui.*;
import org.qupla.runtime.interpreter.QuplaInterpreterRunConfiguration;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.*;

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

    public void publishCallStack() {
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

    public void registerStepIntoBreakpoint() {

    }


    public void registerStepOverBreakpoint() {

    }


    public void registerStepOutBreakpoint() {

    }

    public List<QuplaCallStackItem> callStack = new ArrayList<>();

    public boolean processMethodEntry(QuplaCallStackItem item, LocatableEvent event) {
        callStack.add(0, item);
        return false;
    }

    public void clearCallstack() {

        callStack.clear();
        QuplaCallStackItem.Factory.clear();
    }

    public boolean processLeaveMethod(LocatableEvent event) {
        if (!callStack.isEmpty()) {
            if (event instanceof MethodExitEvent) {
                if (QuplaEvalContextRequestor.WATCHED_EVAL_METHODS.contains(((MethodExitEvent) event).method().name())) {
                    callStack.remove(0);
                    QuplaCallStackItem.Factory.release();
                }
            }
        }
        return false;
    }

    private static Field stackField;
    private static Field stackFrameField;
    private static Field elementCountField;
    private static Field elementDataField;

    public void updateQuplaDebuggerWindow(StackFrameProxyImpl frame, EvaluationContext evaluationContext) {

        //lazy init
        try {
            if (stackField == null) {
                stackField = frame.thisObject().referenceType().fieldByName("stack");
                stackFrameField = frame.thisObject().referenceType().fieldByName("stackFrame");
                ObjectReferenceImpl stackReference = (ObjectReferenceImpl) frame.thisObject().getValue(stackField);
                elementCountField = stackReference.referenceType().fieldByName("elementCount");
                elementDataField = stackReference.referenceType().fieldByName("elementData");
            }
        }catch (EvaluateException e){
            e.printStackTrace();
            stackField = null;
            return;
        }


        //prepare callstack
        ObjectReferenceImpl stackReference = null;
        try {
            stackReference = (ObjectReferenceImpl) frame.thisObject().getValue(stackField);
        }catch (EvaluateException e){
            e.printStackTrace();
            return;
        }
        ArrayReferenceImpl arrayRef = (ArrayReferenceImpl) stackReference.getValue(elementDataField);

        int currentFromIndex = -1;
        int currentToIndex = ((IntegerValueImpl) stackReference.getValue(elementCountField)).intValue();
        int oldFromIndex = -1;

        for(QuplaCallStackItem callStackItem:callStack) {
            if(callStackItem!=callStack.get(callStack.size()-1)) {
                try {
                    currentFromIndex = callStackItem.getStackFrameIndex();
                    if (oldFromIndex != -1) {
                        if (currentFromIndex != oldFromIndex) {
                            currentToIndex = oldFromIndex;
                        }
                    }

                    MutableTreeNode root = buildVariableTree(evaluationContext, currentFromIndex, currentToIndex, arrayRef);

                    callStackItem.setRootNode(root);

                    oldFromIndex = currentFromIndex;
                } catch (EvaluateException e) {
                    e.printStackTrace();
                    callStackItem.setRootNode(new DefaultMutableTreeNode("not available"));
                }
            }else{
                callStackItem.setRootNode(new DefaultMutableTreeNode("(no variables)"));
            }
        }


        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                QuplaDebugSession session = debugProcess.getProject().getComponent(QuplaDebuggerManager.class).getSession(evaluationContext.getDebugProcess());
                if(session!=null){
                    session.publishCallStack();
                }
            }
        });
    }


    private static Field tritVectorNameField;
    private static Field tritVectorOffsetField;
    private static Field tritVectorSizeField;
    private static Field tritVectorVectorField;
    private static Field tritVectorValueTritField;
    private static List<Field> tritVectorFields;

    @NotNull
    public static MutableTreeNode buildVariableTree(EvaluationContext evaluationContext, int stackFrame, int elementCount, ArrayReferenceImpl arrayRef) throws EvaluateException {
        MutableTreeNode root = new VariablesNode();
        for (int j = stackFrame; j < elementCount; j++) {
            ObjectReferenceImpl tritVectorRef = (ObjectReferenceImpl) arrayRef.getValue(j);
            if (tritVectorNameField == null) {
                tritVectorNameField = tritVectorRef.referenceType().fieldByName("name");
                tritVectorOffsetField = tritVectorRef.referenceType().fieldByName("offset");
                tritVectorSizeField = tritVectorRef.referenceType().fieldByName("size");
                tritVectorVectorField = tritVectorRef.referenceType().fieldByName("vector");
                tritVectorValueTritField = tritVectorRef.referenceType().fieldByName("valueTrits");
                tritVectorFields = Arrays.asList(tritVectorNameField,tritVectorOffsetField,tritVectorSizeField,tritVectorVectorField,tritVectorValueTritField);
            }

            Map<Field,Value> values = tritVectorRef.getValues(tritVectorFields);

            String name = ((StringReference)values.get(tritVectorNameField)).value();


//            String vector = DebuggerUtils.getValueAsString(
//                    evaluationContext, tritVectorRef.getValue(tritVectorVectorField));
            ObjectReference tritBufferRef = (ObjectReference) tritVectorRef.getValue(tritVectorVectorField);
            String vector = ((StringReference)evaluationContext.getDebugProcess().invokeInstanceMethod(
                    evaluationContext, tritBufferRef,
                    tritBufferRef.referenceType().methodsByName("toString").get(0),
                    Collections.EMPTY_LIST,0)).value();

            int size = ((IntegerValueImpl) values.get(tritVectorSizeField)).intValue();
            int offset = ((IntegerValueImpl) values.get(tritVectorOffsetField)).intValue();
            int valueTrit = ((IntegerValueImpl) values.get(tritVectorValueTritField)).intValue();

            TritVectorView tritVectorView = new TritVectorView(name, offset, size, valueTrit, vector);
            ((VariablesNode) root).add(new TritVectorNode(root, tritVectorView));
        }
        return root;
    }


}
