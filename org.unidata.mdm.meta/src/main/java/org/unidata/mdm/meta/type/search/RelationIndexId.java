package org.unidata.mdm.meta.type.search;

import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.search.type.id.AbstractManagedIndexId;

/**
 * @author Mikhail Mikhailov
 * managed relation data record index id.
 */
public abstract class RelationIndexId extends AbstractManagedIndexId {
    /**
     * Relation name.
     */
    protected String relationName;
    /**
     * Constructor.
     */
    protected RelationIndexId() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public IndexType getSearchType() {
        return EntityIndexType.RELATION;
    }
    /**
     * @return the relationName
     */
    public String getRelationName() {
        return relationName;
    }
}
