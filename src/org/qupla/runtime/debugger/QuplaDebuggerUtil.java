package org.qupla.runtime.debugger;

import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.requests.RequestManagerImpl;
import com.intellij.debugger.ui.breakpoints.FilteredRequestor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.BreakpointRequest;
import org.qupla.language.psi.*;
import org.qupla.runtime.debugger.requestor.EvalEntryRequestor;
import org.qupla.runtime.debugger.requestor.EvalExitRequestor;

import java.util.ArrayList;
import java.util.List;

public class QuplaDebuggerUtil {

    public static PsiElement getQuplaPsiElement(PsiElement element) {
        while (element instanceof PsiWhiteSpace) element = element.getNextSibling();
        if (element == null) return null;
        if (element instanceof QuplaFuncBody){
            if (((QuplaFuncBody) element).getAssignExprList().size() == 0) {
                return getQuplaPsiElement(((QuplaFuncBody) element).getReturnExpr());
            }else{
                return getQuplaPsiElement(((QuplaFuncBody) element).getAssignExprList().get(0));
            }
        }
        if(element instanceof QuplaFuncStmt){
            return getQuplaPsiElement(((QuplaFuncStmt) element).getFuncBody());
        }
        if(element instanceof QuplaCondExpr){
            return getQuplaPsiElement(((QuplaCondExpr)element).getMergeExprList().get(0));
        }
        while (!(element instanceof QuplaFuncExpr) && !(element instanceof QuplaConcatExpr)
                && !(element instanceof QuplaAssignExpr) && !(element instanceof QuplaLutExpr)
                && !(element instanceof QuplaMergeExpr) && !(element instanceof QuplaSliceExpr)
                && !(element instanceof QuplaStateExpr) && !(element instanceof QuplaReturnExpr) && !(element instanceof QuplaFile)
                && !(element instanceof QuplaFuncStmt)) {
            element = element.getParent();
        }
        return element;
    }

    public static PsiElement findEvaluable(PsiElement element) {
        if (element instanceof QuplaReturnExpr) {
            element = ((QuplaReturnExpr) element).getCondExpr();
            while (element.getChildren().length == 1 && QuplaDebuggerUtil.isEvaluable(element.getChildren()[0])) {
                element = element.getChildren()[0];
            }
        }
        if (element instanceof QuplaFuncStmt) {
            if (((QuplaFuncStmt) element).getFuncBody().getAssignExprList().size() == 0) {
                return findEvaluable(((QuplaFuncStmt) element).getFuncBody().getReturnExpr());
            }
        }
        return element;
    }

    public static boolean isEvaluable(PsiElement element) {
        return (element instanceof QuplaFuncExpr) || (element instanceof QuplaConcatExpr)
                || (element instanceof QuplaAssignExpr) || (element instanceof QuplaLutExpr)
                || (element instanceof QuplaMergeExpr) || (element instanceof QuplaSliceExpr)
                || (element instanceof QuplaStateExpr) || (element instanceof QuplaReturnExpr) || (element instanceof QuplaPostfixExpr);
    }

    public static PsiElement findEvaluableNearElement(PsiElement element) {
        return findEvaluable(QuplaDebuggerUtil.getQuplaPsiElement(element));
    }


    public static void registerContextBreakpoints(DebugProcess debugProcess, ReferenceType referenceType, String methodName, FilteredRequestor entryRequestor, FilteredRequestor exitRequestor){
        registerContextBreakpoints(debugProcess, referenceType, methodName, -79, entryRequestor, exitRequestor);
    }

    public static void registerContextBreakpoints(DebugProcess debugProcess, ReferenceType referenceType, String methodName, int returnBytecode, FilteredRequestor entryRequestor, FilteredRequestor exitRequestor){
        Method method = referenceType.methodsByName(methodName).get(0);
        try {
            RequestManagerImpl requestManager = (RequestManagerImpl) debugProcess.getRequestsManager();

            Location firstLine = method.allLineLocations().get(0);
            BreakpointRequest request = requestManager.createBreakpointRequest(entryRequestor,firstLine);
            requestManager.enableRequest(request);

            List<Location> exitLocations = new ArrayList<>();
            byte[] bytecodes = method.bytecodes();
            for(int i=0;i<bytecodes.length;i++){
                if(bytecodes[i]==returnBytecode)exitLocations.add(method.locationOfCodeIndex(i));
            }
            for(Location exitLoc:exitLocations){
                BreakpointRequest exitRequest = requestManager.createBreakpointRequest(exitRequestor,exitLoc);
                requestManager.enableRequest(exitRequest);
            }

        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
    }
}
