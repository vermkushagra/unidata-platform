package com.unidata.mdm.backend.common.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author Michael Yashin. Created on 05.04.2015.
 */
public class SystemRuntimeException extends RuntimeException {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -3263717469960233639L;

    /**
     * This exception Id.
     */
    private final ExceptionId id;

    /**
     * Arguments if any.
     */
    private final Object[] args;

    /**
     * Constructor from superclass.
     *
     * @param message the message
     * @param id      exception id
     */
    public SystemRuntimeException(String message, ExceptionId id, Object... args) {
        super(message);
        this.id = id;
        this.args = args;
    }

    /**
     * Constructor from superclass.
     *
     * @param message the message
     * @param cause   exception cause
     * @param id      exception id
     */
    public SystemRuntimeException(String message, Throwable cause, ExceptionId id, Object... args) {
        super(message, cause);
        this.id = id;
        this.args = args;
    }

    /**
     * Constructor from superclass.
     *
     * @param cause exception cause
     * @param id    exception id
     */
    public SystemRuntimeException(Throwable cause, ExceptionId id, Object... args) {
        super(cause);
        this.id = id;
        this.args = args;
    }

    /**
     * @return the id
     */
    public ExceptionId getId() {
        return id;
    }

    /**
     * @return the args
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * Overridden to include exception ID.
     *
     * @see java.lang.Throwable#toString()
     */
    @Override
    public String toString() {
        String localizedMessage = getLocalizedMessage();
        return getClass().getName() + ": [" + id.name() + "] " + (localizedMessage == null ? "" : localizedMessage);
    }

    /**
     * Overridden to support args.
     *
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {

        String thisMessage = super.getMessage();
        if (thisMessage != null && args != null && args.length > 0) {
            return MessageFormatter.arrayFormat(thisMessage, args).getMessage();
        }

        return thisMessage;
    }

    //for our internal exceptions we have and ExceptionId for identifying exception place!
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    // for presenting information to external users about exceptions, we should use cause exception
    @Override
    public StackTraceElement[] getStackTrace() {
        return getCause() == null ? super.getStackTrace() : getCause().getStackTrace();
    }
}
