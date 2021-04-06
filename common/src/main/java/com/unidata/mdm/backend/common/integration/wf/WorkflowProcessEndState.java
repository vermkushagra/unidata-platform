package com.unidata.mdm.backend.common.integration.wf;

/**
 * @author Mikhail Mikhailov
 * Process completion state.
 */
public class WorkflowProcessEndState {

    /**
     * Tells the caller, whether to submit the changes (true), or discard (false).
     */
    private boolean complete;
    /**
     * Describe denial or acceptance reason.
     */
    private String message;
    /**
     * Constructor.
     */
    public WorkflowProcessEndState() {
        super();
    }
    /**
     * Constructor.
     * @param complete signal 'save changes' state
     * @param message optional end message
     */
    public WorkflowProcessEndState(boolean complete, String message) {
        super();
        this.complete = complete;
        this.message = message;
    }
    /**
     * @return the complete
     */
    public boolean isComplete() {
        return complete;
    }
    /**
     * @param complete the complete to set
     */
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
