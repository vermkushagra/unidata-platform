package com.unidata.mdm.backend.configuration.application;

import java.util.Collection;

import com.unidata.mdm.backend.common.exception.AbstractValidationException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.ValidationResult;
import org.apache.commons.collections.CollectionUtils;

public class ConfigurationValidationException extends AbstractValidationException {

    public ConfigurationValidationException(
            final String message,
            final ExceptionId id,
            Collection<ValidationResult> validationResult
    ) {
        super(message, id, validationResult);
        if (CollectionUtils.isEmpty(validationResult)) {
            throw new IllegalArgumentException("Empty list of invalid parameters");
        }
    }
}
