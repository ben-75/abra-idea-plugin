package org.abra.utils;

public enum TRIT {

    O("1"),Z("0"),M("-");

    private String label;

    TRIT(String label) {
        this.label = label;
    }

    public static String print(TRIT... data){
        StringBuilder sb = new StringBuilder();
        for(TRIT trit:data)sb.append(trit.label);
        return sb.toString();
    }

    public String label() {
        return label;
    }
}
