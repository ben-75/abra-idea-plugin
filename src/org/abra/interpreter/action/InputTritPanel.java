package org.abra.interpreter.action;

import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.JBColor;
import org.abra.utils.TRIT;
import org.abra.utils.TritUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigInteger;

public class InputTritPanel extends JPanel implements DocumentListener {
    private final JTextField textField;
    private final JLabel otherFormat1;
    private final JLabel otherFormat2;

    private TRIT[] trits;

    public InputTritPanel(String paramName, int preferredWidth){
        super(new VerticalFlowLayout(VerticalFlowLayout.TOP,5,1,true,false));

        JLabel paramLabel = new JLabel();
        paramLabel.setText(paramName+" :");
        paramLabel.setMaximumSize(new Dimension(preferredWidth, paramLabel.getPreferredSize().height));

        textField = new JTextField();
//        JPanel textFieldExpander = new JPanel(new BorderLayout());
//        textFieldExpander.add(textField, BorderLayout.CENTER);
        JPanel topLine = new JPanel(new BorderLayout());
        topLine.add(paramLabel, BorderLayout.WEST);
        topLine.add(textField, BorderLayout.CENTER);
        add(topLine);

        JPanel otherFormats = new JPanel(new VerticalFlowLayout());
        otherFormat1 = new JLabel();
        otherFormat2 = new JLabel();
        otherFormats.add(otherFormat1);
        otherFormats.add(otherFormat2);

        JPanel bottomLine = new JPanel(new BorderLayout());
        JLabel glue = new JLabel();
        glue.setPreferredSize(new Dimension(preferredWidth, glue.getPreferredSize().height));

        bottomLine.add(glue, BorderLayout.WEST);
        bottomLine.add(otherFormats, BorderLayout.CENTER);

        add(otherFormats);

        textField.getDocument().addDocumentListener(this);
    }

    public TRIT[] getTrits() {
        return trits;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateOtherFormats(textField.getText());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateOtherFormats(textField.getText());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateOtherFormats(textField.getText());
    }

    private void updateOtherFormats(String s){
        if(s==null || s.isEmpty()){
            otherFormat1.setText("");
            otherFormat2.setText("");
        }else {
            TritUtils.DATA_FORMAT[] formats = TritUtils.detectFormat(s);
            if (formats[0] == TritUtils.DATA_FORMAT.INVALID) {
                otherFormat1.setText("Invalid input!");
                otherFormat2.setText("");
                otherFormat1.setForeground(JBColor.RED);
                trits = null;
            } else {
                otherFormat1.setForeground(JBColor.LIGHT_GRAY);
                if (formats[0] == TritUtils.DATA_FORMAT.TRYTE) {
                    trits = TritUtils.trytes2Trits(s);
                    otherFormat1.setText("Decimal : \t" + TritUtils.trit2Decimal(trits));
                    otherFormat2.setText("Trits : \t" + TritUtils.trit2String(trits));
                } else if (formats[0] == TritUtils.DATA_FORMAT.TRIT_FMT) {
                    trits = TritUtils.stringToTrits(s);
                    otherFormat1.setText("Decimal : \t" + TritUtils.trit2Decimal(trits));
                    otherFormat2.setText("Trytes : \t" + TritUtils.trit2Trytes(trits));
                } else if (formats[0] == TritUtils.DATA_FORMAT.DECIMAL) {
                    trits = TritUtils.bigInt2Trits(new BigInteger(s, 10));
                    otherFormat1.setText("Trytes : \t" + TritUtils.trit2Trytes(trits));
                    otherFormat2.setText("Trits : \t" + TritUtils.trit2String(trits));
                }
            }
        }
    }
}
