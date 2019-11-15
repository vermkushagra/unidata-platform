package org.unidata.mdm.data.type.apply.batch.impl;

import org.unidata.mdm.data.type.apply.batch.BatchKeyReference;
import org.unidata.mdm.data.type.keys.RelationKeys;

/**
 * @author Mikhail Mikhailov
 * Simple wrapper for the keys + current revision number for the ext. id being imported.
 * This schema implies, that same keys are always in the same partition block for historical/multiversion records.
 */
public class RelationBatchKeyReference implements BatchKeyReference<RelationKeys> {
    /**
     * Current revision of this OKey.
     */
    private int revision;
    /**
     * Keys.
     */
    private final RelationKeys keys;
    /**
     * Constructor.
     * @param keys
     * @param revision
     */
    public RelationBatchKeyReference(RelationKeys keys) {
        super();
        this.keys = keys;
        this.revision = keys.getOriginKey() != null && keys.getOriginKey().getId() != null ? keys.getOriginKey().getRevision() : 0;
    }
    /**
     * @return the revision
     */
    @Override
    public int getRevision() {
        return revision;
    }
    /**
     * @param revision the revision to set
     */
    @Override
    public void setRevision(int revision) {
        this.revision = revision;
    }
    /**
     * @return the keys
     */
    @Override
    public RelationKeys getKeys() {
        return keys;
    }
}
