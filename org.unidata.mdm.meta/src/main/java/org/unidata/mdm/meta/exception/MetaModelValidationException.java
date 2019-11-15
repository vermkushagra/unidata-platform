package org.unidata.mdm.meta.exception;

import java.util.Collection;

import org.unidata.mdm.system.exception.DomainId;
import org.unidata.mdm.system.exception.ExceptionId;
import org.unidata.mdm.system.exception.PlatformValidationException;
import org.unidata.mdm.system.exception.ValidationResult;

/**
 * @author Mikhail Mikhailov on Oct 29, 2019
 */
public class MetaModelValidationException extends PlatformValidationException {
    /**
     * This exception domain.
     */
    private static final DomainId MODEL_VALIDATION_EXCEPTION = () -> "MODEL_VALIDATION_EXCEPTION";
    /**
     * GSVUID
     */
    private static final long serialVersionUID = 2190312383766112305L;
    /**
     * Constructor.
     * @param message
     * @param id
     * @param validationResult
     * @param args
     */
    public MetaModelValidationException(String message, ExceptionId id, Collection<ValidationResult> validationResult,
            Object... args) {
        super(message, id, validationResult, args);
    }

    /**
     * Constructor.
     * @param message
     * @param cause
     * @param id
     * @param validationResult
     * @param args
     */
    public MetaModelValidationException(String message, Throwable cause, ExceptionId id,
            Collection<ValidationResult> validationResult, Object... args) {
        super(message, cause, id, validationResult, args);
    }

    /**
     * Constructor.
     * @param message
     * @param id
     * @param validationResults
     */
    public MetaModelValidationException(String message, ExceptionId id,
            Collection<ValidationResult> validationResults) {
        super(message, id, validationResults);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DomainId getDomain() {
        return MODEL_VALIDATION_EXCEPTION;
    }
}
