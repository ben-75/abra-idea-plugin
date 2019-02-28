package org.qupla.runtime.debugger;

import com.intellij.debugger.DebuggerManager;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.DebugProcessListener;
import com.intellij.debugger.engine.DebuggerManagerThreadImpl;
import com.intellij.debugger.engine.SuspendContext;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.evaluation.EvaluationContext;
import com.intellij.debugger.engine.events.DebuggerCommandImpl;
import com.intellij.debugger.engine.requests.RequestManagerImpl;
import com.intellij.debugger.impl.DebuggerManagerImpl;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.debugger.requests.RequestManager;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RemoteConnection;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.sun.jdi.*;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.tools.jdi.ArrayReferenceImpl;
import com.sun.tools.jdi.IntegerValueImpl;
import com.sun.tools.jdi.ObjectReferenceImpl;
import org.jetbrains.annotations.NotNull;
import org.qupla.language.module.QuplaModule;
import org.qupla.runtime.debugger.requestor.EvalEntryRequestor;
import org.qupla.runtime.debugger.requestor.EvalExitRequestor;
import org.qupla.runtime.debugger.ui.*;
import org.qupla.runtime.interpreter.QuplaInterpreterRunConfiguration;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.*;

public class QuplaDebugSession implements DebugProcessListener {

    private final DebugProcess debugProcess;
    private final QuplaInterpreterRunConfiguration runConfiguration;
    private Content content;
    private QuplaDebuggerToolWindow quplaDebuggerToolWindow;
    public static final String BASE_EXPR_CLASSNAME = "org.iota.qupla.qupla.expression.base.BaseExpr";
    private ConsoleView consoleView;
    private StringBuilder indent = new StringBuilder();

    public QuplaDebugSession(DebugProcess debugProcess, QuplaInterpreterRunConfiguration runConfiguration, String contextClassName) {
        this.debugProcess = debugProcess;
        this.runConfiguration = runConfiguration;
        debugProcess.addDebugProcessListener(this);
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

    boolean stopOnEntry = false;
    public void registerStepIntoBreakpoint() {
        stopOnEntry = true;
    }


    public void registerStepOverBreakpoint() {
        if(callStack.size()>1)
            callStack.get(1).setStopAfterPush();
    }


    public void registerStepOutBreakpoint() {
            callStack.get(0).setStopAfterDrop();
    }

    public List<QuplaCallStackItem> callStack = new ArrayList<>();

    public boolean processMethodEntry(QuplaCallStackItem item, LocatableEvent event) {
        if(!callStack.isEmpty()){
            if(callStack.get(0).isStopAfterPush())stopOnEntry=true;
            callStack.get(0).clearStopAfterPush();
        }
        callStack.add(0, item);
        if(consoleView!=null){

            consoleView.print(indent.toString()+item.getExpr(), ConsoleViewContentType.NORMAL_OUTPUT);
            if(item.getModulePath()!=null) {
                consoleView.print("    (", ConsoleViewContentType.NORMAL_OUTPUT);
                consoleView.printHyperlink(item.getLocationString(), new QuplaHyperlinkInfo(item.getModulePath(), item.getLine(), item.getCol()));
                consoleView.print(")", ConsoleViewContentType.NORMAL_OUTPUT);
            }
            consoleView.print("\n", ConsoleViewContentType.NORMAL_OUTPUT);

        }
        indent.append("  ");
        return isStopOnEntry();
    }

    public void clearCallstack() {

        callStack.clear();
        QuplaCallStackItem.Factory.clear();
    }

    public boolean processLeaveMethod(LocatableEvent event) {
        if (!callStack.isEmpty()) {
//            if (event instanceof MethodExitEvent) {
//                if (QuplaEvalContextRequestor.WATCHED_EVAL_METHODS.contains(((MethodExitEvent) event).method().name())) {
                    if((callStack.remove(0)).isStopAfterDrop()){
                        stopOnEntry = true;
                    };
                    indent.delete(indent.length()-2,indent.length());
                    QuplaCallStackItem.Factory.release();
//                }
//            }
        }
        return false;
    }

    public boolean isStopOnEntry(){
        if(stopOnEntry){
            stopOnEntry = false;
            return true;
        }
        return false;
    }

    private  Field stackField;
    private  Field stackFrameField;
    private  Field elementCountField;
    private  Field elementDataField;

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


    private  Field tritVectorNameField;
    private  Field tritVectorOffsetField;
    private  Field tritVectorSizeField;
    private  Field tritVectorVectorField;
    private  Field tritVectorValueTritField;
    private  List<Field> tritVectorFields;

    @NotNull
    public  MutableTreeNode buildVariableTree(EvaluationContext evaluationContext, int stackFrame, int elementCount, ArrayReferenceImpl arrayRef) throws EvaluateException {
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


    public QuplaCallStackItem getTopCallstackItem() {
        if(callStack.isEmpty())return null;
        return callStack.get(0);
    }


    @Override
    public void connectorIsReady() {

    }

    @Override
    public void paused(SuspendContext suspendContext) {

    }

    @Override
    public void resumed(SuspendContext suspendContext) {

    }

    @Override
    public void processDetached(DebugProcess process, boolean closedByUser) {
        debugProcess.getProject().getComponent(QuplaDebuggerManager.class).forgetSession(debugProcess.getProcessHandler());
        isActive = false;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                quplaDebuggerToolWindow.applyCallStack(null);
                content.setDisplayName(content.getDisplayName()+" (DEAD)");
//                content.getManager().removeContent(content,true);
//                ToolWindowManager.getActiveToolWindow().hide(null);
                quplaDebuggerToolWindow.hideDebugTab();
            }
        });
    }

    @Override
    public void processAttached(DebugProcess process) {
        QuplaEvalContextMapper mapper = new QuplaEvalContextMapper();
        RequestManager requestManager = debugProcess.getRequestsManager();
        //we are interested by the evaluation context class to register breakpoints
        //in eval methods
        ClassPrepareRequest requestPrepareContext = requestManager.createClassPrepareRequest(new ContextPreparator(mapper),runConfiguration.getContextClassName());

        //we are interested by base expr class to introspect it and get reference to
        //field references required during runtime evaluation of the interpreter state.
        //(this could have been done lazily...)
        ClassPrepareRequest requestPrepareBaseExpr = requestManager.createClassPrepareRequest(new BaseExprPreparator(mapper),BASE_EXPR_CLASSNAME);
        ClassPrepareRequest requestPrepareToken = requestManager.createClassPrepareRequest(new TokenPreparator(mapper),BASE_EXPR_CLASSNAME);
        requestManager.enableRequest(requestPrepareContext);
        requestManager.enableRequest(requestPrepareBaseExpr);
        requestManager.enableRequest(requestPrepareToken);
        isActive = true;
    }

    @Override
    public void attachException(RunProfileState state, ExecutionException exception, RemoteConnection remoteConnection) {

    }

    @Override
    public void threadStarted(DebugProcess proc, ThreadReference thread) {

    }

    @Override
    public void threadStopped(DebugProcess proc, ThreadReference thread) {

    }

    private boolean isActive = false;
    public boolean isActive() {
        return isActive;
    }

    public void registerConsoleView(ConsoleViewImpl consoleView) {
        this.consoleView = consoleView;
    }

    private class ContextPreparator implements ClassPrepareRequestor {

        private QuplaEvalContextMapper mapper;

        public ContextPreparator(QuplaEvalContextMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        public void processClassPrepare(DebugProcess debuggerProcess, ReferenceType referenceType) {
            System.out.println("Prepare context");
            mapper.setStackFrameField(referenceType.fieldByName("stackFrame"));
            for (String methodName : new String[]{"evalAssign", "evalSlice", "evalLutLookup",
                    "evalMerge", "evalFuncCall", "evalConcat", "evalConditional", "evalType", "evalState"}) {
                EvalEntryRequestor entryRequestor = new EvalEntryRequestor(QuplaDebugSession.this, methodName, mapper);
                EvalExitRequestor exitRequestor = new EvalExitRequestor(QuplaDebugSession.this, methodName);
                QuplaDebuggerUtil.registerContextBreakpoints(debugProcess, referenceType, methodName, entryRequestor, exitRequestor);
            }
            //TODO : "evalState
        }
    }

    private class BaseExprPreparator implements ClassPrepareRequestor {

        private QuplaEvalContextMapper mapper;

        public BaseExprPreparator(QuplaEvalContextMapper mapper) {
            this.mapper = mapper;
        }
        @Override
        public void processClassPrepare(DebugProcess debuggerProcess, ReferenceType referenceType) {
            mapper.setTokenField(referenceType.fieldByName("origin"));
        }
    }

    private class TokenPreparator implements ClassPrepareRequestor {

        private QuplaEvalContextMapper mapper;

        public TokenPreparator(QuplaEvalContextMapper mapper) {
            this.mapper = mapper;
        }
        @Override
        public void processClassPrepare(DebugProcess debuggerProcess, ReferenceType referenceType) {
            mapper.setLineNrField(referenceType.fieldByName("lineNr"));
            mapper.setColNrField(referenceType.fieldByName("colNr"));
        }
    }
}
