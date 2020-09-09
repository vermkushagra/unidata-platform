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

package com.unidata.mdm.backend.service.search.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.search.types.ServiceSearchType;

/**
 * Component responsible for working with indexes over abstract search engine.
 */
public interface IndexComponent {

    /**
     * Default index for indexing meta model data
     */
    String MODEL_SEARCH_INDEX = ServiceSearchType.MODEL.getIndexName();

    /**
     * Default index for indexing classifiers data
     */
    String CLASSIFIER_NODE_SEARCH_INDEX = ServiceSearchType.CLASSIFIER.getIndexName();

    /**
     * Default index for indexing audit data
     */
    String AUDIT_SEARCH_INDEX = ServiceSearchType.AUDIT.getIndexName();

    /**
     * Collection of reserved index names.
     */
    Collection<String> RESERVED_INDEX_NAMES = Arrays.asList(MODEL_SEARCH_INDEX, CLASSIFIER_NODE_SEARCH_INDEX, AUDIT_SEARCH_INDEX);

    /**
     * Creates a new index but if the index already exist operation do nothing.
     *
     * @param ctx        the context to use
     * @param properties properties, if properties is not defined will be used properties from backed.properties
     * @return true if successful, false otherwise
     */
    boolean safeCreateIndex(@Nonnull final SearchRequestContext ctx, @Nullable Properties properties);

    /**
     * Creates a new index even if the index is existed.
     *
     * @param ctx        the context to use
     * @param properties properties, if properties is not defined will be used properties from backed.properties
     * @return true if successful, false otherwise
     */
    boolean forceCreateIndex(@Nonnull final SearchRequestContext ctx, @Nullable Properties properties);


    /**
     * Tells if an index exists.
     *
     * @param ctx the context
     * @return true, if exists, false otherwise
     */
    boolean indexExists(@Nonnull final SearchRequestContext ctx);

    /**
     * Drops an index.
     *
     * @param ctx the context to use
     * @return true, if successful, false otherwise
     */
    boolean dropIndex(@Nonnull final SearchRequestContext ctx);

    /**
     * Triggers manual refresh on an index.
     *
     * @param ctx the context to use
     * @param wait will wait for foreground thread to complete forever or until interrupted
     * @return true, if successful, false otherwise
     */
    boolean refreshIndex(@Nonnull final SearchRequestContext ctx, boolean wait);

    /**
     * Sets index refresh interval.
     *
     * @param ctx the context to use
     * @param value the value
     * @return true, if successful, false otherwise
     */
    boolean setIndexRefreshInterval(@Nonnull final SearchRequestContext ctx, String value);
    /**
     * Sets index settings.
     *
     * @param ctx the context
     * @param settings the settings
     * @return true, if successful, false otherwise
     */
    boolean setIndexSettings(SearchRequestContext ctx, Map<String, Object> settings);
    /**
     * Sets cluster settings.
     *
     * @param settings the settings to set
     * @param persistent type of settings
     * @return true, if successful, false otherwise
     */
    boolean setClusterSettings(Map<String, Object> settings, boolean persistent);
    /**
     * Closes an index.
     * @param ctx the context
     * @return true, if successful, false otherwise
     */
    boolean closeIndex(SearchRequestContext ctx);
    /**
     * Opens an index.
     * @param ctx the context
     * @return true, if successful, false otherwise
     */
    boolean openIndex(SearchRequestContext ctx);
}
