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
 * Classifier vistory record.
 */
public class OriginsVistoryClassifierPO {
    /**
     * Table name.
     */
    public static final String TABLE_NAME = "origins_classifiers_vistory";
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
     * Protostuff data.
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
     * Classifieer etalon id.
     */
    public static final String FIELD_ETALON_ID = "etalon_id";
    /**
     * Read only type name.
     */
    public static final String FIELD_NAME = "name";
    /**
     * Read only node id.
     */
    public static final String FIELD_NODE_ID = "node_id";
    /**
     * Created by (created by of the version).
     */
    public static final String FIELD_SOURCE_SYSTEM = "source_system";
    /**
     * Origin id record.
     */
    public static final String FIELD_ORIGIN_ID_RECORD = "origin_id_record";
    /**
     * Origin record status.
     */
    public static final String FIELD_ORIGIN_RECORD_STATUS = "origin_record_status";
    /**
     * Origin record external id.
     */
    public static final String FIELD_ORIGIN_RECORD_EXTERNAL_ID = "origin_record_external_id";
    /**
     * Origin record name.
     */
    public static final String FIELD_ORIGIN_RECORD_NAME = "origin_record_name";
    /**
     * Origin record source system.
     */
    public static final String FIELD_ORIGIN_RECORD_SOURCE_SYSTEM = "origin_record_source_system";
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
     * Relation etalon id.
     */
    private String etalonId;
    /**
     * Type name (read only).
     */
    private String name;
    /**
     * Classifier node id.
     */
    private String nodeId;
    /**
     * Source system (read only).
     */
    private String sourceSystem;
    /**
     * Origin id from.
     */
    private String originIdRecord;
    /**
     * Origin record external id.
     */
    private String originRecordExternalId;
    /**
     * Origin record name.
     */
    private String originRecordName;
    /**
     * Origin record source system.
     */
    private String originRecordSourceSystem;
    /**
     * Origin record status.
     */
    private RecordStatus originRecordStatus;
    /**
     * Constructor.
     */
    public OriginsVistoryClassifierPO() {
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
    public String getEtalonId() {
        return etalonId;
    }

    /**
     * @param relationEtalonId the relationEtalonId to set
     */
    public void setEtalonId(String relationEtalonId) {
        this.etalonId = relationEtalonId;
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
     * @return the nodeId
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId the nodeId to set
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
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
    public String getOriginIdRecord() {
        return originIdRecord;
    }

    /**
     * @param originIdFrom the originIdFrom to set
     */
    public void setOriginIdRecord(String originIdFrom) {
        this.originIdRecord = originIdFrom;
    }

    /**
     * @return the originFromExternalId
     */
    public String getOriginRecordExternalId() {
        return originRecordExternalId;
    }

    /**
     * @param originRecordExternalId the originFromExternalId to set
     */
    public void setOriginRecordExternalId(String originExternalId) {
        this.originRecordExternalId = originExternalId;
    }

    /**
     * @return the originFromName
     */
    public String getOriginRecordName() {
        return originRecordName;
    }

    /**
     * @param originFromName the originFromName to set
     */
    public void setOriginRecordName(String originFromName) {
        this.originRecordName = originFromName;
    }

    /**
     * @return the originFromSourceSystem
     */
    public String getOriginRecordSourceSystem() {
        return originRecordSourceSystem;
    }

    /**
     * @param originFromSourceSystem the originFromSourceSystem to set
     */
    public void setOriginRecordSourceSystem(String originFromSourceSystem) {
        this.originRecordSourceSystem = originFromSourceSystem;
    }

    /**
     * @return the originRecordStatus
     */
    public RecordStatus getOriginRecordStatus() {
        return originRecordStatus;
    }

    /**
     * @param originRecordStatus the originRecordStatus to set
     */
    public void setOriginRecordStatus(RecordStatus originRecordStatus) {
        this.originRecordStatus = originRecordStatus;
    }

}
