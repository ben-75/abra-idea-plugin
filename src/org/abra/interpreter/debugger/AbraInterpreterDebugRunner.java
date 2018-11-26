package org.abra.interpreter.debugger;

import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import org.abra.interpreter.action.AbraInterpreterProgramRunner;
import org.abra.interpreter.runconfig.AbraInterpreterRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbraInterpreterDebugRunner extends GenericDebuggerRunner {

    @Override
    protected void execute(@NotNull ExecutionEnvironment environment, @Nullable Callback callback, @NotNull RunProfileState state) throws ExecutionException {
        System.out.println("abra debugger execute");
        //state.execute(environment.getExecutor(),this);
       // XDebuggerManager.getInstance(environment.getProject()).startSession(environment, new AbraDebugProcessStarter());
        super.execute(environment, callback, state);
    }

    @NotNull
    @Override
    public String getRunnerId() {
        return "org.abra.interpreter.debugger";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return /*executorId.equals("Run")*/ super.canRun(executorId, profile) && profile instanceof AbraInterpreterRunConfiguration;
    }

}
