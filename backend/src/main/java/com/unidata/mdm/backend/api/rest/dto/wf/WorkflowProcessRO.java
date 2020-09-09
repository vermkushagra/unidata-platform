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

package com.unidata.mdm.backend.api.rest.dto.wf;


import java.util.Map;

/**
 * @author Dmitry Kopin on 14.05.2018.
 */
public class WorkflowProcessRO {

    private String processId;
    /**
     * Process title.
     */
    private String processTitle;
    /**
     * Process type.
     */
    private String processType;
    /**
     * Process type name
     */
    private String processTypeName;
    /**
     * Trigger type.
     */
    private String triggerType;
    /**
     * Process definition id.
     */
    private String processDefinitionId;
    /**
     * Originator user login.
     */
    private String originator;
    /**
     * Originator user full name.
     */
    private String originatorName;
    /**
     * Originator email.
     */
    private String originatorEmail;
    /**
     * Process finished or not.
     */
    private boolean processFinished;
    /**
     * Process suspended.
     */
    private boolean suspended;

    /**
     * Variables.
     */
    private Map<String, Object> variables = null;


    /**
     * Process title.
     */
    public String getProcessTitle() {
        return processTitle;
    }

    public void setProcessTitle(String processTitle) {
        this.processTitle = processTitle;
    }

    /**
     * Process definition id.
     */
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    /**
     * Process suspended.
     */
    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    /**
     * @return the variables
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    /**
     * @param variables the variables to set
     */
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    /**
     * Process type.
     */
    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    /**
     * Originator user name.
     */
    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    /**
     * Originator user full name.
     */
    public String getOriginatorName() {
        return originatorName;
    }

    public void setOriginatorName(String originatorName) {
        this.originatorName = originatorName;
    }

    /**
     * Originator email.
     */
    public String getOriginatorEmail() {
        return originatorEmail;
    }

    public void setOriginatorEmail(String originatorEmail) {
        this.originatorEmail = originatorEmail;
    }

    /**
     * Process finished or not.
     */
    public boolean isProcessFinished() {
        return processFinished;
    }

    public void setProcessFinished(boolean processFinished) {
        this.processFinished = processFinished;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    /**
     * Trigger type.
     */
    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    /**
     * Process type.
     */
    public String getProcessTypeName() {
        return processTypeName;
    }

    public void setProcessTypeName(String processTypeName) {
        this.processTypeName = processTypeName;
    }
}
