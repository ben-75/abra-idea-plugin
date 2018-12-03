package org.abra.interpreter.debugger;

import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.debugger.ui.breakpoints.JavaLineBreakpointType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import org.jetbrains.annotations.NotNull;

public class AbraBreakpointType extends JavaLineBreakpointType {
    //public class AbraBreakpointType<P extends AbraBreakpointProperties> extends XLineBreakpointType<P> {
    public static final String ID = "org.abra.breakpoint";

    public static final AbraBreakpointType INSTANCE = new AbraBreakpointType();

    public AbraBreakpointType() {
        super(ID, "Abra Breakpoint");
    }

    @NotNull
    @Override
    public Breakpoint createJavaBreakpoint(Project project, XBreakpoint xBreakpoint) {
        if (true) return AbraLineBreakpoint.create(project, xBreakpoint);
        Breakpoint breakpoint = super.createJavaBreakpoint(project, xBreakpoint);
        return breakpoint;
    }


    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        if (!file.getName().endsWith(".abra")) return false;
        PsiFile abraFile = PsiManager.getInstance(project).findFile(file);
        Document abraDoc = PsiDocumentManager.getInstance(project).getDocument(abraFile);
        int offset = abraDoc.getLineStartOffset(line);
        PsiElement element = AbraDebuggerUtil.findEvaluable(AbraDebuggerUtil.getAbraPsiElement(abraFile, offset));
        if (element == null) return false;
        return abraDoc.getLineNumber(element.getTextRange().getStartOffset()) == line;
    }
}
