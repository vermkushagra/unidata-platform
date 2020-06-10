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

package com.unidata.mdm.backend.common.integration.exits;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;

/**
 * @author Dmitry Kopin
 * Search listener interface.
 */
public interface SearchListener {
    /**
     * Method is called before search operation,
     * @param ctx search request context
     * @return {@link ExitResult} with information about execution user exit
     */
     default ExitResult beforeSearch(SearchRequestContext ctx){
         return null;
     }
    /**
     * Method is called after search operation and after build search result
     * @param ctx search request context
     * @param searchResult search result
     * @return {@link ExitResult} with information about execution user exit
     */
    default ExitResult afterSearch(SearchRequestContext ctx, SearchResultDTO searchResult){
        return null;
    }
}
