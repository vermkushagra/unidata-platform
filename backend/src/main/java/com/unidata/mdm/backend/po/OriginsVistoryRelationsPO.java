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

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.VistoryOperationType;

/**
 * @author Mikhail Mikhailov
 * Relations vistory record.
 */
public class OriginsVistoryRelationsPO {
    /**
     * Table name.
     */
    public static final String TABLE_NAME = "origins_relations_vistory";
    /**
     * Id.
     */
    public static final String FIELD_ID = "id";
    /**
     * Origin id.
     */
    public static final String FIELD_ORIGIN_ID = "origin_id";
    /**
     * Operation id.
     */
    public static final String FIELD_OPERATION_ID = "operation_id";
    /**
     * Created by (created by of the version).
     */
    public static final String FIELD_SOURCE_SYSTEM = "source_system";
    /**
     * Origin id from.
     */
    public static final String FIELD_ORIGIN_ID_FROM = "origin_id_from";
    /**
     * Origin from status.
     */
    public static final String FIELD_ORIGIN_FROM_STATUS = "origin_from_status";
    /**
     * Origin from external id.
     */
    public static final String FIELD_ORIGIN_FROM_EXTERNAL_ID = "origin_from_external_id";
    /**
     * Origin from name.
     */
    public static final String FIELD_ORIGIN_FROM_NAME = "origin_from_name";
    /**
     * Origin from source system.
     */
    public static final String FIELD_ORIGIN_FROM_SOURCE_SYSTEM = "origin_from_source_system";
    /**
     * Origin id to.
     */
    public static final String FIELD_ORIGIN_ID_TO = "origin_id_to";
    /**
     * Origin to status.
     */
    public static final String FIELD_ORIGIN_TO_STATUS = "origin_to_status";
    /**
     * Origin to external id.
     */
    public static final String FIELD_ORIGIN_TO_EXTERNAL_ID = "origin_to_external_id";
    /**
     * Origin to name.
     */
    public static final String FIELD_ORIGIN_TO_NAME = "origin_to_name";
    /**
     * Origin to source system.
     */
    public static final String FIELD_ORIGIN_TO_SOURCE_SYSTEM = "origin_to_source_system";
    /**
     * Revision number.
     */
    public static final String FIELD_REVISION = "revision";
    /**
     * Valid from.
     */
    public static final String FIELD_VALID_FROM = "valid_from";
    /**
     * Valid to.
     */
    public static final String FIELD_VALID_TO = "valid_to";
    /**
     * JAXB data.
     */
    public static final String FIELD_DATA_A = "data_a";
    /**
     * Binary data.
     */
    public static final String FIELD_DATA_B = "data_b";
    /**
     * Create date (create date of the origin record).
     */
    public static final String FIELD_CREATE_DATE = "create_date";
    /**
     * Created by (created by of the origin record).
     */
    public static final String FIELD_CREATED_BY = "created_by";
    /**
     * Update date (create date of the version).
     */
    public static final String FIELD_UPDATE_DATE = "update_date";
    /**
     * Created by (created by of the version).
     */
    public static final String FIELD_UPDATED_BY = "updated_by";
    /**
     * Status.
     */
    public static final String FIELD_STATUS = "status";
    /**
     * Approval state.
     */
    public static final String FIELD_APPROVAL = "approval";
    /**
     * Major data API number.
     */
    public static final String FIELD_MAJOR = "major";
    /**
     * Minor data API number.
     */
    public static final String FIELD_MINOR = "minor";
    /**
     * Data shift of the record.
     * May be one of {@linkplain DataState.PRISTINE} or {@linkplain DataState.REVISED}.
     */
    public static final String FIELD_SHIFT = "shift";
    /**
     * Relation etalon id.
     */
    public static final String FIELD_RELATION_ETALON_ID = "relation_etalon_id";
    /**
     * Read only type name.
     */
    public static final String FIELD_NAME = "name";
    /**
     * Vistory operation type.
     */
    public static final String FIELD_OPERATION_TYPE = "operation_type";
    /**
     * Record id.
     */
    private String id;
    /**
     * Origin id.
     */
    private String originId;
    /**
     * Operation id.
     */
    private String operationId;
    /**
     * Relation etalon id.
     */
    private String relationEtalonId;
    /**
     * Revision number.
     */
    private int revision;
    /**
     * Valid from.
     */
    private Date validFrom;
    /**
     * Valid to.
     */
    private Date validTo;
    /**
     * Data.
     */
    private DataRecord data;
    /**
     * Create date.
     */
    private Date createDate;
    /**
     * Created by.
     */
    private String createdBy;
    /**
     * Update date.
     */
    private Date updateDate;
    /**
     * Updated by.
     */
    private String updatedBy;
    /**
     * Source system (read only).
     */
    private String sourceSystem;
    /**
     * Type name (read only).
     */
    private String name;
    /**
     * Status.
     */
    private RecordStatus status;
    /**
     * Approval state.
     */
    private ApprovalState approval;
    /**
     * Data state.
     */
    private DataShift shift;
    /**
     * Major number.
     */
    private int major;
    /**
     * Minor number.
     */
    private int minor;
    /**
     * Origin id from.
     */
    private String originIdFrom;
    /**
     * Origin from external id.
     */
    private String originFromExternalId;
    /**
     * Origin from name.
     */
    private String originFromName;
    /**
     * Origin from source system.
     */
    private String originFromSourceSystem;
    /**
     * Origin id to.
     */
    private String originIdTo;
    /**
     * Origin to external id.
     */
    private String originToExternalId;
    /**
     * Origin to name.
     */
    private String originToName;
    /**
     * Origin to source system.
     */
    private String originToSourceSystem;
    /**
     * VistoryOperationType
     */
    private VistoryOperationType operationType;
    /**
     * Constructor.
     */
    public OriginsVistoryRelationsPO() {
        super();
    }

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
     * @return the originId
     */
    public String getOriginId() {
        return originId;
    }

    /**
     * @param originId the originId to set
     */
    public void setOriginId(String originId) {
        this.originId = originId;
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
     * @return the relationEtalonId
     */
    public String getRelationEtalonId() {
        return relationEtalonId;
    }

    /**
     * @param relationEtalonId the relationEtalonId to set
     */
    public void setRelationEtalonId(String relationEtalonId) {
        this.relationEtalonId = relationEtalonId;
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
     * @return the validFrom
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the validTo
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * @param validTo the validTo to set
     */
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    /**
     * @return the data
     */
    public DataRecord getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(DataRecord data) {
        this.data = data;
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the updateDate
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @param updateDate the updateDate to set
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * @return the updatedBy
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * @return the sourceSystem
     */
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * @param sourceSystem the sourceSystem to set
     */
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the status
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
     * @return the approval
     */
    public ApprovalState getApproval() {
        return approval;
    }

    /**
     * @param approval the approval to set
     */
    public void setApproval(ApprovalState approval) {
        this.approval = approval;
    }

    /**
     * @return the shift
     */
    public DataShift getShift() {
        return shift;
    }

    /**
     * @param dataShift the shift to set
     */
    public void setShift(DataShift dataState) {
        this.shift = dataState;
    }

    /**
     * @return the major
     */
    public int getMajor() {
        return major;
    }

    /**
     * @param major the major to set
     */
    public void setMajor(int major) {
        this.major = major;
    }

    /**
     * @return the minor
     */
    public int getMinor() {
        return minor;
    }

    /**
     * @param minor the minor to set
     */
    public void setMinor(int minor) {
        this.minor = minor;
    }

    /**
     * @return the originIdFrom
     */
    public String getOriginIdFrom() {
        return originIdFrom;
    }

    /**
     * @param originIdFrom the originIdFrom to set
     */
    public void setOriginIdFrom(String originIdFrom) {
        this.originIdFrom = originIdFrom;
    }

    /**
     * @return the originFromExternalId
     */
    public String getOriginFromExternalId() {
        return originFromExternalId;
    }

    /**
     * @param originFromExternalId the originFromExternalId to set
     */
    public void setOriginFromExternalId(String originExternalId) {
        this.originFromExternalId = originExternalId;
    }

    /**
     * @return the originFromName
     */
    public String getOriginFromName() {
        return originFromName;
    }

    /**
     * @param originFromName the originFromName to set
     */
    public void setOriginFromName(String originFromName) {
        this.originFromName = originFromName;
    }

    /**
     * @return the originFromSourceSystem
     */
    public String getOriginFromSourceSystem() {
        return originFromSourceSystem;
    }

    /**
     * @param originFromSourceSystem the originFromSourceSystem to set
     */
    public void setOriginFromSourceSystem(String originFromSourceSystem) {
        this.originFromSourceSystem = originFromSourceSystem;
    }

    /**
     * @return the originIdTo
     */
    public String getOriginIdTo() {
        return originIdTo;
    }

    /**
     * @param originIdTo the originIdTo to set
     */
    public void setOriginIdTo(String originIdTo) {
        this.originIdTo = originIdTo;
    }

    /**
     * @return the originToExternalId
     */
    public String getOriginToExternalId() {
        return originToExternalId;
    }

    /**
     * @param originToExternalId the originToExternalId to set
     */
    public void setOriginToExternalId(String originToExternalId) {
        this.originToExternalId = originToExternalId;
    }

    /**
     * @return the originToName
     */
    public String getOriginToName() {
        return originToName;
    }

    /**
     * @param originToName the originToName to set
     */
    public void setOriginToName(String originToName) {
        this.originToName = originToName;
    }

    /**
     * @return the originToSourceSystem
     */
    public String getOriginToSourceSystem() {
        return originToSourceSystem;
    }

    /**
     * @param originToSourceSystem the originToSourceSystem to set
     */
    public void setOriginToSourceSystem(String originToSourceSystem) {
        this.originToSourceSystem = originToSourceSystem;
    }

    /**
     *
     * @return VistoryOperationType
     */
    public VistoryOperationType getOperationType() {
        return operationType;
    }

    /**
     * @param operationType
     */
    public void setOperationType(VistoryOperationType operationType) {
        this.operationType = operationType;
    }


}
