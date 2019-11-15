package org.unidata.mdm.data.po.data;

import java.util.UUID;

/**
 * @author Mikhail Mikhailov
 * Reparent PO (from one etalon id to another).
 */
public class RelationOriginRemapPO extends RelationOriginPO {
    /**
     * The new etalon id.
     */
    private UUID newEtalonId;
    /**
     * The new target shard.
     */
    private int newShard;
    /**
     * Constructor.
     */
    public RelationOriginRemapPO() {
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
