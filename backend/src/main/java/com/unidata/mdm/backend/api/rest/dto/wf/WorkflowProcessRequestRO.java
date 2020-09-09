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

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.Map;

/**
 * @author Dmitry Kopin
 * Workflow processes request.
 */
public class WorkflowProcessRequestRO {
    /**
     * Process initiator.
     */
    private String initiator;
    /**
     * Process involved
     */
    private String involved;
    /**
     * Status query
     */
    private Status status;
    /**
     * Don't fetch process variables.
     */
    private boolean skipVariables;
    /**
     * Process started after.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
    private Date processStartAfter;
    /**
     * Process started before.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
    private Date processStartBefore;
    /**
     * Variables for filter
     */
    private Map<String, Object> variables;
    /**
     * Return count.
     */
    private int count = 10;
    /**
     * Return page.
     */
    private int page = 0;
    /**
     * Constructor.
     */
    public WorkflowProcessRequestRO() {
        super();
    }

    /**
     * @return the initiator
     */
    public String getInitiator() {
        return initiator;
    }

    /**
     * @param initiator the initiator to set
     */
    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    /**
     * Process involved
     */
    public String getInvolved() {
        return involved;
    }

    public void setInvolved(String involved) {
        this.involved = involved;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }

    public boolean isSkipVariables() {
        return skipVariables;
    }

    public void setSkipVariables(boolean skipVariables) {
        this.skipVariables = skipVariables;
    }

    /**
     * Process started after.
     */
    public Date getProcessStartAfter() {
        return processStartAfter;
    }

    public void setProcessStartAfter(Date processStartAfter) {
        this.processStartAfter = processStartAfter;
    }

    /**
     * Process started before.
     */
    public Date getProcessStartBefore() {
        return processStartBefore;
    }

    public void setProcessStartBefore(Date processStartBefore) {
        this.processStartBefore = processStartBefore;
    }

    /**
     * Variables.
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    /**
     * finished status
     */
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        ALL, COMPLETED, DECLINED, RUNNING
    }
}
