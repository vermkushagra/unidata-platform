package com.unidata.mdm.backend.po;

import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Classifier origin record.
 */
public class OriginClassifierPO extends AbstractPO {

    /**
     * Table name.
     */
    public static final String TABLE_NAME = "origins_classifiers";
    /**
     * Id.
     */
    public static final String FIELD_ID = "id";
    /**
     * Etalon ID.
     */
    public static final String FIELD_ETALON_ID = "etalon_id";
    /**
     * Classifier name.
     */
    public static final String FIELD_NAME = "name";
    /**
     * Classifier node id.
     */
    public static final String FIELD_NODE_ID = "node_id";
    /**
     * Origin id record.
     */
    public static final String FIELD_ORIGIN_ID_RECORD = "origin_id_record";
    /**
     * Source system.
     */
    public static final String FIELD_SOURCE_SYSTEM = "source_system";
    /**
     * Status.
     */
    public static final String FIELD_STATUS = "status";
    /**
     * Version.
     */
    public static final String FIELD_VERSION = "version";
    /**
     * Record id.
     */
    private String id;
    /**
     * Etalon ID.
     */
    private String etalonId;
    /**
     * Classiifer name.
     */
    private String name;
    /**
     * Classifier node ID.
     */
    private String nodeId;
    /**
     * Origin id record.
     */
    private String originIdRecord;
    /**
     * Source system.
     */
    private String sourceSystem;
    /**
     * Status.
     */
    private RecordStatus status;
    /**
     * Constructor.
     */
    public OriginClassifierPO() {
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
     * @return the originIdRecord
     */
    public String getOriginIdRecord() {
        return originIdRecord;
    }

    /**
     * @param originIdRecord the originIdRecord to set
     */
    public void setOriginIdRecord(String originIdRecord) {
        this.originIdRecord = originIdRecord;
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

}
