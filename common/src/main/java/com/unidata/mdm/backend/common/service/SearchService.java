package com.unidata.mdm.backend.common.service;

import java.util.Collection;
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

    /**
     * Count errors by severity
     *
     * @param severity severity
     * @param ctx      context.
     * @return number of errors.
     */
    long countErrorsBySeverity(String severity, SearchRequestContext ctx);

    /**
     * Count records with errors.
     *
     * @param ctx search context.
     * @return number of records with errors.
     */
    long countErrorRecords(SearchRequestContext ctx);

    /**
     * Search multiple.
     * @param ctxts contexts
     * @return map
     */
    @Deprecated
    Map<SearchRequestContext, SearchResultDTO> search(Collection<SearchRequestContext> ctxts);

    /**
     * Search multiple.
     * @param ctxts the contexts
     * @param sayt search as you type mode
     * @return map
     */
    @Deprecated
    Map<SearchRequestContext, SearchResultDTO> search(Collection<SearchRequestContext> ctxts, boolean sayt);

    /**
     * Returns true, if a given ID exists.
     *
     * @param type object's type
     * @param id   the object id
     * @return true, if exists, false otherwise
     */
    @Deprecated
    boolean exists(String type, String id);

    /**
     * Full blown search method, taking parameters from the context.
     *
     * @param ctx  the search context
     * @param sayt use SAYT version
     * @return search results
     */
    @Deprecated
    SearchResultDTO search(SearchRequestContext ctx, boolean sayt);

    /**
     * Returns number of all existing records for an entity.
     *
     * @param type the name of the type
     * @return count
     */
    @Deprecated
    long countAll(String type);
}