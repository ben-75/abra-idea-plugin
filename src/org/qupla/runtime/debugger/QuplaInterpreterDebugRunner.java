package org.qupla.runtime.debugger;

import com.intellij.debugger.DebugEnvironment;
import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.DefaultDebugEnvironment;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.JavaDebugProcess;
import com.intellij.debugger.impl.DebuggerManagerImpl;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RemoteConnection;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qupla.runtime.debugger.ui.QuplaDebuggerManager;
import org.qupla.runtime.interpreter.QuplaInterpreterRunConfiguration;
import org.qupla.runtime.interpreter.QuplaInterpreterState;

public class QuplaInterpreterDebugRunner  extends GenericDebuggerRunner {

    @Override
    public void onProcessStarted(RunnerSettings settings, ExecutionResult executionResult) {
        super.onProcessStarted(settings, executionResult);
    }

    @Override
    protected void execute(@NotNull ExecutionEnvironment environment, @Nullable Callback callback, @NotNull RunProfileState state) throws ExecutionException {
        super.execute(environment, callback, state);
    }

    @NotNull
    @Override
    public String getRunnerId() {
        return "org.qupla.interpreter.debugger";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return super.canRun(executorId, profile) && profile instanceof QuplaInterpreterRunConfiguration;
    }

    @Nullable
    protected RunContentDescriptor attachVirtualMachine(RunProfileState state,
                                                        @NotNull ExecutionEnvironment env,
                                                        RemoteConnection connection,
                                                        long pollTimeout) throws ExecutionException {
        DebugEnvironment environment = new DefaultDebugEnvironment(env, state, connection, pollTimeout);
        final DebuggerSession debuggerSession = DebuggerManagerEx.getInstanceEx(env.getProject()).attachVirtualMachine(environment);
        if (debuggerSession == null) {
            return null;
        }

        final DebugProcessImpl debugProcess = debuggerSession.getProcess();

        env.getProject().getComponent(QuplaDebuggerManager.class).createSession(debugProcess, ((QuplaInterpreterState)state).getRunConfiguration());
        return XDebuggerManager.getInstance(env.getProject()).startSession(env, new XDebugProcessStarter() {
            @Override
            @NotNull
            public XDebugProcess start(@NotNull XDebugSession session) {
                XDebugSessionImpl sessionImpl = (XDebugSessionImpl)session;
                ExecutionResult executionResult = debugProcess.getExecutionResult();
                sessionImpl.addExtraActions(executionResult.getActions());
                if (executionResult instanceof DefaultExecutionResult) {
                    sessionImpl.addRestartActions(((DefaultExecutionResult)executionResult).getRestartActions());
                }
                return JavaDebugProcess.create(session, debuggerSession);
            }
        }).getRunContentDescriptor();
    }
}

