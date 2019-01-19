package org.qupla.runtime.debugger.ui;

import org.qupla.utils.TritUtils;

import java.math.BigInteger;

public class TritVectorView {

    public final String name;
    public final int offset;
    public final int size;
    public final int valueTrits;
    public final String vector;

    public TritVectorView(String name, int offset, int size, int valueTrits, String vector) {
        this.name = name;
        this.offset = offset;
        this.size = size;
        this.valueTrits = valueTrits;
        this.vector = vector;
    }

    public String displayTrits(){
        return vector.substring(offset,offset+size);
    }

    public String displayDecimals(){
        return TritUtils.trit2Decimal(TritUtils.stringToTrits(displayTrits())).toString(10);
    }
}
