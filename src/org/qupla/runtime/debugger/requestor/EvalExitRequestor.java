package org.qupla.runtime.debugger.requestor;

import com.intellij.debugger.engine.events.SuspendContextCommandImpl;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.LocatableEvent;
import org.qupla.runtime.debugger.QuplaDebugSession;

public class EvalExitRequestor extends QuplaRequestor {

    public String methodName;

    public EvalExitRequestor(QuplaDebugSession session, String methodName) {
        super(session);
        this.methodName = methodName;
    }

    @Override
    public boolean processLocatableEvent(SuspendContextCommandImpl action, LocatableEvent event) throws EventProcessingException {
        return session.processLeaveMethod(event);
    }
}