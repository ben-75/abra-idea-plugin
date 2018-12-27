package org.qupla.language;

public class UnresolvableTokenException extends RuntimeException {

    public UnresolvableTokenException() {
    }

    public UnresolvableTokenException(String message) {
        super(message);
    }

    public UnresolvableTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnresolvableTokenException(Throwable cause) {
        super(cause);
    }

    public UnresolvableTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
