package com.unidata.mdm.backend.common.service;

import java.util.Map;

import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;

public interface SearchService {

    /**
     * Returns number of all existing records for an entity.
     *
     * @param type the name of the type
     * @return count
     */
    long countAllIndexedRecords(String type);

    /**
     * Complex Search. Supported two types:
     *  - HIERARCHICAL: linked types search
     *  - MULTI: multi index search
     * @param searchRequest - search request
     * @return map where keys is a main request in related request or each request in multi.
     */
    Map<SearchRequestContext, SearchResultDTO> search(ComplexSearchRequestContext searchRequest);

    /**
     * Full blown search method, taking parameters from the context.
     *
     * @param ctx the search context
     * @return search results
     */
    SearchResultDTO search(SearchRequestContext ctx);



}