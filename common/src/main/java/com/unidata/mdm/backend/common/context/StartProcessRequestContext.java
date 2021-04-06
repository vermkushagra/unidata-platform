/**
 *
 */
package com.unidata.mdm.backend.common.context;

import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Start process request context.
 */
public class StartProcessRequestContext extends CommonRequestContext {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -6009471888976992815L;

    /**
     * Process definition id.
     */
    private final String processDefinitionId;
    /**
     * Initiator.
     */
    private final String initiator;
    /**
     * Variables.
     */
    private final transient Map<String, Object> variables;
    /**
     * Process key (such as etalon id.)
     */
    private final String processKey;
    /**
     * Constructor.
     */
    private StartProcessRequestContext(StartProcessRequestContextBuilder b) {
        super();
        this.initiator = b.initiator;
        this.processDefinitionId = b.processDefinitionId;
        this.variables = b.variables;
        this.processKey = b.processKey;
    }


    /**
     * @return the process
     */
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }


    /**
     * @return the initiator
     */
    public String getInitiator() {
        return initiator;
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
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class StartProcessRequestContextBuilder {
        /**
         * Process definition id.
         */
        private String processDefinitionId;
        /**
         * Initiator.
         */
        private String initiator;
        /**
         * Variables.
         */
        private Map<String, Object> variables;
        /**
         * Process key (such as etalon id.)
         */
        private String processKey;
        /**
         * Constructor.
         */
        public StartProcessRequestContextBuilder() {
            super();
        }

        /**
         * @param process the process to set
         */
        public StartProcessRequestContextBuilder processDefinitionId(String processDefinitionId) {
            this.processDefinitionId = processDefinitionId;
            return this;
        }

        /**
         * @param initiator the initiator to set
         */
        public StartProcessRequestContextBuilder initiator(String initiator) {
            this.initiator = initiator;
            return this;
        }

        /**
         * @param variables the variables to set
         */
        public StartProcessRequestContextBuilder variables(Map<String, Object> variables) {
            this.variables = variables;
            return this;
        }

        /**
         * @param processKey the processKey to set
         */
        public StartProcessRequestContextBuilder processKey(String processKey) {
            this.processKey = processKey;
            return this;
        }

        /**
         * Builder method.
         * @return new context.
         */
        public StartProcessRequestContext build() {
            return new StartProcessRequestContext(this);
        }
    }
}
