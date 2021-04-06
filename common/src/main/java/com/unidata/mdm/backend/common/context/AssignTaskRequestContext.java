/**
 *
 */
package com.unidata.mdm.backend.common.context;

import java.util.Map;

/**
 * Assign task context.
 * @author Denis Kostovarov
 */
public class AssignTaskRequestContext extends CommonRequestContext {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -8808887345297607338L;
    /**
     * Variables.
     */
    private final transient Map<String, Object> variables;
    /**
     * Task id.
     */
    private final String taskId;
    /**
     * Username.
     */
    private final String username;

    /**
     * Constructor.
     */
    private AssignTaskRequestContext(AssignTaskRequestContextBuilder b) {
        super();
        this.variables = b.variables;
        this.taskId = b.taskId;
        this.username = b.username;
    }

    /**
     * @return the variables
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Builder class.
     * @author Denis Kostovarov
     */
    public static class AssignTaskRequestContextBuilder {
        /**
         * Variables.
         */
        private Map<String, Object> variables;
        /**
         * Task id.
         */
        private String taskId;
        /**
         * Username.
         */
        private String username;
        /**
         * Constructor.
         */
        public AssignTaskRequestContextBuilder() {
            super();
        }

        /**
         * @param variables the variables to set
         */
        public AssignTaskRequestContextBuilder variables(Map<String, Object> variables) {
            this.variables = variables;
            return this;
        }

        /**
         * @param taskId the taskId to set
         */
        public AssignTaskRequestContextBuilder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        /**
         * @param username username to set.
         */
        public AssignTaskRequestContextBuilder username(String username) {
            this.username = username;
            return this;
        }

        /**
         * Builder method.
         * @return new context.
         */
        public AssignTaskRequestContext build() {
            return new AssignTaskRequestContext(this);
        }
    }
}
