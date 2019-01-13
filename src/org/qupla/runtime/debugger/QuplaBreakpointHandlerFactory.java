package org.qupla.runtime.debugger;

import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.JavaBreakpointHandler;
import com.intellij.debugger.engine.JavaBreakpointHandlerFactory;

public class QuplaBreakpointHandlerFactory implements JavaBreakpointHandlerFactory {

    @Override
    public QuplaBreakpointHandler createHandler(DebugProcessImpl process) {
        return new QuplaBreakpointHandler(process);
    }


}
