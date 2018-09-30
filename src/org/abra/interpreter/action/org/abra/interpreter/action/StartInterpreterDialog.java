package org.abra.interpreter.action.org.abra.interpreter.action;

import com.intellij.execution.impl.RunDialog;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import org.abra.interpreter.AbraEvaluationContext;
import org.abra.interpreter.FuncEvaluator;
import org.abra.language.psi.AbraFuncDefinition;
import org.abra.language.psi.AbraFuncName;
import org.abra.language.psi.AbraFuncParameter;
import org.abra.language.psi.AbraParamName;
import org.abra.utils.TRIT;
import org.abra.utils.TritUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class StartInterpreterDialog extends DialogWrapper {
    private JButton buttonOK;
    private JButton buttonCancel;
    private AbraFuncName funcName;
    private Map<AbraParamName, InputTritPanel> paramMap = new HashMap<>();

    public StartInterpreterDialog(AbraFuncName funcName) {
        super(false);
        this.funcName = funcName;
        init();
        setModal(true);
//        JComponent center = createContentPane();
//        center.setLayout(new BorderLayout());
//        center.add(new JLabel(funcName.getText()),BorderLayout.NORTH);
//
//
//        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        south.add(createJButtonForAction(getOKAction()));
//        south.add(createJButtonForAction(getCancelAction()));
//        center.add(south,BorderLayout.SOUTH);
//        buttonOK.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onOK();
//            }
//        });
//
//        buttonCancel.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        });

        // call onCancel() on ESCAPE
//        contentPane.registerKeyboardAction(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction(), getCancelAction()};
    }

    @Nullable
    @Override
    protected JComponent createNorthPanel() {
        return new JLabel(funcName.getText());
    }

    protected JComponent createCenterPanel() {
        JPanel main = new JPanel(new VerticalFlowLayout());
        for(AbraFuncParameter p:((AbraFuncDefinition)funcName.getParent()).getFuncParameterList()){
            InputTritPanel inputTritPanel = new InputTritPanel(p.getParamName().getText(),100);
            main.add(inputTritPanel);
            paramMap.put(p.getParamName(), inputTritPanel);
        }
        return main;
    }

    public TRIT[] getTrits(AbraParamName paramName){
        return paramMap.get(paramName).getTrits();
    }

    private void onOK() {
        // add your code here

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }



//    public static void main(String[] args) {
//        StartInterpreterDialog dialog = new StartInterpreterDialog();
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
//    }
}
