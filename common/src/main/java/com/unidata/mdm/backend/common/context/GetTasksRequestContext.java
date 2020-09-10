/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

/**
 *
 */
package com.unidata.mdm.backend.common.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.conf.WorkflowProcessType;

/**
 * @author Mikhail Mikhailov
 * Gets workflow tasks, according to search criteria.
 */
public class GetTasksRequestContext extends CommonRequestContext {

    /**
     * Process type.
     */
    private final WorkflowProcessType processType;
    /**
     * Process definition id.
     */
    private final String processDefinitionId;
    /**
     * Process key (such as etalon id.)
     */
    private final String processKey;
    /**
     * Task id.
     */
    private final String taskId;
    /**
     * Candidate user.
     */
    private final String candidateUser;
    /**
     * Assigned user.
     */
    private final String assignedUser;
    /**
     * Candidate or assignee for currently executed tasks only (not for historic queries).
     */
    private final String candidateOrAssignee;
    /**
     * Starter user.
     */
    private final String initiator;
    /**
     * Task approval state.
     */
    private final ApprovalState approvalState;
    /**
     * Variables.
     */
    private final transient Map<String, Object> variables;
    /**
     * Max hit count to return.
     */
    private final int count;
    /**
     * Page number, 0 based.
     */
    private final int page;
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 5849268637470363136L;
    /**
     * Marks query as historical.
     */
    private final boolean historical;
    /**
     * Task completed by.
     */
    private final String taskCompletedBy;
    /**
     * Process start period boundary (after, before).
     */
    private final Pair<Date, Date> processStart;
    /**
     * Task start period boundary (after, before).
     */
    private final Pair<Date, Date> taskStart;
    /**
     * Task start period boundary (after, before).
     */
    private final Pair<Date, Date> taskEnd;
    /**
     * Return total count only and no other results.
     */
    private final boolean countOnly;

    private final List<String> candidateGroups = new ArrayList<>();

    /**
     * Constructor.
     */
    private GetTasksRequestContext(GetTasksRequestContextBuilder b) {
        super();
        this.processType = b.processType;
        this.processDefinitionId = b.processDefinitionId;
        this.processKey = b.processKey;
        this.taskId = b.taskId;
        this.candidateUser = b.candidateUser;
        this.assignedUser = b.assignedUser;
        this.candidateOrAssignee = b.candidateOrAssignee;
        this.initiator = b.initiator;
        this.approvalState = b.approvalState;
        this.variables = b.variables;
        this.page = b.page;
        this.count = b.count;
        this.historical = b.historical;
        this.taskCompletedBy = b.taskCompletedBy;
        this.processStart = b.processStart;
        this.taskStart = b.taskStart;
        this.taskEnd = b.taskEnd;
        this.countOnly = b.countOnly;
        this.candidateGroups.addAll(b.candidateGroups);
    }

    /**
     * @return the processType
     */
    public WorkflowProcessType getProcessType() {
        return processType;
    }


    /**
     * @return the processDefinitionId
     */
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    /**
     * @return the processKey
     */
    public String getProcessKey() {
        return processKey;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @return the assignedUser
     */
    public String getCandidateUser() {
        return candidateUser;
    }


    /**
     * @return the assignedUser
     */
    public String getAssignedUser() {
        return assignedUser;
    }


    /**
     * @return the candidateOrAssignee
     */
    public String getCandidateOrAssignee() {
        return candidateOrAssignee;
    }

    /**
     * @return the originator
     */
    public String getInitiator() {
        return initiator;
    }

    /**
     * @return the approvalState
     */
    public ApprovalState getApprovalState() {
        return approvalState;
    }

    /**
     * @return the variables
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }


    /**
     * @return the historical
     */
    public boolean isHistorical() {
        return historical;
    }


    /**
     * @return the taskCompletedBy
     */
    public String getTaskCompletedBy() {
        return taskCompletedBy;
    }


    /**
     * @return the processStart
     */
    public Pair<Date, Date> getProcessStart() {
        return processStart;
    }


    /**
     * @return the taskStart
     */
    public Pair<Date, Date> getTaskStart() {
        return taskStart;
    }


    /**
     * @return the taskEnd
     */
    public Pair<Date, Date> getTaskEnd() {
        return taskEnd;
    }
    /**
     * @return count only query
     */
    public boolean isCountOnly() {
        return countOnly;
    }

    public List<String> getCandidateGroups() {
        return Collections.unmodifiableList(candidateGroups);
    }

    /**
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class GetTasksRequestContextBuilder {
        /**
         * Process.
         */
        private WorkflowProcessType processType;
        /**
         * Process definition id.
         */
        private String processDefinitionId;
        /**
         * Process key (such as etalon id.)
         */
        private String processKey;
        /**
         * Task id.
         */
        private String taskId;
        /**
         * Candidate user.
         */
        private String candidateUser;
        /**
         * Assigned user.
         */
        private String assignedUser;
        /**
         * This combo is only used for active tasks queries.
         */
        private String candidateOrAssignee;
        /**
         * Starter user.
         */
        private String initiator;
        /**
         * Task approval state.
         */
        private ApprovalState approvalState;
        /**
         * Variables.
         */
        private Map<String, Object> variables;
        /**
         * Objects count.
         */
        private int count;
        /**
         * Page number, 0 based.
         */
        private int page;
        /**
         * Marks query as historical.
         */
        private boolean historical;
        /**
         * Task completed by.
         */
        private String taskCompletedBy;
        /**
         * Process start period boundary (after, before).
         */
        private Pair<Date, Date> processStart;
        /**
         * Task start period boundary (after, before).
         */
        private Pair<Date, Date> taskStart;
        /**
         * Task start period boundary (after, before).
         */
        private Pair<Date, Date> taskEnd;
        /**
         * Return total count only and no other results.
         */
        private boolean countOnly;

        private final List<String> candidateGroups = new ArrayList<>();

        /**
         * Constructor.
         */
        public GetTasksRequestContextBuilder() {
            super();
        }

        /**
         * @param processType the processType to set
         */
        public GetTasksRequestContextBuilder processType(WorkflowProcessType processType) {
            this.processType = processType;
            return this;
        }

        /**
         * @param processDefinitionId
         * @return
         */
        public GetTasksRequestContextBuilder processDefinitionId(String processDefinitionId) {
            this.processDefinitionId = processDefinitionId;
            return this;
        }
        /**
         * @param processKey the processKey to set
         */
        public GetTasksRequestContextBuilder processKey(String processKey) {
            this.processKey = processKey;
            return this;
        }

        /**
         * @param taskId the taskId to set
         */
        public GetTasksRequestContextBuilder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        /**
         * @param candidateUser the candidateUser to set
         */
        public GetTasksRequestContextBuilder candidateUser(String candidateUser) {
            this.candidateUser = candidateUser;
            return this;
        }

        /**
         * @param assignedUser the assignedUser to set
         */
        public GetTasksRequestContextBuilder assignedUser(String assignedUser) {
            this.assignedUser = assignedUser;
            return this;
        }

        /**
         * @param candidateOrAssignee the candidateOrAssignee to set
         */
        public GetTasksRequestContextBuilder candidateOrAssignee(String candidateOrAssignee) {
            this.candidateOrAssignee = candidateOrAssignee;
            return this;
        }

        /**
         * @param initiator the originator to set
         */
        public GetTasksRequestContextBuilder initiator(String initiator) {
            this.initiator = initiator;
            return this;
        }

        /**
         * @param approvalState the approvalState to set.
         */
        public GetTasksRequestContextBuilder approvalState(ApprovalState approvalState) {
            this.approvalState = approvalState;
            return this;
        }
        /**
         * @param variables sets the variables
         * @return self
         */
        public GetTasksRequestContextBuilder variables(Map<String, Object> variables) {
            this.variables = variables;
            return this;
        }
        /**
         * Sets page number, 0 based.
         * @param page the page
         * @return this
         */
        public GetTasksRequestContextBuilder page(int page) {
            this.page = page;
            return this;
        }
        /**
         * Sets the max count to return.
         * @param count the count
         * @return this
         */
        public GetTasksRequestContextBuilder count(int count) {
            this.count = count;
            return this;
        }
        /**
         * Sets historical flag to this context.
         * @param historical
         * @return slf
         */
        public GetTasksRequestContextBuilder historical(boolean historical) {
            this.historical = historical;
            return this;
        }
        /**
         * Task completed by.
         */
        public GetTasksRequestContextBuilder taskCompletedBy(String taskCompletedBy) {
            this.taskCompletedBy = taskCompletedBy;
            return this;
        }
        /**
         * Process start period boundary (after, before).
         */
        public GetTasksRequestContextBuilder processStart(Pair<Date, Date> processStart) {
            this.processStart = processStart;
            return this;
        }
        /**
         * Task start period boundary (after, before).
         */
        public GetTasksRequestContextBuilder taskStart(Pair<Date, Date> taskStart) {
            this.taskStart = taskStart;
            return this;
        }
        /**
         * Task start period boundary (after, before).
         */
        public GetTasksRequestContextBuilder taskEnd(Pair<Date, Date> taskEnd) {
            this.taskEnd = taskEnd;
            return this;
        }
        /**
         * Task start period boundary (after, before).
         */
        public GetTasksRequestContextBuilder countOnly(boolean countOnly) {
            this.countOnly = countOnly;
            return this;
        }

        public GetTasksRequestContextBuilder candidateGroups(Collection<String> assignments) {
            this.candidateGroups.clear();
            if (CollectionUtils.isNotEmpty(assignments)) {
                this.candidateGroups.addAll(assignments);
            }
            return this;
        }

        /**
         * Builder method.
         * @return new context
         */
        public GetTasksRequestContext build() {
            return new GetTasksRequestContext(this);
        }
    }
}
