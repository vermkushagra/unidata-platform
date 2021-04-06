package com.unidata.mdm.backend.service.data.batch;

import com.unidata.mdm.backend.common.keys.Keys;

/**
 * @author Mikhail Mikhailov
 * Batch key reference interface.
 * @param <T> the key type, either record or relation or classifier keys.
 */
public interface BatchKeyReference <T extends Keys> {
    /**
     * @return the revision
     */
    int getRevision();
    /**
     * @param revision the revision to set
     */
    void setRevision(int revision);
    /**
     * @return the keys
     */
    public T getKeys();
}
