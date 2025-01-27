package com.unidata.mdm.backend.po;

import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Relation origin record.
 */
public class OriginRelationPO extends AbstractPO {

    /**
     * Table name.
     */
    public static final String TABLE_NAME = "origins_relations";
    /**
     * Id.
     */
    public static final String FIELD_ID = "id";
    /**
     * Etalon ID.
     */
    public static final String FIELD_ETALON_ID = "etalon_id";
    /**
     * Relation name.
     */
    public static final String FIELD_NAME = "name";
    /**
     * Origin id from.
     */
    public static final String FIELD_ORIGIN_ID_FROM = "origin_id_from";
    /**
     * Origin ID to.
     */
    public static final String FIELD_ORIGIN_ID_TO = "origin_id_to";
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
     * Relation name.
     */
    private String name;
    /**
     * Origin id from.
     */
    private String originIdFrom;
    /**
     * Origin ID to.
     */
    private String originIdTo;
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
    public OriginRelationPO() {
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
