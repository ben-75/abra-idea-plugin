package org.qupla.runtime.debugger;

import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.requests.RequestManagerImpl;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.debugger.ui.breakpoints.FilteredRequestor;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;

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
            location= findLocation(referenceType);
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }

        if(location!=null) {
            requestsManager.createBreakpointRequest(new QuplaBreakpointRequestor(position), location);
        }
    }

    private Location findLocation(ReferenceType referenceType) throws AbsentInformationException {
        String targetMethodName = "evalAssign";
        return referenceType.methodsByName(targetMethodName).get(0).allLineLocations().get(0);
    }


}
