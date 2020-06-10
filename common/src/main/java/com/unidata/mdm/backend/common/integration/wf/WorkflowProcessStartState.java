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
 * Process start state.
 */
public class WorkflowProcessStartState {
    /**
     * Tells the caller, whether it is allowed to start the process.
     */
    private boolean allow;
    /**
     * Describe denial or acceptance reason.
     */
    private String message;
    /**
     * Additional process variables.
     */
    private Map<String, Object> additionalProcessVariables;
    /**
     * Constructor.
     */
    public WorkflowProcessStartState() {
        super();
    }
    /**
     * Constructor.
     */
    public WorkflowProcessStartState(boolean allow, String message) {
        super();
        this.allow = allow;
        this.message = message;
    }
    /**
     * @return the allow
     */
    public boolean isAllowed() {
        return allow;
    }
    /**
     * @param allow the allow to set
     */
    public void setAllow(boolean allow) {
        this.allow = allow;
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
}
