package org.qupla.runtime.debugger;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
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
        return true;  //TODO
    }

    @Nullable
    @Override
    public QuplaBreakpointProperties createProperties() {
        return new QuplaBreakpointProperties(); //TODO
    }

    @Nullable
    @Override
    public XBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
        return createProperties(); //TODO
    }
}
