package org.unidata.mdm.system.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * Common parent type for exception used in Unidata Platform
 *
 * @author Alexander Malyshev
 */
public abstract class PlatformRuntimeException extends RuntimeException {
    /**
     * GSVUID.
     */
    private static final long serialVersionUID = 6127303709077828586L;
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
    public PlatformRuntimeException(String message, ExceptionId id, Object... args) {
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
    public PlatformRuntimeException(String message, Throwable cause, ExceptionId id, Object... args) {
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
    public PlatformRuntimeException(Throwable cause, ExceptionId id, Object... args) {
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
     * @see Throwable#toString()
     */
    @Override
    public String toString() {
        String localizedMessage = getLocalizedMessage();
        return getClass().getName() + ": [" + id.code() + "] " + (localizedMessage == null ? "" : localizedMessage);
    }

    /**
     * Overridden to support args.
     *
     * @see Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        String thisMessage = super.getMessage();
        if (thisMessage != null && args != null && args.length > 0) {
            return MessageFormatter.arrayFormat(thisMessage, args).getMessage();
        }

        return thisMessage;
    }
    /**
     * Gets the exception domain descriptor.
     * @return domain descriptor
     */
    public abstract DomainId getDomain();
}
