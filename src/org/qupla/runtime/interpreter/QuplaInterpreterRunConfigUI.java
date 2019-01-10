package org.qupla.runtime.interpreter;

import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;

public class QuplaInterpreterRunConfigUI {
    public JPanel rootConfigPane;
    JComboBox modules;
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
    private JTextPane textPane1;

    private void createUIComponents() {
        argsContainer = new JPanel(new VerticalLayout());
    }
}
