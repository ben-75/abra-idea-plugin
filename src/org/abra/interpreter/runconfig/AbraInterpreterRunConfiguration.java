package org.abra.interpreter.runconfig;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.abra.language.psi.*;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AbraInterpreterRunConfiguration extends RunConfigurationBase {

    private boolean runTest = false;
    private boolean runEval = false;
    private boolean checkTrits = false;
    private boolean echo = false;
    private AbraFile targetModule = null;
    private PsiElement targetFunc = null;
    private List<String> args;
    private VirtualFileFilter abraFileFilter = file -> file.isDirectory() || file.getName().endsWith(".abra");

    protected AbraInterpreterRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);

    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new AbraInterpreterSettingsEditor();
    }

    public ComboBoxModel getModulesModel(){
        VirtualFile[] roots = ProjectRootManager.getInstance(getProject()).getContentSourceRoots();
        if(roots.length==0){
            roots = ProjectRootManager.getInstance(getProject()).getContentRoots();
        }
        List<VirtualFile> abraFiles = new ArrayList<>();
        for(VirtualFile f:roots){
            VfsUtilCore.iterateChildrenRecursively(f, abraFileFilter, fileOrDir -> abraFiles.add(fileOrDir));
        }
        return new ListComboBoxModel(abraFiles);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if(targetModule==null)throw new RuntimeConfigurationException("Module is not defined");
        if(targetFunc==null && !runTest && !runEval && !checkTrits){
            throw new RuntimeConfigurationException("Execution target (eval, test, checkTrits or function) is not defined");
        }

        if(targetFunc!=null){
            String funcName = null;
            if(targetFunc instanceof AbraUseStmt){
                funcName = ((AbraUseStmt)targetFunc).getTemplateNameRef().getText();
            }
            else if(targetFunc instanceof AbraFuncStmt){
                funcName = ((AbraFuncStmt)targetFunc).getFuncDefinition().getFuncName().getText();
            }
            if(funcName==null){
                throw new RuntimeConfigurationException("Element "+targetFunc.getText()+" is not a valid evaluation target");
            }
            boolean foundTargetFunc = false;
            for(ASTNode n:targetModule.getNode().getChildren(TokenSet.create(AbraTypes.USE_STMT, AbraTypes.FUNC_STMT))){
                if(n.getElementType()==AbraTypes.FUNC_STMT){
                    if(((AbraFuncStmt)n.getPsi()).getFuncDefinition().getFuncName().getText().equals(funcName)){
                        foundTargetFunc = true;
                        break;
                    }
                }else if(n.getElementType()==AbraTypes.USE_STMT){
                    AbraTemplateName templateName = (AbraTemplateName) ((AbraUseStmt)n.getPsi()).getTemplateNameRef().getReference().resolve();
                    if(templateName!=null) {
                        AbraTemplateStmt templateStmt = (AbraTemplateStmt) templateName.getParent();
                        if (templateStmt.getFuncDefinition().getFuncName().getText().equals(funcName)) {
                            foundTargetFunc = true;
                            break;
                        }
                    }
                }
            }
            if(!foundTargetFunc){
                throw new RuntimeConfigurationException("Function "+funcName+" is not defined");
            }
        }
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return null;
    }
}