package com.unidata.mdm.backend.api.rest.dto.cleanse;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionDefinition;

// TODO: Auto-generated Javadoc
/**
 * The Class CFCustomUploaderResponse.
 */
public class CFCustomUploaderResponse {

    /** The temporary id. */
    private String temporaryId;
    /** The status. */
    private CFSaveStatus status;

    /** The functions. */
    private List<CFFunction> functions;

    /**
     * Gets the status.
     *
     * @return the status
     */
    public CFSaveStatus getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status
     *            the new status
     */
    public void setStatus(CFSaveStatus status) {
        this.status = status;
    }

    /**
     * Gets the functions.
     *
     * @return the functions
     */
    public List<CFFunction> getFunctions() {
        return functions;
    }

    /**
     * Gets the temporary id.
     *
     * @return the temporary id
     */
    public String getTemporaryId() {
        return temporaryId;
    }

    /**
     * Sets the temporary id.
     *
     * @param temporaryId
     *            the new temporary id
     */
    public void setTemporaryId(String temporaryId) {
        this.temporaryId = temporaryId;
    }

    /**
     * Sets the functions.
     *
     * @param functions
     *            the new functions
     */
    public void setFunctions(List<CFFunction> functions) {
        this.functions = functions;
    }

    /**
     * Adds the function.
     *
     * @param function
     *            the function
     */
    public void addFunction(CFFunction function) {
        if (this.functions == null) {
            this.functions = new ArrayList<CFCustomUploaderResponse.CFFunction>();
        }
        this.functions.add(function);
    }

    /**
     * The Class CFFunction.
     */
    public class CFFunction {

        /** The name. */
        private String name;

        /** The state. */
        private CFState state;
        private CleanseFunctionDefinition definition;

        /**
         * Gets the name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name.
         *
         * @param name
         *            the new name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the state.
         *
         * @return the state
         */
        public CFState getState() {
            return state;
        }

        /**
         * Sets the state.
         *
         * @param state
         *            the new state
         */
        public void setState(CFState state) {
            this.state = state;
        }

        /**
         * @return the definition
         */
        public CleanseFunctionDefinition getDefinition() {
            return definition;
        }

        /**
         * @param definition
         *            the definition to set
         */
        public void setDefinition(CleanseFunctionDefinition definition) {
            this.definition = definition;
        }
    }
}
