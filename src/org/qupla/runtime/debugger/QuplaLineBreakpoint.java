package org.qupla.runtime.debugger;

import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.ui.breakpoints.BreakpointWithHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.LocatableEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class QuplaLineBreakpoint <P extends QuplaBreakpointProperties> extends BreakpointWithHighlighter<P> {

    public QuplaLineBreakpoint(@NotNull Project project, XBreakpoint xBreakpoint) {
        super(project, xBreakpoint);
    }

    public static QuplaLineBreakpoint create(@NotNull Project project, XBreakpoint xBreakpoint) {
        QuplaLineBreakpoint breakpoint = new QuplaLineBreakpoint(project, xBreakpoint);
        return (QuplaLineBreakpoint) breakpoint.init();
    }

    @Override
    protected void createRequestForPreparedClass(DebugProcessImpl debugProcess, ReferenceType classType) {

    }

    @Override
    protected Icon getDisabledIcon(boolean isMuted) {
        return null;
    }

    @Override
    protected Icon getVerifiedIcon(boolean isMuted) {
        return null;
    }

    @Override
    protected Icon getVerifiedWarningsIcon(boolean isMuted) {
        return null;
    }

    @Override
    public Key<? extends BreakpointWithHighlighter> getCategory() {
        return null;
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
        return null;
    }
}
