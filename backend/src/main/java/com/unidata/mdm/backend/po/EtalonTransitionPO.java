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
package com.unidata.mdm.backend.po;

import java.util.Date;

import com.unidata.mdm.backend.service.data.merge.TransitionType;

/**
 * @author Mikhail Mikhailov
 * Etalon transition point.
 */
public class EtalonTransitionPO {
    /**
     * Table name.
     */
    public static final String TABLE_NAME = "etalons_transitions";
    /**
     * ID.
     */
    public static final String FIELD_ID = "id";
    /**
     * Etalon id.
     */
    public static final String FIELD_ETALON_ID = "etalon_id";
    /**
     * Operation id.
     */
    public static final String FIELD_OPERATION_ID = "operation_id";
    /**
     * Merge point type.
     */
    public static final String FIELD_TYPE = "type";
    /**
     * Revision number.
     */
    public static final String FIELD_REVISION = "revision";
    /**
     * Create date.
     */
    public static final String FIELD_CREATE_DATE = "create_date";
    /**
     * Created by.
     */
    public static final String FIELD_CREATED_BY = "created_by";
    /**
     * Record id.
     */
    private String id;
    /**
     * Etalon id.
     */
    private String etalonId;
    /**
     * Operation id.
     */
    private String operationId;
    /**
     * Type.
     */
    private TransitionType type;
    /**
     * Revision of the merge point of the record.
     */
    private int revision;
    /**
     * Create time stamp.
     */
    protected Date createDate;
    /**
     * Created by.
     */
    protected String createdBy;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the masterId
     */
    public String getEtalonId() {
        return etalonId;
    }

    /**
     * @param masterId the masterId to set
     */
    public void setEtalonId(String masterId) {
        this.etalonId = masterId;
    }

    /**
     * @return the operationId
     */
    public String getOperationId() {
        return operationId;
    }


    /**
     * @param operationId the operationId to set
     */
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    /**
     * @return the type
     */
    public TransitionType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(TransitionType type) {
        this.type = type;
    }

    /**
     * @return the revision
     */
    public int getRevision() {
        return revision;
    }

    /**
     * @param revision the revision to set
     */
    public void setRevision(int revision) {
        this.revision = revision;
    }

    /**
     * Gets create date.
     * @return date
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Sets create date.
     * @param createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * Gets created by.
     * @return
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets created by.
     * @param createdBy
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}