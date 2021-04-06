/**
 *
 */
package com.unidata.mdm.backend.common.exception;

/**
 * @author Mikhail Mikhailov
 *
 */
public class MetadataException extends SystemRuntimeException {

    /**
     * SUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     * @param id
     */
    public MetadataException(String message, ExceptionId id, Object... args) {
        super(message, id, args);
    }

    /**
     * @param message
     * @param cause
     * @param id
     */
    public MetadataException(String message, Throwable cause, ExceptionId id, Object... args) {
        super(message, cause, id, args);
    }

    /**
     * @param cause
     * @param id
     */
    public MetadataException(Throwable cause, ExceptionId id, Object... args) {
        super(cause, id, args);
    }
}
