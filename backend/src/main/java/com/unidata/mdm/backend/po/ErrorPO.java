package com.unidata.mdm.backend.po;

/**
 * Common error object
 */
public class ErrorPO {

    private String error;

    private String description;

    private String operationId;

    public ErrorPO(String error, String description, String operationId) {
        this.error = error;
        this.description = description;
        this.operationId = operationId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
}
