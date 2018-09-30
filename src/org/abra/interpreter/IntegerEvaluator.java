package org.abra.interpreter;

import org.abra.language.psi.AbraInteger;
import org.abra.utils.TRIT;
import org.abra.utils.TritUtils;

import java.math.BigInteger;

public class IntegerEvaluator {

    public static TRIT[] eval(AbraInteger expr){
        if(expr.getTrit()!=null) return expr.getTrit().eval();
        return TritUtils.bigInt2Trits(new BigInteger(expr.getText()));
    }
}
