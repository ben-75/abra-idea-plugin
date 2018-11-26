package org.abra.interpreter.debugger;

import com.intellij.debugger.engine.JavaDebugAware;
import com.intellij.psi.PsiFile;
import org.abra.language.psi.AbraFile;
import org.jetbrains.annotations.NotNull;

public class AbraJavaDebugAware extends JavaDebugAware {

    @Override
    public boolean isBreakpointAware(@NotNull PsiFile psiFile) {
        return psiFile instanceof AbraFile;
    }
}
