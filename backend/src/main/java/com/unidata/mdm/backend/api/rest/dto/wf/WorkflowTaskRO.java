/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.wf;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Workflow task REST object.
 */
public class WorkflowTaskRO {

    /**
     * Task id.
     */
    private String taskId;
    /**
     * Task definition key.
     */
    private String taskKey;
    /**
     * Task title.
     */
    private String taskTitle;
    /**
     * Task title.
     */
    private String taskDescription;
    /**
     * Task assignee.
     */
    private String taskAssignee;
    /**
     * Task assignee full name.
     */
    private String taskAssigneeName;
    /**
     * Task completed by.
     */
    private String taskCompletedBy;
    /**
     * Task candidate.
     */
    private String taskCandidate;
    /**
     * Process id.,
     */
    private String processId;
    /**
     * Process title.
     */
    private String processTitle;
    /**
     * Process type.
     */
    private String processType;
    /**
     * Process type.
     */
    private String triggerType;
    /**
     * Process definition id.
     */
    private String processDefinitionId;
    /**
     * Originator user name.
     */
    private String originator;
    /**
     * Originator user full name.
     */
    private String originatorName;
    /**
     * Originator email.
     */
    private String originatorEmail;
    /**
     * Create date of the task instance.
     */
    private Date createDate;
    /**
     * Create date of the task instance.
     */
    private Date finishedDate;
    /**
     * Finished or not.
     */
    private boolean finished;
    /**
     * Process finished or not.
     */
    private boolean processFinished;
    /**
     * Assignable to current user.
     */
    private boolean assignableToCurrentUser;
    /**
     * The task can be unclaimed.
     */
    private boolean unassignableByCurrentUser;
    /**
     * Approval message.
     */
    private String approvalMessage;
    /**
     * Variables.
     */
    private Map<String, Object> variables = new HashMap<>();
    /**
     * Actions.
     */
    private List<WorkflowActionRO> actions;
    /**
     * Constructor.
     */
    public WorkflowTaskRO() {
        super();
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }


    /**
     * @return the taskKey
     */
    public String getTaskKey() {
        return taskKey;
    }


    /**
     * @param taskKey the taskKey to set
     */
    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    /**
     * @return the taskName
     */
    public String getTaskTitle() {
        return taskTitle;
    }

    /**
     * @param taskName the taskName to set
     */
    public void setTaskTitle(String taskName) {
        this.taskTitle = taskName;
    }

    /**
     * @return the taskDescription
     */
    public String getTaskDescription() {
        return taskDescription;
    }

    /**
     * @param taskDescription the taskDescription to set
     */
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    /**
     * @return the taskAssignee
     */
    public String getTaskAssignee() {
        return taskAssignee;
    }

    /**
     * @param taskAssignee the taskAssignee to set
     */
    public void setTaskAssignee(String taskAssignee) {
        this.taskAssignee = taskAssignee;
    }


    /**
     * @return the taskCandidate
     */
    public String getTaskCandidate() {
        return taskCandidate;
    }


    /**
     * @param taskCandidate the taskCandidate to set
     */
    public void setTaskCandidate(String taskCandidate) {
        this.taskCandidate = taskCandidate;
    }

    /**
     * @return the processId
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * @param processId the processId to set
     */
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    /**
     * @return the processTitle
     */
    public String getProcessTitle() {
        return processTitle;
    }

    /**
     * @param processTitle the processTitle to set
     */
    public void setProcessTitle(String processTitle) {
        this.processTitle = processTitle;
    }

    /**
     * @return the processType
     */
    public String getProcessType() {
        return processType;
    }

    /**
     * @param processType the processType to set
     */
    public void setProcessType(String processType) {
        this.processType = processType;
    }


    /**
     * @return the triggerType
     */
    public String getTriggerType() {
        return triggerType;
    }

    /**
     * @param triggerType the triggerType to set
     */
    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    /**
     * @return the processDefinitionId
     */
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }


    /**
     * @param processDefinitionId the processDefinitionId to set
     */
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    /**
     * @return the originator
     */
    public String getOriginator() {
        return originator;
    }

    /**
     * @param originator the originator to set
     */
    public void setOriginator(String originator) {
        this.originator = originator;
    }

    /**
     * @return the originatorEmail
     */
    public String getOriginatorEmail() {
        return originatorEmail;
    }

    /**
     * @param originatorEmail the originatorEmail to set
     */
    public void setOriginatorEmail(String originatorEmail) {
        this.originatorEmail = originatorEmail;
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    /**
     * @return the finished
     */
    public boolean isFinished() {
        return finished;
    }


    /**
     * @param finished the finished to set
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }



    /**
     * @return the processFinished
     */
    public boolean isProcessFinished() {
        return processFinished;
    }


    /**
     * @param processFinished the processFinished to set
     */
    public void setProcessFinished(boolean processFinished) {
        this.processFinished = processFinished;
    }

    /**
     * @return the approvalMessage
     */
    public String getApprovalMessage() {
        return approvalMessage;
    }


    /**
     * @param approvalMessage the approvalMessage to set
     */
    public void setApprovalMessage(String approvalMessage) {
        this.approvalMessage = approvalMessage;
    }

    /**
     * @return the taskCompletedBy
     */
    public String getTaskCompletedBy() {
        return taskCompletedBy;
    }

    /**
     * @param taskCompletedBy the taskCompletedBy to set
     */
    public void setTaskCompletedBy(String taskCompletedBy) {
        this.taskCompletedBy = taskCompletedBy;
    }

    /**
     * @return the finishedDate
     */
    public Date getFinishedDate() {
        return finishedDate;
    }

    /**
     * @param finishedDate the finishedDate to set
     */
    public void setFinishedDate(Date finishedDate) {
        this.finishedDate = finishedDate;
    }

    /**
     * @return the variables
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    /**
     * @param variables the variables to set
     */
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }


    /**
     * @return the actions
     */
    public List<WorkflowActionRO> getActions() {
        return actions;
    }


    /**
     * @param actions the actions to set
     */
    public void setActions(List<WorkflowActionRO> actions) {
        this.actions = actions;
    }


    /**
     * @return the assignableToCurrentUser
     */
    public boolean isAssignableToCurrentUser() {
        return assignableToCurrentUser;
    }


    /**
     * @param assignableToCurrentUser the assignableToCurrentUser to set
     */
    public void setAssignableToCurrentUser(boolean assignableToCurrentUser) {
        this.assignableToCurrentUser = assignableToCurrentUser;
    }


    /**
     * @return the unclaimableByCurrentUser
     */
    public boolean isUnassignableByCurrentUser() {
        return unassignableByCurrentUser;
    }


    /**
     * @param unclaimableByCurrentUser the unclaimableByCurrentUser to set
     */
    public void setUnassignableByCurrentUser(boolean unclaimableByCurrentUser) {
        this.unassignableByCurrentUser = unclaimableByCurrentUser;
    }

    public String getTaskAssigneeName() {
        return taskAssigneeName;
    }

    public void setTaskAssigneeName(String taskAssigneeName) {
        this.taskAssigneeName = taskAssigneeName;
    }

    public String getOriginatorName() {
        return originatorName;
    }

    public void setOriginatorName(String originatorName) {
        this.originatorName = originatorName;
    }
}
