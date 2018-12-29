package org.qupla.runtime.interpreter;

import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBFont;
import org.qupla.language.psi.*;
import org.qupla.utils.TRIT;
import org.qupla.utils.TritUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.BigInteger;

public class InputTritPanel extends JPanel implements DocumentListener {
    private final JTextField textField;
    private final JLabel otherFormat1;
    private final JLabel otherFormat2;
    JPanel otherFormats;
    private static Font SMALL_FONT = new Font(JBFont.MONOSPACED, Font.PLAIN, 11);
    int charWidth;
    QuplaFuncParameter param;
    private TRIT[] trits;
    private JComboBox targetTypeInst;
    private Runnable onChange;
    public InputTritPanel(QuplaFuncParameter param, int preferredWidth, JComboBox targetTypeInst){
        super(new VerticalFlowLayout(VerticalFlowLayout.TOP,5,1,true,false));
        this.param = param;
        this.targetTypeInst = targetTypeInst;
        JLabel paramLabel = new JLabel();
        paramLabel.setText(param.getParamName().getText()+" :");
        paramLabel.setMaximumSize(new Dimension(preferredWidth, paramLabel.getPreferredSize().height));

        textField = new JTextField();
//        JPanel textFieldExpander = new JPanel(new BorderLayout());
//        textFieldExpander.add(textField, BorderLayout.CENTER);
        JPanel topLine = new JPanel(new BorderLayout());
        topLine.add(paramLabel, BorderLayout.WEST);
        topLine.add(textField, BorderLayout.CENTER);
        add(topLine);

        otherFormats = new JPanel(new VerticalFlowLayout());
        otherFormat1 = new JLabel();
        otherFormat2 = new JLabel();
        otherFormat1.setFont(SMALL_FONT);
        otherFormat2.setFont(SMALL_FONT);
        charWidth = otherFormat1.getFontMetrics(SMALL_FONT).charWidth('0');
        otherFormats.add(otherFormat1);
        otherFormats.add(otherFormat2);

//        JPanel bottomLine = new JPanel(new BorderLayout());
//        JLabel glue = new JLabel();
//        glue.setPreferredSize(new Dimension(preferredWidth, glue.getPreferredSize().height));
//
//        bottomLine.add(glue, BorderLayout.WEST);
//        bottomLine.add(otherFormats, BorderLayout.CENTER);

        add(otherFormats);

        textField.getDocument().addDocumentListener(this);
    }

    public String getUserInput(){
        return textField.getText();
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
                otherFormat1.setForeground(otherFormat2.getForeground());
                if (formats[0] == TritUtils.DATA_FORMAT.TRYTE) {
                    trits = TritUtils.trytes2Trits(s);
                    otherFormat1.setText(truncate("Decimal : " + TritUtils.trit2Decimal(trits)));
                    otherFormat2.setText(truncate("Trits   : " + TritUtils.trit2String(trits)));
                } else if (formats[0] == TritUtils.DATA_FORMAT.TRIT_FMT) {
                    trits = TritUtils.stringToTrits(s);
                    otherFormat1.setText(truncate("Decimal : " + TritUtils.trit2Decimal(trits)));
                    otherFormat2.setText(truncate("Trytes  : " + TritUtils.trit2Trytes(trits)));
                } else if (formats[0] == TritUtils.DATA_FORMAT.DECIMAL) {
                    trits = TritUtils.bigInt2Trits(new BigInteger(s, 10));
                    otherFormat1.setText(truncate("Trytes  : " + TritUtils.trit2Trytes(trits)));
                    otherFormat2.setText(truncate("Trits   : " + TritUtils.trit2String(trits)));
                } else if (formats[0] == TritUtils.DATA_FORMAT.FLOAT_FMT) {
                    int manSize = 9;
                    int expSize = 3;
                    if(targetTypeInst!=null && targetTypeInst.getSelectedItem() instanceof QuplaInterpreterSettingsEditor.QuplaTypeInstComboBoxItem){
                        QuplaTypeInstantiation typeInstantiation = ((QuplaInterpreterSettingsEditor.QuplaTypeInstComboBoxItem)targetTypeInst.getSelectedItem()).getTypeInstantiation();
                        if(typeInstantiation.getTypeNameRefList().size()==2){
                            manSize = (QuplaPsiImplUtil.getResolvedSize((QuplaTypeStmt) typeInstantiation.getTypeNameRefList().get(0).getReference().resolve().getParent()));
                            expSize = (QuplaPsiImplUtil.getResolvedSize((QuplaTypeStmt) typeInstantiation.getTypeNameRefList().get(1).getReference().resolve().getParent()));
                        }
                    }
                    trits = TritUtils.floatToTrits(new BigDecimal(s),manSize, expSize);
                    otherFormat1.setText(truncate("Trytes  : " + TritUtils.trit2Trytes(trits)));
                    otherFormat2.setText(truncate("Trits   : " + TritUtils.trit2String(trits)));
                }
            }
        }
        if(this.onChange!=null)this.onChange.run();
    }

    private String truncate(String s){
        int l = otherFormats.getWidth()/charWidth;
        if(l>0&& s.length()>l) s = s.substring(0,l-4)+"...";
        return s;
    }

    public void setUserInput(String s) {
        textField.setText(s);
    }

    public void onChange(Runnable runnable) {
        this.onChange = runnable;
    }
}
