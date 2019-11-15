package org.unidata.mdm.search.type.indexing.impl;

import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.search.type.indexing.IndexingField;

/**
 * @author Mikhail Mikhailov on Oct 7, 2019
 * Name.
 */
public abstract class AbstractIndexingField<X extends AbstractIndexingField<X>> implements IndexingField {
    /**
     * The name.
     */
    private final String name;
    /**
     * IT.
     */
    private IndexType indexType;
    /**
     * Constructor.
     */
    public AbstractIndexingField(String name) {
        super();
        this.name = name;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public IndexType getType() {
        return indexType;
    }
    /**
     * @param indexType the indexType to set
     */
    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }
    /**
     * Sets index type fluently.
     * @param type the type
     * @return self
     */
    public X withIndexType(IndexType type) {
        setIndexType(type);
        return self();
    }
    /**
     * Self cast.
     * @return
     */
    @SuppressWarnings("unchecked")
    protected X self() {
        return (X) this;
    }
}
