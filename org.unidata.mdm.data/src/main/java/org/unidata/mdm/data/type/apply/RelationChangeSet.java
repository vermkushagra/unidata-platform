package org.unidata.mdm.data.type.apply;

import java.util.ArrayList;
import java.util.List;

import org.unidata.mdm.core.type.change.ChangeSet;
import org.unidata.mdm.data.po.EtalonRelationDraftStatePO;
import org.unidata.mdm.data.po.data.RelationEtalonPO;
import org.unidata.mdm.data.po.data.RelationOriginPO;
import org.unidata.mdm.data.po.data.RelationVistoryPO;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.search.context.IndexRequestContext;

/**
 * @author Mikhail Mikhailov
 * Basic relation change set.
 */
public class RelationChangeSet implements ChangeSet {
    /**
     * Etalon relation update PO.
     */
    protected final List<RelationEtalonPO> etalonRelationUpdatePOs = new ArrayList<>(2);
    /**
     * Origin relation update POs.
     */
    protected final List<RelationOriginPO> originRelationUpdatePOs = new ArrayList<>(2);
    /**
     * Origin relation vistory POs.
     */
    protected final List<RelationVistoryPO> originsVistoryRelationsPOs = new ArrayList<>(3);
    /**
     * Drafts. WF support.
     */
    protected final List<EtalonRelationDraftStatePO> etalonRelationDraftStatePOs = new ArrayList<>(2);
    /**
     * Index context.
     */
    protected final List<IndexRequestContext> indexRequestContexts = new ArrayList<>(2);
    /**
     * Relation type.
     */
    protected RelationType relationType;
    /**
     * Constructor.
     */
    public RelationChangeSet() {
        super();
    }
    /**
     * @return the etalonRelationUpdatePO
     */
    public List<RelationEtalonPO> getEtalonRelationUpdatePOs() {
        return etalonRelationUpdatePOs;
    }
    /**
     * @return the indexRequestContext
     */
    public List<IndexRequestContext> getIndexRequestContexts() {
        return indexRequestContexts;
    }
    /**
     * @return the originRelationUpdatePOs
     */
    public List<RelationOriginPO> getOriginRelationUpdatePOs() {
        return originRelationUpdatePOs;
    }
    /**
     * @return the originsVistoryRelationsPOs
     */
    public List<RelationVistoryPO> getOriginsVistoryRelationsPOs() {
        return originsVistoryRelationsPOs;
    }
    /**
     * @return the etalonRelationDraftStatePOs
     */
    public List<EtalonRelationDraftStatePO> getEtalonRelationDraftStatePOs() {
        return etalonRelationDraftStatePOs;
    }
    /**
     * @return the relationType
     */
    public RelationType getRelationType() {
        return relationType;
    }
    /**
     * @param relationType the relationType to set
     */
    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return indexRequestContexts.isEmpty()
            && etalonRelationUpdatePOs.isEmpty()
            && originRelationUpdatePOs.isEmpty()
            && originsVistoryRelationsPOs.isEmpty()
            && etalonRelationDraftStatePOs.isEmpty();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        indexRequestContexts.clear();
        etalonRelationUpdatePOs.clear();
        originRelationUpdatePOs.clear();
        originsVistoryRelationsPOs.clear();
        etalonRelationDraftStatePOs.clear();
    }
}
