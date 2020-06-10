package com.unidata.mdm.backend.common.integration.exits;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Kopin on 19.09.2017.
 */
public class ExitResult {
    /**
     * status for user exit
     */
    private Status status;
    /**
     * list of warnings
     */
    private List<String> warnings;
    /**
     * 'record was changed' flag
     */
    private boolean wasModified;

    public ExitResult() {
        this.status = Status.SUCCESS;
    }

    public ExitResult(Status status) {
        this.status = status;
    }

    public enum Status {
        SUCCESS, WARNING, ERROR
    }

    public void addWarning(String warning) {
        if (warnings == null) {
            warnings = new ArrayList<>();
        }
        warnings.add(warning);
    }

    public String getWarningMessage(){
        return warnings == null ? "" : warnings.toString();
    }

    public Status getStatus() {
        return status;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public boolean isWasModified() {
        return wasModified;
    }

    public void setWasModified(boolean wasModify) {
        this.wasModified = wasModify;
    }
}
