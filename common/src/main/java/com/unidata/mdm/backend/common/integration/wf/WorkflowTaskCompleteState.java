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

package com.unidata.mdm.backend.common.integration.wf;

import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Completeness state for workflow tasks.
 */
public class WorkflowTaskCompleteState {

    /**
     * Signal complete or not.
     */
    private boolean complete;
    /**
     * Describe denial or acceptance reason.
     */
    private String message;
    /**
     * Additional process variables.
     */
    private Map<String, Object> additionalProcessVariables;
    /**
     * Additional task variables.
     */
    private Map<String, Object> additionalTaskVariables;

    /**
     * Constructor.
     */
    public WorkflowTaskCompleteState() {
        super();
    }

    /**
     * Constructor.
     */
    public WorkflowTaskCompleteState(boolean complete, String message) {
        super();
        this.complete = complete;
        this.message = message;
    }

    /**
     * @return the complete
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * @param complete the complete to set
     */
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the additionalProcessVariables
     */
    public Map<String, Object> getAdditionalProcessVariables() {
        return additionalProcessVariables;
    }

    /**
     * @param additionalProcessVariables the additionalProcessVariables to set
     */
    public void setAdditionalProcessVariables(Map<String, Object> additionalProcessVariables) {
        this.additionalProcessVariables = additionalProcessVariables;
    }

    /**
     * @return the additionalTaskVariables
     */
    public Map<String, Object> getAdditionalTaskVariables() {
        return additionalTaskVariables;
    }

    /**
     * @param additionalTaskVariables the additionalTaskVariables to set
     */
    public void setAdditionalTaskVariables(Map<String, Object> additionalTaskVariables) {
        this.additionalTaskVariables = additionalTaskVariables;
    }
}
