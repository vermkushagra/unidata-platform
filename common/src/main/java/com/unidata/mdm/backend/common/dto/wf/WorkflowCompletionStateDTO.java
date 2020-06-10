/**
 *
 */
package com.unidata.mdm.backend.common.dto.wf;


/**
 * @author Mikhail Mikhailov
 *
 */
public class WorkflowCompletionStateDTO {

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
    public WorkflowCompletionStateDTO() {
        super();
    }
    /**
     * Constructor.
     */
    public WorkflowCompletionStateDTO(boolean complete, String message) {
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
