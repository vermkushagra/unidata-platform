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

/**
 *
 */
package org.unidata.mdm.search.exception;

import org.unidata.mdm.system.exception.DomainId;
import org.unidata.mdm.system.exception.ExceptionId;
import org.unidata.mdm.system.exception.PlatformRuntimeException;

/**
 * @author Mikhail Mikhailov
 * Search exception class.
 */
public class SearchApplicationException extends PlatformRuntimeException {
    /**
     * SFE.
     */
    private static final DomainId SEARCH_FAILURE_EXCEPTION = () -> "SEARCH_FAILURE_EXCEPTION";
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 4401101538239705307L;
    /**
     * BulkResponse or similar.
     */
    private final transient Object response;
    /**
     * Constructor.
     * @param message the error message
     * @param id exception id
     * @param args additional error message arguments
     */
    public SearchApplicationException(String message, ExceptionId id, Object... args) {
        super(message, id, args);
        this.response = null;
    }
    /**
     * Constructor.
     * @param message the error message
     * @param cause exception cause
     * @param id exception id
     * @param args additional error message arguments
     */
    public SearchApplicationException(String message, Throwable cause, ExceptionId id, Object... args) {
        super(message, cause, id, args);
        this.response = null;
    }
    /**
     * Constructor.
     * @param message the error message
     * @param id the exception id
     * @param response ES response as object (to reduce type visibility in 'common')
     */
    public SearchApplicationException(String message, ExceptionId id, Object response) {
        super(message, id);
        this.response = response;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public DomainId getDomain() {
        return SEARCH_FAILURE_EXCEPTION;
    }
    /**
     * ES response object you should cast.
     * @return the response
     */
    public Object getResponse() {
        return response;
    }
}
