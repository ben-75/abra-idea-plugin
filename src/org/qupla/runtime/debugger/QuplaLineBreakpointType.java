package org.qupla.runtime.debugger;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuplaLineBreakpointType extends XLineBreakpointTypeBase {

    public QuplaLineBreakpointType() {
        super("qupla-line-breakpoint", "Qupla line breakpoint",null);
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        if(!file.getExtension().equals("qpl"))return false;
        PsiFile abraFile = PsiManager.getInstance(project).findFile(file);
        Document abraDoc = PsiDocumentManager.getInstance(project).getDocument(abraFile);
        int offset = abraDoc.getLineStartOffset(line);
        PsiElement element = QuplaDebuggerUtil.findEvaluableNearElement(abraFile.findElementAt(offset));
        if (element == null) return false;
        return abraDoc.getLineNumber(element.getTextRange().getStartOffset()) == line;
    }

}
