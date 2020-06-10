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

import org.apache.commons.lang3.StringUtils;

/**
 * The Class CleanseFunctionException.
 */
public class CleanseFunctionExecutionException extends SystemRuntimeException {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -8171957635042513810L;
    /**
     * Instantiates a new cleanse function exception.
     */
    public CleanseFunctionExecutionException(String message, Throwable cause, ExceptionId id, Object... args) {
        super(message, cause, id, args);
    }
    /**
     * Constructor.
     * @param message
     * @param id
     * @param args
     */
    public CleanseFunctionExecutionException(String message, ExceptionId id, Object... args) {
        super(message, id, args);
    }
    /**
     * Constructor.
     * @param cause
     * @param id
     * @param args
     */
    public CleanseFunctionExecutionException(Throwable cause, ExceptionId id, Object... args) {
        super(cause, id, args);
    }

    /**
     * Instantiates a new cleanse function exception.
     * Special form, used by CF implementations.
     *
     * @param cleanseFunctionId
     *            the cleansefunction id
     * @param errorMessages
     *            the error messages
     */
    public CleanseFunctionExecutionException(String cleanseFunctionId, String... errorMessages) {
        this(constructExecMessage(cleanseFunctionId, errorMessages),
             ExceptionId.EX_DQ_CLEANSE_FUNCTION_EXEC,
             constructExecArgs(cleanseFunctionId, errorMessages));
    }

    /**
     * Instantiates a new cleanse function exception.
     *
     * @param cleanseFunctionId the cleansefunction id
     * @param cause the cause
     * @param errorMessages the error messages
     */
    public CleanseFunctionExecutionException(String cleanseFunctionId, Throwable cause, String... errorMessages) {
        this(constructExecMessage(cleanseFunctionId, errorMessages),
             cause,
             ExceptionId.EX_DQ_CLEANSE_FUNCTION_EXEC,
             constructExecArgs(cleanseFunctionId, errorMessages));
    }

    /**
     * Constructs exec message.
     * @param id CF id
     * @param errors errors
     * @return message
     */
    private static String constructExecMessage(String id, String... errors) {
        return errors == null || errors.length == 0
                ? "Error, while executing cleanse function {}: Unspecified."
                : "Error, while executing cleanse function {}: Details: {}.";
    }
    /**
     * Constructs exec args.
     * @param id CF id
     * @param errors errors
     * @return args
     */
    private static Object[] constructExecArgs(String id, String... errors) {

        final Object[] args = errors == null || errors.length == 0
                ? new Object[1]
                : new Object[2];

        args[0] = id;
        if (errors != null && errors.length > 0) {

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < errors.length; i++) {
                sb.append(StringUtils.LF)
                  .append("- ")
                  .append(errors[i]);
            }

            args[1] = sb.toString();
        }

        return args;
    }
}
