/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.unidata.mdm.search.service;

import java.util.Collection;
import java.util.Map;

import org.unidata.mdm.search.context.ComplexSearchRequestContext;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.search.context.MappingRequestContext;
import org.unidata.mdm.search.context.SearchRequestContext;
import org.unidata.mdm.search.context.TypedSearchContext;
import org.unidata.mdm.search.dto.SearchResultDTO;

public interface SearchService {
    // From search component
    /**
     * Returns number of all existing records for an entity.
     *
     * @param type the name of the type
     * @return count
     */
    long countAll(SearchRequestContext ctx);
    /**
     * Delete using DBQ.
     *
     * @param ctx the context
     * @return true, if successful, false otherwise (some failed shards returned)
     */
    boolean deleteAll(SearchRequestContext ctx);
    /**
     * Delete using DBQ.
     *
     * @param ctx the context
     * @param refresh forcibly refresh index
     * @return true, if successful, false otherwise
     */
    boolean deleteAll(SearchRequestContext ctx, boolean refresh);
    /**
     * Result of search will be deleted.
     * Notice : post filters, facets, roles won't be applied
     *
     * @param requestForDelete - search request
     * @return true, if successful, false otherwise
     */
    boolean deleteFoundResult(SearchRequestContext requestForDelete);

    /**
     * Result of search will be deleted.
     * Notice : post filters, facets, roles won't be applied
     *
     * @param requestForDelete - search request
     * @return true, if successful, false otherwise
     */
    boolean deleteFoundResult(SearchRequestContext requestForDelete, boolean refreshImmediate);

    /**
     * Result of search will be deleted.
     * Notice : post filters, facets, roles won't be applied
     *
     * @param requestForDelete - search request
     * @return true, if successful, false otherwise
     */
    boolean deleteFoundResult(ComplexSearchRequestContext requestForDelete);
    /**
     * Complex Search. Supported two types:
     *  - HIERARCHICAL: linked types search
     *  - MULTI: multi index search
     *
     * @param searchRequest - search request
     * @return map where keys is a main request in related request or each request in multi.
     */
    Map<SearchRequestContext, SearchResultDTO> search(ComplexSearchRequestContext searchRequest);
    /**
     * Search method, taking parameters from the context.
     *
     * @param ctx the search context
     * @return search results
     */
    SearchResultDTO search(SearchRequestContext ctx);
    // From mapping component
    /**
     * Does process a mapping request.
     *
     * @param ctx the request
     * @return true, if successful, false otherwise
     */
    boolean process(MappingRequestContext ctx);
    /**
     * Attempts to drop an index, identified by the supplied context.
     *
     * @param ctx the context
     * @return true if succesful
     */
    boolean dropIndex(TypedSearchContext ctx);
    /**
     * Checks existence of an index, identified by the supplied context.
     *
     * @param ctx the context
     * @return true, if exists
     */
    boolean indexExists(TypedSearchContext ctx);
    /**
     * Attempts to refresh an index, identified by the supplied context.
     *
     * @param ctx the context
     * @param wait wait for completion or not
     * @return true if succesful
     */
    boolean refreshIndex(TypedSearchContext ctx, boolean wait);
    /**
     * Attempts to close an index, identified by the supplied context.
     *
     * @param ctx the context
     * @return true if succesful
     */
    boolean closeIndex(TypedSearchContext ctx);
    /**
     * Attempts to open an index, identified by the supplied context.
     *
     * @param ctx the context
     * @return true if succesful
     */
    boolean openIndex(TypedSearchContext ctx);
    /**
     * Attempts to set index settings.
     *
     * @param ctx the context
     * @param settings the setting to set (direct ES format)
     * @return true if succesful
     */
    boolean setIndexSettings(TypedSearchContext ctx, Map<String, Object> settings);
    /**
     * Attempts to set cluster wide settings.
     *
     * @param settings the settings to set (direct ES format)
     * @param persistent set the settings persistently or not
     * @return true if succesful
     */
    boolean setClusterSettings(Map<String, Object> settings, boolean persistent);
    // From index component
    /**
     * Does indexing processing.
     * @param ctx the context to process
     * @return true, if successful, false otherwise
     */
    void process(IndexRequestContext ctx);
    /**
     * Does indexing processing.
     * @param ctxs the contexts to process
     * @return true, if successful, false otherwise
     */
    void process(Collection<IndexRequestContext> ctxs);
    /**
     * Does indexing processing.
     * @param ctxs the contexts to process
     * @return true, if successful, false otherwise
     */
    void process(Collection<IndexRequestContext> ctxs, boolean refresh);
    // Settings
    /**
     * @return the numberOfShardsForSystem
     */
    String getNumberOfShardsForSystem();
    /**
     * @return the numberOfReplicasForSystem
     */
    String getNumberOfReplicasForSystem();
    /**
     * State of the system setting 'unidata.data.refresh.immediate'.
     * @return true, if current system settings tells to refresh indexed records immediatly. False otherwise.
     */
    boolean isRefreshImmediate();
}