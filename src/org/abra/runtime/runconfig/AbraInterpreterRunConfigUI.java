package org.abra.runtime.runconfig;

import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;

public class AbraInterpreterRunConfigUI {
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
    JCheckBox emitCheckBox;
    JCheckBox trimCheckBox;
    JCheckBox treeCheckBox;
    JLabel argsContainerLabel;

    private void createUIComponents() {
        argsContainer = new JPanel(new VerticalLayout());
    }
}