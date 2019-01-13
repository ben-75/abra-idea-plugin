package org.qupla.runtime.debugger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.java.debugger.breakpoints.properties.JavaBreakpointProperties;

public class QuplaBreakpointProperties<T extends QuplaBreakpointProperties> extends JavaBreakpointProperties<T> {

    public QuplaBreakpointProperties() {
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