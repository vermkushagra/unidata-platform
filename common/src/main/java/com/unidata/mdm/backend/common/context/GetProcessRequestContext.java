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

import com.unidata.mdm.conf.WorkflowProcessType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Identify process context.
 */
public class GetProcessRequestContext extends CommonRequestContext {

    /**
     * Process.
     */
    private final WorkflowProcessType processType;
    /**
     * Process.
     */
    private final String processDefinitionId;
    /**
     * Process key (such as etalon id.)
     */
    private final String processKey;
    /**
     * Return only suspended process instances.
     */
    private final boolean suspended;
    /**
     * Return only finished process instances.
     */
    private final Status status;
    /**
     * Process instance id.
     */
    private final String processInstanceId;
    /**
     * Marks query as historical.
     */
    private final boolean historical;
    /**
     * Process start period boundary (after, before).
     */
    private final Pair<Date, Date> processStart;
    /**
     * Dont fetch process variables.
     */
    private final boolean skipVariables;
    /**
     * Starter user.
     */
    private final String initiator;
    /**
     * Process involved
     */
    private String involved;
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
     *
     */
    private final List<String> assignments = new ArrayList<>();

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 3605074953289163186L;

    /**
     * Constructor.
     */
    private GetProcessRequestContext(GetProcessRequestContextBuilder b) {
        super();
        this.processType = b.processType;
        this.processDefinitionId = b.processDefinitionId;
        this.processKey = b.processKey;
        this.suspended = b.suspended;
        this.status = b.status;
        this.processInstanceId = b.processInstanceId;
        this.historical = b.historical;
        this.skipVariables = b.skipVariables;
        this.initiator = b.initiator;
        this.page = b.page;
        this.count = b.count;
        this.variables = b.variables;
        this.processStart = b.processStart;
        this.involved = b.involved;
        this.assignments.addAll(b.assignments);
    }

    /**
     * @return the process
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
     * @return the process status
     */
    public Status getStatus() {
        return status;
    }


    /**
     * @return the suspended
     */
    public boolean isSuspended() {
        return suspended;
    }

    /**
     * @return the historical
     */
    public boolean isHistorical() {
        return historical;
    }

    /**
     * @return the processInstanceId
     */
    public String getProcessInstanceId() {
        return processInstanceId;
    }


    /**
     * @return the skipVariables
     */
    public boolean isSkipVariables() {
        return skipVariables;
    }

    /**
     * Starter user.
     */
    public String getInitiator() {
        return initiator;
    }

    /**
     * Starter user.
     */
    public String getInvolved() {
        return involved;
    }

    /**
     * @return the variables
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    /**
     * @return the processStart
     */
    public Pair<Date, Date> getProcessStart() {
        return processStart;
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

    public List<String> getAssignments() {
        return Collections.unmodifiableList(assignments);
    }

    public void setAssignments(Collection<String> assignments) {
        this.assignments.clear();
        if (CollectionUtils.isNotEmpty(assignments)) {
            this.assignments.addAll(assignments);
        }
    }

    public static GetProcessRequestContextBuilder builder(){
        return new GetProcessRequestContextBuilder();
    }


    /**
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class GetProcessRequestContextBuilder {
        /**
         * Process type.
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
         * Return only suspended process instances.
         */
        private boolean suspended;
        /**
         * Process status
         */
        private Status status;
        /**
         * Process instance id.
         */
        private String processInstanceId;
        /**
         * Marks query as historical.
         */
        private boolean historical;
        /**
         * Dont fetch process variables.
         */
        private boolean skipVariables;
        /**
         * Starter user.
         */
        private String initiator;
        /**
         * Involved user.
         */
        private String involved;
        /**
         * Objects count.
         */
        private int count;
        /**
         * Page number, 0 based.
         */
        private int page;
        /**
         * Variables.
         */
        private Map<String, Object> variables;
        /**
         * Process start period boundary (after, before).
         */
        private Pair<Date, Date> processStart;

        private final List<String> assignments = new ArrayList<>();


        /**
         * Constructor.
         */
        public GetProcessRequestContextBuilder() {
            super();
        }

        /**
         * @param processType the process to set
         */
        public GetProcessRequestContextBuilder process(WorkflowProcessType processType) {
            this.processType = processType;
            return this;
        }

        /**
         * @param processDefinitionId the process to set
         */
        public GetProcessRequestContextBuilder processDefinitionId(String processDefinitionId) {
            this.processDefinitionId = processDefinitionId;
            return this;
        }

        /**
         * @param processKey the processKey to set
         */
        public GetProcessRequestContextBuilder processKey(String processKey) {
            this.processKey = processKey;
            return this;
        }

        /**
         * @param suspended the suspended to set
         * @return self
         */
        public GetProcessRequestContextBuilder suspended(boolean suspended) {
            this.suspended = suspended;
            return this;
        }

        /**
         * @param status to set
         * @return self
         */
        public GetProcessRequestContextBuilder status(Status status) {
            this.status = status;
            return this;
        }

        /**
         * @param processInstanceId the processInstanceId to set
         * @return self
         */
        public GetProcessRequestContextBuilder processInstanceId(String processInstanceId) {
            this.processInstanceId = processInstanceId;
            return this;
        }

        /**
         * Sets historical flag to this context.
         * @param historical
         * @return self
         */
        public GetProcessRequestContextBuilder historical(boolean historical) {
            this.historical = historical;
            return this;
        }


        public GetProcessRequestContextBuilder initiator(String initiator) {
            this.initiator = initiator;
            return this;
        }

        public GetProcessRequestContextBuilder involved(String involved) {
            this.involved = involved;
            return this;
        }

        /**
         * Sets page number, 0 based.
         * @param page the page
         * @return this
         */
        public GetProcessRequestContextBuilder page(int page) {
            this.page = page;
            return this;
        }
        /**
         * Sets the max count to return.
         * @param count the count
         * @return this
         */
        public GetProcessRequestContextBuilder count(int count) {
            this.count = count;
            return this;
        }

        /**
         * Sets skipVariables flag to this context.
         * @param skipVariables
         * @return self
         */
        public GetProcessRequestContextBuilder skipVariables(boolean skipVariables) {
            this.skipVariables = skipVariables;
            return this;
        }

        /**
         * Process start period boundary (after, before).
         */
        public GetProcessRequestContextBuilder processStart(Pair<Date, Date> processStart) {
            this.processStart = processStart;
            return this;
        }
        /**
         * @param variables sets the variables
         * @return self
         */
        public GetProcessRequestContextBuilder variables(Map<String, Object> variables) {
            this.variables = variables;
            return this;
        }

        public List<String> getAssignments() {
            return Collections.unmodifiableList(assignments);
        }

        public void setAssignments(Collection<String> assignments) {
            this.assignments.clear();
            if (CollectionUtils.isNotEmpty(assignments)) {
                this.assignments.addAll(assignments);
            }
        }

        /**
         * Builder method.
         * @return new immutable context
         */
        public GetProcessRequestContext build() {
            return new GetProcessRequestContext(this);
        }
    }

    public enum Status {
        ALL, COMPLETED, DECLINED, RUNNING, FINISHED
    }
}
