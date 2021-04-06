package com.unidata.mdm.backend.api.rest.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.MDC;

import com.unidata.mdm.backend.MDCKeys;

public class ErrorResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String stackTrace;

    private String requestUuid;

    private List<ErrorInfo> errors = new ArrayList<>();

    /**  Is successful?. */
    private boolean success = false;

    public ErrorResponse() {
        requestUuid = MDC.get(MDCKeys.REQUEST_ID);
    }

    public ErrorResponse(Throwable throwable) {
        this();
        if (throwable != null) {
            stackTrace = ExceptionUtils.getStackTrace(throwable);
        }
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getRequestUuid() {
        return requestUuid;
    }

    public void setRequestUuid(String requestUuid) {
        this.requestUuid = requestUuid;
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
