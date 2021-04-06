package com.unidata.mdm.backend.common.exception;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author Mikhail Mikhailov
 * Base validation exception.
 */
public abstract class AbstractValidationException extends SystemRuntimeException {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -7601301119411683597L;
    /**
     * The validation result.
     */
    private final Collection<ValidationResult> validationResult;
    /**
     * Constructor.
     * @param message the message
     * @param id the id
     * @param args additional args
     */
    public AbstractValidationException(String message, ExceptionId id, Collection<ValidationResult> validationResult, Object... args) {
        super(message, id, args);
        this.validationResult = validationResult == null ? Collections.emptyList() : validationResult;
    }
    /**
     * Constructor.
     * @param message the message
     * @param id the exception id
     */
    public AbstractValidationException(String message, ExceptionId id, Collection<ValidationResult> validationResult) {
        super(message, id);
        this.validationResult = validationResult == null ? Collections.emptyList() : validationResult;
    }
    /**
     * @return the validationResult
     */
    public Collection<ValidationResult> getValidationResult() {
        return validationResult;
    }

    /**
     *
     * @return string error message
     */
    @Override
    public String getMessage() {
        String prefix = super.getMessage() + ". Inner validation errors:\n";
        return getValidationResult().stream()
                                    .map(ValidationResult::getSystemMessage)
                                    .collect(Collectors.joining("\n-",  prefix , ""));
    }
}
