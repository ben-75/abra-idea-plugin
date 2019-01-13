package org.qupla.runtime.debugger;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase;
import org.jetbrains.annotations.NotNull;

public class QuplaLineBreakpointType extends XLineBreakpointTypeBase {

    public QuplaLineBreakpointType() {
        super("qupla-line-breakpoint", "Qupla line breakpoint",null);
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        if(!file.getExtension().equals("qpl"))return false;
        return true;  //TODO
    }

}
