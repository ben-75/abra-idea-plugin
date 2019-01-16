package org.qupla.runtime.interpreter;

import com.intellij.execution.CantRunException;
import com.intellij.execution.configurations.GeneralCommandLine;
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
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

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
                                    ComboBoxModel comboModel = getTypeInstanciationListModel(runConfig.getTargetFunc());
                                    if(comboModel!=null && comboModel.getSize()>0) {
                                        myPanel.targetTypeInstantiation.setModel(comboModel);
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
                                                    myPanel.typeNameLabel.setVisible(false);
                                                    myPanel.typeNameCombo.setVisible(false);
                                                    if (runConfig.getTargetTypeInstantiation() != null) {
                                                        clearFuncParameters();
                                                        makeFuncParameters(runConfig.getTargetFunc(), runConfig.args == null ? new ArrayList<>() : Arrays.asList(runConfig.args));
                                                    } else {
                                                        clearFuncParameters();
                                                    }
                                                    break;
                                                }
                                            }
                                        } else {
                                            myPanel.targetTypeInstantiation.setSelectedItem(null);
                                        }
                                    } else {
                                        comboModel = getTypeNameListModel(runConfig.getTargetFunc());

                                        if(comboModel!=null && comboModel.getSize()>0) {
                                            myPanel.typeNameCombo.setModel(comboModel);
                                            String typeLabel = runConfig.getTargetFunc().getFuncSignature().getTypeLabelWithBrackets();
                                            myPanel.typeNameLabel.setText("Mapping for " + typeLabel + " :");
                                            myPanel.typeNameLabel.setVisible(true);
                                            myPanel.typeNameCombo.setVisible(true);
                                            myPanel.typeInstLabel.setVisible(false);
                                            myPanel.targetTypeInstantiation.setVisible(false);
                                            if (runConfig.getTargetTypeName() != null) {
                                                for (int k = 0; k < myPanel.typeNameCombo.getModel().getSize(); k++) {
                                                    if (((QuplaTypeNameComboBoxItem) myPanel.typeNameCombo.getModel().getElementAt(k)).getQuplaTypeName().isEquivalentTo(runConfig.getTargetTypeName())) {
                                                        myPanel.typeNameCombo.setSelectedIndex(k);
                                                        myPanel.typeNameLabel.setVisible(true);
                                                        myPanel.typeNameCombo.setVisible(true);
                                                        if (runConfig.getTargetTypeName() != null) {
                                                            clearFuncParameters();
                                                            makeFuncParameters(runConfig.getTargetFunc(), runConfig.args == null ? new ArrayList<>() : Arrays.asList(runConfig.args));
                                                        } else {
                                                            clearFuncParameters();
                                                        }
                                                        break;
                                                    }
                                                }
                                            } else {
                                                myPanel.typeNameCombo.setSelectedItem(null);
                                            }
                                        }
                                    }


                                }else{
                                    myPanel.typeInstLabel.setVisible(false);
                                    myPanel.targetTypeInstantiation.setVisible(false);
                                    myPanel.typeNameLabel.setVisible(false);
                                    myPanel.typeNameCombo.setVisible(false);
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
        myPanel.abraCheckBox.setSelected(runConfig.isAbra());
        myPanel.treeCheckBox.setSelected(runConfig.isTree());

        updateCommandLine();

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

                if(myPanel.typeNameCombo.getSelectedItem()!=null){
                    runCongig.setTargetTypeName(((QuplaTypeNameComboBoxItem)myPanel.typeNameCombo.getSelectedItem()).getQuplaTypeName());
                }else{
                    runCongig.setTargetTypeName(null);
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
        runCongig.setAbra(myPanel.abraCheckBox.isSelected());
        runCongig.setTree(myPanel.treeCheckBox.isSelected());

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
                updateCommandLine();
            }
        });
        myPanel.functionsInSelectedModule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(myPanel.functionsInSelectedModule.getSelectedItem()!=null) {
                    QuplaFuncStmt funcStmt = ((QuplaFuncStmtComboBoxItem)myPanel.functionsInSelectedModule.getSelectedItem()).getFuncStmt();
                    if(funcStmt.isInTemplate()){
                        ComboBoxModel typeInstModel = getTypeInstanciationListModel(funcStmt);
                        if(typeInstModel.getSize()>0) {
                            myPanel.typeNameLabel.setVisible(false);
                            myPanel.typeNameCombo.setVisible(false);
                            myPanel.targetTypeInstantiation.setModel(typeInstModel);
                            myPanel.targetTypeInstantiation.setSelectedItem(null);
                            String typeLabel = funcStmt.getFuncSignature().getTypeLabelWithBrackets();
                            myPanel.typeInstLabel.setText("Mapping for " + typeLabel + " :");
                            myPanel.typeInstLabel.setVisible(true);
                            myPanel.targetTypeInstantiation.setVisible(true);
                        }else{
                            myPanel.typeInstLabel.setVisible(false);
                            myPanel.targetTypeInstantiation.setVisible(false);
                            ComboBoxModel typeNameModel = getTypeNameListModel(funcStmt);

                            myPanel.typeNameCombo.setModel(typeNameModel);
                            myPanel.typeNameCombo.setSelectedItem(null);
                            String typeLabel = funcStmt.getFuncSignature().getTypeLabelWithBrackets();
                            myPanel.typeNameLabel.setText("Mapping for " + typeLabel + " :");
                            myPanel.typeNameLabel.setVisible(true);
                            myPanel.typeNameCombo.setVisible(true);

                        }
                    }else{
                        myPanel.typeInstLabel.setVisible(false);
                        myPanel.targetTypeInstantiation.setVisible(false);
                        myPanel.typeNameLabel.setVisible(false);
                        myPanel.typeNameCombo.setVisible(false);
                    }
                    List<String> previousArgs = clearFuncParameters();
                    makeFuncParameters(funcStmt, previousArgs);
                    myPanel.argsContainer.setVisible(true);
                    myPanel.argsContainerLabel.setVisible(true);
                }else{
                    myPanel.typeInstLabel.setVisible(false);
                    myPanel.targetTypeInstantiation.setVisible(false);
                    myPanel.typeNameLabel.setVisible(false);
                    myPanel.typeNameCombo.setVisible(false);
                    myPanel.argsContainer.setVisible(false);
                    myPanel.argsContainerLabel.setVisible(false);

                }
                updateCommandLine();
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
                updateCommandLine();
            }
        });

        myPanel.typeNameCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(myPanel.typeNameCombo.getSelectedItem()==null){
                    clearFuncParameters();
                }else{
                    List<String> previousArgs = clearFuncParameters();
                    makeFuncParameters(((QuplaFuncStmtComboBoxItem)myPanel.functionsInSelectedModule.getSelectedItem()).getFuncStmt(),previousArgs);
                }
                updateCommandLine();
            }
        });

        myPanel.commandLine.setEditable(false);
        myPanel.commandLine.setLineWrap(true);
        myPanel.copyToClipboard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myPanel.copyToClipboard.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                String myString = myPanel.commandLine.getText();
                StringSelection stringSelection = new StringSelection(myString);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);

                int delay = 300;
                Timer timer = new Timer( delay, new ActionListener(){
                    @Override
                    public void actionPerformed( ActionEvent e ){
                        myPanel.copyToClipboard.setBorder(BorderFactory.createEmptyBorder());
                    }
                } );
                timer.setRepeats( false );
                timer.start();


            }
        });
        myPanel.copyToClipboard.setBorder(BorderFactory.createEmptyBorder());
        ActionListener updateCommandLineListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCommandLine();
            }
        };
        myPanel.echoCheckBox.addActionListener(updateCommandLineListener);
        myPanel.runEvalCheckBox.addActionListener(updateCommandLineListener);
        myPanel.runTestsCheckBox.addActionListener(updateCommandLineListener);
        myPanel.abraCheckBox.addActionListener(updateCommandLineListener);
        myPanel.treeCheckBox.addActionListener(updateCommandLineListener);
        myPanel.verilogCheckBox.addActionListener(updateCommandLineListener);
        return myPanel.rootConfigPane;
    }

    private void makeFuncParameters(QuplaFuncStmt funcStmt, List<String> args){
        int i=0;
        for(QuplaFuncParameter funcParameter:funcStmt.getFuncSignature().getFuncParameterList()){
            InputTritPanel inputTritPanel = new InputTritPanel(funcParameter,300, myPanel.targetTypeInstantiation);
            myPanel.argsContainer.add(inputTritPanel);
            if(args!=null && args.size()>=i+1 && args.get(i)!=null){
                inputTritPanel.setUserInput(args.get(i));
                inputTritPanel.onChange(new Runnable(){
                    @Override
                    public void run() {
                        updateCommandLine();
                    }
                });
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

    public void updateCommandLine(){
        if(myPanel!=null && myPanel.modules!=null && myPanel.modules.getModel()!=null) {
            QuplaFileComboBoxItem item = (QuplaFileComboBoxItem) myPanel.modules.getModel().getElementAt(0);
            if (item != null) {
                Project project = item.quplaFile.getProject();
                QuplaInterpreterRunConfiguration dummy = (QuplaInterpreterRunConfiguration) QuplaInterpreterConfigurationType.getInstance().getConfigurationFactories()[0].createTemplateConfiguration(project);
                try {
                    applyEditorTo(dummy);
                    GeneralCommandLine cmd = QuplaInterpreterState.buildJavaParameters(dummy).toCommandLine();
                    myPanel.commandLine.setText(cmd.getCommandLineString());
                } catch (ConfigurationException e) {
                    myPanel.commandLine.setText("Invalid configuration !");
                } catch (CantRunException e) {
                    myPanel.commandLine.setText("Error !");
                } catch (Exception e) {
                    myPanel.commandLine.setText("Configuration is not complete");
                    e.printStackTrace();
                }
            } else {
                myPanel.commandLine.setText("");
            }
        }else{
            myPanel.commandLine.setText("");
        }
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

    private static ComboBoxModel getTypeNameListModel(QuplaFuncStmt funcStmt){
        if(funcStmt==null)return new ListComboBoxModel(new ArrayList());
        List<QuplaTypeNameComboBoxItem> model = new ArrayList<>();
        for(QuplaTypeName item:((QuplaFile)funcStmt.getContainingFile()).findAllVisibleConcreteTypeName(null)){
            model.add(new QuplaTypeNameComboBoxItem(item));
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


    public static class QuplaTypeNameComboBoxItem {
        private final QuplaTypeName quplaTypeName;

        public QuplaTypeNameComboBoxItem(QuplaTypeName quplaTypeName) {
            this.quplaTypeName = quplaTypeName;
        }

        @Override
        public String toString() {
            return "<"+ quplaTypeName.getText()+">"+" ("+ quplaTypeName.getResolvedSize()+" trits)";
        }

        public QuplaTypeName getQuplaTypeName() {
            return quplaTypeName;
        }
    }
}