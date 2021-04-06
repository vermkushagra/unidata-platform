package com.unidata.mdm.backend.po;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * PO for keys digest.
 * @author Mikhail Mikhailov
 */
public class ClassifierKeysPO {
    /**
     * Etalon ID.
     */
    public static final String FIELD_ETALON_ID = "etalon_id";
    /**
     * Etalon name.
     */
    public static final String FIELD_ETALON_NAME = "etalon_name";
    /**
     * Etalon status.
     */
    public static final String FIELD_ETALON_STATUS = "etalon_status";
    /**
     * Etalon state.
     */
    public static final String FIELD_ETALON_STATE = "etalon_state";
    /**
     * Etalon id record.
     */
    public static final String FIELD_ETALON_ID_RECORD = "etalon_id_record";
    /**
     * Etalon record status.
     */
    public static final String FIELD_ETALON_RECORD_STATUS = "etalon_record_status";
    /**
     * Etalon record approval state.
     */
    public static final String FIELD_ETALON_RECORD_STATE = "etalon_record_state";
    /**
     * Etalon record name.
     */
    public static final String FIELD_ETALON_RECORD_NAME = "etalon_record_name";
    /**
     * Origin ID.
     */
    public static final String FIELD_ORIGIN_ID = "origin_id";
    /**
     * Origin name.
     */
    public static final String FIELD_ORIGIN_NAME = "origin_name";
    /**
     * Origin node id.
     */
    public static final String FIELD_ORIGIN_NODE_ID = "origin_node_id";
    /**
     * Origin status.
     */
    public static final String FIELD_ORIGIN_STATUS = "origin_status";
    /**
     * Origin source system.
     */
    public static final String FIELD_ORIGIN_SOURCE_SYSTEM = "origin_source_system";
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
     * Current origin's revision.
     */
    public static final String FIELD_ORIGIN_REVISION = "origin_revision";
    /**
     * Record etalon id.
     */
    private String etalonId;
    /**
     * Type name as set by entity definition.
     */
    private String etalonName;
    /**
     * Etalon status of the record.
     */
    private RecordStatus etalonStatus;
    /**
     * Etalon approval state of the record.
     */
    private ApprovalState etalonState;
    /**
     * Etalon id record.
     */
    private String etalonIdRecord;
    /**
     * Etalon from status.
     */
    private RecordStatus etalonRecordStatus;
    /**
     * Etalon from approval state.
     */
    private ApprovalState etalonRecordState;
    /**
     * Etalon from name.
     */
    private String etalonRecordName;
    /**
     * Record origin id.
     */
    private String originId;
    /**
     * Type name as set by entity definition.
     */
    private String originName;
    /**
     * Node id.
     */
    private String originNodeId;
    /**
     * Origin status of the record.
     */
    private RecordStatus originStatus;
    /**
     * Origin source system.
     */
    private String originSourceSystem;
    /**
     * Origin id from.
     */
    private String originIdRecord;
    /**
     * Origin from status.
     */
    private RecordStatus originRecordStatus;
    /**
     * Current origin's revision.
     */
    private int originRevision;
    /**
     * Origin from external id.
     */
    private String originRecordExternalId;
    /**
     * Origin from name.
     */
    private String originRecordName;
    /**
     * Origin from source system.
     */
    private String originRecordSourceSystem;
    /**
     * Constructor.
     */
    public ClassifierKeysPO() {
        super();
    }

    /**
     * @return the etalonId
     */
    public String getEtalonId() {
        return etalonId;
    }

    /**
     * @param etalonId the etalonId to set
     */
    public void setEtalonId(String etalonId) {
        this.etalonId = etalonId;
    }

    /**
     * @return the etalonName
     */
    public String getEtalonName() {
        return etalonName;
    }

    /**
     * @param etalonName the etalonName to set
     */
    public void setEtalonName(String etalonName) {
        this.etalonName = etalonName;
    }

    /**
     * @return the etalonStatus
     */
    public RecordStatus getEtalonStatus() {
        return etalonStatus;
    }

    /**
     * @param etalonStatus the etalonStatus to set
     */
    public void setEtalonStatus(RecordStatus etalonStatus) {
        this.etalonStatus = etalonStatus;
    }

    /**
     * @return the etalonState
     */
    public ApprovalState getEtalonState() {
        return etalonState;
    }

    /**
     * @param etalonState the etalonState to set
     */
    public void setEtalonState(ApprovalState etalonState) {
        this.etalonState = etalonState;
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
     * @return the originName
     */
    public String getOriginName() {
        return originName;
    }

    /**
     * @param originName the originName to set
     */
    public void setOriginName(String originName) {
        this.originName = originName;
    }

    /**
     * @return the originNodeId
     */
    public String getOriginNodeId() {
        return originNodeId;
    }

    /**
     * @param originNodeId the originNodeId to set
     */
    public void setOriginNodeId(String originNodeId) {
        this.originNodeId = originNodeId;
    }

    /**
     * @return the originStatus
     */
    public RecordStatus getOriginStatus() {
        return originStatus;
    }

    /**
     * @param originStatus the originStatus to set
     */
    public void setOriginStatus(RecordStatus originStatus) {
        this.originStatus = originStatus;
    }

    /**
     * @return the originSourceSystem
     */
    public String getOriginSourceSystem() {
        return originSourceSystem;
    }

    /**
     * @param originSourceSystem the originSourceSystem to set
     */
    public void setOriginSourceSystem(String originSourceSystem) {
        this.originSourceSystem = originSourceSystem;
    }

    /**
     * @return the revision
     */
    public int getOriginRevision() {
        return originRevision;
    }

    /**
     * @param revision the revision to set
     */
    public void setOriginRevision(int revision) {
        this.originRevision = revision;
    }

    /**
     * @return the etalonIdFrom
     */
    public String getEtalonIdRecord() {
        return etalonIdRecord;
    }


    /**
     * @param etalonIdFrom the etalonIdFrom to set
     */
    public void setEtalonIdRecord(String etalonIdFrom) {
        this.etalonIdRecord = etalonIdFrom;
    }


    /**
     * @return the etalonFromStatus
     */
    public RecordStatus getEtalonRecordStatus() {
        return etalonRecordStatus;
    }


    /**
     * @param etalonFromStatus the etalonFromStatus to set
     */
    public void setEtalonRecordStatus(RecordStatus etalonFromStatus) {
        this.etalonRecordStatus = etalonFromStatus;
    }


    /**
     * @return the etalonFromName
     */
    public String getEtalonRecordName() {
        return etalonRecordName;
    }


    /**
     * @param etalonFromName the etalonFromName to set
     */
    public void setEtalonRecordName(String etalonFromName) {
        this.etalonRecordName = etalonFromName;
    }


    /**
     * @return the etalonFromState
     */
    public ApprovalState getEtalonRecordState() {
        return etalonRecordState;
    }


    /**
     * @param etalonFromState the etalonFromState to set
     */
    public void setEtalonRecordState(ApprovalState etalonFromState) {
        this.etalonRecordState = etalonFromState;
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
     * @return the originFromStatus
     */
    public RecordStatus getOriginRecordStatus() {
        return originRecordStatus;
    }


    /**
     * @param originFromStatus the originFromStatus to set
     */
    public void setOriginRecordStatus(RecordStatus originFromStatus) {
        this.originRecordStatus = originFromStatus;
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

}
