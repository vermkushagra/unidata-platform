/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.wf;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author Mikhail Mikhailov
 * Workflow task request.
 */
public class WorkflowTaskRequestRO {
    /**
     * The task id.
     */
    private String taskId;
    /**
     * Marks query as historical.
     */
    private boolean historical;
    /**
     * Task completed by.
     */
    private String taskCompletedBy;
    /**
     * Process started after.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
    private Date processStartAfter;
    /**
     * Process ended before.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
    private Date processStartBefore;
    /**
     * Task started after.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
    private Date taskStartAfter;
    /**
     * Task started before.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
    private Date taskStartBefore;
    /**
     * Task started after.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
    private Date taskEndAfter;
    /**
     * Task started before.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
    private Date taskEndBefore;
    /**
     * Process initiator.
     */
    private String initiator;
    /**
     * Approval state.
     */
    private String approvalState;
    /**
     * Find tasks by user name.
     */
    private String candidateUser;
    /**
     * Candidate or assignee, mutually exclusive to candidateUser and assignedUser.
     */
    private String candidateOrAssignee;
    /**
     * Find tasks by user name.
     */
    private String assignedUser;
    /**
     * Variables.
     */
    private Map<String, Object> variables;
    /**
     * Return count.
     */
    private int count = 10;
    /**
     * Return page.
     */
    private int page = 0;
    /**
     * Constructor.
     */
    public WorkflowTaskRequestRO() {
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
     * @return the historical
     */
    public boolean isHistorical() {
        return historical;
    }

    /**
     * @param historical the historical to set
     */
    public void setHistorical(boolean historical) {
        this.historical = historical;
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
     * @return the processStartAfter
     */
    public Date getProcessStartAfter() {
        return processStartAfter;
    }


    /**
     * @param processStartAfter the processStartAfter to set
     */
    public void setProcessStartAfter(Date processStartAfter) {
        this.processStartAfter = processStartAfter;
    }


    /**
     * @return the processStartBefore
     */
    public Date getProcessStartBefore() {
        return processStartBefore;
    }


    /**
     * @param processStartBefore the processStartBefore to set
     */
    public void setProcessStartBefore(Date processStartBefore) {
        this.processStartBefore = processStartBefore;
    }


    /**
     * @return the taskStartAfter
     */
    public Date getTaskStartAfter() {
        return taskStartAfter;
    }


    /**
     * @param taskStartAfter the taskStartAfter to set
     */
    public void setTaskStartAfter(Date taskStartAfter) {
        this.taskStartAfter = taskStartAfter;
    }


    /**
     * @return the taskStartBefore
     */
    public Date getTaskStartBefore() {
        return taskStartBefore;
    }


    /**
     * @param taskStartBefore the taskStartBefore to set
     */
    public void setTaskStartBefore(Date taskStartBefore) {
        this.taskStartBefore = taskStartBefore;
    }


    /**
     * @return the taskEndAfter
     */
    public Date getTaskEndAfter() {
        return taskEndAfter;
    }


    /**
     * @param taskEndAfter the taskEndAfter to set
     */
    public void setTaskEndAfter(Date taskEndAfter) {
        this.taskEndAfter = taskEndAfter;
    }


    /**
     * @return the taskEndBefore
     */
    public Date getTaskEndBefore() {
        return taskEndBefore;
    }


    /**
     * @param taskEndBefore the taskEndBefore to set
     */
    public void setTaskEndBefore(Date taskEndBefore) {
        this.taskEndBefore = taskEndBefore;
    }

    /**
     * @return the assignedUser
     */
    public String getCandidateUser() {
        return candidateUser;
    }

    /**
     * @param assignedUser the assignedUser to set
     */
    public void setCandidateUser(String assignedUser) {
        this.candidateUser = assignedUser;
    }


    /**
     * @return the assignedUser
     */
    public String getAssignedUser() {
        return assignedUser;
    }


    /**
     * @param assignedUser the assignedUser to set
     */
    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }


    /**
     * @return the candidateOrAssignee
     */
    public String getCandidateOrAssignee() {
        return candidateOrAssignee;
    }


    /**
     * @param candidateOrAssignee the candidateOrAssignee to set
     */
    public void setCandidateOrAssignee(String candidateOrAssignee) {
        this.candidateOrAssignee = candidateOrAssignee;
    }

    /**
     * @return the initiator
     */
    public String getInitiator() {
        return initiator;
    }

    /**
     * @param initiator the initiator to set
     */
    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    /**
     * @return the approvalState
     */
    public String getApprovalState() {
        return approvalState;
    }

    /**
     * @param approvalState the approvalState to set
     */
    public void setApprovalState(String approvalState) {
        this.approvalState = approvalState;
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
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }

}
