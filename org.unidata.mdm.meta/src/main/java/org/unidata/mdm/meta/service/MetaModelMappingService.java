package org.unidata.mdm.meta.service;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.unidata.mdm.core.type.model.ModelSearchObject;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov on Oct 14, 2019
 */
public interface MetaModelMappingService {
    /**
     * Creates meta model index, if needed.
     */
    void ensureMetaModelIndex();
    /**
     * Performs full clean from MM digest index.
     */
    void cleanMetaModelIndex();
    /**
     * Puts info about the given objects to the MM digest index.
     * @param objects the objects
     */
    void putToMetaModelIndex(Collection<ModelSearchObject> objects);
    /**
     * Removes entity info from MM digest.
     * @param entityNames the names
     */
    void removeFromMetaModelIndex(String... entityNames);
    /**
     * Drops all the indexes for all the entities/lookups, found in current model.
     * @param storageId the storage id
     */
    void dropAllEntityIndexes(@Nullable String storageId);
    /**
     * Updates mappings for given MM objects (possibly forcibly).
     * @param storageId the storage id, current user's one will be taken if null
     * @param force drop / create (data will be lost)
     * @param names the names to process
     */
    void updateEntityMappings(@Nullable String storageId, boolean force, List<String> names);
    /**
     * Updates mappings for given MM objects (possibly forcibly).
     * @param storageId the storage id, current user's one will be taken if null
     * @param force drop / create (data will be lost)
     * @param names the names to process
     */
    void updateEntityMappings(@Nullable String storageId, boolean force, String... names);
    /**
     * Puts / updates an entity mapping.
     * @param storageId the storage id to use
     * @param force force recreate
     * @param entity the entity
     * @param nested nested (complex) entities
     */
    void updateEntityMapping(@Nullable String storageId, boolean force, EntityDef entity, List<NestedEntityDef> nested);
    /**
     * Puts / updates a lookup mapping.
     * @param storageId the storage id to use
     * @param force force recreate
     * @param entity the entity
     */
    void updateLookupMapping(@Nullable String storageId, boolean force, LookupEntityDef entity);
    /**
     * Updates relation mappings.
     * Relations are indexed on both sides ('from' and 'to').
     * If entityName is used, relation mappings will be updated only on the given entity's side.
     * If entityName is not used, relation mappings will be created ob both sides.
     * @param storageId the storage id, current user's one will be taken if null
     * @param entityName the side relation name
     * @param relationNames relation names to process
     */
    void updateRelationMappings(@Nullable String storageId, String entityName, List<String> relationNames);
    /**
     * Updates relation mappings.
     * Relations are indexed on both sides ('from' and 'to').
     * If entityName is used, relation mappings will be updated only on the given entity's side.
     * If entityName is not used, relation mappings will be created ob both sides.
     * @param storageId the storage id, current user's one will be taken if null
     * @param entityName the side relation name
     * @param relationNames relation names to process
     */
    void updateRelationMappings(@Nullable String storageId, String entityName, String... relationNames);
    /**
     * Updates relation mappings.
     * Relations are indexed on both sides ('from' and 'to').
     * If entityName is used, relation mappings will be updated only on the given entity's side.
     * If entityName is not used, relation mappings will be created ob both sides.
     * @param storageId the storage id, current user's one will be taken if null
     * @param entityName the side relation name
     * @param relation relation to process
     */
    void updateRelationMapping(@Nullable String storageId, String entityName, RelationDef relation);
}
