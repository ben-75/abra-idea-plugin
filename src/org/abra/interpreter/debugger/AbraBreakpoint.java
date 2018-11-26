package org.abra.interpreter.debugger;

import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointType;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import com.intellij.xdebugger.impl.breakpoints.*;
import org.jetbrains.annotations.Nullable;

//public class AbraBreakpoint extends XBreakpointBase<AbraBreakpoint,AbraBreakpointProperties, BreakpointState> implements XBreakpoint<AbraBreakpointProperties> {
public class AbraBreakpoint extends XLineBreakpointImpl<AbraBreakpointProperties> implements XLineBreakpoint<AbraBreakpointProperties> {
    public AbraBreakpoint(XLineBreakpointType<AbraBreakpointProperties> type, XBreakpointManagerImpl breakpointManager, @Nullable AbraBreakpointProperties properties, LineBreakpointState<AbraBreakpointProperties> state) {
        super(type, breakpointManager, properties, state);
    }

//    public AbraBreakpoint(Project project, @Nullable AbraBreakpointProperties properties, LineBreakpointState<AbraBreakpointProperties> state) {
//        super(AbraBreakpointType.INSTANCE,
//                (XBreakpointManagerImpl) XDebuggerManager.getInstance(project).getBreakpointManager(),
//                properties, state);
//    }
}

