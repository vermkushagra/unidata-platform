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
