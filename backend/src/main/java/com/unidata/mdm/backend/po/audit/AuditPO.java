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

package com.unidata.mdm.backend.po.audit;

import java.util.Date;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class AuditPO {
    /**
     * Field Audit ID.
     */
    public static final String FIELD_AUDIT_ID = "id";
    /**
     * Field create date.
     */
    public static final String FIELD_CREATE_DATE = "create_date";
    /**
     * Field created by.
     */
    public static final String FIELD_CREATED_BY = "created_by";
    /**
     * Field operation id.
     */
    public static final String FIELD_OPERATION_ID = "operation_id";
    /**
     * Field details.
     */
    public static final String FIELD_DETAILS = "details";
    /**
     * Field action.
     */
    public static final String FIELD_ACTION = "action";

    /**
     *  id
     */
    private Long id;
    /**
     * Create time stamp.
     */
    private Date createDate;
    /**
     * Created by.
     */
    private String createdBy;
    /**
     * Operation id
     */
    private String operationId;
    /**
     * Audit details
     */
    private String details;
    /**
     * Audit action
     */
    private String action;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
