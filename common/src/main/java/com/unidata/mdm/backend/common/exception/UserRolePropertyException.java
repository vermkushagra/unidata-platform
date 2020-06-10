/**
 *
 */

package com.unidata.mdm.backend.common.exception;

import java.util.Collection;

/**
 *
 * @author Alexander Magdenko
 */
public class UserRolePropertyException extends AbstractValidationException {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 8510394858670372960L;
    /**
     * Constructor.
     * @param message
     * @param id
     * @param validationResult
     * @param args
     */
    public UserRolePropertyException(String message, ExceptionId id, Collection<ValidationResult> validationResult,
            Object... args) {
        super(message, id, validationResult, args);
    }

    /**
     * Constructor.
     * @param message
     * @param id
     * @param validationResult
     */
    public UserRolePropertyException(String message, ExceptionId id, Collection<ValidationResult> validationResult) {
        super(message, id, validationResult);
    }
}
