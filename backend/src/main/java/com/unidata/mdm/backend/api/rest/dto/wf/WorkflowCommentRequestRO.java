package com.unidata.mdm.backend.api.rest.dto.wf;

/**
 * Comment request object.
 * @author Denis Kostovarov
 */
public class WorkflowCommentRequestRO {
    /**
     * Task id.
     */
    private String taskId;
    /**
     * Process id.
     */
    private String processInstanceId;
    /**
     * Message.
     */
    private String message;

    /**
    /**
     * Constructor.
     */
    public WorkflowCommentRequestRO() {
        super();
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
