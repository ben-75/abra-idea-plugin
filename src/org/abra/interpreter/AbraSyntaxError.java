package org.abra.interpreter;

public class AbraSyntaxError extends RuntimeException {

    public AbraSyntaxError() {
    }

    public AbraSyntaxError(String message) {
        super(message);
    }

    public AbraSyntaxError(String message, Throwable cause) {
        super(message, cause);
    }

    public AbraSyntaxError(Throwable cause) {
        super(cause);
    }

    public AbraSyntaxError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
