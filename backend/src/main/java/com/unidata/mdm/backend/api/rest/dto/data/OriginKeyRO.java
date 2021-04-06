package com.unidata.mdm.backend.api.rest.dto.data;

/**
 * @author Mikhail Mikhailov
 * Origin key RO.
 */
public class OriginKeyRO {
    /**
     * Origin id.
     */
    private String id;
    /**
     * External id.
     */
    private String externalId;
    /**
     * Source system.
     */
    private String sourceSystem;
    /**
     * Entity name.
     */
    private String entityName;
    /**
     * Enrichment
     */
    private boolean enrichment;

    /**
     * Constructor.
     */
    public OriginKeyRO() {
        super();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the externalId
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * @return the sourceSystem
     */
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @return the enrichment
     */
    public boolean isEnrichment() {
        return enrichment;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param externalId the externalId to set
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * @param sourceSystem the sourceSystem to set
     */
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    /**
     * @param entityName the entityName to set
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @param enrichment the enrichment to set
     */
    public void setEnrichment(boolean enrichment) {
        this.enrichment = enrichment;
    }

}
