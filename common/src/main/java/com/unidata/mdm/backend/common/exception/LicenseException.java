package com.unidata.mdm.backend.common.exception;

/**
 * @author Mikhail Mikhailov
 * License exception mark.
 */
public class LicenseException extends SystemRuntimeException {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -1544438852504865706L;

    /**
     * Constructor.
     * @param message
     * @param id
     * @param args
     */
    public LicenseException(String message, ExceptionId id, Object... args) {
        super(message, id, args);
    }

    /**
     * Constructor.
     * @param message
     * @param cause
     * @param id
     * @param args
     */
    public LicenseException(String message, Throwable cause, ExceptionId id, Object... args) {
        super(message, cause, id, args);
    }

    /**
     * Constructor.
     * @param cause
     * @param id
     * @param args
     */
    public LicenseException(Throwable cause, ExceptionId id, Object... args) {
        super(cause, id, args);
    }

}
