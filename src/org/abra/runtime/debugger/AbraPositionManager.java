package org.abra.runtime.debugger;

import com.intellij.debugger.MultiRequestPositionManager;
import com.intellij.debugger.NoDataException;
import com.intellij.debugger.PositionManager;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.DebugProcessEvents;
import com.intellij.debugger.engine.DebuggerUtils;
import com.intellij.debugger.engine.JVMNameUtil;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.impl.DebuggerUtilsEx;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.sun.jdi.*;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.tools.jdi.*;
import org.abra.language.AbraFileType;
import org.abra.language.psi.AbraPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AbraPositionManager implements MultiRequestPositionManager {

    private final DebugProcess debugProcess;
    private PsiClass evalContextClass;
    private String classPattern;
    private final AbraToJavaMapper abraToJavaMapper;

    public AbraPositionManager(DebugProcess process, AbraToJavaMapper abraToJavaMapper) {
        this.debugProcess = process;
        this.abraToJavaMapper = abraToJavaMapper;
        ApplicationManager.getApplication().runReadAction(
                () -> {
                    evalContextClass = DebuggerUtils.findClass("org.iota.abra.context.EvalContext", debugProcess.getProject(), GlobalSearchScope.allScope(debugProcess.getProject()));
                    classPattern = JVMNameUtil.getNonAnonymousClassName(evalContextClass);
                });
        DebuggerUtilsEx.setAlternativeSourceUrl("org.iota.abra.context.EvalContext", "dummy", evalContextClass.getProject());
    }

    @Nullable
    @Override
    public SourcePosition getSourcePosition(Location location) throws NoDataException {
        StackFrameProxyImpl stackFrameProxy = ((DebugProcessEvents) debugProcess).getDebuggerContext().getFrameProxy();
        if (stackFrameProxy != null) {
            try {
                List<Value> args = stackFrameProxy.getArgumentValues();
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
                            Field moduleField = abraExprType.fieldByName("module");
                            ObjectReferenceImpl token = (ObjectReferenceImpl) ((ObjectReferenceImpl) expr).getValue(originField);
                            Field lineNrField = ((ReferenceTypeImpl) token.type()).fieldByName("lineNr");
                            int lineNumber = ((IntegerValueImpl) token.getValue(lineNrField)).intValue();
                            String modulePath = DebuggerUtils.getValueAsString(
                                    ((DebugProcessEvents) debugProcess).getDebuggerContext().createEvaluationContext(),
                                    ((ObjectReferenceImpl) expr).getValue(moduleField)).substring(7);
                            PsiFile file = AbraPsiImplUtil.findFileForPath(debugProcess.getProject(), modulePath);
                            if (file == null) {
                                throw NoDataException.INSTANCE;
                            }
                            System.out.println("Abra source position found: " + modulePath + " at line " + lineNumber);
                            Document abraDoc = PsiDocumentManager.getInstance(debugProcess.getProject()).getDocument(file);
                            int offset = abraDoc.getLineStartOffset(lineNumber);
                            return new AbraSourcePosition(AbraDebuggerUtil.getAbraPsiElement(file, offset), lineNumber);
                        }
                    }
                }
            } catch (EvaluateException e) {
                e.printStackTrace();
            }
        }

        throw NoDataException.INSTANCE;
    }

    @NotNull
    @Override
    public List<ReferenceType> getAllClasses(@NotNull SourcePosition classPosition) throws NoDataException {
        if (classPosition.getFile().getFileType() == AbraFileType.INSTANCE) {
            return debugProcess.getVirtualMachineProxy().classesByName("org.iota.abra.context.EvalContext");
        }
        throw NoDataException.INSTANCE;
    }

    @NotNull
    @Override
    public List<Location> locationsOfLine(@NotNull ReferenceType type, @NotNull SourcePosition position) throws NoDataException {
        if (position.getFile().getFileType() == AbraFileType.INSTANCE) {
            try {

                PsiElement psiElement = AbraDebuggerUtil.getAbraPsiElement(position.getFile().findElementAt(position.getOffset()));
                psiElement = AbraDebuggerUtil.findEvaluable(psiElement);
                List<Location> locations = type.locationsOfLine(abraToJavaMapper.getEvalMethod(psiElement).getLineNumber());
                return Collections.singletonList(locations.get(0));
            } catch (AbsentInformationException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        throw NoDataException.INSTANCE;
    }


    @Nullable
    @Override
    public ClassPrepareRequest createPrepareRequest(@NotNull ClassPrepareRequestor requestor, @NotNull SourcePosition position) throws NoDataException {
        throw new IllegalStateException("This class implements MultiRequestPositionManager, corresponding createPrepareRequests version should be used");
    }

    @NotNull
    //@Override
    public List<ClassPrepareRequest> createPrepareRequests(@NotNull final ClassPrepareRequestor requestor, @NotNull final SourcePosition position)
            throws NoDataException {
        if (position.getFile().getFileType() == AbraFileType.INSTANCE) {
            List<ClassPrepareRequest> res = new ArrayList<>();
            if (evalContextClass != null) {
                ClassPrepareRequestor prepareRequestor = requestor;
                ClassPrepareRequest request = debugProcess.getRequestsManager().createClassPrepareRequest(prepareRequestor, classPattern);
                if (request != null) {
                    res.add(request);
                }
            }
            return res;
        }
        throw NoDataException.INSTANCE;
    }
}
