package org.unidata.mdm.data.po.data;

import java.util.UUID;

/**
 * @author Mikhail Mikhailov
 * Merge remap of a record origin.
 */
public class RecordOriginRemapPO extends RecordOriginPO {
    /**
     * Id of the golden record (Etalon).
     */
    private UUID newEtalonId;
    /**
     * The new target shard.
     */
    private int newShard;
    /**
     * Constructor.
     */
    public RecordOriginRemapPO() {
        super();
    }
    /**
     * @return the newEtalonId
     */
    public UUID getNewEtalonId() {
        return newEtalonId;
    }
    /**
     * @param newEtalonId the newEtalonId to set
     */
    public void setNewEtalonId(UUID newEtalonId) {
        this.newEtalonId = newEtalonId;
    }
    /**
     * @return the newShard
     */
    public int getNewShard() {
        return newShard;
    }
    /**
     * @param newShard the newShard to set
     */
    public void setNewShard(int newShard) {
        this.newShard = newShard;
    }

}
