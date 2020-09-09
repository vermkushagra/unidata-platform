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


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;

/**
 * Component responsible for working with mappings over abstract search engine.
 */
public interface MappingComponent {

    /**
     * Drops a mapping for entity in an abstract search engine .
     *
     * @param entityName -the context
     * @param storageId  - storage id
     * @param searchType  - type of dropped entity
     */
    void dropMapping(@Nonnull String entityName, @Nonnull EntitySearchType searchType, @Nullable String storageId);

    /**
     * Drops a mappings for entities in an abstract search engine.
     *
     * @param entitiesNames -the collection of entities names.
     * @param storageId     - storage id
     * @param searchType  - type of dropped entities
     */
    void dropMappings(@Nonnull Collection<String> entitiesNames, @Nonnull EntitySearchType searchType, @Nullable String storageId);

    /**
     * Update mapping for lookup entity in an abstract search engine
     *
     * @param lookupEntityDef - the basis for mapping
     * @param storageId       - storage id
     * @return result of operation
     */
    boolean updateLookupEntityMapping(@Nonnull LookupEntityDef lookupEntityDef, @Nullable String storageId);

    /**
     * Update mapping for entity in an abstract search engine
     *
     * @param entity         - the basis for mapping
     * @param nestedEntities - related with the entity nested entities.
     * @param storageId      - storage id
     * @return result of operation
     */
    boolean updateEntityMapping(@Nonnull EntityDef entity, @Nonnull Collection<NestedEntityDef> nestedEntities,
                                @Nullable String storageId);

    /**
     * Update mapping for relation entity in an abstract search engine
     *
     * @param relation         - the relation for mapping
     * @param storageId      - storage id
     * @return result of operation
     */
    boolean updateRelationDefMapping (RelationDef relation, @Nullable String storageId);


    /**
     * Update mapping for index which contain information about model.
     *
     * @param storageId - storage id
     * @return result of operation
     */
    boolean updateModelSearchElementsMapping(@Nonnull String storageId);

    /**
     * Update mapping for index which contain information about classifiers.
     *
     * @param storageId - storage id
     * @return result of operation
     */
    boolean updateClassifierSearchNodesMapping(@Nonnull String storageId);

    /**
     * Update mapping for index which contain information about audit.
     *
     * @param storageId - storage id
     * @return result of operation
     */
    boolean updateAuditSearchNodesMapping(@Nonnull String storageId);
}
