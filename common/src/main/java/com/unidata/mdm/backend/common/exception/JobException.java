package com.unidata.mdm.backend.common.exception;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author Alexander Magdenko
 */
public class JobException extends SystemRuntimeException {
    /**
     * Key-value pairs.
     */
    private final List<Pair<String, String>> params;
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 4401101538239705307L;
    /**
     * @param message
     */
    public JobException(String message, ExceptionId id, Object... args) {
        super(message, id, args);
        this.params = null;
    }

    /**
     * Validation error
     * @param message    Validation message.
     * @param id         Error id.
     * @param params     Params in error.
     * @param args       Params for message.
     */
    public JobException(String message, ExceptionId id, List<Pair<String, String>> params, Object... args) {
        super(message, id, args);
        this.params = params;
    }
    /**
     * @param message
     * @param cause
     */
    public JobException(String message, Throwable cause, ExceptionId id, Object... args) {
        super(message, cause, id, args);
        this.params = null;
    }

    /**
     * @param cause
     */
    public JobException(Throwable cause, ExceptionId id, Object... args) {
        super(cause, id, args);
        this.params = null;
    }

    public List<Pair<String, String>> getParams() {
        return params;
    }
}
