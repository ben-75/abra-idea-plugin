package org.abra.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class TritUtils {

    public static TRIT[] stringToTrits(String input) {
        TRIT[] resp = new TRIT[input.length()];
        int i = 0;
        for(char c:input.toCharArray()){
            resp[i] = (c=='-'?TRIT.M:(c=='1'?TRIT.O:TRIT.Z));
            i++;
        }
        return resp;
    }

    public enum DATA_FORMAT {
        TRIT_FMT, TRYTE, DECIMAL, INVALID
    }

    private static DATA_FORMAT[] TRIT_OR_DECIMAL = new DATA_FORMAT[]{DATA_FORMAT.TRIT_FMT,DATA_FORMAT.DECIMAL};
    private static DATA_FORMAT[] TRYTE_OR_DECIMAL = new DATA_FORMAT[]{DATA_FORMAT.TRYTE,DATA_FORMAT.DECIMAL};
    private static DATA_FORMAT[] INVALID = new DATA_FORMAT[]{DATA_FORMAT.INVALID};
    private static DATA_FORMAT[] TRIT_FMT = new DATA_FORMAT[]{DATA_FORMAT.TRIT_FMT};
    private static DATA_FORMAT[] TRYTE = new DATA_FORMAT[]{DATA_FORMAT.TRYTE};
    private static DATA_FORMAT[] DECIMAL = new DATA_FORMAT[]{DATA_FORMAT.DECIMAL};

    private static Pattern ambiguousTritDecimalPattern = Pattern.compile("(1)[0-1]*");
    private static Pattern ambiguousDecimalTrytePattern = Pattern.compile("[9]*");
    private static Pattern tritPattern = Pattern.compile("[\\-0-1]*");
    private static Pattern decimalPattern = Pattern.compile("[0-9]*");
    private static Pattern trytePattern = Pattern.compile("[9A-Z]*");

    public static DATA_FORMAT[] detectFormat(String input){
        if(ambiguousTritDecimalPattern.matcher(input).matches())return TRIT_OR_DECIMAL;
        if(ambiguousDecimalTrytePattern.matcher(input).matches())return TRYTE_OR_DECIMAL;
        if(tritPattern.matcher(input).matches())return TRIT_FMT;
        if(trytePattern.matcher(input).matches())return TRYTE;
        if(decimalPattern.matcher(input).matches())return DECIMAL;
        return INVALID;
    }

    public static TRIT[] trytes2Trits(String input){
        if(detectFormat(input)[0]!=DATA_FORMAT.TRYTE)throw new IllegalArgumentException("input is not a trytes string :"+input);
        TRIT[] resp = new TRIT[input.length()*3];
        for(int i=0;i<input.length();i++){
            int j = i*3;
            switch (input.charAt(i)){
                case '9' : resp[j]= TRIT.Z;resp[j+1]= TRIT.Z;resp[j+2]= TRIT.Z;break;
                case 'A' : resp[j]= TRIT.O;resp[j+1]= TRIT.Z;resp[j+2]= TRIT.Z;break;
                case 'B' : resp[j]= TRIT.M;resp[j+1]= TRIT.O;resp[j+2]= TRIT.Z;break;
                case 'C' : resp[j]= TRIT.Z;resp[j+1]= TRIT.O;resp[j+2]= TRIT.Z;break;
                case 'D' : resp[j]= TRIT.O;resp[j+1]= TRIT.O;resp[j+2]= TRIT.Z;break;
                case 'E' : resp[j]= TRIT.M;resp[j+1]= TRIT.M;resp[j+2]= TRIT.O;break;
                case 'F' : resp[j]= TRIT.Z;resp[j+1]= TRIT.M;resp[j+2]= TRIT.O;break;
                case 'G' : resp[j]= TRIT.O;resp[j+1]= TRIT.M;resp[j+2]= TRIT.O;break;
                case 'H' : resp[j]= TRIT.M;resp[j+1]= TRIT.Z;resp[j+2]= TRIT.O;break;
                case 'I' : resp[j]= TRIT.Z;resp[j+1]= TRIT.Z;resp[j+2]= TRIT.O;break;
                case 'J' : resp[j]= TRIT.O;resp[j+1]= TRIT.Z;resp[j+2]= TRIT.O;break;
                case 'K' : resp[j]= TRIT.M;resp[j+1]= TRIT.O;resp[j+2]= TRIT.O;break;
                case 'L' : resp[j]= TRIT.Z;resp[j+1]= TRIT.O;resp[j+2]= TRIT.O;break;
                case 'M' : resp[j]= TRIT.O;resp[j+1]= TRIT.O;resp[j+2]= TRIT.O;break;
                case 'N' : resp[j]= TRIT.M;resp[j+1]= TRIT.M;resp[j+2]= TRIT.M;break;
                case 'O' : resp[j]= TRIT.Z;resp[j+1]= TRIT.M;resp[j+2]= TRIT.M;break;
                case 'P' : resp[j]= TRIT.O;resp[j+1]= TRIT.M;resp[j+2]= TRIT.M;break;
                case 'Q' : resp[j]= TRIT.M;resp[j+1]= TRIT.Z;resp[j+2]= TRIT.M;break;
                case 'R' : resp[j]= TRIT.Z;resp[j+1]= TRIT.Z;resp[j+2]= TRIT.M;break;
                case 'S' : resp[j]= TRIT.O;resp[j+1]= TRIT.Z;resp[j+2]= TRIT.M;break;
                case 'T' : resp[j]= TRIT.M;resp[j+1]= TRIT.O;resp[j+2]= TRIT.M;break;
                case 'U' : resp[j]= TRIT.Z;resp[j+1]= TRIT.O;resp[j+2]= TRIT.M;break;
                case 'V' : resp[j]= TRIT.O;resp[j+1]= TRIT.O;resp[j+2]= TRIT.M;break;
                case 'W' : resp[j]= TRIT.M;resp[j+1]= TRIT.M;resp[j+2]= TRIT.Z;break;
                case 'X' : resp[j]= TRIT.Z;resp[j+1]= TRIT.M;resp[j+2]= TRIT.Z;break;
                case 'Y' : resp[j]= TRIT.O;resp[j+1]= TRIT.M;resp[j+2]= TRIT.Z;break;
                case 'Z' : resp[j]= TRIT.M;resp[j+1]= TRIT.Z;resp[j+2]= TRIT.Z;break;
            }
        }
        return resp;
    }

    public static BigInteger trit2Decimal(TRIT[] trits){
        BigInteger resp = new BigInteger("0",10);
        BigInteger big3 = BigInteger.valueOf(3);
        BigInteger factor = BigInteger.ONE;
        for(int i=0;i<trits.length;i++){
            switch (trits[i]){
                case O : resp = resp.add(factor);break;
                case M : resp = resp.subtract(factor);break;
            }
            factor = factor.multiply(big3);
        }
        return resp;
    }

    public static TRIT[] long2Trits(long val) {
        if(val==0)return new TRIT[]{TRIT.Z};
        boolean negative = val < 0;
        if (negative) val = val * -1;
        ArrayList<Integer> unbalanced = new ArrayList<>();
        long quotien = val;
        while (quotien > 0) {
            unbalanced.add((int) quotien % 3);
            quotien = quotien / 3;
        }

        ArrayList<Integer> resp = new ArrayList<>();
        int carry = 0;
        for (int i = 0; i < unbalanced.size(); i++) {
            int unbal = unbalanced.get(i);
            if(unbal==2){
                if(carry==0){
                    resp.add(-1);
                    carry= 1;
                }else{
                    resp.add(0);
                    carry= 1;
                }
            }else{
                if(unbal+carry==2){
                    resp.add(-1);
                    carry=1;
                }else{
                    if(unbal+carry==1){
                        resp.add(1);
                        carry=0;
                    }else{
                        resp.add(0);
                        carry=0;
                    }
                }
            }

        }
        if (carry!=0) {
            resp.add(1);
        }
        TRIT[] response = new TRIT[resp.size()];
        for (int i = 0; i < resp.size(); i++) {
            if(negative){
                switch(resp.get(i)){
                    case 1:response[i]=TRIT.M;break;
                    case 0:response[i]=TRIT.Z;break;
                    case -1:response[i]=TRIT.O;break;
                }
            }else{
                switch(resp.get(i)){
                    case 1:response[i]=TRIT.O;break;
                    case 0:response[i]=TRIT.Z;break;
                    case -1:response[i]=TRIT.M;break;
                }
            }
        }
        return response;
    }

    private static final BigInteger BIG_3 = BigInteger.valueOf(3);
    private static final BigInteger BIG_2 = BigInteger.valueOf(2);

    public static TRIT[] bigInt2Trits(BigInteger val) {
        if(val.equals(BigInteger.ZERO))return new TRIT[]{TRIT.Z};
        boolean negative = val.compareTo(BigInteger.ZERO) < 0;
        if (negative) val = val.negate();
        ArrayList<BigInteger> unbalanced = new ArrayList<>();
        BigInteger quotien = val;
        while (quotien.compareTo(BigInteger.ZERO) > 0) {
            unbalanced.add( quotien.mod(BIG_3));
            quotien = quotien.divide(BIG_3);
        }

        ArrayList<Integer> resp = new ArrayList<>();
        BigInteger carry = BigInteger.ZERO;
        for (int i = 0; i < unbalanced.size(); i++) {
            BigInteger unbal = unbalanced.get(i);
            if(unbal.compareTo(BIG_2)==0){
                if(carry.compareTo(BigInteger.ZERO)==0){
                    resp.add(-1);
                    carry= BigInteger.ONE;
                }else{
                    resp.add(0);
                    carry= BigInteger.ONE;
                }
            }else{
                if(unbal.add(carry).compareTo(BIG_2)==0){
                    resp.add(-1);
                    carry=BigInteger.ONE;
                }else{
                    if(unbal.add(carry).compareTo(BigInteger.ONE)==0){
                        resp.add(1);
                        carry=BigInteger.ZERO;
                    }else{
                        resp.add(0);
                        carry=BigInteger.ZERO;
                    }
                }
            }

        }
        if (carry.compareTo(BigInteger.ZERO)!=0) {
            resp.add(1);
        }
        TRIT[] response = new TRIT[resp.size()];
        for (int i = 0; i < resp.size(); i++) {
            if(negative){
                switch(resp.get(i)){
                    case 1:response[i]=TRIT.M;break;
                    case 0:response[i]=TRIT.Z;break;
                    case -1:response[i]=TRIT.O;break;
                }
            }else{
                switch(resp.get(i)){
                    case 1:response[i]=TRIT.O;break;
                    case 0:response[i]=TRIT.Z;break;
                    case -1:response[i]=TRIT.M;break;
                }
            }
        }
        return response;

    }

    public static String trit2String(TRIT[] input) {
        StringBuilder sb = new StringBuilder("");
        for(TRIT t:input){
            sb.append(t.label());
        }
        return sb.toString();
    }

    public static String trit2Trytes(TRIT[] input){
        StringBuilder sb = new StringBuilder("");
        int i=0;
        while(i<input.length){
            String chars = input[i].label();
            if(i+3==input.length+2) chars = chars+"00";
            else{
                chars = chars+input[i+1].label();
                if(i+3==input.length+1){
                    chars = chars+"0";
                }else{
                    chars = chars+input[i+2].label();
                }
            }
            i = i+3;
            switch (chars){
                case "000" : sb.append("9");break;
                case "100" : sb.append("A");break;
                case "-10" : sb.append("B");break;
                case "010" : sb.append("C");break;
                case "110" : sb.append("D");break;
                case "--1" : sb.append("E");break;
                case "0-1" : sb.append("F");break;
                case "1-1" : sb.append("G");break;
                case "-01" : sb.append("H");break;
                case "001" : sb.append("I");break;
                case "101" : sb.append("J");break;
                case "-11" : sb.append("K");break;
                case "011" : sb.append("L");break;
                case "111" : sb.append("M");break;
                case "---" : sb.append("N");break;
                case "0--" : sb.append("O");break;
                case "1--" : sb.append("P");break;
                case "-0-" : sb.append("Q");break;
                case "00-" : sb.append("R");break;
                case "10-" : sb.append("S");break;
                case "-1-" : sb.append("T");break;
                case "01-" : sb.append("U");break;
                case "11-" : sb.append("V");break;
                case "--0" : sb.append("W");break;
                case "0-0" : sb.append("X");break;
                case "1-0" : sb.append("Y");break;
                case "-00" : sb.append("Z");break;
            }
        }
        return sb.toString();
    }
}
