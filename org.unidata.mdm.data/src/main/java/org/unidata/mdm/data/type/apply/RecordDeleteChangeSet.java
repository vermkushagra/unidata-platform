package org.unidata.mdm.data.type.apply;

import java.util.ArrayList;
import java.util.List;

import org.unidata.mdm.data.po.keys.RecordExternalKeysPO;
import org.unidata.mdm.data.po.keys.RecordKeysPO;

/**
 * @author Mikhail Mikhailov
 * Delete related stuff.
 */
public class RecordDeleteChangeSet extends RecordChangeSet {
    /**
     * Record keys to wipe.
     */
    protected final List<RecordKeysPO> wipeRecordKeys = new ArrayList<>(4);
    /**
     * External keys to wipe.
     */
    protected final List<RecordExternalKeysPO> wipeExternalKeys = new ArrayList<>(8);
    /**
     * Constructor.
     */
    public RecordDeleteChangeSet() {
        super();
    }
    /**
     * Gets collection of records to wipe.
     * @return collection
     */
    public List<RecordKeysPO> getWipeRecordKeys() {
        return wipeRecordKeys;
    }
    /**
     * Gets collection of external key records to wipe.
     * @return collection
     */
    public List<RecordExternalKeysPO> getWipeExternalKeys() {
        return wipeExternalKeys;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return wipeRecordKeys.isEmpty()
            && wipeExternalKeys.isEmpty()
            && super.isEmpty();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        wipeRecordKeys.clear();
        wipeExternalKeys.clear();
        super.clear();
    }
}
