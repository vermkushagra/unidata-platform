/**
 *
 */
package com.unidata.mdm.backend.common.exception;

/**
 * @author Mikhail Mikhailov
 * Search exception class.
 */
public class SearchApplicationException extends SystemRuntimeException {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 4401101538239705307L;

    /**
     * @param message
     */
    public SearchApplicationException(String message, ExceptionId id, Object... args) {
        super(message, id, args);
    }

    /**
     * @param message
     * @param cause
     */
    public SearchApplicationException(String message, Throwable cause, ExceptionId id, Object... args) {
        super(message, cause, id, args);
    }

    /**
     * @param cause
     */
    public SearchApplicationException(Throwable cause, ExceptionId id, Object... args) {
        super(cause, id, args);
    }
}
