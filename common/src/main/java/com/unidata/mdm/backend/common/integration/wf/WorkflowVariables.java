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
package com.unidata.mdm.backend.common.integration.wf;


/**
 * @author Mikhail Mikhailov
 * Workflow variables.
 */
public enum WorkflowVariables {
    /**
     * Etalon record field.
     */
    VAR_PROCESS_TYPE("record"),
    /**
     * Process trigger type.
     */
    VAR_PROCESS_TRIGGER_TYPE("processTriggerType"),
    /**
     * Etalon id.
     */
    VAR_ETALON_ID("etalonId"),
    /**
     * Etalon record title.
     */
    VAR_ETALON_RECORD_TITLE("recordTitle"),
    /**
     * Process initiator.
     */
    VAR_INITIATOR("initiator"),
    /**
     * Initiator's email.
     */
    VAR_INITIATOR_EMAIL("initiatorEmail"),
    /**
     * Process initiator name.
     */
    VAR_INITIATOR_NAME("initiatorName"),
    /**
     * General date field.
     */
    VAR_DATE("date"),
    /**
     * General from field.
     */
    VAR_FROM("from"),
    /**
     * General to field.
     */
    VAR_TO("to"),
    /**
     * Approval state.
     */
    VAR_APPROVAL_STATE("approvalState"),
    /**
     * Published state.
     */
    VAR_PUBLISHED_STATE("publishedState"),
    /**
     * Entity name.
     */
    VAR_ENTITY_NAME("entityName"),
    /**
     * Entity type.
     */
    VAR_ENTITY_TYPE("entityType"),
    /**
     * Entity type title.
     */
    VAR_ENTITY_TYPE_TITLE("entityTypeTitle"),
    /**
     * Task completed by flag.
     */
    VAR_TASK_COMPLETED_BY("taskCompletedBy"),
    /**
     * Operation Id.
     */
    VAR_OPERATION_ID("operationId"),
    /**
     * Delete period flag.
     */
    VAR_DELETE_PERIOD_OPERATION("deletePeriod"),
    /**
     * WF create date.
     */
    VAR_WF_CREATE_DATE("wfCreateDate"),
    /**
     * Completed by (login name).
     */
    VAR_COMPLETED_BY("CompletedBy"),
    /**
     * First name + Last name.
     */
    VAR_COMPLETED_BY_NAME("CompletedByName"),
    /**
     * Completed by email.
     */
    VAR_COMPLETED_BY_EMAIL("CompletedByEmail"),
    /**
     * Complete timestamp.
     */
    VAR_COMPLETED_TIMSETAMP("CompletedTimestamp"),
    /**
     * Task name.
     */
    VAR_TASK_NAME("taskName"),
    /**
     * Process name.
     */
    VAR_PROCESS_NAME("processName");
    /**
     * Field value
     */
    private final String value;

    /**
     * Constructor.
     */
    private WorkflowVariables(String value) {
        this.value = value;
    }


    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

}
