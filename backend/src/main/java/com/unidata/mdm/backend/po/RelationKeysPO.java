package com.unidata.mdm.backend.po;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * PO for keys digest.
 * @author Mikhail Mikhailov
 */
public class RelationKeysPO {
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
     * Etalon id from.
     */
    public static final String FIELD_ETALON_ID_FROM = "etalon_id_from";
    /**
     * Etalon from status.
     */
    public static final String FIELD_ETALON_FROM_STATUS = "etalon_from_status";
    /**
     * Etalon from approval state.
     */
    public static final String FIELD_ETALON_FROM_STATE = "etalon_from_state";
    /**
     * Etalon from name.
     */
    public static final String FIELD_ETALON_FROM_NAME = "etalon_from_name";
    /**
     * Etalon id to.
     */
    public static final String FIELD_ETALON_ID_TO = "etalon_id_to";
    /**
     * Etalon to status.
     */
    public static final String FIELD_ETALON_TO_STATUS = "etalon_to_status";
    /**
     * Etalon to approval state.
     */
    public static final String FIELD_ETALON_TO_STATE = "etalon_to_state";
    /**
     * Etalon to name.
     */
    public static final String FIELD_ETALON_TO_NAME = "etalon_to_name";
    /**
     * Origin ID.
     */
    public static final String FIELD_ORIGIN_ID = "origin_id";
    /**
     * Current origin's revision.
     */
    public static final String FIELD_ORIGIN_REVISION = "origin_revision";
    /**
     * Origin name.
     */
    public static final String FIELD_ORIGIN_NAME = "origin_name";
    /**
     * Origin status.
     */
    public static final String FIELD_ORIGIN_STATUS = "origin_status";
    /**
     * Origin source system.
     */
    public static final String FIELD_ORIGIN_SOURCE_SYSTEM = "origin_source_system";
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
     * Etalon id from.
     */
    private String etalonIdFrom;
    /**
     * Etalon from status.
     */
    private RecordStatus etalonFromStatus;
    /**
     * Etalon from approval state.
     */
    private ApprovalState etalonFromState;
    /**
     * Etalon from name.
     */
    private String etalonFromName;
    /**
     * Etalon id to.
     */
    private String etalonIdTo;
    /**
     * Etalon to status.
     */
    private RecordStatus etalonToStatus;
    /**
     * Etalon to approval state.
     */
    private ApprovalState etalonToState;
    /**
     * Etalon to name.
     */
    private String etalonToName;
    /**
     * Record origin id.
     */
    private String originId;
    /**
     * Current origin's revision.
     */
    private int originRevision;
    /**
     * Type name as set by entity definition.
     */
    private String originName;
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
    private String originIdFrom;
    /**
     * Origin from status.
     */
    private RecordStatus originFromStatus;
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
     * Origin to status.
     */
    private RecordStatus originToStatus;
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
     * Constructor.
     */
    public RelationKeysPO() {
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
     * @return the etalonIdFrom
     */
    public String getEtalonIdFrom() {
        return etalonIdFrom;
    }


    /**
     * @param etalonIdFrom the etalonIdFrom to set
     */
    public void setEtalonIdFrom(String etalonIdFrom) {
        this.etalonIdFrom = etalonIdFrom;
    }


    /**
     * @return the etalonFromStatus
     */
    public RecordStatus getEtalonFromStatus() {
        return etalonFromStatus;
    }


    /**
     * @param etalonFromStatus the etalonFromStatus to set
     */
    public void setEtalonFromStatus(RecordStatus etalonFromStatus) {
        this.etalonFromStatus = etalonFromStatus;
    }


    /**
     * @return the etalonFromName
     */
    public String getEtalonFromName() {
        return etalonFromName;
    }


    /**
     * @param etalonFromName the etalonFromName to set
     */
    public void setEtalonFromName(String etalonFromName) {
        this.etalonFromName = etalonFromName;
    }


    /**
     * @return the etalonIdTo
     */
    public String getEtalonIdTo() {
        return etalonIdTo;
    }


    /**
     * @param etalonIdTo the etalonIdTo to set
     */
    public void setEtalonIdTo(String etalonIdTo) {
        this.etalonIdTo = etalonIdTo;
    }


    /**
     * @return the etalonToStatus
     */
    public RecordStatus getEtalonToStatus() {
        return etalonToStatus;
    }


    /**
     * @param etalonToStatus the etalonToStatus to set
     */
    public void setEtalonToStatus(RecordStatus etalonToStatus) {
        this.etalonToStatus = etalonToStatus;
    }


    /**
     * @return the etalonToName
     */
    public String getEtalonToName() {
        return etalonToName;
    }


    /**
     * @param etalonToName the etalonToName to set
     */
    public void setEtalonToName(String etalonToName) {
        this.etalonToName = etalonToName;
    }


    /**
     * @return the etalonFromState
     */
    public ApprovalState getEtalonFromState() {
        return etalonFromState;
    }


    /**
     * @param etalonFromState the etalonFromState to set
     */
    public void setEtalonFromState(ApprovalState etalonFromState) {
        this.etalonFromState = etalonFromState;
    }


    /**
     * @return the etalonToState
     */
    public ApprovalState getEtalonToState() {
        return etalonToState;
    }


    /**
     * @param etalonToState the etalonToState to set
     */
    public void setEtalonToState(ApprovalState etalonToState) {
        this.etalonToState = etalonToState;
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
     * @return the originFromStatus
     */
    public RecordStatus getOriginFromStatus() {
        return originFromStatus;
    }


    /**
     * @param originFromStatus the originFromStatus to set
     */
    public void setOriginFromStatus(RecordStatus originFromStatus) {
        this.originFromStatus = originFromStatus;
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
     * @return the originToStatus
     */
    public RecordStatus getOriginToStatus() {
        return originToStatus;
    }


    /**
     * @param originToStatus the originToStatus to set
     */
    public void setOriginToStatus(RecordStatus originToStatus) {
        this.originToStatus = originToStatus;
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

}
