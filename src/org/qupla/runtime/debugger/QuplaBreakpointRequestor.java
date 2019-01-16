package org.qupla.runtime.debugger;

import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.events.SuspendContextCommandImpl;
import com.sun.jdi.event.LocatableEvent;

public class QuplaBreakpointRequestor extends QuplaEvalContextFilteredAbstractRequestor {

    private final SourcePosition position;

    public QuplaBreakpointRequestor(SourcePosition position) {
        this.position = position;
    }

    @Override
    public boolean processLocatableEvent(SuspendContextCommandImpl action, LocatableEvent event) throws EventProcessingException {
        //need to evaluate if the current eval call match our source location
        //TODO
        return false;
    }
}
