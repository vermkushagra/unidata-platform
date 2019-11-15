package org.unidata.mdm.data.type.apply.batch.impl;

import org.unidata.mdm.data.type.apply.batch.BatchKeyReference;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;

/**
 * @author Mikhail Mikhailov
 * Simple wrapper for the keys + current revision number for the ext. id being imported.
 * This schema implies, that same keys are always in the same partition block for historical/multiversion records.
 */
public class RecordBatchKeyReference implements BatchKeyReference<RecordKeys> {
    /**
     * Current revision of this OKey.
     */
    private int revision;
    /**
     * Keys.
     */
    private RecordKeys keys;
    /**
     * Constructor.
     * @param keys
     * @param revision
     */
    public RecordBatchKeyReference(RecordKeys keys) {
        super();
        this.keys = keys;
        this.revision = keys.getOriginKey() != null ? keys.getOriginKey().getRevision() : 0;
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
        if (revision > this.revision) {
            keys = RecordKeys.builder(keys)
                    .originKey(RecordOriginKey
                            .builder(keys.getOriginKey())
                            .revision(revision)
                            .build())
                    .build();
        }
        this.revision = revision;
    }
    /**
     * @return the keys
     */
    @Override
    public RecordKeys getKeys() {
        return keys;
    }
    /**
     * @param keys the keys to set
     */
    public void setKeys(RecordKeys keys) {
        this.keys = keys;
    }
}
