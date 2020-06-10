package com.unidata.mdm.backend.common.exception;

import java.util.Collection;

public class MatchingValidationException extends AbstractValidationException {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 2245853418869216160L;
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
