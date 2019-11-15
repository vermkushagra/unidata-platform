package org.unidata.mdm.data.type.apply;

import java.util.ArrayList;
import java.util.List;

import org.unidata.mdm.core.type.change.ChangeSet;
import org.unidata.mdm.data.po.data.RelationEtalonPO;
import org.unidata.mdm.data.po.data.RelationEtalonRemapFromPO;
import org.unidata.mdm.data.po.data.RelationEtalonRemapToPO;
import org.unidata.mdm.data.po.data.RelationOriginRemapPO;
import org.unidata.mdm.data.po.keys.RelationExternalKeyPO;
import org.unidata.mdm.search.context.IndexRequestContext;

/**
 * @author Mikhail Mikhailov
 * Relation merge change set.
 */
public class RelationMergeChangeSet implements ChangeSet {
    /**
     * Remap from PO objects.
     */
    protected final List<RelationEtalonRemapFromPO> etalonFromRemaps = new ArrayList<>(4);
    /**
     * Remap from PO objects.
     */
    protected final List<RelationEtalonRemapToPO> etalonToRemaps = new ArrayList<>(4);
    /**
     * The origins to reparent.
     */
    protected final List<RelationOriginRemapPO> originRemaps = new ArrayList<>(4);
    /**
     * Origin relation update POs.
     */
    protected final List<RelationEtalonPO> etalonUpdates = new ArrayList<>(2);
    /**
     * External key POs.
     */
    protected final List<RelationExternalKeyPO> externalKeyInserts = new ArrayList<>(4);
    /**
     * External keys to wipe.
     */
    protected final List<RelationExternalKeyPO> externalKeyWipes = new ArrayList<>(4);
    /**
     * Index contexts.
     */
    protected final List<IndexRequestContext> indexRequestContexts = new ArrayList<>(2);
    /**
     * Constructor.
     */
    public RelationMergeChangeSet() {
        super();
    }
    /**
     * @return the etalonRelationRemapFromPOs
     */
    public List<RelationEtalonRemapFromPO> getEtalonFromRemaps() {
        return etalonFromRemaps;
    }
    /**
     * @return the etalonRelationRemapToPOs
     */
    public List<RelationEtalonRemapToPO> getEtalonToRemaps() {
        return etalonToRemaps;
    }
    /**
     * @return the originReparents
     */
    public List<RelationOriginRemapPO> getOriginRemaps() {
        return originRemaps;
    }
    /**
     * @return the etalonRelationUpdatePOs
     */
    public List<RelationEtalonPO> getEtalonUpdates() {
        return etalonUpdates;
    }
    /**
     * @return the externalKeyInserts
     */
    public List<RelationExternalKeyPO> getExternalKeyInserts() {
        return externalKeyInserts;
    }
    /**
     * @return the externalKeyWipes
     */
    public List<RelationExternalKeyPO> getExternalKeyWipes() {
        return externalKeyWipes;
    }
    /**
     * @return the indexRequestContexts
     */
    public List<IndexRequestContext> getIndexRequestContexts() {
        return indexRequestContexts;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return etalonFromRemaps.isEmpty()
            && etalonToRemaps.isEmpty()
            && originRemaps.isEmpty()
            && etalonUpdates.isEmpty()
            && externalKeyInserts.isEmpty()
            && externalKeyWipes.isEmpty()
            && indexRequestContexts.isEmpty();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        etalonFromRemaps.clear();
        etalonToRemaps.clear();
        originRemaps.clear();
        etalonUpdates.clear();
        externalKeyInserts.clear();
        externalKeyWipes.clear();
        indexRequestContexts.clear();
    }
}
