package org.abra.interpreter.debugger;

import com.intellij.debugger.MultiRequestPositionManager;
import com.intellij.debugger.NoDataException;
import com.intellij.debugger.PositionManager;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.*;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.impl.DebuggerUtilsEx;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.sun.jdi.*;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.tools.jdi.*;
import org.abra.language.AbraFileType;
import org.abra.language.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class AbraPositionManager /*extends PositionManagerImpl*/ implements PositionManager {

    private final DebugProcess debugProcess;
    private PsiClass evalContextClass;
    private String classPattern;
    private final AbraToJavaMapper abraToJavaMapper;
    private Map<Location, List<SourcePosition>> map = new HashMap<>();

    public AbraPositionManager(DebugProcess process, AbraToJavaMapper abraToJavaMapper) {
        //super((DebugProcessImpl) process);
        this.debugProcess = process;
        this.abraToJavaMapper = abraToJavaMapper;
        ApplicationManager.getApplication().runReadAction(
                () -> {
                    evalContextClass = DebuggerUtils.findClass("org.iota.abra.context.EvalContext",debugProcess.getProject(), GlobalSearchScope.allScope(debugProcess.getProject()));
                    classPattern = JVMNameUtil.getNonAnonymousClassName(evalContextClass);
                });
        DebuggerUtilsEx.setAlternativeSourceUrl("org.iota.abra.context.EvalContext", "dummy", evalContextClass.getProject());
    }

//    @Nullable
//    @Override
//    public Set<? extends FileType> getAcceptedFileTypes() {
//        return Collections.singleton(AbraFileType.INSTANCE);
//    }

    @Nullable
    @Override
    public SourcePosition getSourcePosition(Location location) throws NoDataException {
        StackFrameProxyImpl stackFrameProxy = ((DebugProcessEvents) debugProcess).getDebuggerContext().getFrameProxy();
        if(stackFrameProxy!=null) {
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
                            modulePath = modulePath.substring(modulePath.indexOf("/") + 1);
                            PsiFile file = AbraPsiImplUtil.findFileForPath(debugProcess.getProject(), modulePath);
                            System.out.println(modulePath + " at line " + lineNumber);
                            return SourcePosition.createFromLine(file, lineNumber);
                        }
                    }
                }
            } catch (EvaluateException e) {
                e.printStackTrace();
            }
        }

        throw NoDataException.INSTANCE;
//        List<SourcePosition> sourcePositions = map.get(location);
//        if(sourcePositions==null)
//            throw NoDataException.INSTANCE;
//        if(sourcePositions.size()==1)
//            return sourcePositions.get(0);
//        return sourcePositions.get(1);
    }

    @NotNull
    @Override
    public List<ReferenceType> getAllClasses(@NotNull SourcePosition classPosition) throws NoDataException {
        if(classPosition.getFile().getFileType()==AbraFileType.INSTANCE){
            return debugProcess.getVirtualMachineProxy().classesByName("org.iota.abra.context.EvalContext");
        }
        throw NoDataException.INSTANCE;
    }

    @NotNull
    @Override
    public List<Location> locationsOfLine(@NotNull ReferenceType type, @NotNull SourcePosition position) throws NoDataException {
        if(position.getFile().getFileType()==AbraFileType.INSTANCE){
            try {

                PsiElement psiElement = getAbraPsiElement(position.getFile().findElementAt(position.getOffset()));
                while(psiElement.getChildren().length==1 && isEvaluable(psiElement.getChildren()[0])){
                    psiElement = psiElement.getChildren()[0];
                }

                List<Location> locations =  type.locationsOfLine(abraToJavaMapper.getEvalMethod(psiElement).getLineNumber());
                List<SourcePosition> arr = map.get(locations.get(0));
                if(arr==null){
                    arr = new ArrayList<>();
                }
                arr.add(position);
                //AbraLocation abraLocation = new AbraLocation(locations.get(0));
                map.put(locations.get(0), arr);
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
        if (position.getFile().getFileType() == AbraFileType.INSTANCE) {
            List<ClassPrepareRequest> res = new ArrayList<>();
            if (evalContextClass != null) {
                ClassPrepareRequestor prepareRequestor = requestor;
//                if (classPattern == null) {
//                    final PsiClass parent = JVMNameUtil.getTopLevelParentClass(evalContextClass);
//                    if (parent != null) {
//
//                        final String parentQName = JVMNameUtil.getNonAnonymousClassName(parent);
//                        if (parentQName != null) {
//
//                            classPattern = parentQName + "*";
//                            prepareRequestor = new ClassPrepareRequestor() {
//                                @Override
//                                public void processClassPrepare(DebugProcess debuggerProcess, ReferenceType referenceType) {
//                                    if (((DebugProcessImpl) debuggerProcess).getPositionManager().getAllClasses(position).contains(referenceType)) {
//                                        requestor.processClassPrepare(debuggerProcess, referenceType);
//                                    }
//                                }
//                            };
//                        }
//                    }
//                }
                ClassPrepareRequest request = debugProcess.getRequestsManager().createClassPrepareRequest(prepareRequestor, classPattern);
                return request;
//                if (request != null) {
//                    res.add(request);
//                }
            }
//            return res;
        }
        throw NoDataException.INSTANCE;
//        return null;//super.createPrepareRequests(requestor, position);    }
    }
    @NotNull
    //@Override
    public List<ClassPrepareRequest> createPrepareRequests(@NotNull final ClassPrepareRequestor requestor, @NotNull final SourcePosition position)
            throws NoDataException {
        if(position.getFile().getFileType()==AbraFileType.INSTANCE){
            List<ClassPrepareRequest> res = new ArrayList<>();
            if(evalContextClass!=null) {
                ClassPrepareRequestor prepareRequestor = requestor;
//                if (classPattern == null) {
//                    final PsiClass parent = JVMNameUtil.getTopLevelParentClass(evalContextClass);
//                    if (parent != null) {
//
//                        final String parentQName = JVMNameUtil.getNonAnonymousClassName(parent);
//                        if (parentQName != null) {
//
//                            classPattern = parentQName + "*";
//                            prepareRequestor = new ClassPrepareRequestor() {
//                                @Override
//                                public void processClassPrepare(DebugProcess debuggerProcess, ReferenceType referenceType) {
//                                    if (((DebugProcessImpl) debuggerProcess).getPositionManager().getAllClasses(position).contains(referenceType)) {
//                                        requestor.processClassPrepare(debuggerProcess, referenceType);
//                                    }
//                                }
//                            };
//                        }
//                    }
//                }
                ClassPrepareRequest request = debugProcess.getRequestsManager().createClassPrepareRequest(prepareRequestor, classPattern);
                if (request != null) {
                    res.add(request);
                }
            }
            return res;
        }
        return null;//super.createPrepareRequests(requestor, position);
    }

    public static PsiElement getAbraPsiElement(PsiFile file, int offset){
        return getAbraPsiElement(file.findElementAt(offset));
    }

    private static PsiElement getAbraPsiElement(PsiElement element){
        while(element instanceof PsiWhiteSpace && element!=null)element = element.getNextSibling();
        if(element==null)return null;
        if(element instanceof AbraFuncBody && ((AbraFuncBody)element).getMergeExpr()!=null)return getAbraPsiElement(((AbraFuncBody)element).getMergeExpr());
        while(!(element instanceof AbraFuncExpr) && !(element instanceof AbraConcatExpr)
                && !(element instanceof AbraAssignExpr) && !(element instanceof AbraLutExpr)
                && !(element instanceof AbraMergeExpr) && !(element instanceof AbraSliceExpr)
                && !(element instanceof AbraStateExpr) && !(element instanceof AbraReturnExpr)&& !(element instanceof AbraFile)
                && !(element instanceof AbraFuncStmt)){
            element = element.getParent();
        }
        if(element instanceof AbraFile)
            return null;
        if(element instanceof AbraFuncStmt){
            if(((AbraFuncStmt)element).getFuncBody().getMergeExpr()!=null){
                return getAbraPsiElement(((AbraFuncStmt)element).getFuncBody().getMergeExpr());
            }
        }
        return element;
    }

    public static boolean isEvaluable(PsiElement element) {
         return (element instanceof AbraFuncExpr) || (element instanceof AbraConcatExpr)
                 || (element instanceof AbraAssignExpr) || (element instanceof AbraLutExpr)
                 || (element instanceof AbraMergeExpr) || (element instanceof AbraSliceExpr)
                 || (element instanceof AbraStateExpr)|| (element instanceof AbraReturnExpr)|| (element instanceof AbraPostfixExpr);
    }

    private static class AbraLocation implements Location {

        private final Location delegate;

        public AbraLocation(Location delegate) {
            this.delegate = delegate;
        }

        @Override
        public ReferenceType declaringType() {
            return delegate.declaringType();
        }

        @Override
        public Method method() {
            return delegate.method();
        }

        @Override
        public long codeIndex() {
            return delegate.codeIndex();
        }

        @Override
        public String sourceName() throws AbsentInformationException {
            return delegate.sourceName();
        }

        @Override
        public String sourceName(String s) throws AbsentInformationException {
            return delegate.sourceName(s);
        }

        @Override
        public String sourcePath() throws AbsentInformationException {
            return delegate.sourcePath();
        }

        @Override
        public String sourcePath(String s) throws AbsentInformationException {
            return delegate.sourcePath(s);
        }

        @Override
        public int lineNumber() {
            return delegate.lineNumber();
        }

        @Override
        public int lineNumber(String s) {
            return delegate.lineNumber(s);
        }

        @Override
        public VirtualMachine virtualMachine() {
            return delegate.virtualMachine();
        }

        @Override
        public int compareTo(@NotNull Location o) {
            return delegate.compareTo(o);
        }
    }
}
