package org.unidata.mdm.system.exception;

/**
 * Any external or system errors during platform work.
 *
 * @author Alexander Malyshev
 */
public class PlatformFailureException extends PlatformRuntimeException {
    /**
     * ABI SVUID.
     */
    private static final long serialVersionUID = 6597321286543795338L;
    /**
     * This exception domain.
     */
    private static final DomainId GENERIC_FAILURE_EXCEPTION = () -> "GENERIC_FAILURE_EXCEPTION";

    public PlatformFailureException(String message, ExceptionId id, Object... args) {
        super(message, id, args);
    }

    public PlatformFailureException(String message, Throwable cause, ExceptionId id, Object... args) {
        super(message, cause, id, args);
    }

    public PlatformFailureException(Throwable cause, ExceptionId id, Object... args) {
        super(cause, id, args);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public DomainId getDomain() {
        return GENERIC_FAILURE_EXCEPTION;
    }
}
