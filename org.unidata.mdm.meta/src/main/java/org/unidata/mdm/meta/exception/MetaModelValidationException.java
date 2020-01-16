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
