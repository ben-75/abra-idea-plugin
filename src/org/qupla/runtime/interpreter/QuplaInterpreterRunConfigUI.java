package org.qupla.runtime.interpreter;

import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;

public class QuplaInterpreterRunConfigUI {
    public JPanel rootConfigPane;
    ModuleSelector modules;
    JComboBox functionsInSelectedModule;
    JCheckBox runTestsCheckBox;
    JCheckBox runEvalCheckBox;
    JCheckBox echoCheckBox;
    JComboBox targetTypeInstantiation;
    JLabel typeInstLabel;
    JPanel argsContainer;
    JCheckBox verilogCheckBox;
    JCheckBox abraCheckBox;
    JCheckBox treeCheckBox;
    JLabel argsContainerLabel;
    public JTextArea commandLine;
    public JButton copyToClipboard;
    JComboBox typeNameCombo;
    JLabel typeNameLabel;
    public JCheckBox viewCheckBox;
    public JRadioButton functionRadioButton;
    public JRadioButton moduleRadioButton;
    public JRadioButton customRadioButton;
    public ButtonGroup runModeGroup;
    public JTextField customArgs;
    public JLabel argsLabel;
    public JLabel optionsLabel;
    public JLabel functionLabel;
    public JSeparator functionSeparator;
    private JTextPane textPane1;

    private void createUIComponents() {
        modules = new ModuleSelector();
        argsContainer = new JPanel(new VerticalLayout());
        runModeGroup = new ButtonGroup();
        runModeGroup.add(functionRadioButton);
        runModeGroup.add(moduleRadioButton);
        runModeGroup.add(customRadioButton);
        functionSeparator=new JSeparator();
    }
}
