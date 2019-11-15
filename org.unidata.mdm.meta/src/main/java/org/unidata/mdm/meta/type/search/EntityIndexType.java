package org.unidata.mdm.meta.type.search;

import javax.annotation.Nonnull;

import org.unidata.mdm.core.type.search.DataIndexType;
import org.unidata.mdm.search.type.IndexType;

/**
 * Index type for data entity indexes.
 */
public enum EntityIndexType implements DataIndexType {
    /**
     * Etalon type - the top type.
     */
    ETALON("etalon"){
        @Override
        public boolean isTopType() {
            return true;
        }
    },
    /**
     * Data type
     */
    RECORD("data"),
    /**
     * Relation type
     */
    RELATION("relation");
    /**
     * Name of type
     */
    private final String type;
    /**
     * Constructor.
     * @param type the name of the type
     */
    EntityIndexType(String type) {
        this.type = type;
    }
    /**
     * @return name of type
     */
    @Nonnull
    @Override
    public String getName() {
        return type;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRelated(IndexType searchType) {
        return searchType instanceof DataIndexType;
    }
    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public DataIndexType getTopType() {
        return EntityIndexType.ETALON;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTopType() {
        return false;
    }
}
