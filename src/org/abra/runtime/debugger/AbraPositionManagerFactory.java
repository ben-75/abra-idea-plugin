package org.abra.interpreter.debugger;

import com.intellij.debugger.PositionManager;
import com.intellij.debugger.PositionManagerFactory;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbraPositionManagerFactory extends PositionManagerFactory {

    private AbraToJavaMapper abraToJavaMapper;
    @Nullable
    @Override
    public PositionManager createPositionManager(@NotNull DebugProcess process) {
        ApplicationManager.getApplication().runReadAction(
                () -> {
                    abraToJavaMapper = new AbraToJavaMapper(process.getProject());
                });
        PositionManager positionManager = new AbraPositionManager(process, abraToJavaMapper);
        return positionManager;
    }
}
