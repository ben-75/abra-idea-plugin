package org.qupla.runtime.interpreter;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuplaInterpreterProgramRunner extends DefaultJavaProgramRunner {

    @Override
    protected void execute(@NotNull ExecutionEnvironment environment, @Nullable Callback callback, @NotNull RunProfileState state) throws ExecutionException {
        super.execute(environment, callback, state);
    }

    @NotNull
    @Override
    public String getRunnerId() {
        return "org.qupla.runtime.java";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return /*executorId.equals("Run")*/ super.canRun(executorId, profile) && profile instanceof QuplaInterpreterRunConfiguration;
    }
}
