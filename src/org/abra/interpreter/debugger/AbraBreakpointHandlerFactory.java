package org.abra.interpreter.debugger;

import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.JavaBreakpointHandler;
import com.intellij.debugger.engine.JavaBreakpointHandlerFactory;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.ArrayList;
import java.util.List;

public class AbraBreakpointHandlerFactory implements JavaBreakpointHandlerFactory {

    private AbraToJavaMapper abraToJavaMapper;

    @Override
    public JavaBreakpointHandler createHandler(DebugProcessImpl process) {
        if(abraToJavaMapper==null){
            abraToJavaMapper = new AbraToJavaMapper(process.getProject());
        }

        return new AbraBreakpointHandler(process, abraToJavaMapper);
    }


}
