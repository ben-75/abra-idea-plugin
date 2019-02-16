package org.qupla.runtime.debugger;

import com.intellij.debugger.DebuggerManager;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.JavaBreakpointHandler;
import com.intellij.debugger.engine.events.DebuggerCommandImpl;
import com.intellij.debugger.impl.DebuggerManagerImpl;
import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.debugger.ui.breakpoints.BreakpointManager;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import org.jetbrains.annotations.NotNull;
import org.qupla.runtime.debugger.ui.QuplaDebuggerManager;

public class QuplaBreakpointHandler extends JavaBreakpointHandler {

    private QuplaDebugSession quplaDebugSession;
    public QuplaBreakpointHandler(@NotNull DebugProcessImpl process) {
        super(QuplaLineBreakpointType.class, process);
        quplaDebugSession = process.getProject().getComponent(QuplaDebuggerManager.class).getSession(process);

    }

    @Override
    public void registerBreakpoint(@NotNull XBreakpoint xBreakpoint) {
        Breakpoint breakpoint = xBreakpoint.getUserData(Breakpoint.DATA_KEY);
        if(breakpoint==null){
            breakpoint = QuplaLineBreakpoint.create(myProcess.getProject(),xBreakpoint, quplaDebugSession);
            xBreakpoint.putUserData(Breakpoint.DATA_KEY, breakpoint);
        }
        BreakpointManager.addBreakpoint(breakpoint);
        final Breakpoint bpt = breakpoint;
        myProcess.getManagerThread().schedule(new DebuggerCommandImpl() {
            @Override
            protected void action() {
                bpt.createRequest(myProcess);
            }

            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }
        });
    }

    @Override
    public void unregisterBreakpoint(@NotNull XBreakpoint xBreakpoint, boolean temporary) {
        final Breakpoint breakpoint = xBreakpoint.getUserData(Breakpoint.DATA_KEY);
        if(breakpoint!=null) {
            ((DebuggerManagerImpl)DebuggerManager.getInstance(myProcess.getProject())).getBreakpointManager().removeBreakpoint(breakpoint);
            // use schedule not to block initBreakpoints
            myProcess.getManagerThread().schedule(new DebuggerCommandImpl() {
                @Override
                protected void action() {
                    myProcess.getRequestsManager().deleteRequest(breakpoint);
                }

                @Override
                public Priority getPriority() {
                    return Priority.HIGH;
                }
            });
        }
    }

}
