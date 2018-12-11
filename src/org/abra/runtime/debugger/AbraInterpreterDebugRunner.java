package org.abra.interpreter.debugger;

import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.abra.interpreter.runconfig.AbraInterpreterRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbraInterpreterDebugRunner extends GenericDebuggerRunner {

    @Override
    protected void execute(@NotNull ExecutionEnvironment environment, @Nullable Callback callback, @NotNull RunProfileState state) throws ExecutionException {
        super.execute(environment, callback, state);
    }

    @NotNull
    @Override
    public String getRunnerId() {
        return "org.abra.interpreter.debugger";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return super.canRun(executorId, profile) && profile instanceof AbraInterpreterRunConfiguration;
    }

}
