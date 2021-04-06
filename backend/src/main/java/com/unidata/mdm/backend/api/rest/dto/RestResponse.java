package com.unidata.mdm.backend.api.rest.dto;

import java.util.List;

/**
 * @author Michael Yashin. Created on 03.06.2015.
 */
public class RestResponse<T> {
    /**  Is successful?. */
    protected boolean success = true;
    protected T content;
    protected List<ErrorInfo> errors;

    public RestResponse() {

    }

    public RestResponse(T content) {
        this.content = content;
    }

    public RestResponse(T content, boolean success) {
        this.content = content;
        this.success = success;
    }

    public RestResponse(T content, List<ErrorInfo> errors) {
        this.content = content;
        this.errors = errors;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public List<ErrorInfo> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorInfo> errors) {
        this.errors = errors;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

}
