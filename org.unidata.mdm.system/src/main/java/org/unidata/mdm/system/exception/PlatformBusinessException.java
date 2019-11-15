package org.unidata.mdm.system.exception;

/**
 * Business exception
 *
 * @author Alexander Malyshev
 */
public class PlatformBusinessException extends PlatformRuntimeException {
    /**
     * ABI SVUID.
     */
    private static final long serialVersionUID = -3206300072672935440L;
    /**
     * This exception domain.
     */
    private static final DomainId GENERIC_BUSINESS_EXCEPTION = () -> "GENERIC_BUSINESS_EXCEPTION";
    /**
     * Constructor.
     * @param message the message
     * @param id the id
     * @param args arguments
     */
    public PlatformBusinessException(String message, ExceptionId id, Object... args) {
        super(message, id, args);
    }
    /**
     * Constructor.
     * @param message the message
     * @param cause throwable cause
     * @param id the id
     * @param args arguments
     */
    public PlatformBusinessException(String message, Throwable cause, ExceptionId id, Object... args) {
        super(message, cause, id, args);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public DomainId getDomain() {
        return GENERIC_BUSINESS_EXCEPTION;
    }
    //for our internal exceptions we have and ExceptionId for identifying exception place!
    @Override
    public synchronized Throwable fillInStackTrace() {
        final String enableFillStackTrace = System.getProperty("enableFillStackTrace", "false");
        return enableFillStackTrace.equalsIgnoreCase("true") ? super.fillInStackTrace() : this;
    }

    // for presenting information to external users about exceptions, we should use cause exception
    @Override
    public StackTraceElement[] getStackTrace() {
        return getCause() == null ? super.getStackTrace() : getCause().getStackTrace();
    }
}
