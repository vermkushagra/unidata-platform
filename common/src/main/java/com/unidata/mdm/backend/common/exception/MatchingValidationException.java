package com.unidata.mdm.backend.common.exception;

import java.util.Collection;

public class MatchingValidationException extends AbstractValidationException {
    /**
     *
     * @param message
     * @param id
     * @param validationResult
     */
    public MatchingValidationException(String message, ExceptionId id, Collection<ValidationResult> validationResult) {
        super(message, id, validationResult);
    }
}
