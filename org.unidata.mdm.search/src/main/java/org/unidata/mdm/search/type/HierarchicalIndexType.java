package org.unidata.mdm.search.type;

import javax.annotation.Nonnull;

/**
 * Special extension of search type, which detect that search type is included in a hierarchical structure (directed graph).
 * Notice: now it used only for directed graph with one level of leafs, and should be extended if it will be needed.
 */
public interface HierarchicalIndexType extends IndexType {
    /**
     * @return top element of a directed graph.
     */
    @Nonnull
    HierarchicalIndexType getTopType();
    /**
     * @return true if search type is a top type of a hierarchical structure.
     */
    boolean isTopType();
    /**
     * {@inheritDoc}
     */
    @Override
    default boolean isHierarchical() {
        return true;
    }
}
