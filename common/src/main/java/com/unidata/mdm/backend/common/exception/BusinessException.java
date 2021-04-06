package com.unidata.mdm.backend.common.exception;

/**
 * @author Mikhail Mikhailov
 * 'Business' i. e. validation, business condition etc. exception.
 */
public class BusinessException extends SystemRuntimeException {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -1409086135382733471L;
    /**
     * Constructor.
     * @param message the message
     * @param id id
     * @param args additional args
     */
    public BusinessException(String message, ExceptionId id, Object... args) {
        super(message, id, args);
    }
    /**
     * Constructor.
     * @param message the message
     * @param id id
     */
    public BusinessException(String message, ExceptionId id) {
        super(message, id);
    }
}
