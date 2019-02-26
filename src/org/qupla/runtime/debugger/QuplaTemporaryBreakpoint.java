package org.qupla.runtime.debugger;

import com.intellij.debugger.DebuggerBundle;
import com.intellij.debugger.InstanceFilter;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebuggerUtils;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.evaluation.EvaluationContextImpl;
import com.intellij.debugger.engine.events.SuspendContextCommandImpl;
import com.intellij.debugger.engine.requests.RequestManagerImpl;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.debugger.jdi.ThreadReferenceProxyImpl;
import com.intellij.debugger.settings.DebuggerSettings;
import com.intellij.debugger.ui.breakpoints.FilteredRequestor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.classFilter.ClassFilter;
import com.sun.jdi.Field;
import com.sun.jdi.Location;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Value;
import com.sun.jdi.event.LocatableEvent;
import com.sun.tools.jdi.*;
import org.qupla.runtime.debugger.ui.QuplaDebuggerToolWindow;

import javax.swing.*;
import java.util.List;

public class QuplaTemporaryBreakpoint implements FilteredRequestor {

    private Location location;
    private Project project;
    private QuplaDebugSession session;

    public QuplaTemporaryBreakpoint(Project project, QuplaDebugSession session, Location location) {
        this.location = location;
        this.project = project;
        this.session = session;
    }

    @Override
    public String getSuspendPolicy() {
        return DebuggerSettings.SUSPEND_ALL;
    }

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
    public boolean processLocatableEvent(SuspendContextCommandImpl action, LocatableEvent event) throws EventProcessingException {
        ((RequestManagerImpl)session.getDebugProcess().getRequestsManager()).deleteRequest(this);

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
                    abraExprType = (ReferenceTypeImpl) DebuggerUtils.getSuperType(expr.type(), QuplaEvalContextRequestor.BASE_EXPR_CLASSNAME);
                    if (abraExprType != null) {
                        Field originField = abraExprType.fieldByName("origin");
                        ObjectReferenceImpl token = (ObjectReferenceImpl) ((ObjectReferenceImpl) expr).getValue(originField);
                        Field sourceField = token.referenceType().fieldByName("source");
                        Field lineNrField = token.referenceType().fieldByName("lineNr");
                        Field colNrField = token.referenceType().fieldByName("colNr");
                        int lineNumber = ((IntegerValueImpl) token.getValue(lineNrField)).intValue();
                        int colNumber = ((IntegerValueImpl) token.getValue(colNrField)).intValue();
                        EvaluationContextImpl evaluationContext = new EvaluationContextImpl(context, frameProxy, () -> getThisObject(context, event));
                        String currentPath = DebuggerUtils.getValueAsString(evaluationContext, token.getValue(sourceField));
                        context.getDebugProcess().getPositionManager().clearCache();
                        //QuplaPositionManager.current.setLastSourcePosition(position);


                        QuplaCallStackItem item = session.getTopCallstackItem();
                        if(item!=null) {
                            SourcePosition sourcePosition = item.getSourcePosition();
                            if (sourcePosition != null) {
                                QuplaPositionManager.current.setLastSourcePosition(sourcePosition);
                            }
                        }


                        session.updateQuplaDebuggerWindow(frameProxy, evaluationContext);

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {

                                ToolWindow quplaDebugger = ToolWindowManager.getInstance(project).getToolWindow(QuplaDebuggerToolWindow.ID);
                                if(quplaDebugger!=null)quplaDebugger.activate(null);
                            }
                        });

                        return true;
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
        return false;
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
}
