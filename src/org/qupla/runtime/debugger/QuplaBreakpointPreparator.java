package org.qupla.runtime.debugger;

import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.requests.RequestManagerImpl;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.debugger.ui.breakpoints.FilteredRequestor;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.BreakpointRequest;

public class QuplaBreakpointPreparator implements ClassPrepareRequestor {

    private final SourcePosition position;

    public QuplaBreakpointPreparator(SourcePosition position) {
        this.position = position;
    }

    @Override
    public void processClassPrepare(DebugProcess debugProcess, ReferenceType referenceType) {
        System.out.println("here need to request a breakpoint");
        RequestManagerImpl requestsManager = (RequestManagerImpl) debugProcess.getRequestsManager();
        Location location = null;

        try {
            location= QuplaLineBreakpoint.findLocation(referenceType);
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }

        if(location!=null) {
            BreakpointRequest request = requestsManager.createBreakpointRequest(new QuplaBreakpointRequestor(position), location);
            requestsManager.enableRequest(request);
        }
    }

}
