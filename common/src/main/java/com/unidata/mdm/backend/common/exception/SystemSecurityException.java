package com.unidata.mdm.backend.common.exception;

/**
 * @author Mikhail Mikhailov
 * Security related events.
 */
public class SystemSecurityException extends SystemRuntimeException {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -6296416496586742294L;

    /**
     * Constructor.
     * @param message
     * @param id
     * @param args
     */
    public SystemSecurityException(String message, ExceptionId id, Object... args) {
        super(message, id, args);
    }

    /**
     * Constructor.
     * @param message
     * @param cause
     * @param id
     * @param args
     */
    public SystemSecurityException(String message, Throwable cause, ExceptionId id, Object... args) {
        super(message, cause, id, args);
    }

    /**
     * Constructor.
     * @param cause
     * @param id
     * @param args
     */
    public SystemSecurityException(Throwable cause, ExceptionId id, Object... args) {
        super(cause, id, args);
    }
}
