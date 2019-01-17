package org.qupla.runtime.debugger;

import com.intellij.debugger.DebuggerBundle;
import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.InstanceFilter;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.*;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.evaluation.EvaluationContextImpl;
import com.intellij.debugger.engine.events.SuspendContextCommandImpl;
import com.intellij.debugger.engine.requests.RequestManagerImpl;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.debugger.settings.DebuggerSettings;
import com.intellij.debugger.ui.breakpoints.BreakpointCategory;
import com.intellij.debugger.ui.breakpoints.BreakpointWithHighlighter;
import com.intellij.debugger.ui.breakpoints.FilteredRequestor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.LayeredIcon;
import com.intellij.ui.classFilter.ClassFilter;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.impl.XDebuggerUtilImpl;
import com.sun.jdi.*;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.tools.jdi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qupla.language.psi.*;
import org.qupla.runtime.debugger.ui.QuplaDebuggerToolWindow;

import javax.swing.*;
import java.util.List;

public class QuplaLineBreakpoint <P extends QuplaBreakpointProperties> extends BreakpointWithHighlighter<P> implements FilteredRequestor {

    private final XBreakpoint myXBreakpoint;
    Key<QuplaLineBreakpoint> CATEGORY = BreakpointCategory.lookup("line_breakpoints");
    private SourcePosition position;
    private PsiFile file;

    private final PsiElement evaluable;
    private final int column;
    private final int line;
    private final String methodName;
    private final String modulePath;


    public QuplaLineBreakpoint(@NotNull Project project, XBreakpoint xBreakpoint) {
        super(project, xBreakpoint);
        myXBreakpoint = xBreakpoint;
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                file = PsiManager.getInstance(getProject()).findFile(myXBreakpoint.getSourcePosition().getFile());
            }
        });
        position = SourcePosition.createFromLine(file, myXBreakpoint.getSourcePosition().getLine());

        evaluable = QuplaDebuggerUtil.findEvaluableNearElement(position.getElementAt());
        methodName = findMethodForEvaluable(evaluable);
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        Document document = psiDocumentManager.getDocument(file);
        line = document.getLineNumber(evaluable.getTextOffset());
        column = evaluable.getTextOffset()-document.getLineStartOffset(line);
        modulePath = ((QuplaFile)file).getImportableFilePath();
    }

    public static QuplaLineBreakpoint create(@NotNull Project project, XBreakpoint xBreakpoint) {
        QuplaLineBreakpoint breakpoint = new QuplaLineBreakpoint(project, xBreakpoint);
        return (QuplaLineBreakpoint) breakpoint.init();
    }

    @Override
    protected void createRequestForPreparedClass(DebugProcessImpl debugProcess, ReferenceType referenceType) {
        RequestManagerImpl requestsManager = (RequestManagerImpl) debugProcess.getRequestsManager();
        Location location = null;
        try {
            location= findLocation(referenceType);
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
        if(location!=null) {
            BreakpointRequest request = requestsManager.createBreakpointRequest(this, location);
            requestsManager.enableRequest(request);
        }
    }

    public Location findLocation(ReferenceType referenceType) throws AbsentInformationException {
        String targetMethodName = findMethodForEvaluable(evaluable);
        return referenceType.methodsByName(targetMethodName).get(0).allLineLocations().get(0);
    }

    @Override
    protected Icon getDisabledIcon(boolean isMuted) {
        if (DebuggerManagerEx.getInstanceEx(myProject).getBreakpointManager().findMasterBreakpoint(this) != null) {
            return isMuted ? AllIcons.Debugger.Db_muted_dep_line_breakpoint : AllIcons.Debugger.Db_dep_line_breakpoint;
        }
        return null;
    }

    @Override
    protected Icon getVerifiedIcon(boolean isMuted) {
        return XDebuggerUtilImpl.getVerifiedIcon(myXBreakpoint);
    }

    @Override
    protected Icon getVerifiedWarningsIcon(boolean isMuted) {
        return new LayeredIcon(isMuted ? AllIcons.Debugger.Db_muted_breakpoint : AllIcons.Debugger.Db_set_breakpoint,
                AllIcons.General.WarningDecorator);
    }

    @Override
    public Key<? extends BreakpointWithHighlighter> getCategory() {
        return CATEGORY;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getEventMessage(LocatableEvent event) {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getEvaluationElement() {
        return ContextUtil.getContextElement(getSourcePosition());
    }

    @NotNull
    @Override
    protected P getProperties() {
        return super.getProperties();
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
    public String getSuspendPolicy() {
        return DebuggerSettings.SUSPEND_ALL;
    }


    @Override
    public boolean processLocatableEvent(SuspendContextCommandImpl action, LocatableEvent event) throws EventProcessingException {
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
                    abraExprType = (ReferenceTypeImpl) DebuggerUtils.getSuperType(expr.type(), QuplaEvalContextRequestor.BASE_EXPR_CLASSNAME);
                    if (abraExprType != null) {
                        Field originField = abraExprType.fieldByName("origin");
                        ObjectReferenceImpl token = (ObjectReferenceImpl) ((ObjectReferenceImpl) expr).getValue(originField);
                        Field sourceField = token.referenceType().fieldByName("source");
                        Field lineNrField = token.referenceType().fieldByName("lineNr");
                        Field colNrField = token.referenceType().fieldByName("colNr");
                        int lineNumber = ((IntegerValueImpl) token.getValue(lineNrField)).intValue();
                        int colNumber = ((IntegerValueImpl) token.getValue(colNrField)).intValue();
                        String currentPath = DebuggerUtils.getValueAsString(
                                evaluationContext,
                                token.getValue(token.referenceType().fieldByName("source")));
                        boolean pause = lineNumber == line && colNumber==column && currentPath!=null && currentPath.length()>3 && currentPath.substring(0,currentPath.length()-4).equals(modulePath);
                        if(pause){
                            context.getDebugProcess().getPositionManager().clearCache();
                            QuplaPositionManager.current.setLastSourcePosition(position);
                            updateQuplaDebuggerWindow();
                        }
                        return pause;
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

    private void updateQuplaDebuggerWindow(){
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {

                ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow(ToolWindowId.DEBUG);
                ContentManager contentManager = toolWindow.getContentManager();
                Content content = contentManager.findContent("CallStack");
                if(content==null){
                    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                    QuplaDebuggerToolWindow quplaDebuggerToolWindow = new QuplaDebuggerToolWindow(toolWindow);
                    content = contentFactory.createContent(quplaDebuggerToolWindow.getContent(), "CallStack", false);
                    toolWindow.getContentManager().addContent(content);
                }
                //TODO : populate callstack
            }
        });
    }

    private static String findMethodForEvaluable(PsiElement e){
        if(e==null)return null;
        if(e instanceof QuplaAssignExpr) return "evalAssign";
        if(e instanceof QuplaSliceExpr) return "evalSlice";
        if(e instanceof QuplaConcatExpr) return "evalConcat";
        if(e instanceof QuplaLutExpr) return "evalLutLookup";
        if(e instanceof QuplaMergeExpr) return "evalMerge";
        if(e instanceof QuplaFuncExpr) return "evalFuncCall";
        if(e instanceof QuplaCondExpr) return "evalConditional";
        if(e instanceof QuplaStateExpr) return "evalState";
        if(e instanceof QuplaTypeExpr) return "evalType";
        if(e instanceof QuplaInteger) return "evalVector";
        return null;
    }
}
