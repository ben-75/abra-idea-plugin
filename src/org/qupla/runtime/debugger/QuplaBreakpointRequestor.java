package org.qupla.runtime.debugger;

import com.intellij.debugger.DebuggerBundle;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebuggerUtils;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.evaluation.EvaluationContextImpl;
import com.intellij.debugger.engine.events.SuspendContextCommandImpl;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.debugger.jdi.ThreadReferenceProxyImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.sun.jdi.Field;
import com.sun.jdi.Location;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Value;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.tools.jdi.*;
import org.qupla.language.psi.*;

import java.util.Arrays;
import java.util.List;

public class QuplaBreakpointRequestor extends QuplaEvalContextFilteredAbstractRequestor {

    private final SourcePosition position;
    private final PsiElement evaluable;
    private final int column;
    private final int line;
    private final String methodName;
    private final String modulePath;

    public QuplaBreakpointRequestor(SourcePosition position) {
        this.position = position;
        evaluable = QuplaDebuggerUtil.findEvaluableNearElement(position.getElementAt());
        methodName = findMethodForEvaluable(evaluable);
        PsiFile containingFile = evaluable.getContainingFile();
        Project project = containingFile.getProject();
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        Document document = psiDocumentManager.getDocument(containingFile);
        line = document.getLineNumber(evaluable.getTextOffset());
        column = evaluable.getTextOffset()-document.getLineStartOffset(line);
        modulePath = ((QuplaFile)containingFile).getImportableFilePath();
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

    public void computePosition(PsiElement element){

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

    private String findMethodForEvaluable(PsiElement e){
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
