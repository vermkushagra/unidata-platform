/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
