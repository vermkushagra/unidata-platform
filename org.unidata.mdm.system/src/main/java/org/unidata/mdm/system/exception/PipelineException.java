package org.unidata.mdm.system.exception;

/**
 * @author Mikhail Mikhailov on Oct 2, 2019
 * Pipeline exceptions family.
 */
public class PipelineException extends PlatformRuntimeException {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 7955101479392963573L;
    /**
     * Pipeline exceptions domain.
     */
    private static final DomainId PIPELINE_EXCEPTION = () -> "PIPELINE_EXCEPTION";
    /**
     * Constructor.
     * @param message
     * @param id
     * @param args
     */
    public PipelineException(String message, ExceptionId id, Object... args) {
        super(message, id, args);
    }
    /**
     * Constructor.
     * @param message
     * @param cause
     * @param id
     * @param args
     */
    public PipelineException(String message, Throwable cause, ExceptionId id, Object... args) {
        super(message, cause, id, args);
    }
    /**
     * Constructor.
     * @param cause
     * @param id
     * @param args
     */
    public PipelineException(Throwable cause, ExceptionId id, Object... args) {
        super(cause, id, args);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public DomainId getDomain() {
        return PIPELINE_EXCEPTION;
    }
}
