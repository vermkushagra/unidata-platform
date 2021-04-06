package com.unidata.mdm.backend.service.classifier;

import java.util.Collection;

import com.unidata.mdm.backend.common.exception.AbstractValidationException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.ValidationResult;

public class CodePatternValidationException extends AbstractValidationException {

    public CodePatternValidationException(String message, ExceptionId id, Collection<ValidationResult> validationResult, Object... args) {
        super(message, id, validationResult, args);
    }
}
