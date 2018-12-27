package org.qupla.language.psi.impl;

public class FieldRange {

    private final int startOffset;
    private final int length;

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
