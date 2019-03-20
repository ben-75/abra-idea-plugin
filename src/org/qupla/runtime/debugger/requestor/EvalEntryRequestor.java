package org.qupla.runtime.debugger.requestor;

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
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.sun.jdi.*;
import com.sun.jdi.event.LocatableEvent;
import com.sun.tools.jdi.*;
import org.qupla.language.module.QuplaModuleManager;
import org.qupla.runtime.debugger.*;
import org.qupla.runtime.debugger.ui.QuplaDebuggerToolWindow;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.qupla.runtime.debugger.QuplaDebugSession.BASE_EXPR_CLASSNAME;

public class EvalEntryRequestor extends QuplaRequestor {

    private PsiFile tmp;
    public String methodName;
    private QuplaEvalContextMapper R;

    private Field lineNrField;
    private Field colNrField;
    private Field sourceField;
    private Field pathNameField;
    private List<? extends Field> tokenFields;
    private Method srcToString;

    public EvalEntryRequestor(QuplaDebugSession session, String methodName, QuplaEvalContextMapper mapper) {
        super(session);
        this.methodName = methodName;
        R = mapper;
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
//                        if(sourceField==null){
//                            sourceField = token.referenceType().fieldByName("source");
//                            lineNrField = token.referenceType().fieldByName("lineNr");
//                            colNrField = token.referenceType().fieldByName("colNr");
//                            //stackFrameField = frameProxy.thisObject().referenceType().fieldByName("stackFrame");
//                            lineAndColField = Arrays.asList(lineNrField, colNrField);
//                        }
                        if(lineNrField==null){
                            lineNrField = token.referenceType().fieldByName("lineNr");
                            colNrField = token.referenceType().fieldByName("colNr");
                            sourceField = token.referenceType().fieldByName("source");
                            tokenFields = Arrays.asList(lineNrField,colNrField,sourceField);
                        }
                        Map<Field,Value> tokenValues = token.getValues(tokenFields);
                        int lineNumber = ((IntegerValueImpl) tokenValues.get(lineNrField)).intValue();
                        int colNumber = ((IntegerValueImpl) tokenValues.get(colNrField)).intValue();
                        int stackFrame = ((IntegerValueImpl) frameProxy.thisObject().getValue(R.getStackFrameField())).intValue();
                        EvaluationContextImpl evaluationContext = new EvaluationContextImpl(context, frameProxy, () -> getThisObject(context, event));

                        ObjectReferenceImpl srcRef = (ObjectReferenceImpl) tokenValues.get(sourceField);
                        if(pathNameField==null && srcRef!=null){
                            pathNameField = srcRef.referenceType().fieldByName("pathName");
                        }
                        if(srcToString==null){
                            srcToString = ((ObjectReferenceImpl) expr).referenceType().methodsByName("toString").get(0);
                        }
//                        String exprString = ((StringReference)context.getDebugProcess().invokeInstanceMethod(
//                                evaluationContext, (ObjectReference) expr,
//                                srcToString,
//                                Collections.EMPTY_LIST,0)).value();
                        String exprString = "blabla";
                        //DebuggerUtils.getValueAsString(evaluationContext, expr);
                        if(pathNameField==null && srcRef!=null){
                            pathNameField = srcRef.referenceType().fieldByName("pathName");
                        }
                        String modulePath = srcRef==null?null: ((StringReferenceImpl)srcRef.getValue(pathNameField)).value();
//                        (StringReference)context.getDebugProcess().invokeInstanceMethod(
//                                evaluationContext, (ObjectReference) srcRef,
//                                srcRef.referenceType().methodsByName("toString").get(0),
//                                Collections.EMPTY_LIST,0)).value();


                        //DebuggerUtils.getValueAsString(evaluationContext, token.getValue(sourceField));

                        QuplaCallStackItem item = QuplaCallStackItem.Factory.newQuplaCallStackItem(session.getProject(),
                                methodName,
                                exprString,
                                lineNumber + 1, colNumber + 1, stackFrame, modulePath);
                        boolean pause = session.processMethodEntry(item,event);
                        if(pause){
                            context.getDebugProcess().getPositionManager().clearCache();
                            QuplaModuleManager moduleManager = session.getProject().getComponent(QuplaModuleManager.class);
                            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(moduleManager.getFullQuplaSourceRootPath()+"/"+modulePath);
                            if(virtualFile!=null) {
                                tmp = null;
                                ApplicationManager.getApplication().runReadAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        tmp = PsiManager.getInstance(session.getProject()).findFile(virtualFile);
                                    }
                                });
                                PsiFile psiFile = tmp;
                                if(psiFile!=null) {
                                    PsiDocumentManager docManager = PsiDocumentManager.getInstance(session.getProject());

                                    int offset = docManager.getDocument(psiFile).getLineStartOffset(lineNumber)+colNumber;
                                    PsiElement element = psiFile.findElementAt(offset);
                                    SourcePosition position = new QuplaSourcePosition(element, lineNumber, null);
                                    QuplaPositionManager.current.setLastSourcePosition(position);
                                    session.updateQuplaDebuggerWindow(frameProxy, evaluationContext);

                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToolWindow quplaDebugger = ToolWindowManager.getInstance(session.getProject()).getToolWindow(QuplaDebuggerToolWindow.ID);
                                            if (quplaDebugger != null) quplaDebugger.activate(null);
                                        }
                                    });
                                }
                            }
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
