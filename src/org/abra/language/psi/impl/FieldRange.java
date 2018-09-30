package org.abra.language.psi.impl;

public class FieldRange {

    private int startOffset;
    private int length;

    public FieldRange(int startOffset, int length) {
        this.startOffset = startOffset;
        this.length = length;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getLength() {
        return length;
    }
}
