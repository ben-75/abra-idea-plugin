package org.qupla.runtime.debugger;

import com.intellij.debugger.DebuggerBundle;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.DebuggerUtils;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.evaluation.EvaluationContext;
import com.intellij.debugger.engine.evaluation.EvaluationContextImpl;
import com.intellij.debugger.engine.events.SuspendContextCommandImpl;
import com.intellij.debugger.engine.requests.RequestManagerImpl;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.debugger.jdi.ThreadReferenceProxyImpl;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.debugger.ui.breakpoints.FilteredRequestor;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.xdebugger.impl.actions.FocusOnBreakpointAction;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.tools.jdi.*;
import org.qupla.runtime.debugger.ui.QuplaDebuggerToolWindow;
import org.qupla.runtime.debugger.ui.TritVectorNode;
import org.qupla.runtime.debugger.ui.TritVectorView;
import org.qupla.runtime.debugger.ui.VariablesNode;

import javax.swing.*;
import javax.swing.tree.MutableTreeNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class create debugger request for the QuplaEvalContext class.
 * It will request entry and leave breakpoints on the following strategical methods of interest :
 * QuplaEvalContext.evalAssign(final AssignExpr assign)
 * QuplaEvalContext.evalConcat(final ConcatExpr concat)
 * QuplaEvalContext.evalConditional(final CondExpr conditional)
 * QuplaEvalContext.evalFuncCall(final FuncExpr call)
 * QuplaEvalContext.evalLutLookup(final LutExpr lookup)
 * QuplaEvalContext.evalMerge(final MergeExpr merge)
 * QuplaEvalContext.evalSlice(final SliceExpr slice)
 * QuplaEvalContext.evalState(final StateExpr state)
 * QuplaEvalContext.evalType(final TypeExpr type)
 * QuplaEvalContext.evalVector(final VectorExpr integer)
 */
public class QuplaEvalContextRequestor implements ClassPrepareRequestor {

    public static final String BASE_EXPR_CLASSNAME = "org.iota.qupla.qupla.expression.base.BaseExpr";

    private static final String[] watchedEvalMethods = new String[]{"evalAssign", "evalSlice", "evalConcat", "evalLutLookup",
            "evalMerge", "evalFuncCall", "evalVector", "evalConditional", "evalState", "evalType"};

    public List<QuplaCallStackItem> callStack = new ArrayList<>();
    private boolean requestRegistered = false;
    private final Project myProject;

    public QuplaEvalContextRequestor(Project project) {
        this.myProject = project;
    }

    public List<QuplaCallStackItem> getCallStack() {
        return callStack;
    }

    @Override
    public void processClassPrepare(DebugProcess debugProcess, ReferenceType referenceType) {
        System.out.println("processClassPrepare");
        if (!requestRegistered) {
            System.out.println("need to process class prepare here");
            RequestManagerImpl requestsManager = (RequestManagerImpl) debugProcess.getRequestsManager();

            FilteredRequestor enterEvalRequestor = new EnterEvalRequestor();
            MethodEntryRequest methodEntryRequest = requestsManager.createMethodEntryRequest(enterEvalRequestor);
            requestsManager.enableRequest(methodEntryRequest);

            FilteredRequestor leaveEvalRequestor = new LeaveEvalRequestor();
            MethodExitRequest methodExitRequest = requestsManager.createMethodExitRequest(leaveEvalRequestor);
            requestsManager.enableRequest(methodExitRequest);

            callStack.clear();


            requestRegistered = true;
        }
    }

    private class LeaveEvalRequestor extends QuplaEvalContextFilteredAbstractRequestor {
        @Override
        public boolean processLocatableEvent(SuspendContextCommandImpl action, LocatableEvent event) throws EventProcessingException {
            if (!callStack.isEmpty()) {
                if (event instanceof MethodExitEvent) {
                    if (Arrays.asList(watchedEvalMethods).contains(((MethodExitEvent) event).method().name())) {
                        callStack.remove(0);
                    }
                }
            }
            return false;
        }

    }

    private class EnterEvalRequestor extends QuplaEvalContextFilteredAbstractRequestor {


        public EnterEvalRequestor() {
        }

        @Override
        public boolean processLocatableEvent(SuspendContextCommandImpl action, LocatableEvent event) throws EventProcessingException {
            if (Arrays.asList(watchedEvalMethods).contains(((MethodEntryEvent) event).method().name())) {
                SuspendContextImpl context = action.getSuspendContext();

                String title = DebuggerBundle.message("title.error.evaluating.breakpoint.condition");
                try {
                    StackFrameProxyImpl frameProxy = context.getThread().frame(0);
                    if (frameProxy == null) {
                        // might be if the thread has been collected
                        return false;
                    }
                    EvaluationContextImpl evaluationContext = new EvaluationContextImpl(context, frameProxy, () -> getThisObject(context, event));
                    List<Value> args = frameProxy.getArgumentValues();
                    if (args.size() == 1) {
                        Value expr = args.get(0);
                        if (expr instanceof ObjectReferenceImpl) {
                            ReferenceTypeImpl abraExprType = null;
                            if (expr.type().name().equals("java.util.ArrayList")) {
                                ArrayReferenceImpl arrRef = (ArrayReferenceImpl) ((ObjectReferenceImpl) expr).getValue(((ClassTypeImpl) expr.type()).fieldByName("elementData"));
                                expr = arrRef.getValue(0);
                            }
                            abraExprType = (ReferenceTypeImpl) DebuggerUtils.getSuperType(expr.type(), BASE_EXPR_CLASSNAME);
                            if (abraExprType != null) {
                                Field originField = abraExprType.fieldByName("origin");
                                ObjectReferenceImpl token = (ObjectReferenceImpl) ((ObjectReferenceImpl) expr).getValue(originField);
                                Field sourceField = token.referenceType().fieldByName("source");
                                Field lineNrField = token.referenceType().fieldByName("lineNr");
                                Field colNrField = token.referenceType().fieldByName("colNr");
                                int lineNumber = ((IntegerValueImpl) token.getValue(lineNrField)).intValue();
                                int colNumber = ((IntegerValueImpl) token.getValue(colNrField)).intValue();
                                String exprString = DebuggerUtils.getValueAsString(evaluationContext, expr);
                                String modulePath = DebuggerUtils.getValueAsString(
                                        evaluationContext,
                                        token.getValue(token.referenceType().fieldByName("source")));
                                QuplaCallStackItem item = new QuplaCallStackItem(myProject, ((MethodEntryEvent) event).method().name(), exprString, lineNumber + 1, colNumber + 1, modulePath);
                                item.setStackFrameProxy(frameProxy);
                                item.setRootNode(buildTree(frameProxy,evaluationContext));
                                callStack.add(0, item);
                                return false;
                            }
                        }
                    }
                } catch (final EvaluateException ex) {
                    if (ApplicationManager.getApplication().isUnitTestMode()) {
                        System.out.println(ex.getMessage());
                        return false;
                    }

                    throw new EventProcessingException(title, ex.getMessage(), ex);
                }
            }
            return false;
        }

    }


    private static Field stackField;
    private static Field stackFrameField;
    private static Field elementCountField;
    private static Field elementDataField;
    private static Field tritVectorNameField;
    private static Field tritVectorOffsetField;
    private static Field tritVectorSizeField;
    private static Field tritVectorVectorField;
    private static Field tritVectorValueTritField;


    private MutableTreeNode buildTree(StackFrameProxyImpl frame, EvaluationContext evaluationContext) throws EvaluateException {
        if (stackField == null) {
            stackField = frame.thisObject().referenceType().fieldByName("stack");
            stackFrameField = frame.thisObject().referenceType().fieldByName("stackFrame");
            ObjectReferenceImpl stackReference = (ObjectReferenceImpl) frame.thisObject().getValue(stackField);
            elementCountField = stackReference.referenceType().fieldByName("elementCount");
            elementDataField = stackReference.referenceType().fieldByName("elementData");
        }
        ObjectReferenceImpl stackReference = (ObjectReferenceImpl) frame.thisObject().getValue(stackField);
        int stackFrame = ((IntegerValueImpl) frame.thisObject().getValue(stackFrameField)).intValue();
        int elementCount = ((IntegerValueImpl) stackReference.getValue(elementCountField)).intValue();
        ArrayReferenceImpl arrayRef = (ArrayReferenceImpl) stackReference.getValue(elementDataField);

        MutableTreeNode root = new VariablesNode();

        for (int j = stackFrame; j < elementCount; j++) {
            ObjectReferenceImpl tritVectorRef = (ObjectReferenceImpl) arrayRef.getValue(j);
            if (tritVectorNameField == null) {
                tritVectorNameField = tritVectorRef.referenceType().fieldByName("name");
                tritVectorOffsetField = tritVectorRef.referenceType().fieldByName("offset");
                tritVectorSizeField = tritVectorRef.referenceType().fieldByName("size");
                tritVectorVectorField = tritVectorRef.referenceType().fieldByName("vector");
                tritVectorValueTritField = tritVectorRef.referenceType().fieldByName("valueTrits");
            }
            String name = DebuggerUtils.getValueAsString(
                    evaluationContext, tritVectorRef.getValue(tritVectorNameField));
            String vector = DebuggerUtils.getValueAsString(
                    evaluationContext, tritVectorRef.getValue(tritVectorVectorField));
            int size = ((IntegerValueImpl) tritVectorRef.getValue(tritVectorSizeField)).intValue();
            int offset = ((IntegerValueImpl) tritVectorRef.getValue(tritVectorOffsetField)).intValue();
            int valueTrit = ((IntegerValueImpl) tritVectorRef.getValue(tritVectorValueTritField)).intValue();

            TritVectorView tritVectorView = new TritVectorView(name, offset, size, valueTrit, vector);
            ((VariablesNode) root).add(new TritVectorNode(root, tritVectorView));
        }

        return root;
    }


    public void updateQuplaDebuggerWindow() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                if (quplaDebuggerToolWindow == null) {
                    ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow(ToolWindowId.DEBUG);
                    ContentManager contentManager = toolWindow.getContentManager();
                    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                    quplaDebuggerToolWindow = new QuplaDebuggerToolWindow(toolWindow);
                    Content content = contentFactory.createContent(quplaDebuggerToolWindow.getContent(), "Qupla CallStack", false);
                    toolWindow.getContentManager().addContent(content);
                }
                quplaDebuggerToolWindow.applyCallStack(getCallStack());
            }
        });
    }

    QuplaDebuggerToolWindow quplaDebuggerToolWindow;

    protected ObjectReference getThisObject(SuspendContextImpl context, LocatableEvent event) throws EvaluateException {
        ThreadReferenceProxyImpl thread = context.getThread();
        if (thread != null) {
            StackFrameProxyImpl stackFrameProxy = thread.frame(0);
            if (stackFrameProxy != null) {
                return stackFrameProxy.thisObject();
            }
        }
        return null;
    }

}
