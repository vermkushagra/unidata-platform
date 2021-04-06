/**
 *
 */
package com.unidata.mdm.backend.common.exception;

/**
 * @author Mikhail Mikhailov
 *
 */
public class DataProcessingException extends SystemRuntimeException {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 8737700428142785237L;

    /**
     * Constructor.
     * @param message
     * @param id
     * @param args
     */
    public DataProcessingException(String message, ExceptionId id, Object... args) {
        super(message, id, args);
    }

    /**
     * Constructor.
     * @param message
     * @param cause
     * @param id
     * @param args
     */
    public DataProcessingException(String message, Throwable cause, ExceptionId id, Object... args) {
        super(message, cause, id, args);
    }

    /**
     * Constructor.
     * @param cause
     * @param id
     * @param args
     */
    public DataProcessingException(Throwable cause, ExceptionId id, Object... args) {
        super(cause, id, args);
    }
}
