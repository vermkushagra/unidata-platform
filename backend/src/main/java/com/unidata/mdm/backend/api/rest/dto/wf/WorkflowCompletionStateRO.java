package com.unidata.mdm.backend.api.rest.dto.wf;


/**
 * @author Mikhail Mikhailov
 * Task completion state.
 */
public class WorkflowCompletionStateRO {

    /**
     * Complete?
     */
    private boolean complete;
    /**
     * Message.
     */
    private String message;
    /**
     * Constructor.
     */
    public WorkflowCompletionStateRO() {
        super();
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
