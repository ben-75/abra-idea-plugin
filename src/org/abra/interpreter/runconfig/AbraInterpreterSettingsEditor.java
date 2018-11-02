package org.abra.interpreter.runconfig;

import b.j.b.A;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.PsiManager;
import org.abra.interpreter.action.InputTritPanel;
import org.abra.language.psi.*;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class AbraInterpreterSettingsEditor extends SettingsEditor<AbraInterpreterRunConfiguration> {
    private AbraInterpreterRunConfigUI myPanel;
    private LabeledComponent<ComponentWithBrowseButton> myMainClass;
    private static VirtualFileFilter abraFileFilter = file -> file.isDirectory() || file.getName().endsWith(".abra");


    @Override
    protected void resetEditorFrom(@NotNull AbraInterpreterRunConfiguration runConfig) {
        myPanel.modules.setModel(getModulesModel(runConfig.getProject()));
        if(runConfig.getTargetModule()!=null){
            for(int i = 0; i< myPanel.modules.getModel().getSize(); i++){
                if(((AbraFileComboBoxItem) myPanel.modules.getModel().getElementAt(i)).getAbraFile().isEquivalentTo(runConfig.getTargetModule())){
                    myPanel.modules.setSelectedIndex(i);
                    myPanel.functionsInSelectedModule.setModel(getFunctionsModel(((AbraFileComboBoxItem) myPanel.modules.getModel().getElementAt(i)).getAbraFile()));
                    if(runConfig.getTargetFunc()!=null){
                        for(int j = 0; j< myPanel.functionsInSelectedModule.getModel().getSize(); j++){
                            if(((AbraFuncStmtComboBoxItem) myPanel.functionsInSelectedModule.getModel().getElementAt(j)).getFuncStmt().isEquivalentTo(runConfig.getTargetFunc())){
                                myPanel.functionsInSelectedModule.setSelectedIndex(j);
                                if(runConfig.getTargetFunc().isInTemplate()) {
                                    myPanel.targetTypeInstantiation.setModel(getTypeInstanciationListModel(runConfig.getTargetFunc()));
                                    String typeLabel = runConfig.getTargetFunc().getFuncSignature().getText().substring(
                                            runConfig.getTargetFunc().getFuncSignature().getOpenTag().getStartOffsetInParent(),
                                            runConfig.getTargetFunc().getFuncSignature().getCloseTag().getStartOffsetInParent() + 1);
                                    myPanel.typeInstLabel.setText("Mapping for " + typeLabel + " :");
                                    myPanel.typeInstLabel.setVisible(true);
                                    myPanel.targetTypeInstantiation.setVisible(true);
                                    if (runConfig.getTargetTypeInstantiation() != null) {
                                        for (int k = 0; k < myPanel.targetTypeInstantiation.getModel().getSize(); k++) {
                                            if (((AbraTypeInstComboBoxItem) myPanel.targetTypeInstantiation.getModel().getElementAt(k)).getTypeInstantiation().isEquivalentTo(runConfig.getTargetTypeInstantiation())) {
                                                myPanel.targetTypeInstantiation.setSelectedIndex(k);
                                                myPanel.typeInstLabel.setVisible(true);
                                                myPanel.targetTypeInstantiation.setVisible(true);
                                                if (runConfig.getTargetTypeInstantiation() != null) {
                                                    clearFuncParameters();
                                                    makeFuncParameters(runConfig.getTargetFunc(), runConfig.args==null?new ArrayList<>():Arrays.asList(runConfig.args));
                                                } else {
                                                    clearFuncParameters();
                                                }
                                                break;
                                            }
                                        }


                                    } else {
                                        myPanel.targetTypeInstantiation.setSelectedItem(null);
                                    }
                                }else{
                                    myPanel.typeInstLabel.setVisible(false);
                                    myPanel.targetTypeInstantiation.setVisible(false);
                                }
                                break;
                            }
                        }
                    }else{
                        myPanel.functionsInSelectedModule.setSelectedItem(null);
                        myPanel.targetTypeInstantiation.setVisible(false);
                        myPanel.typeInstLabel.setVisible(false);
                        myPanel.argsContainer.setVisible(false);
                    }
                    break;
                }
            }
        }else{
            myPanel.modules.setSelectedItem(null);
            myPanel.functionsInSelectedModule.setSelectedItem(null);
        }
        myPanel.runTestsCheckBox.setSelected(runConfig.isRunTest());
        myPanel.runEvalCheckBox.setSelected(runConfig.isRunEval());
        myPanel.echoCheckBox.setSelected(runConfig.isEcho());
        myPanel.verilogCheckBox.setSelected(runConfig.isFpga());
        myPanel.emitCheckBox.setSelected(runConfig.isEmit());
        myPanel.trimCheckBox.setSelected(runConfig.isTrim());
        myPanel.treeCheckBox.setSelected(runConfig.isTree());
    }

    @Override
    protected void applyEditorTo(@NotNull AbraInterpreterRunConfiguration runCongig) throws ConfigurationException {
        if(myPanel.modules.getSelectedItem()!=null) {
            runCongig.setTargetModule(((AbraFileComboBoxItem) myPanel.modules.getSelectedItem()).getAbraFile());
            if (myPanel.functionsInSelectedModule.getSelectedItem() != null) {
                runCongig.setTargetFunc(((AbraFuncStmtComboBoxItem) myPanel.functionsInSelectedModule.getSelectedItem()).getFuncStmt());
                if(myPanel.targetTypeInstantiation.getSelectedItem()!=null){
                    runCongig.setTargetTypeInstantiation(((AbraTypeInstComboBoxItem)myPanel.targetTypeInstantiation.getSelectedItem()).getTypeInstantiation());
                }else{
                    runCongig.setTargetTypeInstantiation(null);
                }
                runCongig.args = new String[myPanel.argsContainer.getComponentCount()];
                for(int i=0;i<myPanel.argsContainer.getComponentCount();i++){
                    runCongig.args[i]=((InputTritPanel)myPanel.argsContainer.getComponent(i)).getUserInput();
                }
            }else{
                runCongig.setTargetFunc(null);
                runCongig.setTargetTypeInstantiation(null);
            }
        } else {
            runCongig.setTargetModule(null);
            runCongig.setTargetFunc(null);
            runCongig.setTargetTypeInstantiation(null);
        }
        runCongig.setRunTest(myPanel.runTestsCheckBox.isSelected());
        runCongig.setRunEval(myPanel.runEvalCheckBox.isSelected());
        runCongig.setEcho(myPanel.echoCheckBox.isSelected());
        runCongig.setFpga(myPanel.verilogCheckBox.isSelected());
        runCongig.setEmit(myPanel.emitCheckBox.isSelected());
        runCongig.setTree(myPanel.treeCheckBox.isSelected());
        runCongig.setTrim(myPanel.trimCheckBox.isSelected());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        myPanel = new AbraInterpreterRunConfigUI();
        myPanel.modules.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(myPanel.modules.getSelectedItem()!=null) {
                    myPanel.functionsInSelectedModule.setModel(getFunctionsModel(((AbraFileComboBoxItem) myPanel.modules.getSelectedItem()).getAbraFile()));
                    myPanel.functionsInSelectedModule.setEnabled(true);
                    myPanel.functionsInSelectedModule.setSelectedItem(null);
                }else{
                    myPanel.functionsInSelectedModule.setModel(new ListComboBoxModel(new ArrayList<AbraFileComboBoxItem>()));
                    myPanel.functionsInSelectedModule.setEnabled(false);
                    myPanel.typeInstLabel.setVisible(false);
                    myPanel.targetTypeInstantiation.setVisible(false);
                }
            }
        });
        myPanel.functionsInSelectedModule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(myPanel.functionsInSelectedModule.getSelectedItem()!=null) {
                    AbraFuncStmt funcStmt = ((AbraFuncStmtComboBoxItem)myPanel.functionsInSelectedModule.getSelectedItem()).getFuncStmt();
                    if(funcStmt.isInTemplate()){
                        myPanel.targetTypeInstantiation.setModel(getTypeInstanciationListModel(funcStmt));
                        myPanel.targetTypeInstantiation.setSelectedItem(null);
                        myPanel.typeInstLabel.setVisible(true);
                        myPanel.targetTypeInstantiation.setVisible(true);
                    }else{
                        myPanel.typeInstLabel.setVisible(false);
                        myPanel.targetTypeInstantiation.setVisible(false);
                    }
                    List<String> previousArgs = clearFuncParameters();
                    makeFuncParameters(funcStmt, previousArgs);
                    myPanel.argsContainer.setVisible(true);
                    myPanel.argsContainerLabel.setVisible(true);
                }else{
                    myPanel.typeInstLabel.setVisible(false);
                    myPanel.targetTypeInstantiation.setVisible(false);
                    myPanel.argsContainer.setVisible(false);
                    myPanel.argsContainerLabel.setVisible(false);

                }
            }
        });
        myPanel.modules.setSelectedItem(null);
        myPanel.functionsInSelectedModule.setSelectedItem(null);

        myPanel.targetTypeInstantiation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(myPanel.targetTypeInstantiation.getSelectedItem()==null){
                    clearFuncParameters();
                }else{
                    List<String> previousArgs = clearFuncParameters();
                    makeFuncParameters(((AbraFuncStmtComboBoxItem)myPanel.functionsInSelectedModule.getSelectedItem()).getFuncStmt(),previousArgs);
                }
            }
        });
        return myPanel.rootConfigPane;
    }

    private void makeFuncParameters(AbraFuncStmt funcStmt, List<String> args){
        int i=0;
        for(AbraFuncParameter funcParameter:funcStmt.getFuncSignature().getFuncParameterList()){
            InputTritPanel inputTritPanel = new InputTritPanel(funcParameter.getParamName().getText(),300);
            myPanel.argsContainer.add(inputTritPanel);
            if(args!=null && args.size()>=i+1 && args.get(i)!=null){
                inputTritPanel.setUserInput(args.get(i));
            }
            i++;
        }
    }

    private List<String> clearFuncParameters(){
        ArrayList<String> data = new ArrayList<>();
        for(int i=0;i<myPanel.argsContainer.getComponentCount();i++){
            data.add(((InputTritPanel)myPanel.argsContainer.getComponent(i)).getUserInput());
        }
        myPanel.argsContainer.removeAll();
        return data;
    }

    private void createUIComponents() {
        myMainClass = new LabeledComponent<ComponentWithBrowseButton>();
        myMainClass.setComponent(new TextFieldWithBrowseButton());
    }


    private static ComboBoxModel getModulesModel(Project project){
        VirtualFile[] roots = ProjectRootManager.getInstance(project).getContentSourceRoots();
        if(roots.length==0){
            roots = ProjectRootManager.getInstance(project).getContentRoots();
        }
        List<AbraFileComboBoxItem> abraFiles = new ArrayList<>();
        for(VirtualFile f:roots){
            VfsUtilCore.iterateChildrenRecursively(f, abraFileFilter, fileOrDir -> {
                if(!fileOrDir.isDirectory()){
                    abraFiles.add(new AbraFileComboBoxItem((AbraFile)PsiManager.getInstance(project).findFile(fileOrDir)));
                }
                return true;
            });
        }
        abraFiles.sort(new Comparator<AbraFileComboBoxItem>() {
            @Override
            public int compare(AbraFileComboBoxItem o1, AbraFileComboBoxItem o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        return new ListComboBoxModel(abraFiles);
    }

    private static ComboBoxModel getFunctionsModel(AbraFile abraModule){
        if(abraModule==null)return new ListComboBoxModel(new ArrayList());
        List<AbraFuncStmtComboBoxItem> model = new ArrayList<>();
        for(AbraFuncStmt f:abraModule.findAllFuncStmt()){
            model.add(new AbraFuncStmtComboBoxItem(f));
        }
        return new ListComboBoxModel(model);
    }

    private static ComboBoxModel getTypeInstanciationListModel(AbraFuncStmt funcStmt){
        if(funcStmt==null)return new ListComboBoxModel(new ArrayList());
        List<AbraTypeInstComboBoxItem> model = new ArrayList<>();
        for(AbraTypeInstantiation inst:funcStmt.getAllTypeInstantiation()){
            model.add(new AbraTypeInstComboBoxItem(inst));
        }
        return new ListComboBoxModel(model);
    }
    public static class AbraFileComboBoxItem{
        private final AbraFile abraFile;

        public AbraFileComboBoxItem(AbraFile abraFile) {
            this.abraFile = abraFile;
        }

        @Override
        public String toString() {
            return abraFile.getImportableFilePath();
        }

        public AbraFile getAbraFile() {
            return abraFile;
        }
    }

    public static class AbraFuncStmtComboBoxItem{
        private final AbraFuncStmt abraFuncStmt;

        public AbraFuncStmtComboBoxItem(AbraFuncStmt abraFuncStmt) {
            this.abraFuncStmt = abraFuncStmt;
        }

        @Override
        public String toString() {
            return abraFuncStmt.getFuncSignature().getText()+
                    (abraFuncStmt.getParent() instanceof AbraTemplateStmt ?
                            (" (from template "+((AbraTemplateStmt)abraFuncStmt.getParent()).getTemplateName().getText()+")"):"");
        }

        public AbraFuncStmt getFuncStmt() {
            return abraFuncStmt;
        }
    }

    public static class AbraTypeInstComboBoxItem{
        private final AbraTypeInstantiation typeInstantiation;

        public AbraTypeInstComboBoxItem(AbraTypeInstantiation typeInstantiation) {
            this.typeInstantiation = typeInstantiation;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("<");
            for(AbraTypeNameRef typeNameRef:typeInstantiation.getTypeNameRefList()){
                AbraTypeName typeName = (AbraTypeName) typeNameRef.getReference().resolve();
                if(typeName!=null){
                    sb.append(AbraPsiImplUtil.getResolvedSize((AbraTypeStmt)typeName.getParent()));
                }else {
                    sb.append("?");
                }
                sb.append(" , ");
            }
            String s = sb.toString().substring(0,sb.length()-3)+">";

            return typeInstantiation.getText()+" ("+s+" trits)";
        }

        public AbraTypeInstantiation getTypeInstantiation() {
            return typeInstantiation;
        }
    }
}