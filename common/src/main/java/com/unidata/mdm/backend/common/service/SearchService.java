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

    /**
     * Count errors by severity
     *
     * @param severity severity
     * @param ctx      context.
     * @return number of errors.
     */
    long countErrorsBySeverity(String severity, SearchRequestContext ctx);

}