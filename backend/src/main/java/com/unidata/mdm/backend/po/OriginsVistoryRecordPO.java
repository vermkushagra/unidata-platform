/**
 *
 */
package com.unidata.mdm.backend.po;

import java.util.Date;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Immutable vistory (versions + history) records table.
 */
public class OriginsVistoryRecordPO {
    /**
     * Table name.
     */
    public static final String TABLE_NAME = "origins_vistory";
    /**
     * ID.
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
     * Revision number.
     */
    public static final String FIELD_REVISION = "revision";
    /**
     * Valid from (validity range start time stamp).
     */
    public static final String FIELD_VALID_FROM = "valid_from";
    /**
     * Valid to (validity range end time stamp).
     */
    public static final String FIELD_VALID_TO = "valid_to";
    /**
     * JAXB data in SOAP data schema format.
     */
    public static final String FIELD_DATA_A = "data_a";
    /**
     * Binary protostuff data.
     */
    public static final String FIELD_DATA_B = "data_b";
    /**
     * Create date. Returned as update date for origin objects.
     */
    public static final String FIELD_CREATE_DATE = "create_date";
    /**
     * Created by. Returned as updated by for origin objects.
     */
    public static final String FIELD_CREATED_BY = "created_by";
    /**
     * Informational, read only. Update date of an origin. Vistory create date value is used.
     */
    public static final String FIELD_UPDATE_DATE = "update_date";
    /**
     * Informational, read only. Updated by value of an origin. Vistory created by value is used.
     */
    public static final String FIELD_UPDATED_BY = "updated_by";
    /**
     * Status of the record.
     */
    public static final String FIELD_STATUS = "status";
    /**
     * Approval state of the record.
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
     * Read-only data section from origins - external id.
     */
    public static final String FIELD_EXTERNAL_ID = "external_id";
    /**
     * Read-only data section from origins - source system.
     */
    public static final String FIELD_SOURCE_SYSTEM = "source_system";
    /**
     * Read-only data section from origins - name.
     */
    public static final String FIELD_NAME = "name";
    /**
     * Read-only data section from origins - is enrichment.
     */
    public static final String FIELD_IS_ENRICHMENT = "is_enrichment";
    /**
     * Global sequence number.
     */
    public static final String FIELD_GSN = "gsn";
    /**
     * ID.
     */
    private String id;
    /**
     * Origin id fkey.
     */
    private String originId;
    /**
     * Operation id.
     */
    private String operationId;
    /**
     * Revision number. Read-only.
     */
    private int revision;
    /**
     * Valid from (validity range start time stamp).
     */
    private Date validFrom;
    /**
     * Valid to (validity range end time stamp).
     */
    private Date validTo;
    /**
     * Data (marshaled approval).
     */
    private DataRecord data;
    /**
     * Create date of the origin object.
     */
    private Date createDate;
    /**
     * Created by of the origin object.
     */
    private String createdBy;
    /**
     * Create date of the vistory record.
     */
    private Date updateDate;
    /**
     * Created by of the vistory record.
     */
    private String updatedBy;
    /**
     * Status of the record.
     */
    private RecordStatus status;
    /**
     * Approval approval.
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
     * Origin external id.
     */
    private String externalId;
    /**
     * Origin source system.
     */
    private String sourceSystem;
    /**
     * Origin entity name.
     */
    private String name;
    /**
     * Origin enrichment flag..
     */
    private boolean enrichment;
    /**
     * Global sequence number.
     */
    private long gsn;

    /**
     * Constructor.
     */
    public OriginsVistoryRecordPO() {
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
     * @return the revision
     */
    public int getRevision() {
        return revision;
    }

    /**
     * Sets the revision.
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
     * @return the data as object
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
    public void setApproval(ApprovalState state) {
        this.approval = state;
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
     * @return the externalId
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * @param externalId the externalId to set
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
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
     * @return the enrichment
     */
    public boolean isEnrichment() {
        return enrichment;
    }

    /**
     * @param enrichment the enrichment to set
     */
    public void setEnrichment(boolean enrichment) {
        this.enrichment = enrichment;
    }

    /**
     * @return the gsn
     */
    public long getGsn() {
        return gsn;
    }

    /**
     * @param gsn the gsn to set
     */
    public void setGsn(long gsn) {
        this.gsn = gsn;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return
            String.format(new StringBuilder()
                .append(TABLE_NAME)
                .append("%n")
                .append(FIELD_ID)
                .append(": %s%n")
                .append(FIELD_ORIGIN_ID)
                .append(": %s%n")
                .append(FIELD_REVISION)
                .append(": %d%n")
                .append(FIELD_VALID_FROM)
                .append(": %tc%n")
                .append(FIELD_VALID_TO)
                .append(": %tc%n")
                .append(FIELD_DATA_A)
                .append(": %s%n")
                .append(FIELD_CREATE_DATE)
                .append(": %tc%n")
                .append(FIELD_CREATED_BY)
                .append(": %s%n")
                .append(FIELD_UPDATE_DATE)
                .append(": %tc%n")
                .append(FIELD_UPDATED_BY)
                .append(": %s%n")
                .append(FIELD_STATUS)
                .append(": %s%n")
                .append(FIELD_APPROVAL)
                .append(": %s%n")
                .append(FIELD_SHIFT)
                .append(": %s%n")
                .toString(),
                id,
                originId,
                revision,
                validFrom,
                validTo,
                data == null ? "null" : "[record]",
                createDate,
                createdBy,
                updateDate,
                updatedBy,
                status,
                approval,
                shift);
    }
}
