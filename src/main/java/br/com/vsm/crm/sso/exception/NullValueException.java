package br.com.vsm.crm.sso.exception;

public class NullValueException extends Exception {

    public NullValueException(String message) {
        super(message);
    }

    public NullValueException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
