package org.abra.interpreter.action;

import com.intellij.execution.*;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.roots.ProjectRootManager;
import org.abra.interpreter.runconfig.AbraInterpreterRunConfiguration;
import org.abra.language.psi.AbraPsiImplUtil;
import org.abra.utils.TritUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class AbraInterpreterState extends JavaCommandLineState {

    private AbraInterpreterRunConfiguration runConfiguration;

    public AbraInterpreterState(ExecutionEnvironment environment, AbraInterpreterRunConfiguration runConfig) {
        super(environment);
        runConfiguration = runConfig;
    }

    @Override
    @NotNull
    public ExecutionResult execute(@NotNull final Executor executor, @NotNull final ProgramRunner runner) throws ExecutionException {
        final ProcessHandler processHandler = startProcess();
        final ConsoleView console = createConsole(executor);
        if (console != null) {
            console.attachToProcess(processHandler);
        }
        return new DefaultExecutionResult(console, processHandler, createActions(console, processHandler, executor));
    }

    @NotNull
    @Override
    protected OSProcessHandler startProcess() throws ExecutionException {
        return super.startProcess();
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        JavaParameters javaParameters = new JavaParameters();
        javaParameters.setWorkingDirectory(
                AbraPsiImplUtil.getSourceRoot(runConfiguration.getProject(),
                        runConfiguration.getTargetModule().getVirtualFile()).getPath());
        javaParameters.setMainClass("org.iota.abra.Main");
        StringBuilder sb = new StringBuilder();

        javaParameters.getProgramParametersList().add(runConfiguration.getTargetModule().getImportableFilePath());

        if(runConfiguration.isCheckTrits()){
            javaParameters.getProgramParametersList().add("-trit");
        }
        if(runConfiguration.isEcho()){
            javaParameters.getProgramParametersList().add("-echo");
        }
        if(runConfiguration.isRunTest()){
            javaParameters.getProgramParametersList().add("-test");
        }
        if(runConfiguration.isRunEval()){
            javaParameters.getProgramParametersList().add("-eval");
        }
        if(runConfiguration.isEmit()){
            javaParameters.getProgramParametersList().add("-emit");
        }
        if(runConfiguration.isFpga()){
            javaParameters.getProgramParametersList().add("-fpga");
        }
        if(runConfiguration.isTree()){
            javaParameters.getProgramParametersList().add("-tree");
        }
        if(runConfiguration.isTrim()){
            javaParameters.getProgramParametersList().add("-trim");
        }
        String type = "";
        if(runConfiguration.getTargetTypeInstantiation()!=null){
            type=runConfiguration.getTargetTypeInstantiation().getText();
        }
        if(runConfiguration.getTargetFunc()!=null && runConfiguration.hasArgs()) {
            for (String s : runConfiguration.getArgs()) {
                sb.append(TritUtils.decimalValue(s)).append(",");
            }
            String args = sb.substring(0,sb.length()-1);
            javaParameters.getProgramParametersList().add(runConfiguration.getTargetFunc().getFuncSignature().getFuncName().getText() + type + "(" + args + ")");
        }
        javaParameters.setJdk(ProjectRootManager.getInstance(runConfiguration.getProject()).getProjectSdk());
        javaParameters.getClassPath().add(new File(PropertiesComponent.getInstance().getValue("org.abra.language.interpreterpath")));
        return javaParameters;
    }
}
