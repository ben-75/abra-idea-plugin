package org.qupla.runtime.debugger;

import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.sun.jdi.ReferenceType;

public class QuplaBreakpointRequestor  implements ClassPrepareRequestor {
    private final SourcePosition position;

    public QuplaBreakpointRequestor(SourcePosition position) {
        this.position = position;
    }

    @Override
    public void processClassPrepare(DebugProcess debuggerProcess, ReferenceType referenceType) {
        System.out.println("here need to request a breakpoint");
    }
}
