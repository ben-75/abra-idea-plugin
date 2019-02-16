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
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.sun.jdi.*;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.tools.jdi.*;
import org.jetbrains.annotations.NotNull;
import org.qupla.runtime.debugger.ui.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.*;

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

    public static final List<String> WATCHED_EVAL_METHODS = Arrays.asList(new String[]{"evalAssign", "evalSlice", "evalConcat", "evalLutLookup",
            "evalMerge", "evalFuncCall", "evalVector", "evalConditional", "evalState", "evalType"});

    private boolean requestRegistered = false;
    private final Project myProject;
    private final QuplaDebugSession session;

    public QuplaEvalContextRequestor(QuplaDebugSession session) {
        this.session = session;
        this.myProject = session.getProject();
    }

    @Override
    public void processClassPrepare(DebugProcess debugProcess, ReferenceType referenceType) {
        if (!requestRegistered) {
//            tritVectorNameField = null;
//            stackField = null;
            RequestManagerImpl requestsManager = (RequestManagerImpl) debugProcess.getRequestsManager();

            FilteredRequestor enterEvalRequestor = new EnterEvalRequestor();
            MethodEntryRequest methodEntryRequest = requestsManager.createMethodEntryRequest(enterEvalRequestor);
            requestsManager.enableRequest(methodEntryRequest);

            FilteredRequestor leaveEvalRequestor = new LeaveEvalRequestor();
            MethodExitRequest methodExitRequest = requestsManager.createMethodExitRequest(leaveEvalRequestor);
            requestsManager.enableRequest(methodExitRequest);

            session.clearCallstack();

            requestRegistered = true;
        }
    }

    private class LeaveEvalRequestor extends QuplaEvalContextFilteredAbstractRequestor {
        @Override
        public boolean processLocatableEvent(SuspendContextCommandImpl action, LocatableEvent event) throws EventProcessingException {
            return session.processLeaveMethod(event);
        }

    }

    private class EnterEvalRequestor extends QuplaEvalContextFilteredAbstractRequestor {


        public EnterEvalRequestor() {
        }

        @Override
        public boolean processLocatableEvent(SuspendContextCommandImpl action, LocatableEvent event) throws EventProcessingException {
            if (WATCHED_EVAL_METHODS.contains(((MethodEntryEvent) event).method().name())) {
                SuspendContextImpl context = action.getSuspendContext();

                String title = DebuggerBundle.message("title.error.evaluating.breakpoint.condition");
                try {
                    StackFrameProxyImpl frameProxy = context.getThread().frame(0);
                    if (frameProxy == null) {
                        // might be if the thread has been collected
                        return false;
                    }
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
                                ObjectReferenceImpl token = (ObjectReferenceImpl) ((ObjectReferenceImpl) expr).getValue(abraExprType.fieldByName("origin"));
                                if(sourceField==null){
                                    sourceField = token.referenceType().fieldByName("source");
                                    lineNrField = token.referenceType().fieldByName("lineNr");
                                    colNrField = token.referenceType().fieldByName("colNr");
                                    //stackFrameField = frameProxy.thisObject().referenceType().fieldByName("stackFrame");
                                    lineAndColField = Arrays.asList(lineNrField, colNrField);
                                }
                                Map<Field,Value> lineAndCol = token.getValues(Arrays.asList(
                                        token.referenceType().fieldByName("lineNr"),token.referenceType().fieldByName("colNr")));
                                int lineNumber = ((IntegerValueImpl) lineAndCol.get(token.referenceType().fieldByName("lineNr"))).intValue();
                                int colNumber = ((IntegerValueImpl) lineAndCol.get(token.referenceType().fieldByName("colNr"))).intValue();
                                int stackFrame = ((IntegerValueImpl) frameProxy.thisObject().getValue(frameProxy.thisObject().referenceType().fieldByName("stackFrame"))).intValue();
                                EvaluationContextImpl evaluationContext = new EvaluationContextImpl(context, frameProxy, () -> getThisObject(context, event));
                                String exprString = ((StringReference)context.getDebugProcess().invokeInstanceMethod(
                                        evaluationContext, (ObjectReference) expr,
                                        ((ObjectReferenceImpl) expr).referenceType().methodsByName("toString").get(0),
                                        Collections.EMPTY_LIST,0)).value();

                                        //DebuggerUtils.getValueAsString(evaluationContext, expr);
                                ObjectReferenceImpl srcRef = (ObjectReferenceImpl) token.getValue(token.referenceType().fieldByName("source"));
                                String modulePath = srcRef==null?null:((StringReference)context.getDebugProcess().invokeInstanceMethod(
                                        evaluationContext, (ObjectReference) srcRef,
                                        srcRef.referenceType().methodsByName("toString").get(0),
                                        Collections.EMPTY_LIST,0)).value();


                                //DebuggerUtils.getValueAsString(evaluationContext, token.getValue(sourceField));

                                QuplaCallStackItem item = QuplaCallStackItem.Factory.newQuplaCallStackItem(myProject,
                                        ((MethodEntryEvent) event).method().name(),
                                        exprString,
                                        lineNumber + 1, colNumber + 1, stackFrame, modulePath);
                                return session.processMethodEntry(item,event);

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

    private static Field originField;
    private static Field lineNrField;
    private static Field colNrField;
    private static Field sourceField;
    private static List<Field> lineAndColField;




//    private MutableTreeNode buildTree(StackFrameProxyImpl frame, EvaluationContext evaluationContext) throws EvaluateException {
//        if (stackField == null) {
//            stackField = frame.thisObject().referenceType().fieldByName("stack");
//            stackFrameField = frame.thisObject().referenceType().fieldByName("stackFrame");
//            ObjectReferenceImpl stackReference = (ObjectReferenceImpl) frame.thisObject().getValue(stackField);
//            elementCountField = stackReference.referenceType().fieldByName("elementCount");
//            elementDataField = stackReference.referenceType().fieldByName("elementData");
//        }
//        ObjectReferenceImpl stackReference = (ObjectReferenceImpl) frame.thisObject().getValue(stackField);
//        int stackFrame = ((IntegerValueImpl) frame.thisObject().getValue(stackFrameField)).intValue();
//        int elementCount = ((IntegerValueImpl) stackReference.getValue(elementCountField)).intValue();
//        ArrayReferenceImpl arrayRef = (ArrayReferenceImpl) stackReference.getValue(elementDataField);
//
//        MutableTreeNode root = buildVariableTree(evaluationContext, stackFrame, elementCount, arrayRef);
//
//        return root;
//    }
//



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
