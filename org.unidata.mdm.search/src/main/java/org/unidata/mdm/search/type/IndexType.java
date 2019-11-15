package org.unidata.mdm.search.type;

import javax.annotation.Nonnull;

/**
 * Types in index
 */
public interface IndexType {
    /**
     * @return name of type
     */
    @Nonnull
    String getName();
    /**
     * @param searchType - search type
     * @return true, if search types are related
     */
    boolean isRelated(IndexType searchType);
    /**
     * Tells whether this type is a hierarchical one.
     * @return true, if so, false otherwise.
     */
    default boolean isHierarchical() {
        return false;
    }
    /**
     * Casts self to HIT. Provokes CCE, if this type is not a hierarchical one.
     * @return HIT
     */
    default HierarchicalIndexType toHierarchical() {
        return isHierarchical() ? (HierarchicalIndexType) this : null;
    }
}
