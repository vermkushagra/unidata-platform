package com.unidata.mdm.backend.common.search.id;

import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.search.types.SearchType;

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
    public SearchType getSearchType() {
        return EntitySearchType.ETALON_RELATION;
    }
    /**
     * @return the relationName
     */
    public String getRelationName() {
        return relationName;
    }
}
