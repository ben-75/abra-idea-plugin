package org.qupla.runtime.interpreter;

import com.intellij.execution.*;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.qupla.language.module.QuplaModule;
import org.qupla.language.module.QuplaModuleManager;
import org.qupla.utils.TritUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class QuplaInterpreterState extends JavaCommandLineState {

    private QuplaInterpreterRunConfiguration runConfiguration;

    public QuplaInterpreterState(ExecutionEnvironment environment, QuplaInterpreterRunConfiguration runConfig) {
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
        return buildJavaParameters(runConfiguration);
    }

    @NotNull
    public static JavaParameters buildJavaParameters(QuplaInterpreterRunConfiguration runConfiguration) {
        JavaParameters javaParameters = new JavaParameters();
        javaParameters.setWorkingDirectory(runConfiguration.getProject().getComponent(QuplaModuleManager.class).getFullQuplaSourceRootPath());

        javaParameters.setMainClass("org.iota.qupla.Qupla");

        //javaParameters.getVMParametersList()
        if(!runConfiguration.getRunMode().equals("custom")) {
            if (runConfiguration.isEcho()) {
                javaParameters.getProgramParametersList().add("-echo");
            }
            if (runConfiguration.isRunTest()) {
                javaParameters.getProgramParametersList().add("-test");
            }
            if (runConfiguration.isRunEval()) {
                javaParameters.getProgramParametersList().add("-eval");
            }
            if (runConfiguration.isAbra()) {
                javaParameters.getProgramParametersList().add("-abra");
            }
            if (runConfiguration.isFpga()) {
                javaParameters.getProgramParametersList().add("-fpga");
            }
            if (runConfiguration.isTree()) {
                javaParameters.getProgramParametersList().add("-tree");
            }
            if (runConfiguration.isTrim()) {
                javaParameters.getProgramParametersList().add("-trim");
            }
            if (runConfiguration.isView()) {
                javaParameters.getProgramParametersList().add("-view");
            }
        }

        //module
        if(runConfiguration.getQuplaModules()!=null && runConfiguration.getQuplaModules().size()>0) {
            for(QuplaModule m:runConfiguration.getQuplaModules()){
                javaParameters.getProgramParametersList().add(m.getName());
            }
        }

        //function
        if(runConfiguration.getRunMode().equals("function")) {
            String type = "";
            if (runConfiguration.getTargetTypeInstantiation() != null) {
                type = runConfiguration.getTargetTypeInstantiation().getText();
            } else if (runConfiguration.getTargetTypeName() != null) {
                type = "<" + runConfiguration.getTargetTypeName().getText() + ">";
            }
            StringBuilder sb = new StringBuilder();
            if (runConfiguration.getTargetFunc() != null && runConfiguration.hasArgs()) {
                for (String s : runConfiguration.getArgs()) {
                    sb.append(TritUtils.decimalValue(s)).append(",");
                }
                String args = sb.substring(0, sb.length() - 1);
                javaParameters.getProgramParametersList().add(runConfiguration.getTargetFunc().getFuncSignature().getFuncName().getText() + type + "(" + args + ")");
            }
        }

        if(runConfiguration.getRunMode().equals("custom")){
            if(runConfiguration.getCustomArgs()!=null) {
                for (String s : runConfiguration.getCustomArgs().split(" ")) {
                    javaParameters.getProgramParametersList().add(s);
                }
            }
        }
        javaParameters.setJdk(ProjectRootManager.getInstance(runConfiguration.getProject()).getProjectSdk());

        Collection<Module> modules = ModuleUtil.getModulesOfType(runConfiguration.getProject(), JavaModuleType.getModuleType());

        for (Module module : modules)
        {
            VirtualFile outputDirectory = CompilerPaths.getModuleOutputDirectory(module, false);
            if(outputDirectory!=null)
                javaParameters.getClassPath().add(new File(outputDirectory.getPath()));
        }

        return javaParameters;
    }

    private File[] findClasses(Project project){
        List<File> classes = new LinkedList();
        Collection<Module> modules = ModuleUtil.getModulesOfType(project, JavaModuleType.getModuleType());

        for (Module module : modules)
        {
            VirtualFile outputDirectory = CompilerPaths.getModuleOutputDirectory(module, false);
            if(outputDirectory!=null)
                classes.addAll(getAllChildren(outputDirectory, "class"));
        }

        return classes.toArray(new File[classes.size()]);
    }

    private List<File> getAllChildren(VirtualFile rootDir, String extension)
    {
        assert rootDir.isDirectory() : "rootDir isn't a directory";

        List<File> children = new LinkedList();

        for (VirtualFile entry : rootDir.getChildren())
        {
            if (entry.isDirectory())
                children.addAll(getAllChildren(entry, extension));
            else if (extension.equals(entry.getExtension()))
                children.add(new File(entry.getPath()));
        }

        return children;
    }

    public QuplaInterpreterRunConfiguration getRunConfiguration() {
        return runConfiguration;
    }
}
