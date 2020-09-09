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
