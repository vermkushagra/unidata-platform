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

package com.unidata.mdm.backend.api.rest.util;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.api.rest.util.SearchResultHitModifierImpl.ProcessingElements;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;

/**
 * @author Dmitry Kopin on 30.10.2017.
 */
public interface SearchResultHitModifier {

    /**
     * Method provide search with after processing which replace lookup entity code id to main displayable attrs sequence.
     *
     * @param cCtx          the search context
     * @param searchResult result of search by ctx
     */
    void modifySearchResult(@Nonnull SearchResultDTO searchResult, @Nonnull ComplexSearchRequestContext cCtx);

    /**
     * Method provide search with after processing which modify all processed elements to display view.
     * @param searchResult result of search by ctx
     * @param ctx          the search context
     * @param attributesSource the source of attributes (entity, relation, etc.)
     * @param fieldPrefix needed for relations and classifiers
     */
    void modifySearchResult(@Nonnull SearchResultDTO searchResult, @Nonnull SearchRequestContext ctx,
            String attributesSource, String fieldPrefix);

    /**
     * Method provide search with after processing which modify all processed elements to display view.
     *
     * @param ctx          the search context
     * @param searchResult result of search by ctx
     */
    void modifySearchResult(@Nonnull SearchResultDTO searchResult, @Nonnull SearchRequestContext ctx);

    /**
     * Method provide search with after processing which modify processed elements to display view.
     *
     * @param ctx          the search context
     * @param searchResult result of search by ctx
     * @param enumSet elements to process
     */
    void modifySearchResult(@Nonnull SearchResultDTO searchResult, @Nonnull SearchRequestContext ctx,
                                   EnumSet<SearchResultHitModifierImpl.ProcessingElements> enumSet);

    /**
     * Method provide search with after processing which modify all processed elements to display view.
     * @param searchResult result of search by ctx
     * @param ctx          the search context
     * @param attributesSource the source of attributes (entity, relation, etc.)
     * @param fieldPrefix needed for relations and classifiers
     * @param elements processing elements
     * @param appendDisplayValues TODO
     */
    void modifySearchResult(@Nonnull SearchResultDTO searchResult, @Nonnull SearchRequestContext ctx,
            String attributesSource, String fieldPrefix, Set<ProcessingElements> elements, boolean appendDisplayValues);


}
