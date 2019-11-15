package org.unidata.mdm.meta.type.search;

import javax.annotation.Nonnull;

import org.unidata.mdm.search.type.IndexType;

/**
 * @author Mikhail Mikhailov on Oct 11, 2019
 */
public enum ModelIndexType implements IndexType {
    /**
     * Classifier data.
     */
    MODEL("model_element");
    /**
     * Index name.
     */
    public static final String INDEX_NAME = "model";
    /**
     * Name of type
     */
    private final String type;
    /**
     * Constructor.
     * @param type the name of the type
     */
    ModelIndexType(String type) {
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
        return false;
    }
}
