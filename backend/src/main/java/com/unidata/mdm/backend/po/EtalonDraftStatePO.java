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

package com.unidata.mdm.backend.po;

import java.util.Date;

import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Etalon states drafts.
 */
public class EtalonDraftStatePO {
    /**
     * Table name.
     */
    public static final String TABLE_NAME = "etalons_draft_states";
    /**
     * ID.
     */
    public static final String FIELD_ID = "id";
    /**
     * Etalon id.
     */
    public static final String FIELD_ETALON_ID = "etalon_id";
    /**
     * Revision number.
     */
    public static final String FIELD_REVISION = "revision";
    /**
     * Status.
     */
    public static final String FIELD_STATUS = "status";
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
    private Integer id;
    /**
     * Etalon id.
     */
    private String etalonId;
    /**
     * Revision of the merge point of the record.
     */
    private int revision;
    /**
     * Status.
     */
    private RecordStatus status;
    /**
     * Create time stamp.
     */
    private Date createDate;
    /**
     * Created by.
     */
    private String createdBy;

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
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
    public RecordStatus getStatus() {
        return status;
    }


    /**
     * @param status the status to set
     */
    public void setStatus(RecordStatus status) {
        this.status = status;
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
