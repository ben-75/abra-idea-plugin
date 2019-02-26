package org.qupla.runtime.debugger;

import com.sun.jdi.Field;
import com.sun.jdi.ReferenceType;

public class QuplaEvalContextMapper {

    private Field lineNrField;
    private Field colNrField;
    private Field sourceField;
    private Field tokenField;
    private Field stackFrameField;

    public QuplaEvalContextMapper() {


    }

    public Field getLineNrField() {
        return lineNrField;
    }

    public void setLineNrField(Field lineNrField) {
        this.lineNrField = lineNrField;
    }

    public Field getColNrField() {
        return colNrField;
    }

    public void setColNrField(Field colNrField) {
        this.colNrField = colNrField;
    }

    public Field getSourceField() {
        return sourceField;
    }

    public void setSourceField(Field sourceField) {
        this.sourceField = sourceField;
    }

    public Field getTokenField() {
        return tokenField;
    }

    public void setTokenField(Field tokenField) {
        this.tokenField = tokenField;
    }

    public Field getStackFrameField() {
        return stackFrameField;
    }

    public void setStackFrameField(Field stackFrameField) {
        this.stackFrameField = stackFrameField;
    }
}
