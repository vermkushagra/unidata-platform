package com.unidata.mdm.backend.common.search.types;

import javax.annotation.Nonnull;

/**
 * Search type for entity indexes.
 */
public enum EntitySearchType implements HierarchicalSearchType {

    /**
     * Etalon type
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
    ETALON_DATA("data"),
    /**
     * Relation type
     */
    ETALON_RELATION("relation"),
    /**
     * Classifier data.
     */
    CLASSIFIER("classifier"),
    /**
     * Matching.
     */
    MATCHING_HEAD("matching_head"){
        @Override
        public boolean isTopType() {
            return true;
        }
    },
    /**
     * Matching.
     */
    MATCHING("matching"){
        @Override
        public HierarchicalSearchType getTopType() {
            return EntitySearchType.MATCHING_HEAD;
        }
    };
    /**
     * Name of type
     */
    private final String type;

    EntitySearchType(String type) {
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

    @Override
    public boolean isRelatedWith(SearchType searchType) {
        return searchType instanceof EntitySearchType;
    }

    @Nonnull
    @Override
    public HierarchicalSearchType getTopType() {
        return EntitySearchType.ETALON;
    }

    @Override
    public boolean isTopType() {
        return false;
    }
}
