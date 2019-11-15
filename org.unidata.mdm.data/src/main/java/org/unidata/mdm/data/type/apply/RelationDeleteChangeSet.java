package org.unidata.mdm.data.type.apply;

import java.util.ArrayList;
import java.util.List;

import org.unidata.mdm.data.po.keys.RelationExternalKeyPO;
import org.unidata.mdm.data.po.keys.RelationKeysPO;

/**
 * @author Mikhail Mikhailov
 * Relation delete change set.
 */
public class RelationDeleteChangeSet extends RelationChangeSet {
    /**
     * Wipe records.
     */
    protected final List<RelationKeysPO> wipeRelationKeys = new ArrayList<>(2);
    /**
     * External keys to wipe.
     */
    protected final List<RelationExternalKeyPO> wipeExternalKeys = new ArrayList<>(8);
    /**
     * Constructor.
     */
    public RelationDeleteChangeSet() {
        super();
    }
    /**
     * Gets the elements to wipe.
     * @return the wipe
     */
    public List<RelationKeysPO> getWipeRelationKeys() {
        return wipeRelationKeys;
    }
    /**
     * @return the wipeExternalKeys
     */
    public List<RelationExternalKeyPO> getWipeExternalKeys() {
        return wipeExternalKeys;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return wipeRelationKeys.isEmpty()
            && wipeExternalKeys.isEmpty()
            && super.isEmpty();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        wipeRelationKeys.clear();
        wipeExternalKeys.clear();
        super.clear();
    }
}
