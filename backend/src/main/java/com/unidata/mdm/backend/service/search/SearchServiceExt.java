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

package com.unidata.mdm.backend.service.search;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.service.model.ModelSearchObject;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;

public interface SearchServiceExt extends SearchService {

    /**
     * Creates index with name 'name', forcing recreate (drop and create), if it
     * already exists.
     *
     * @param name        name of the index to create
     * @param storageId   the storage id to use
     * @param forceCreate force create if exists
     * @return true if successful, false otherwise
     * @throws IOException
     */
    boolean createIndex(String name, String storageId, Properties properties, boolean forceCreate) throws IOException;

    /**
     * Drops an index.
     *
     * @param name      name of the index
     * @param storageId the storage id to use
     * @return true if successful, false otherwise
     */
    boolean dropIndex(String name, String storageId);

    /**
     * Refresh an index.
     *
     * @param name      name of the index
     * @param storageId the storage id to use
     * @param wait if true, try to wait forever for background thread to complete or the waiting thread gets interrupted
     * @return true if successful, false otherwise
     */
    boolean refreshIndex(String name, String storageId, boolean wait);

    /**
     * Tells if an index exists.
     *
     * @param name      name of the index
     * @param storageId the storage id to use
     * @return true, if exists, false otherwise
     */
    boolean indexExists(String name, String storageId);

    /**
     * sets index refresh interval.
     *
     * @param name      name of the index
     * @param storageId the storage id to use
     * @return true, if exists, false otherwise
     */
    boolean setIndexRefreshInterval(String name, String storageId, String value);

    /**
     * Sets index settings.
     *
     * @param name the name of the index
     * @param storageId the storage id to use
     * @param settings the settings
     * @return true, if successful, false otherwise
     */
    boolean setIndexSettings(String name, String storageId, Map<String, Object> settings);

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
     *
     * @param name      name of the index
     * @param storageId the storage id to use
     * @return true, if successful, false otherwise
     */
    boolean closeIndex(String name, String storageId);

    /**
     * Opens an index.
     *
     * @param name      name of the index
     * @param storageId the storage id to use
     * @return true, if successful, false otherwise
     */
    boolean openIndex(String name, String storageId);

    /**
     * Tells if an model index exists.
     *
     * @param storageId the storage id to use
     * @return true, if exists, false otherwise
     */
    boolean modelIndexExists(String storageId);

    /**
     * Tells if an classifier index exists.
     *
     * @return true, if exists, false otherwise
     */
    boolean classifierIndexExist(String storageId);

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.search.impl.MappingComponent#dropMapping(java.lang.String,java.lang.String)
     */
    void dropEtalonDataMapping(String entityName, String storageId);

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.search.impl.MappingComponent#dropMappings(java.util.Collection,java.lang.String)
     */
    void dropEtalonDataMappings(Collection<String> entitiesNames, String storageId);

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.search.impl.MappingComponent#updateLookupEntityMapping(com.unidata.mdm.meta.LookupEntityDef,java.lang.String)
     */
    boolean updateLookupEntityMapping(LookupEntityDef lookupEntityDef, String storageId);

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.search.impl.MappingComponent#updateEntityMapping(com.unidata.mdm.meta.EntityDef,java.util.Collection,java.lang.String)
     */
    boolean updateEntityMapping(EntityDef entity, Collection<NestedEntityDef> nestedEntities, String storageId);
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.search.impl.MappingComponent#updateRelationMapping(com.unidata.mdm.meta.RelationDef,java.lang.String)
     */
    boolean updateRelationMapping(RelationDef entity, String storageId);

    /**
     * Create index for indexing of meta model data.
     * @param storageId - storage id
     */
    void createModelIndex(String storageId);

    /**
     * Create index for indexing of meta model data.
     * @param storageId - storage id
     * @param forceCreate force create index flag
     */
    void createAuditIndex(String storageId, boolean forceCreate);

    /**
     * Create index for indexing of meta model data.
     * @param storageId - storage id
     */
    void createClassifierIndex(String storageId);

    /**
     * @param storageId          - storage id
     * @param modelSearchObjects - elements for indexing
     * @return true if index was successfully recreated, otherwise false.
     */
    boolean indexModelSearchElements(String storageId, Collection<ModelSearchObject> modelSearchObjects);

    /**
     *
     * @param storageId         - storage id
     * @param event - audit event
     * @return true if index was successfully recreated, otherwise false.
     */
    boolean indexAuditEvent(String storageId, Event event);

    /**
     * @param storageId - storage id
     * @param node      - classifier node
     * @return true if index was successfully recreated, otherwise false.
     */
    boolean indexClassifierNode(String storageId, ClsfNodeDTO node);

    /**
     * @param storageId - storage id
     * @param nodes      - classifier nodes
     * @return true if index was successfully recreated, otherwise false.
     */
    boolean indexClassifierNodes(@Nullable String storageId, @Nonnull List<ClsfNodeDTO> nodes);

    /**
     * Processes indexing request.
     * @param ctx indexing context
     * @return true, if successful, false otherwise
     */
    boolean index(IndexRequestContext ctx);

    /**
     * Process a bulk portion of indexing requests.
     * @param ctxts the contexts
     * @return true, if successful, false otherwise
     */
    boolean index(final List<IndexRequestContext> ctxts);

    /**
     * @param entityName - entity name
     * @param etalonId - etalon id
     * @param fields - marked fields
     * @return true if successful, false otherwise
     */
    boolean mark(String entityName, String etalonId, @Nonnull Map<RecordHeaderField, Object> fields);

    /**
     * @param context - search request
     * @param fields  - marked fields
     * @return true if successful, false otherwise
     */
    boolean mark(@Nonnull SearchRequestContext context, @Nonnull Map<? extends SearchField, Object> fields);

    /**
     * @param context - search request
     * @param fields  - marked fields
     * @return true if successful, false otherwise
     */
    boolean mark(@Nonnull ComplexSearchRequestContext context, @Nonnull Map<? extends SearchField, Object> fields);
    /**
     * Delete using DBQ.
     * @param ctx the context
     * @return true, if successful, false otherwise (some failed shards returned)
     */
    boolean deleteAll(SearchRequestContext ctx);

    /**
     * Result of search will be deleted.
     * Notice : post filters, facets, roles won't be applied
     * @param requestForDelete - search request
     * @return true, if successful, false otherwise
     */
    boolean deleteFoundResult(SearchRequestContext requestForDelete);
    /**
     * Result of search will be deleted.
     * Notice : post filters, facets, roles won't be applied
     * @param requestForDelete - search request
     * @return true, if successful, false otherwise
     */
    boolean deleteFoundResult(SearchRequestContext requestForDelete, boolean refreshImmediate);

    /**
     * Result of search will be deleted.
     * Notice : post filters, facets, roles won't be applied
     * @param requestForDelete - search request
     * @return true, if successful, false otherwise
     */
    boolean deleteFoundResult(ComplexSearchRequestContext requestForDelete);

}