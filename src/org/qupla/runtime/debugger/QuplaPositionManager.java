package org.qupla.runtime.debugger;

import com.intellij.debugger.MultiRequestPositionManager;
import com.intellij.debugger.NoDataException;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.DebuggerUtils;
import com.intellij.debugger.engine.JVMNameUtil;
import com.intellij.debugger.impl.DebuggerUtilsEx;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qupla.language.QuplaFileType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class QuplaPositionManager implements MultiRequestPositionManager {

    public static final String QUPLA_CONTEXT_CLASSNAME = "org.iota.qupla.qupla.context.QuplaEvalContext";

    private final DebugProcess debugProcess;
    private PsiClass evalContextClass;
    private String classPattern;
    public static QuplaPositionManager current;
    private SourcePosition lastSourcePosition;

    public QuplaPositionManager(DebugProcess process) {
        this.debugProcess = process;
        current = this;
        ApplicationManager.getApplication().runReadAction(
                () -> {
                    evalContextClass = DebuggerUtils.findClass(QUPLA_CONTEXT_CLASSNAME, debugProcess.getProject(), GlobalSearchScope.allScope(debugProcess.getProject()));
                    classPattern = JVMNameUtil.getNonAnonymousClassName(evalContextClass);
                });
        DebuggerUtilsEx.setAlternativeSourceUrl(QUPLA_CONTEXT_CLASSNAME, "dummy", evalContextClass.getProject());
    }

    private QuplaSourcePosition sourcePositionLocalCache;
    @Nullable
    @Override
    public SourcePosition getSourcePosition(@Nullable Location location) throws NoDataException {
        if(lastSourcePosition!=null){
            sourcePositionLocalCache =
                    new QuplaSourcePosition(QuplaDebuggerUtil.findEvaluableNearElement(lastSourcePosition.getElementAt()), lastSourcePosition.getLine(),location);
            lastSourcePosition = null;
        }
        if(sourcePositionLocalCache!=null && sourcePositionLocalCache.getLocation()==location){
            return sourcePositionLocalCache;
        }else{
            sourcePositionLocalCache = null;
        }
        throw NoDataException.INSTANCE;
    }

    @NotNull
    @Override
    public List<ReferenceType> getAllClasses(@NotNull SourcePosition classPosition) throws NoDataException {
        if (classPosition.getFile().getFileType() == QuplaFileType.INSTANCE) {
            return debugProcess.getVirtualMachineProxy().classesByName(QUPLA_CONTEXT_CLASSNAME);
        }
        throw NoDataException.INSTANCE;
    }

    @NotNull
    @Override
    public List<Location> locationsOfLine(@NotNull ReferenceType type, @NotNull SourcePosition position) throws NoDataException {
        //TODO
        throw NoDataException.INSTANCE;
    }


    private QuplaEvalContextRequestor quplaEvalContextRequestor;
    @Nullable
    @Override
    public List<ClassPrepareRequest> createPrepareRequests(@NotNull ClassPrepareRequestor requestor, @NotNull SourcePosition position) throws NoDataException {
        if (position.getFile().getFileType() == QuplaFileType.INSTANCE) {
            if (evalContextClass != null) {
                List<ClassPrepareRequest> resp = new ArrayList<>();
                if(quplaEvalContextRequestor==null) { //we only need this one time
                    quplaEvalContextRequestor = new QuplaEvalContextRequestor(debugProcess.getProject());
                    resp.add(debugProcess.getRequestsManager().createClassPrepareRequest(quplaEvalContextRequestor, classPattern));
                }
                if(requestor instanceof QuplaLineBreakpoint) {
                    //we need this for every breakpoint
                    ((QuplaLineBreakpoint) requestor).setQuplaEvalContextRequestor(quplaEvalContextRequestor);
                    resp.add(debugProcess.getRequestsManager().createClassPrepareRequest(requestor, classPattern));
                }
                return resp;
            } else {
                throw new RuntimeException("Interpreter class "+QUPLA_CONTEXT_CLASSNAME+" not found in classpath. Debugger won't work.");
            }
        }
        throw NoDataException.INSTANCE;
    }

    @Nullable
    @Override
    public ClassPrepareRequest createPrepareRequest(@NotNull ClassPrepareRequestor requestor, @NotNull SourcePosition position) throws NoDataException {
        if (position.getFile().getFileType() == QuplaFileType.INSTANCE) {
            if (evalContextClass != null) {
                return debugProcess.getRequestsManager().createClassPrepareRequest(requestor, classPattern);
            } else {
                throw new RuntimeException("Interpreter class "+QUPLA_CONTEXT_CLASSNAME+" not found in classpath. Debugger won't work.");
            }
        }
        throw NoDataException.INSTANCE;
    }

    @Nullable
    @Override
    public Set<? extends FileType> getAcceptedFileTypes() {
        return Collections.singleton(QuplaFileType.INSTANCE);
    }

    public void setLastSourcePosition(SourcePosition lastSourcePosition) {
        this.lastSourcePosition = lastSourcePosition;
    }

    public SourcePosition getLastSourcePosition() {
        return lastSourcePosition;
    }
}
