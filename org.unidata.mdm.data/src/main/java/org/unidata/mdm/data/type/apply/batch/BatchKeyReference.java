package org.unidata.mdm.data.type.apply.batch;

import org.unidata.mdm.core.type.keys.EtalonKey;
import org.unidata.mdm.core.type.keys.Keys;
import org.unidata.mdm.core.type.keys.OriginKey;

/**
 * @author Mikhail Mikhailov
 * Batch key reference interface.
 * @param <T> the key type, either record or relation or classifier keys.
 */
public interface BatchKeyReference <T extends Keys<? extends EtalonKey, ? extends OriginKey>> {
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
