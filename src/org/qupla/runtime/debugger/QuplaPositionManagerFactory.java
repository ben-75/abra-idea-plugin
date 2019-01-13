package org.qupla.runtime.debugger;

import com.intellij.debugger.PositionManager;
import com.intellij.debugger.PositionManagerFactory;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuplaPositionManagerFactory extends PositionManagerFactory {

    @Nullable
    @Override
    public PositionManager createPositionManager(@NotNull DebugProcess process) {
        PositionManager positionManager = new QuplaPositionManager(process);
        return positionManager;
    }
}
