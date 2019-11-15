package org.unidata.mdm.data.po.keys;

import java.util.Objects;
import java.util.UUID;

import org.unidata.mdm.core.type.keys.ExternalId;
import org.unidata.mdm.data.util.StorageUtils;

/**
 * @author Mikhail Mikhailov
 * Record external keys representation.
 */
public class RecordExternalKeysPO {
    /**
     * Ext key.
     */
    public static final String FIELD_EXT_KEY = "ext_key";
    /**
     * Ext shard.
     */
    public static final String FIELD_EXT_SHARD = "ext_shard";
    /**
     * Etalon id.
     */
    public static final String FIELD_ETALON_ID = "etalon_id";
    /**
     * Origin external id.
     */
    protected ExternalId externalId;
    /**
     * Shard.
     */
    protected int shard = -1;
    /**
     * Etalon ID.
     */
    protected UUID etalonId;
    /**
     * Constructor.
     */
    public RecordExternalKeysPO() {
        super();
    }
    /**
     * @return the externalId
     */
    public String getExternalId() {
        return Objects.isNull(externalId) ? null : externalId.getId();
    }
    /**
     * Gets the underlaying keys object.
     * @return keys
     */
    public ExternalId getExternalIdAsObject() {
        return externalId;
    }
    /**
     * @param externalId the externalId to set
     */
    public void setExternalId(String externalId, String entityName, String sourceSystem) {
        this.externalId = ExternalId.of(externalId, entityName, sourceSystem);
        this.shard = StorageUtils.shard(this.externalId);
    }
    /**
     * @param externalId the externalId to set
     */
    public void setExternalId(ExternalId id) {
        this.externalId = id;
        this.shard = StorageUtils.shard(this.externalId);
    }
    /**
     * @return the entityName
     */
    public String getEntityName() {
        return Objects.isNull(externalId) ? null : externalId.getEntityName();
    }
    /**
     * @return the sourceSystem
     */
    public String getSourceSystem() {
        return Objects.isNull(externalId) ? null : externalId.getSourceSystem();
    }
    /**
     * @return the shard
     */
    public int getShard() {

        if (shard == -1 && Objects.nonNull(externalId)) {
            shard = StorageUtils.shard(externalId);
        }

        return shard;
    }
    /**
     * @param shard the shard to set
     */
    public void setShard(int shard) {
        this.shard = shard;
    }
    /**
     * @return the compactKey
     */
    public String getCompactKey() {
        return Objects.isNull(externalId) ? null : externalId.compact();
    }
    /**
     * @param compactKey the compactKey to set
     */
    public void setCompactKey(String compactKey) {
        this.externalId = ExternalId.uncompact(compactKey);
    }
    /**
     * @return the etalonId
     */
    public UUID getEtalonId() {
        return etalonId;
    }
    /**
     * @param etalonId the etalonId to set
     */
    public void setEtalonId(UUID etalonId) {
        this.etalonId = etalonId;
    }
}
