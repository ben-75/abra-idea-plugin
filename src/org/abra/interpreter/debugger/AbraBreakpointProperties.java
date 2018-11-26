package org.abra.interpreter.debugger;

import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbraBreakpointProperties<T extends AbraBreakpointProperties> extends XBreakpointProperties<T> {

    public AbraBreakpointProperties() {
    }

    @Nullable
    @Override
    public T getState() {
        return null;
    }

    @Override
    public void loadState(@NotNull T state) {

    }
}
