package org.unidata.mdm.data.po.keys;

import java.util.UUID;

/**
 * @author Mikhail Mikhailov
 * Relation ext. key. Serves two tables.
 */
public class RelationExternalKeyPO {
    /**
     * From table name.
     */
    public static final String TABLE_FROM_NAME = "relation_from_keys";
    /**
     * To table name.
     */
    public static final String TABLE_TO_NAME = "relation_to_keys";
    /**
     * Shard.
     */
    public static final String FIELD_SHARD = "shard";
    /**
     * From record id.
     */
    public static final String FIELD_FROM_ID = "from_id";
    /**
     * Relation name.
     */
    public static final String FIELD_NAME = "name";
    /**
     * To record id.
     */
    public static final String FIELD_TO_ID = "to_id";
    /**
     * Relation etalon id.
     */
    public static final String FIELD_ETALON_ID = "etalon_id";
    /**
     * The 'from' side shard number.
     */
    private int fromShard;
    /**
     * The 'to' side shard number.
     */
    private int toShard;
    /**
     * The from id.
     */
    private UUID fromRecordEtalonId;
    /**
     * Rel name.
     */
    private String relationName;
    /**
     * The to etalon id.
     */
    private UUID toRecordEtalonId;
    /**
     * The relation etalon id.
     */
    private UUID relationEtalonId;
    /**
     * Constructor.
     */
    public RelationExternalKeyPO() {
        super();
    }
    /**
     * @return the fromRecordEtalonId
     */
    public UUID getFromRecordEtalonId() {
        return fromRecordEtalonId;
    }
    /**
     * @param fromRecordEtalonId the fromRecordEtalonId to set
     */
    public void setFromRecordEtalonId(UUID fromRecordEtalonId) {
        this.fromRecordEtalonId = fromRecordEtalonId;
    }
    /**
     * @return the relationName
     */
    public String getRelationName() {
        return relationName;
    }
    /**
     * @param relationName the relationName to set
     */
    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }
    /**
     * @return the toRecordEtalonId
     */
    public UUID getToRecordEtalonId() {
        return toRecordEtalonId;
    }
    /**
     * @param toRecordEtalonId the toRecordEtalonId to set
     */
    public void setToRecordEtalonId(UUID toRecordEtalonId) {
        this.toRecordEtalonId = toRecordEtalonId;
    }
    /**
     * @return the shard
     */
    public int getFromShard() {
        return fromShard;
    }
    /**
     * @param shard the shard to set
     */
    public void setFromShard(int shard) {
        this.fromShard = shard;
    }
    /**
     * @return the toShard
     */
    public int getToShard() {
        return toShard;
    }
    /**
     * @param toShard the toShard to set
     */
    public void setToShard(int toShard) {
        this.toShard = toShard;
    }
    /**
     * @return the relationEtalonId
     */
    public UUID getRelationEtalonId() {
        return relationEtalonId;
    }
    /**
     * @param relationEtalonId the relationEtalonId to set
     */
    public void setRelationEtalonId(UUID relationEtalonId) {
        this.relationEtalonId = relationEtalonId;
    }

}
