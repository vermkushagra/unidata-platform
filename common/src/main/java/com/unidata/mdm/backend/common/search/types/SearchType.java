package com.unidata.mdm.backend.common.search.types;

import javax.annotation.Nonnull;

/**
 * Types in index
 */
public interface SearchType {

    /**
     * @return name of type
     */
    @Nonnull
    String getName();

    /**
     * @param searchType - search type
     * @return true, if search types are related
     */
    boolean isRelatedWith(SearchType searchType);
}
