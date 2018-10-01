package org.abra.interpreter;

import org.abra.language.psi.AbraRangeExpr;
import org.abra.utils.TRIT;

public class RangeEvaluator {

    //rangeExpr       ::= constExpr ((RANGE_OPERATOR constExpr?)|smartRange constExpr)?


    public static TRIT[] apply(AbraRangeExpr rangeExpr, TRIT[] value, AbraEvaluationContext context) {
        int start = ConstEvaluator.eval(rangeExpr.getConstExprList().get(0), context);
        if(rangeExpr.getSmartRange()==null && !rangeExpr.hasRangeOperator()){
            return new TRIT[]{value[(int)start]};
        }
        if(rangeExpr.getSmartRange()!=null){
            int length = ConstEvaluator.eval(rangeExpr.getConstExprList().get(1), context);
            TRIT[] dest = new TRIT[length];

            if(start+length>value.length){
                //feed with 0 if required
                TRIT[] expanded = new TRIT[start+length];
                System.arraycopy(value,0,expanded,0,value.length);
                for(int j=value.length;j<start+length;j++){
                    expanded[j]=TRIT.Z;
                }
                System.arraycopy(expanded, start, dest, 0, length);
            }else {
                System.arraycopy(value, start, dest, 0, length);
            }
            return dest;
        }
        //rangeOperator
        if(rangeExpr.hasOpenRange()){
            int length = value.length-start;
            TRIT[] dest = new TRIT[(int)length];
            System.arraycopy(value,start,dest,0,length);
            return dest;
        }

        int end = ConstEvaluator.eval(rangeExpr.getConstExprList().get(1),context);
        TRIT[] dest = new TRIT[end-start];
        System.arraycopy(value,start,dest,0,end-start);
        return dest;
    }
}
