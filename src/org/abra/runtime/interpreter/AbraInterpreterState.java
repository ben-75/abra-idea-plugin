package org.abra.runtime.interpreter;

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
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import org.abra.language.psi.AbraPsiImplUtil;
import org.abra.utils.TritUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
        javaParameters.getVMParametersList().add("-Dabra.tritcode=out/tritcode/build/tritcode");
        javaParameters.setWorkingDirectory(
                AbraPsiImplUtil.getSourceRoot(runConfiguration.getProject(),
                        runConfiguration.getTargetModule().getVirtualFile()).getPath());
        javaParameters.setMainClass("org.iota.abra.Main");
        //javaParameters.getVMParametersList()
        StringBuilder sb = new StringBuilder();

        javaParameters.getProgramParametersList().add(runConfiguration.getTargetModule().getImportableFilePath());

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
       //javaParameters.getClassPath().add(new File(PropertiesComponent.getInstance().getValue("org.abra.language.interpreterpath")));
        //javaParameters.getClassPath().addAllFiles(findClasses(runConfiguration.getProject()));
        Sdk sdk = null;
        try {
            sdk = (Sdk)ProjectRootManager.getInstance(runConfiguration.getProject()).getProjectSdk().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        Collection<Module> modules = ModuleUtil.getModulesOfType(runConfiguration.getProject(), JavaModuleType.getModuleType());
        for (Module module : modules)
        {
            VirtualFile outputDirectory = CompilerPaths.getModuleOutputDirectory(module, false);
            if(outputDirectory!=null) {
                javaParameters.getClassPath().add(new File(outputDirectory.getPath()));
                //sdk.getSdkModificator().addRoot(outputDirectory, OrderRootType.CLASSES);
            }
        }
        //sdk.getSdkModificator().commitChanges();
        javaParameters.setJdk(sdk);
        javaParameters.getClassPath().add(new File("C:\\Users\\bmangez\\IOTA\\abra\\build\\libs\\abra.jar"));
        XBreakpointManager breakpointManager = XDebuggerManager.getInstance(runConfiguration.getProject()).getBreakpointManager();

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
}
