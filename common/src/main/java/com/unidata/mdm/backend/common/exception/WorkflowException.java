/**
 *
 */
package com.unidata.mdm.backend.common.exception;

/**
 * @author Mikhail Mikhailov
 *
 */
public class WorkflowException extends SystemRuntimeException {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 1731833453414350311L;

    /**
     * Constructor.
     * @param message
     * @param id
     * @param args
     */
    public WorkflowException(String message, ExceptionId id, Object... args) {
        super(message, id, args);
    }

    /**
     * Constructor.
     * @param message
     * @param cause
     * @param id
     * @param args
     */
    public WorkflowException(String message, Throwable cause, ExceptionId id, Object... args) {
        super(message, cause, id, args);
    }

    /**
     * Constructor.
     * @param cause
     * @param id
     * @param args
     */
    public WorkflowException(Throwable cause, ExceptionId id, Object... args) {
        super(cause, id, args);
    }

}
