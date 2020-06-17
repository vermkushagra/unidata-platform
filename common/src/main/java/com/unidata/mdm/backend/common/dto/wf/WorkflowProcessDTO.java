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

package com.unidata.mdm.backend.common.dto.wf;

import com.unidata.mdm.backend.common.integration.wf.WorkflowProcessTriggerType;
import com.unidata.mdm.conf.WorkflowProcessType;

/**
 * @author Mikhail Mikhailov
 *
 */
public class WorkflowProcessDTO {

    /**
     * Instance id.
     */
    private String processInstanceId;
    /**
     * Process title.
     */
    private String processTitle;
    /**
     * Process definition id.
     */
    private String processDefinitionId;
    /**
     * Process type.
     */
    private WorkflowProcessType processType;
    /**
     * Trigger type.
     */
    private WorkflowProcessTriggerType triggerType;
    /**
     * Process suspended.
     */
    private boolean suspended;
    /**
     * Process ended.
     */
    private boolean ended;
    /**
     * Constructor.
     */
    public WorkflowProcessDTO() {
        super();
    }

    /**
     * @return the processInstanceId
     */
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    /**
     * @param processInstanceId the processInstanceId to set
     */
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    /**
     * @return the processTitle
     */
    public String getProcessTitle() {
        return processTitle;
    }

    /**
     * @param processTitle the processTitle to set
     */
    public void setProcessTitle(String processTitle) {
        this.processTitle = processTitle;
    }

    /**
     * @return the processType
     */
    public WorkflowProcessType getProcessType() {
        return processType;
    }

    /**
     * @param processType the processType to set
     */
    public void setProcessType(WorkflowProcessType processType) {
        this.processType = processType;
    }



    /**
     * @return the triggerType
     */
    public WorkflowProcessTriggerType getTriggerType() {
        return triggerType;
    }

    /**
     * @param triggerType the triggerType to set
     */
    public void setTriggerType(WorkflowProcessTriggerType triggerType) {
        this.triggerType = triggerType;
    }

    /**
     * @return the processDefinitionId
     */
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }


    /**
     * @param processDefinitionId the processDefinitionId to set
     */
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    /**
     * @return the suspended
     */
    public boolean isSuspended() {
        return suspended;
    }


    /**
     * @param suspended the suspended to set
     */
    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }


    /**
     * @return the ended
     */
    public boolean isEnded() {
        return ended;
    }


    /**
     * @param ended the ended to set
     */
    public void setEnded(boolean ended) {
        this.ended = ended;
    }

}
