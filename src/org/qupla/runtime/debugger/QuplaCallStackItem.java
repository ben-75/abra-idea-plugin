package org.qupla.runtime.debugger;

public class QuplaCallStackItem {

    private String modulePath;
    private int lineNumber;
    private int colNumber;
    private String expr;
    private String operation;


    public QuplaCallStackItem(String operation, String expr, int lineNumber, int colNumber, String modulePath) {
        this.expr = expr;
        this.lineNumber = lineNumber;
        this.colNumber = colNumber;
        this.modulePath = modulePath;
        this.operation = operation;
    }

    @Override
    public String toString() {
        return operation+" : "+expr + "(" + modulePath + " line: "+ lineNumber + " col: "+ colNumber + ')';
    }
}
