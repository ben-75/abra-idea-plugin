package org.qupla.runtime.debugger.ui;

import com.intellij.debugger.engine.DebugProcess;
import com.intellij.execution.process.ProcessHandler;
import org.qupla.runtime.debugger.QuplaDebugSession;
import org.qupla.runtime.interpreter.QuplaInterpreterRunConfiguration;
import org.qupla.runtime.interpreter.QuplaInterpreterState;

public interface QuplaDebuggerManager {

    QuplaDebugSession createSession(DebugProcess debugProcess, QuplaInterpreterRunConfiguration runConfiguration);

    void forgetSession(ProcessHandler processHandler);

    QuplaDebugSession getSession(ProcessHandler processHandler);

    QuplaDebugSession getSession(DebugProcess debugProcess);
}
