package com.unidata.mdm.backend.common.exception;

import java.util.Collection;

public class MetaModelValidationException extends AbstractValidationException {



    public MetaModelValidationException(String message, ExceptionId id, Collection<ValidationResult> validationResult,
            Object... args) {
        super(message, id, validationResult, args);
    }

    public MetaModelValidationException(String message, ExceptionId id, Collection<ValidationResult> validationResult) {
        super(message, id, validationResult);
    }
}
