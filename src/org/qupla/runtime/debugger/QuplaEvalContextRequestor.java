package org.qupla.runtime.debugger;

import com.intellij.debugger.DebuggerBundle;
import com.intellij.debugger.InstanceFilter;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.DebuggerUtils;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.evaluation.EvaluationContextImpl;
import com.intellij.debugger.engine.events.SuspendContextCommandImpl;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.debugger.jdi.ThreadReferenceProxyImpl;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.debugger.settings.DebuggerSettings;
import com.intellij.debugger.ui.breakpoints.FilteredRequestor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.classFilter.ClassFilter;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.tools.jdi.*;

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

    private static final String[] watchedEvalMethods = new String[]{"evalAssign","evalSlice","evalConcat","evalLutLookup",
            "evalMerge","evalFuncCall","evalVector","evalConditional","evalState","evalType"};

    public static List<QuplaCallStackItem> callStack = new ArrayList<>();


    public QuplaEvalContextRequestor() {
    }

    @Override
    public void processClassPrepare(DebugProcess debuggerProcess, ReferenceType referenceType) {

    }

    private class LeaveEvalRequestor extends EvalContextFilteredRequestor {
        @Override
        public boolean processLocatableEvent(SuspendContextCommandImpl action, LocatableEvent event) throws EventProcessingException {
            if(!callStack.isEmpty()){
                if(event instanceof MethodExitEvent) {
                    if(Arrays.asList(watchedEvalMethods).contains(((MethodExitEvent) event).method().name())) {
                        callStack.remove(0);
                    }
                }
            }
            return false;
        }

    }

    private class EnterEvalAssignRequestor extends  EvalContextFilteredRequestor {


        public EnterEvalAssignRequestor() {
        }

        @Override
        public boolean processLocatableEvent(SuspendContextCommandImpl action, LocatableEvent event) throws EventProcessingException {
            if(Arrays.asList(watchedEvalMethods).contains(((MethodEntryEvent) event).method().name())) {
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
                            abraExprType = (ReferenceTypeImpl) DebuggerUtils.getSuperType(expr.type(), "org.iota.abra.helper.AbraExpr");
                            if (abraExprType != null) {
                                Field originField = abraExprType.fieldByName("origin");
                                ObjectReferenceImpl token = (ObjectReferenceImpl) ((ObjectReferenceImpl) expr).getValue(originField);
                                Field sourceField = token.referenceType().fieldByName("source");
                                Field lineNrField = token.referenceType().fieldByName("lineNr");
                                int lineNumber = ((IntegerValueImpl) token.getValue(lineNrField)).intValue();
                                String exprString = DebuggerUtils.getValueAsString(evaluationContext, expr);
                                String modulePath = DebuggerUtils.getValueAsString(
                                        evaluationContext,
                                        ((ObjectReferenceImpl) expr).getValue(sourceField));
                                callStack.add(0, new QuplaCallStackItem(((MethodEntryEvent) event).method().name(), exprString, lineNumber + 1, modulePath));
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

    protected ObjectReference getThisObject(SuspendContextImpl context, LocatableEvent event) throws EvaluateException {
        ThreadReferenceProxyImpl thread = context.getThread();
        if(thread != null) {
            StackFrameProxyImpl stackFrameProxy = thread.frame(0);
            if(stackFrameProxy != null) {
                return stackFrameProxy.thisObject();
            }
        }
        return null;
    }

    private abstract class EvalContextFilteredRequestor implements FilteredRequestor {
        @Override
        public boolean isInstanceFiltersEnabled() {
            return false;
        }

        @Override
        public InstanceFilter[] getInstanceFilters() {
            return new InstanceFilter[0];
        }

        @Override
        public boolean isCountFilterEnabled() {
            return false;
        }

        @Override
        public int getCountFilter() {
            return 0;
        }

        @Override
        public boolean isClassFiltersEnabled() {
            return true;
        }

        @Override
        public ClassFilter[] getClassFilters() {
            return new ClassFilter[]{new ClassFilter(QuplaPositionManager.QUPLA_CONTEXT_CLASSNAME)};
        }

        @Override
        public ClassFilter[] getClassExclusionFilters() {
            return new ClassFilter[0];
        }

        @Override
        public String getSuspendPolicy() {
            return DebuggerSettings.SUSPEND_ALL;
        }
    }
}
