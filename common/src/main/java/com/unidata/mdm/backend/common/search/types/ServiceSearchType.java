package com.unidata.mdm.backend.common.search.types;

import javax.annotation.Nonnull;

/**
 * Search type for service indexes.
 */
public enum ServiceSearchType implements SearchType {
    /**
     * Model elements for search
     */
    MODEL("model_element", "model"),
    /**
     * Audit
     */
    AUDIT("audit_element", "audit"),
    /**
     * Classifier type
     */
    CLASSIFIER("clsf_element", "classifier");
    /**
     * Name of type
     */
    private final String type;

    /**
     * Index name
     */
    private final String indexName;

    ServiceSearchType(String type, String indexName) {
        this.type = type;
        this.indexName = indexName;
    }

    /**
     * @return index name;
     */
    public String getIndexName() {
        return indexName;
    }

    /**
     * @return name of type
     */
    @Nonnull
    @Override
    public String getName() {
        return type;
    }

    @Override
    public boolean isRelatedWith(SearchType searchType) {
        return false;
    }
}
