package org.qupla.runtime.interpreter;

import com.intellij.execution.CantRunException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import org.qupla.language.module.QuplaModule;
import org.qupla.language.module.QuplaModuleManager;
import org.qupla.language.psi.*;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
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
        myPanel.modules.setProject(runConfig.getProject());
        myPanel.modules.applySelectedModules(runConfig.getQuplaModules());
        myPanel.modules.clearListeners();
        myPanel.modules.addListener(new ModuleSelector.Listener() {
            @Override
            public void onSelectionChanged(List<QuplaModule> selectedModules) {
                myPanel.functionsInSelectedModule.setModel(getFunctionsModel(selectedModules));
                myPanel.functionsInSelectedModule.setEnabled(myPanel.functionsInSelectedModule.getModel().getSize()>0);
                updatePanelAccordingAvailableFunctions(runConfig);
                updatePanelForRunMode();
                updateCommandLine();
            }
        });
        getRunMode(runConfig).setSelected(true);


        myPanel.functionsInSelectedModule.setModel(getFunctionsModel(runConfig.getQuplaModules()));
        myPanel.functionsInSelectedModule.setEnabled(myPanel.functionsInSelectedModule.getModel().getSize()>0);
        updatePanelAccordingAvailableFunctions(runConfig);
        myPanel.runTestsCheckBox.setSelected(runConfig.isRunTest());
        myPanel.runEvalCheckBox.setSelected(runConfig.isRunEval());
        myPanel.echoCheckBox.setSelected(runConfig.isEcho());
        myPanel.verilogCheckBox.setSelected(runConfig.isFpga());
        myPanel.abraCheckBox.setSelected(runConfig.isAbra());
        myPanel.viewCheckBox.setSelected(runConfig.isView());
        myPanel.treeCheckBox.setSelected(runConfig.isTree());
        myPanel.customArgs.setText(runConfig.getCustomArgs());
        updatePanelForRunMode();
        updateCommandLine();

    }

    private void updatePanelForRunMode(){
        if(myPanel.customRadioButton.isSelected()){
            functionPartVisible(false);
            optionPartVisible(false);
            customArgsVisible(true);
        }else if(myPanel.moduleRadioButton.isSelected()){
            functionPartVisible(false);
            optionPartVisible(true);
            customArgsVisible(false);
        }else{
            functionPartVisible(true);
            optionPartVisible(true);
            customArgsVisible(false);
        }
        updateCommandLine();
    }
    private void functionPartVisible(boolean visible){
        myPanel.functionLabel.setVisible(visible);
        myPanel.functionsInSelectedModule.setVisible(visible);
        myPanel.typeNameLabel.setVisible(visible);
        myPanel.typeNameCombo.setVisible(visible);
        myPanel.argsContainer.setVisible(visible);
        myPanel.argsContainerLabel.setVisible(visible);
        myPanel.typeInstLabel.setVisible(visible && myPanel.targetTypeInstantiation.getModel()!=null && myPanel.targetTypeInstantiation.getModel().getSize()>0);
        myPanel.targetTypeInstantiation.setVisible(visible && myPanel.targetTypeInstantiation.getModel()!=null && myPanel.targetTypeInstantiation.getModel().getSize()>0);
        myPanel.functionSeparator.setVisible(visible);
    }
    private void customArgsVisible(boolean visible){
        myPanel.customArgs.setVisible(visible);
        myPanel.argsLabel.setVisible(visible);
    }

    private void optionPartVisible(boolean visible){
        myPanel.optionsLabel.setVisible(visible);
        myPanel.runTestsCheckBox.setVisible(visible);
        myPanel.runEvalCheckBox.setVisible(visible);
        myPanel.echoCheckBox.setVisible(visible);
        myPanel.viewCheckBox.setVisible(visible);
        myPanel.verilogCheckBox.setVisible(visible);
        myPanel.abraCheckBox.setVisible(visible);
        myPanel.treeCheckBox.setVisible(visible);
    }

    private AbstractButton getRunMode(QuplaInterpreterRunConfiguration runConfig){
        if(runConfig.getRunMode()==null)return myPanel.functionRadioButton;
        if(runConfig.getRunMode().equals("function"))return myPanel.functionRadioButton;
        if(runConfig.getRunMode().equals("module"))return myPanel.moduleRadioButton;
        return myPanel.customRadioButton;
    }
    private void updatePanelAccordingAvailableFunctions(@NotNull QuplaInterpreterRunConfiguration runConfig) {
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
    }

    @Override
    protected void applyEditorTo(@NotNull QuplaInterpreterRunConfiguration runCongig) throws ConfigurationException {
        runCongig.setQuplaModules(myPanel.modules.getSelectedModules());
        if(myPanel.modules.getSelectedModules().size()>0) {
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
            runCongig.setTargetFunc(null);
            runCongig.setTargetTypeInstantiation(null);
        }
        runCongig.setRunTest(myPanel.runTestsCheckBox.isSelected());
        runCongig.setRunEval(myPanel.runEvalCheckBox.isSelected());
        runCongig.setEcho(myPanel.echoCheckBox.isSelected());
        runCongig.setFpga(myPanel.verilogCheckBox.isSelected());
        runCongig.setAbra(myPanel.abraCheckBox.isSelected());
        runCongig.setView(myPanel.viewCheckBox.isSelected());
        runCongig.setTree(myPanel.treeCheckBox.isSelected());
        runCongig.setRunMode(myPanel.functionRadioButton.isSelected()?"function":(myPanel.moduleRadioButton.isSelected()?"module":"custom"));
        runCongig.setCustomArgs(myPanel.customArgs.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        myPanel = new QuplaInterpreterRunConfigUI();
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
        myPanel.viewCheckBox.addActionListener(updateCommandLineListener);
        myPanel.treeCheckBox.addActionListener(updateCommandLineListener);
        myPanel.verilogCheckBox.addActionListener(updateCommandLineListener);

        ActionListener updateRunModeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePanelForRunMode();
            }
        };
        myPanel.moduleRadioButton.addActionListener(updateRunModeListener);
        myPanel.customRadioButton.addActionListener(updateRunModeListener);
        myPanel.functionRadioButton.addActionListener(updateRunModeListener);
        myPanel.customArgs.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                updateCommandLine();
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
        if(myPanel!=null && myPanel.modules!=null && myPanel.modules.getSelectedModules().size()>0) {
                Project project = myPanel.modules.getSelectedModules().get(0).getModuleFiles().get(0).getProject();
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

    private static ComboBoxModel getFunctionsModel(List<QuplaModule> quplaModules){
        if(quplaModules==null || quplaModules.size()==0)return new ListComboBoxModel(new ArrayList());
        List<QuplaFuncStmtComboBoxItem> model = new ArrayList<>();
        List<QuplaModule> done = new ArrayList<>();
        for(QuplaModule m:quplaModules) {
            for (QuplaFuncStmt f : m.findAllFuncStmt()) {
                model.add(new QuplaFuncStmtComboBoxItem(m,f));
            }
            done.add(m);
        }

        QuplaModuleManager moduleManager = quplaModules.get(0).getProject().getComponent(QuplaModuleManager.class);
        for(QuplaModule m:quplaModules) {
            List<QuplaModule> imported = moduleManager.getImportedModules(m);
            for(QuplaModule qm:imported) {
                if(!done.contains(qm)) {
                    for (QuplaFuncStmt f : qm.findAllFuncStmt()) {
                        model.add(new QuplaFuncStmtComboBoxItem(qm, f));
                    }
                    done.add(qm);
                }
            }
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
        private final QuplaModule module;

        public QuplaFuncStmtComboBoxItem(QuplaModule module, QuplaFuncStmt funcStmt) {
            this.funcStmt = funcStmt;
            this.module = module;
            JLabel label = new JLabel("text");
        }


        @Override
        public String toString() {
            String sig = funcStmt.getFuncSignature().getText();
            String type = sig.substring(0,sig.indexOf(" "));
            sig = sig.substring(sig.indexOf(" ")).trim();
            sig = sig.replaceAll("<","&lt;");
            sig = sig.replaceAll(">","&gt;");
            int endFuncName =
                    Math.min(sig.indexOf("(")==-1?Integer.MAX_VALUE:sig.indexOf("("),
                            sig.indexOf("&")==-1?Integer.MAX_VALUE:sig.indexOf("&"));
            String funcName = sig.substring(0,endFuncName);
            String end = sig.substring(endFuncName);

            String formatted = "<font color=8b6546>"+type+"</font>&nbsp;<font color=ffc66d>"+funcName+"</font>"+end;
            return "<html><font color=808080>["+module.getName()+subModule()+"/"+funcStmt.getContainingFile().getVirtualFile().getName()+"]&nbsp;</font>"+formatted+
                    (funcStmt.getParent() instanceof QuplaTemplateStmt ?
                            ("&nbsp;<font color=808080>(from template "+((QuplaTemplateStmt) funcStmt.getParent()).getTemplateName().getText()+")</font>"):"")+
                    "</html>";
        }

        private String subModule(){
            if(funcStmt.getContainingFile().getVirtualFile().getParent().getName().equals(module.getName()))return "";
            return "/"+funcStmt.getContainingFile().getVirtualFile().getParent().getName();
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