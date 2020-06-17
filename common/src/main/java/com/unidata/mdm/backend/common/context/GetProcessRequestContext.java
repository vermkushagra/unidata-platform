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
     * Process instance id.
     */
    private final String processInstanceId;
    /**
     * Marks query as historical.
     */
    private final boolean historical;
    /**
     * Dont fetch process variables.
     */
    private final boolean skipVariables;
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
        this.processInstanceId = b.processInstanceId;
        this.historical = b.historical;
        this.skipVariables = b.skipVariables;
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
         * Builder method.
         * @return new immutable context
         */
        public GetProcessRequestContext build() {
            return new GetProcessRequestContext(this);
        }
    }
}
