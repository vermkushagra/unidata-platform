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
