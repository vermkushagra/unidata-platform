package org.unidata.mdm.data.type.apply;

import java.util.ArrayList;
import java.util.List;

import org.unidata.mdm.data.po.data.RelationEtalonPO;
import org.unidata.mdm.data.po.data.RelationOriginPO;
import org.unidata.mdm.data.po.keys.RelationExternalKeyPO;

/**
 * @author Mikhail Mikhailov
 * Relation upsert change set.
 */
public class RelationUpsertChangeSet extends RelationChangeSet {
    /**
     * Etalon relation insert PO.
     */
    protected RelationEtalonPO etalonRelationInsertPO;
    /**
     * Origin relation insert POs.
     */
    protected final List<RelationOriginPO> originRelationInsertPOs = new ArrayList<>(2);
    /**
     * External key POs.
     */
    protected final List<RelationExternalKeyPO> externalKeyInsertPOs = new ArrayList<>(2);
    /**
     * Constructor.
     */
    public RelationUpsertChangeSet() {
        super();
    }
    /**
     * @return the etalonRelationPO
     */
    public RelationEtalonPO getEtalonRelationInsertPO() {
        return etalonRelationInsertPO;
    }
    /**
     * @param etalonRelationPO the etalonRelationPO to set
     */
    public void setEtalonRelationInsertPO(RelationEtalonPO etalonRelationPO) {
        this.etalonRelationInsertPO = etalonRelationPO;
    }
    public boolean isEtalonUpdate() {
        return etalonRelationInsertPO == null && etalonRelationUpdatePOs != null;
    }
    public boolean isEtalonInsert() {
        return etalonRelationInsertPO != null && etalonRelationUpdatePOs == null;
    }
    /**
     * @return the originRelationPOs
     */
    public List<RelationOriginPO> getOriginRelationInsertPOs() {
        return originRelationInsertPOs;
    }
    /**
     * @return the externalKeyInsertPOs
     */
    public List<RelationExternalKeyPO> getExternalKeyInsertPOs() {
        return externalKeyInsertPOs;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return etalonRelationInsertPO == null
            && originRelationInsertPOs.isEmpty()
            && externalKeyInsertPOs.isEmpty()
            && super.isEmpty();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        etalonRelationInsertPO = null;
        originRelationInsertPOs.clear();
        externalKeyInsertPOs.clear();
        super.clear();
    }
}
