package org.qupla.runtime.interpreter;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.qupla.language.module.QuplaModule;
import org.qupla.language.module.QuplaModuleManager;
import org.qupla.language.psi.*;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class QuplaInterpreterSettingsEditor extends SettingsEditor<QuplaInterpreterRunConfiguration> {
    private QuplaInterpreterRunConfigUI myPanel;
    private LabeledComponent<ComponentWithBrowseButton> myMainClass;


    @Override
    protected void resetEditorFrom(@NotNull QuplaInterpreterRunConfiguration runConfig) {
        myPanel.modules.setModel(getModulesModel(runConfig.getProject()));
        if(runConfig.getTargetModule()!=null){
            for(int i = 0; i< myPanel.modules.getModel().getSize(); i++){
                if(((QuplaFileComboBoxItem) myPanel.modules.getModel().getElementAt(i)).getQuplaFile().isEquivalentTo(runConfig.getTargetModule())){
                    myPanel.modules.setSelectedIndex(i);
                    myPanel.functionsInSelectedModule.setModel(getFunctionsModel(((QuplaFileComboBoxItem) myPanel.modules.getModel().getElementAt(i)).getQuplaFile()));
                    if(runConfig.getTargetFunc()!=null){
                        for(int j = 0; j< myPanel.functionsInSelectedModule.getModel().getSize(); j++){
                            if(((QuplaFuncStmtComboBoxItem) myPanel.functionsInSelectedModule.getModel().getElementAt(j)).getFuncStmt().isEquivalentTo(runConfig.getTargetFunc())){
                                myPanel.functionsInSelectedModule.setSelectedIndex(j);
                                if(runConfig.getTargetFunc().isInTemplate()) {
                                    myPanel.targetTypeInstantiation.setModel(getTypeInstanciationListModel(runConfig.getTargetFunc()));
                                    String typeLabel = runConfig.getTargetFunc().getFuncSignature().getTypeLabelWithBrackets();
                                    myPanel.typeInstLabel.setText("Mapping for " + typeLabel + " :");
                                    myPanel.typeInstLabel.setVisible(true);
                                    myPanel.targetTypeInstantiation.setVisible(true);
                                    if (runConfig.getTargetTypeInstantiation() != null) {
                                        for (int k = 0; k < myPanel.targetTypeInstantiation.getModel().getSize(); k++) {
                                            if (((QuplaTypeInstComboBoxItem) myPanel.targetTypeInstantiation.getModel().getElementAt(k)).getTypeInstantiation().isEquivalentTo(runConfig.getTargetTypeInstantiation())) {
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
    protected void applyEditorTo(@NotNull QuplaInterpreterRunConfiguration runCongig) throws ConfigurationException {
        if(myPanel.modules.getSelectedItem()!=null) {
            runCongig.setTargetModule(((QuplaFileComboBoxItem) myPanel.modules.getSelectedItem()).getQuplaFile());
            if (myPanel.functionsInSelectedModule.getSelectedItem() != null) {
                runCongig.setTargetFunc(((QuplaFuncStmtComboBoxItem) myPanel.functionsInSelectedModule.getSelectedItem()).getFuncStmt());
                if(myPanel.targetTypeInstantiation.getSelectedItem()!=null){
                    runCongig.setTargetTypeInstantiation(((QuplaTypeInstComboBoxItem)myPanel.targetTypeInstantiation.getSelectedItem()).getTypeInstantiation());
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
        myPanel = new QuplaInterpreterRunConfigUI();
        myPanel.modules.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(myPanel.modules.getSelectedItem()!=null) {
                    myPanel.functionsInSelectedModule.setModel(getFunctionsModel(((QuplaFileComboBoxItem) myPanel.modules.getSelectedItem()).getQuplaFile()));
                    myPanel.functionsInSelectedModule.setEnabled(true);
                    myPanel.functionsInSelectedModule.setSelectedItem(null);
                }else{
                    myPanel.functionsInSelectedModule.setModel(new ListComboBoxModel(new ArrayList<QuplaFileComboBoxItem>()));
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
                    QuplaFuncStmt funcStmt = ((QuplaFuncStmtComboBoxItem)myPanel.functionsInSelectedModule.getSelectedItem()).getFuncStmt();
                    if(funcStmt.isInTemplate()){
                        myPanel.targetTypeInstantiation.setModel(getTypeInstanciationListModel(funcStmt));
                        myPanel.targetTypeInstantiation.setSelectedItem(null);
                        String typeLabel = funcStmt.getFuncSignature().getTypeLabelWithBrackets();
                        myPanel.typeInstLabel.setText("Mapping for " + typeLabel + " :");
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
                    makeFuncParameters(((QuplaFuncStmtComboBoxItem)myPanel.functionsInSelectedModule.getSelectedItem()).getFuncStmt(),previousArgs);
                }
            }
        });
        return myPanel.rootConfigPane;
    }

    private void makeFuncParameters(QuplaFuncStmt funcStmt, List<String> args){
        int i=0;
        for(QuplaFuncParameter funcParameter:funcStmt.getFuncSignature().getFuncParameterList()){
            InputTritPanel inputTritPanel = new InputTritPanel(funcParameter,300, myPanel.targetTypeInstantiation);
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
        QuplaModuleManager quplaModuleManager = project.getComponent(QuplaModuleManager.class);
        Collection<QuplaModule> modules = quplaModuleManager.allModules();
        List<QuplaFileComboBoxItem> quplaFiles = new ArrayList<>();
        for(QuplaModule module:modules){
            for(QuplaFile f:module.getModuleFiles())
                quplaFiles.add(new QuplaFileComboBoxItem(f));
        }
        quplaFiles.sort(new Comparator<QuplaFileComboBoxItem>() {
            @Override
            public int compare(QuplaFileComboBoxItem o1, QuplaFileComboBoxItem o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        return new ListComboBoxModel(quplaFiles);
    }

    private static ComboBoxModel getFunctionsModel(QuplaFile module){
        if(module==null)return new ListComboBoxModel(new ArrayList());
        List<QuplaFuncStmtComboBoxItem> model = new ArrayList<>();
        for(QuplaFuncStmt f:module.findAllFuncStmt()){
            model.add(new QuplaFuncStmtComboBoxItem(f));
        }
        return new ListComboBoxModel(model);
    }

    private static ComboBoxModel getTypeInstanciationListModel(QuplaFuncStmt funcStmt){
        if(funcStmt==null)return new ListComboBoxModel(new ArrayList());
        List<QuplaTypeInstComboBoxItem> model = new ArrayList<>();
        for(QuplaTypeInstantiation inst:funcStmt.getAllTypeInstantiation()){
            model.add(new QuplaTypeInstComboBoxItem(inst));
        }
        return new ListComboBoxModel(model);
    }
    public static class QuplaFileComboBoxItem {
        private final QuplaFile quplaFile;

        public QuplaFileComboBoxItem(QuplaFile quplaFile) {
            this.quplaFile = quplaFile;
        }

        @Override
        public String toString() {
            return quplaFile.getImportableFilePath();
        }

        public QuplaFile getQuplaFile() {
            return quplaFile;
        }
    }

    public static class QuplaFuncStmtComboBoxItem {
        private final QuplaFuncStmt funcStmt;

        public QuplaFuncStmtComboBoxItem(QuplaFuncStmt funcStmt) {
            this.funcStmt = funcStmt;
        }

        @Override
        public String toString() {
            return funcStmt.getFuncSignature().getText()+
                    (funcStmt.getParent() instanceof QuplaTemplateStmt ?
                            (" (from template "+((QuplaTemplateStmt) funcStmt.getParent()).getTemplateName().getText()+")"):"");
        }

        public QuplaFuncStmt getFuncStmt() {
            return funcStmt;
        }
    }

    public static class QuplaTypeInstComboBoxItem {
        private final QuplaTypeInstantiation typeInstantiation;

        public QuplaTypeInstComboBoxItem(QuplaTypeInstantiation typeInstantiation) {
            this.typeInstantiation = typeInstantiation;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("<");
            for(QuplaTypeNameRef typeNameRef:typeInstantiation.getTypeNameRefList()){
                QuplaTypeName typeName = (QuplaTypeName) typeNameRef.getReference().resolve();
                if(typeName!=null){
                    sb.append(QuplaPsiImplUtil.getResolvedSize((QuplaTypeStmt)typeName.getParent()));
                }else {
                    sb.append("?");
                }
                sb.append(" , ");
            }
            String s = sb.toString().substring(0,sb.length()-3)+">";

            return typeInstantiation.getText()+" ("+s+" trits)";
        }

        public QuplaTypeInstantiation getTypeInstantiation() {
            return typeInstantiation;
        }
    }
}