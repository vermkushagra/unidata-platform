package org.unidata.mdm.system.exception;

import java.util.Collection;
import java.util.Collections;

/**
 * Data validation error.
 *
 * @author Alexander Malyshev
 */
public class PlatformValidationException extends PlatformRuntimeException {
    /**
     * ABI SVUID.
     */
    private static final long serialVersionUID = 6846532046659833773L;
    /**
     * This exception domain.
     */
    private static final DomainId GENERIC_VALIDATION_EXCEPTION = () -> "GENERIC_VALIDATION_EXCEPTION";
    /**
     * The validation result.
     */
    private final Collection<ValidationResult> validationResults;
    /**
     * Constructor.
     * @param message the message
     * @param id the id
     * @param validationResult validation result
     * @param args the arguments
     */
    public PlatformValidationException(String message, ExceptionId id, Collection<ValidationResult> validationResult, Object... args) {
        super(message, id, args);
        this.validationResults = validationResult == null ? Collections.emptyList() : validationResult;
    }
    /**
     * Constructor.
     * @param message the message
     * @param cause the exception cause
     * @param id exception id
     * @param validationResult validation elements
     * @param args arguments
     */
    public PlatformValidationException(String message, Throwable cause, ExceptionId id, Collection<ValidationResult> validationResult, Object... args) {
        super(message, cause, id, args);
        this.validationResults = validationResult == null ? Collections.emptyList() : validationResult;
    }
    /**
     * Constructor.
     * @param message the message
     * @param id exception id
     * @param validationResults validation elements
     */
    public PlatformValidationException(
            final String message,
            final ExceptionId id,
            final Collection<ValidationResult> validationResults
    ) {
        super(message, id);
        this.validationResults = validationResults == null ? Collections.emptyList() : validationResults;
    }
    /**
     * @return the validationResult
     */
    public Collection<ValidationResult> getValidationResults() {
        return validationResults;
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
    /**
     * {@inheritDoc}
     */
    @Override
    public DomainId getDomain() {
        return GENERIC_VALIDATION_EXCEPTION;
    }
}
