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

import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Complete task(s) context.
 */
public class CompleteTaskRequestContext extends CommonRequestContext {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -8808884483743727338L;

    /**
     * Process definition key.
     */
    private final String processDefinitionKey;
    /**
     * Variables.
     */
    private final transient Map<String, Object> variables;
    /**
     * Process key (such as etalon id.)
     */
    private final String processKey;
    /**
     * Task id.
     */
    private final String taskId;
    /**
     * Complete action.
     */
    private final String action;

    /**
     * Constructor.
     */
    private CompleteTaskRequestContext(CompleteTaskRequestContextBuilder b) {
        super();
        this.processDefinitionKey = b.processDefinitionKey;
        this.variables = b.variables;
        this.processKey = b.processKey;
        this.taskId = b.taskId;
        this.action = b.action;
    }

    /**
     * @return the process definition key
     */
    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    /**
     * @return the variables
     */
    public Map<String, Object> getVariables() {
        return variables;
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
     * @return the action
     */
    public String getAction() {
        return action;
    }
    /**
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class CompleteTaskRequestContextBuilder {
        /**
         * Process key.
         */
        private String processDefinitionKey;
        /**
         * Variables.
         */
        private Map<String, Object> variables;
        /**
         * Process key (such as etalon id.)
         */
        private String processKey;
        /**
         * Task id.
         */
        private String taskId;
        /**
         * Complete action.
         */
        private String action;
        /**
         * Constructor.
         */
        public CompleteTaskRequestContextBuilder() {
            super();
        }
        /**
         * @param processDefinitionKey the process definition key to set
         */
        public CompleteTaskRequestContextBuilder processDefinitionKey(String processDefinitionKey) {
            this.processDefinitionKey = processDefinitionKey;
            return this;
        }

        /**
         * @param variables the variables to set
         */
        public CompleteTaskRequestContextBuilder variables(Map<String, Object> variables) {
            this.variables = variables;
            return this;
        }

        /**
         * @param processKey the processKey to set
         */
        public CompleteTaskRequestContextBuilder processKey(String processKey) {
            this.processKey = processKey;
            return this;
        }
        /**
         * @param taskId the taskId to set
         */
        public CompleteTaskRequestContextBuilder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }
        /**
         * @param action the action to set
         */
        public CompleteTaskRequestContextBuilder action(String action) {
            this.action = action;
            return this;
        }
        /**
         * Builder method.
         * @return new context.
         */
        public CompleteTaskRequestContext build() {
            return new CompleteTaskRequestContext(this);
        }
    }
}
