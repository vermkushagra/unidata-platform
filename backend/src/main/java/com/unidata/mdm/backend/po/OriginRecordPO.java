package com.unidata.mdm.backend.po;

import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Origin record persistent object.
 */
public class OriginRecordPO extends AbstractPO {
    /**
     * Table name.
     */
    public static final String TABLE_NAME = "origins";
    /**
     * ID.
     */
    public static final String FIELD_ID = "id";
    /**
     * Name (virtual column, actually persisted in 'etalons' table).
     */
    public static final String FIELD_NAME = "name";
    /**
     * Version.
     */
    public static final String FIELD_VERSION = "version";
    /**
     * Origin name.
     */
    public static final String FIELD_SOURCE_SYSTEM = "source_system";
    /**
     * External ID (foreign primary key ID).
     */
    public static final String FIELD_EXTERNAL_ID = "external_id";
    /**
     * Etalon (golden record) id. FK.
     */
    public static final String FIELD_ETALON_ID = "etalon_id";
    /**
     * Tells, if this origin is an enrichment one or not.
     */
    public static final String FIELD_IS_ENRICHMENT = "is_enrichment";
    /**
     * Status.
     */
    public static final String FIELD_STATUS = "status";
    /**
     * This PO object's fields.
     */
    public static final String[] FIELDS = {
        FIELD_ID,
        FIELD_NAME,
        FIELD_VERSION,
        FIELD_SOURCE_SYSTEM,
        FIELD_EXTERNAL_ID,
        FIELD_ETALON_ID,
        FIELD_IS_ENRICHMENT,
        FIELD_STATUS,
        FIELD_CREATE_DATE,
        FIELD_UPDATE_DATE,
        FIELD_CREATED_BY,
        FIELD_UPDATED_BY
    };
    /**
     * Record id.
     */
    private String id;
    /**
     * Type name as set by entity definition (virtual column, actually persisted in 'etalons' table).
     */
    private String name;
    /**
     * Name of the source system.
     */
    private String sourceSystem;
    /**
     * Natural key (external ID) of the record, not necessary unique.
     */
    private String externalId;
    /**
     * Id of the golden record (Etalon).
     */
    private String etalonId;
    /**
     * Version of the record.
     */
    private int version;
    /**
     * Tells, if this origin is an enrichment one or not.
     */
    private boolean enrichment;
    /**
     * Status of the record.
     */
    private RecordStatus status;
    /**
     * Constructor.
     */
    public OriginRecordPO() {
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
     * @return the sourceSystem
     */
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * @param sourceSystem the sourceSystem to set
     */
    public void setSourceSystem(String originName) {
        this.sourceSystem = originName;
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
    public void setExternalId(String naturalKey) {
        this.externalId = naturalKey;
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
    public void setEtalonId(String goldenId) {
        this.etalonId = goldenId;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
        this.version = version;
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
