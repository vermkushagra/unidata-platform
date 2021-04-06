package com.unidata.mdm.backend.common.search;

import com.unidata.mdm.backend.common.search.types.SearchType;

/**
 * Search field
 */
public interface SearchField {

    /**
     * @return filed name in search index
     */
    String getField();

    /**
     * @return search type
     */
    SearchType linkedSearchType();
}
