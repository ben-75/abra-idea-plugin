package org.qupla.runtime.debugger;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.engine.ContextUtil;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.ui.breakpoints.BreakpointCategory;
import com.intellij.debugger.ui.breakpoints.BreakpointWithHighlighter;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.ui.LayeredIcon;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.impl.XDebuggerUtilImpl;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.LocatableEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class QuplaLineBreakpoint <P extends QuplaBreakpointProperties> extends BreakpointWithHighlighter<P> {

    private final XBreakpoint myXBreakpoint;
    Key<QuplaLineBreakpoint> CATEGORY = BreakpointCategory.lookup("line_breakpoints");

    public QuplaLineBreakpoint(@NotNull Project project, XBreakpoint xBreakpoint) {
        super(project, xBreakpoint);
        myXBreakpoint = xBreakpoint;
    }

    public static QuplaLineBreakpoint create(@NotNull Project project, XBreakpoint xBreakpoint) {
        QuplaLineBreakpoint breakpoint = new QuplaLineBreakpoint(project, xBreakpoint);
        return (QuplaLineBreakpoint) breakpoint.init();
    }

    @Override
    protected void createOrWaitPrepare(DebugProcessImpl debugProcess, String classToBeLoaded) {
        super.createOrWaitPrepare(debugProcess, classToBeLoaded);
    }

    @Override
    protected void createRequestForPreparedClass(DebugProcessImpl debugProcess, ReferenceType classType) {
        System.out.println("QuplaLineBreakpoint: createRequestForPreparedClass");
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
}
