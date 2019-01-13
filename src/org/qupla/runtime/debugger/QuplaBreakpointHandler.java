package org.qupla.runtime.debugger;

import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.JavaBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import org.jetbrains.annotations.NotNull;

public class QuplaBreakpointHandler extends JavaBreakpointHandler {

    public QuplaBreakpointHandler(@NotNull DebugProcessImpl process) {
        super(QuplaLineBreakpointType.class, process);
    }

    @Override
    public void registerBreakpoint(@NotNull XBreakpoint breakpoint) {
        //TODO
    }

    @Override
    public void unregisterBreakpoint(@NotNull XBreakpoint breakpoint, boolean temporary) {
        //TODO
    }
}
