package com.unidata.mdm.backend.common.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Class CleanseFunctionException.
 */
public class CleanseFunctionExecutionException extends Exception {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -8171957635042513810L;
    /** The error messages. */
    private List<String> errorMessages = new ArrayList<>();

    /** The cleanse function id. */
    private String cleanseFunctionId;

    /** The cause. */
    private Exception cause;

    /**
     * Instantiates a new cleanse function exception.
     */
    private CleanseFunctionExecutionException() {
        super();
    }

    /**
     * Instantiates a new cleanse function exception.
     *
     * @param cleansefunctionId
     *            the cleansefunction id
     * @param errorMessages
     *            the error messages
     */
    public CleanseFunctionExecutionException(String cleansefunctionId, String... errorMessages) {
        this();
        this.cleanseFunctionId = cleansefunctionId;
        this.errorMessages = Arrays.asList(errorMessages);
    }

    /**
     * Instantiates a new cleanse function exception.
     *
     * @param cleansefunctionId the cleansefunction id
     * @param cause the cause
     * @param errorMessages the error messages
     */
    public CleanseFunctionExecutionException(String cleansefunctionId, Exception cause, String... errorMessages) {
        this();
        this.cleanseFunctionId = cleansefunctionId;
        this.cause = cause;
        this.errorMessages = Arrays.asList(errorMessages);
    }

    /**
     * Instantiates a new cleanse function exception.
     *
     * @param cleanseFunctionId
     *            the cleansefunction id
     * @param errorMessages
     *            the error messages
     */
    public CleanseFunctionExecutionException(String cleanseFunctionId, List<String> errorMessages) {
        this();
        this.cleanseFunctionId = cleanseFunctionId;
        this.errorMessages = errorMessages;
    }

    /**
     * Gets the error messages.
     *
     * @return the error messages
     */
    public List<String> getErrorMessages() {
        return this.errorMessages;
    }

    /**
     * Gets the cleanse function id.
     *
     * @return the cleanse function id
     */
    public String getCleanseFunctionId() {
        return this.cleanseFunctionId;
    }
}
