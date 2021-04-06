package com.unidata.mdm.backend.common.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.dto.data.model.GetEntitiesByRelationSideDTO;
import com.unidata.mdm.backend.common.dto.data.model.GetEntitiesGroupsDTO;
import com.unidata.mdm.backend.common.dto.data.model.GetEntityDTO;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.types.RelationSide;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.ComplexAttributeDef;
import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.Model;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SourceSystemDef;

/**
 * The Interface MetaModelService.
 */
public interface MetaModelService {
    /**
     * Exports meta model.
     *
     * @param storageId the storage ID
     * @return meta model
     */
    Model exportModel(String storageId);

    /**
     * Gets list of active storage ids.
     *
     * @return list
     */
    List<String> getStorageIdsList();

    /**
     * Gets all entities list.
     *
     * @return list
     */
    @Nonnull
    List<EntityDef> getEntitiesList();

    /**
     * Gets all entities list.
     *
     * @return list
     */
    @Nonnull
    List<EntityDef> getUnfilteredEntitiesList();

    /**
     * Gets the root group.
     *
     * @param storageId
     *            the storage id
     * @return the root group
     */
    EntitiesGroupDef getRootGroup(String storageId);

    /**
     * Gets an entity by id.
     *
     * @param id the id
     * @return entity container or null
     */
    GetEntityDTO getEntityById(String id);

    /**
     * Returns entities set view, filtered by side relation.
     *
     * @param id   entity Id
     * @param side relation side
     * @return filtered view
     */
    GetEntitiesByRelationSideDTO getEntitiesFilteredByRelationSide(String id, RelationSide side);

    /**
     * Gets an entity by name from cache.
     *
     * @param entityName the name
     * @return entity or null
     */
    EntityDef getEntityByIdNoDeps(String entityName);

    /**
     * Gets an attribute of a top level entity by path.
     *
     * @param <T>
     *            the generic type
     * @param entityName
     *            entity name
     * @param path
     *            path
     * @return attribute or null
     */
    <T extends AbstractAttributeDef> T getEntityAttributeByPath(
            String entityName, String path);

    /**
     * Gets attribute by path.
     *
     * @param <T>
     *            the generic type
     * @param id
     *            entity or lookup entity id
     * @param path
     *            the path
     * @return attribute
     */
    <T extends AbstractAttributeDef> T getAttributeByPath(String id,
                                                          String path);

    /**
     * Checks, whether the name is an entity name.
     *
     * @param entityId the name
     * @return true, if so, false otherwise
     */
    boolean isEntity(String entityId);

    /**
     * Checks, whether the name is an nested entity name.
     *
     * @param entityId the name
     * @return true, if so, false otherwise
     */
    boolean isNestedEntity(String entityId);

    /**
     * Gets lookup entities list.
     *
     * @return list
     */
    @Nonnull
    List<LookupEntityDef> getLookupEntitiesList();

    /**
     * Gets lookup entities list.
     *
     * @return list
     */
    @Nonnull
    List<LookupEntityDef> getUnfilteredLookupEntitiesList();

    /**
     * Gets a lookup entity by id.
     *
     * @param id the id
     * @return lookup entity
     */
    LookupEntityDef getLookupEntityById(String id);

    /**
     * Gets the group wrappers keyed by wrapper id.
     *
     * @return map of group wrappers keyed by wrapper id
     */
    @Nonnull
    GetEntitiesGroupsDTO getEntitiesGroups();

    /**
     * Gets an attribute of a lookup entity by path.
     *
     * @param <T>
     *            the generic type
     * @param entityName
     *            entity name
     * @param path
     *            path
     * @return attribute or null
     */
    <T extends AbstractAttributeDef> T getLookupEntityAttributeByPath(
            String entityName, String path);

    /**
     * Checks, whether the name is a lookup entity name.
     *
     * @param entityName the name
     * @return true, if so, false otherwise
     */
    boolean isLookupEntity(String entityName);

    /**
     * Gets all nested entities of a top level entity.
     *
     * @param id top level entity id
     * @return list of nested entities
     */
    List<NestedEntityDef> getNestedEntitiesByTopLevelId(String id);

    /**
     * Gets a nested entity by id.
     *
     * @param id the id
     * @return entity
     */
    Map<NestedEntityDef, List<NestedEntityDef>> getNestedEntityById(
            String id);

    /**
     * Gets a nested entity by id.
     *
     * @param id the id
     * @return entity
     */
    NestedEntityDef getNestedEntityByNoDeps(String id);

    /**
     * Gets an attribute of a top level entity by path.
     *
     * @param <T>
     *            the generic type
     * @param relationName
     *            entity name
     * @param path
     *            path
     * @return attribute or null
     */
    <T extends AbstractAttributeDef> T getRelationAttributeByPath(
            String relationName, String path);

    /**
     * Gets a relation by id.
     *
     * @param id the id
     * @return relation
     */
    RelationDef getRelationById(String id);

    /**
     * Gets relation info objects by from entity name.
     * @param entityName the name
     * @return list of relation objects
     */
    @Nonnull
    List<RelationDef> getRelationsByFromEntityName(String entityName);

    /**
     * Gets relation info objects by to entity name.
     * @param entityName the name
     * @return list of relation objects
     */
    @Nonnull
    List<RelationDef> getRelationsByToEntityName(String entityName);

    /**
     * Get classifier's names be entity name
     * @param entityName - entity name
     * @return list of classifiers for entity
     */
    List<String> getClassifiersForEntity(String entityName);

    /**
     * Gets all entities, classified by this classifier name.
     * @param classifierName the classifier name
     * @return list of entities
     */
    List<AbstractEntityDef> getClassifiedEntities(String classifierName);

    /**
     * Returns true if id denotes a relation.
     *
     * @param id the id
     * @return if the id denotes a relation
     */
    boolean isRelation(String id);

    /**
     * Gets relations list.
     *
     * @return the list
     */
    List<RelationDef> getRelationsList();

    /**
     * Gets enumeration by id.
     *
     * @param id the id
     * @return enumeration
     */
    EnumerationDataType getEnumerationById(String id);

    /**
     * Gets enumeration list.
     *
     * @return list
     */
    List<EnumerationDataType> getEnumerationsList();

    /**
     * Gets source systems list.
     *
     * @return list
     */
    List<SourceSystemDef> getSourceSystemsList();
    /**
     * Gets reversed source systems map.
     * @return map
     */
    Map<String, Integer> getReversedSourceSystems();
    /**
     * Gets naturally ordered source systems map.
     * @return map
     */
    Map<String, Integer> getStraightSourceSystems();
    /**
     * Gets the admin source system.
     *
     * @return the admin source system
     */
    SourceSystemDef getAdminSourceSystem();
    /**
     * Tells whether this source system is an admin one.
     * @param id the id
     * @return true, if so, false otherwise
     */
    boolean isAdminSourceSystem(String id);
    /**
     * Gets a source system with the given id.
     *
     * @param id the id
     * @return source system
     */
    SourceSystemDef getSourceSystemById(String id);

    /**
     * Gets the root cleanse function group.
     *
     * @return group
     */
    CleanseFunctionGroupDef getCleanseFunctionRootGroup();

    /**
     * Upsert model.
     *
     * @param ctx
     *            the ctx
     */
    void upsertModel(UpdateModelRequestContext ctx);

    /**
     * Synchronization upsert model.
     *
     * @param ctx
     *            the ctx
     */
    void synchronizationUpsertModel(UpdateModelRequestContext ctx);

    /**
     * Delete model.
     *
     * @param ctx
     *            the ctx
     */
    void deleteModel(DeleteModelRequestContext ctx);

    /**
     * Synchronization delete model.
     *
     * @param ctx
     *            the ctx
     */
    void synchronizationDeleteModel(DeleteModelRequestContext ctx);

    /**
     * Get relations for the specified entity.
     *
     * @param entityName
     *            entity name
     * @param includeTo
     *            include incoming relations
     * @param includeFrom
     *            include outgoing relations
     * @return incomming relations
     */
    Map<RelationDef, EntityDef> getEntityRelations(String entityName, boolean includeTo, boolean includeFrom);

    /**
     * Get relations for the specified entity filtered by type.
     *
     * @param entityName
     *            entity name
     * @param types
     *            specific relation type
     * @param includeTo
     *            include incoming relations
     * @param includeFrom
     *            include outgoing relations
     * @return incomming relations
     */
    public Map<RelationDef, EntityDef> getEntityRelationsByType(String entityName, List<RelType> types, boolean includeTo, boolean includeFrom);

    /**
     * Get Lookups that are linked for the specified entity.
     *
     * @param entityName entity name
     * @return incomming Lookups that are linked for the specified entity.
     */
    Map<LookupEntityDef, Set<AttributeInfoHolder>> getEntityToLinkedLookups(String entityName);

    /**
     * Get Lookups that are linked for the specified lookup.
     *
     * @param lookupName lookup name
     * @return incomming Lookups that are linked for the specified entity.
     */
    Map<LookupEntityDef, Set<AttributeInfoHolder>> getLookupEntityToLinkedLookups(String lookupName);

    /**
     * Get entities and their attributes (only attributes that refer) that refer to the specified lookup.
     *
     * @param lookupName lookup name
     * @return entities and their attributes (only attributes that refer) that refer to the specified lookup.
     */
    Map<EntityDef, Set<AttributeInfoHolder>> getEntitiesReferencingThisLookup(String lookupName);

    /**
     * Get lookups and their attributes (only attributes that refer) that refer to the specified lookup.
     *
     * @param lookupName lookup name
     * @return lookups and their attributes (only attributes that refer) that refer to the specified lookup.
     */
    Map<LookupEntityDef, Set<AttributeInfoHolder>> getLookupsReferencingThisLookup(String lookupName);

    /**
     * Gets simple and code attributes of an object with the given id.
     *
     * @param id the object id
     * @return attributes list
     */
    List<AbstractAttributeDef> getSimpleAttributes(String id);

    /**
     * Gets complex attributes of an object with the given id.
     *
     * @param id the object id
     * @return attributes list
     */
    List<ComplexAttributeDef> getComplexAttributes(String id);

    /**
     * Gets the whole attributes map
     * @param id entity id
     * @return map
     */
    Map<String, AttributeInfoHolder> getAttributesInfoMap(String id);

    /**
     * find main displayable attribute names for entity
     * @param entityName entity name
     * @return attributes list
     */
    List<String> findMainDisplayableAttrNamesSorted(String entityName);
    /**
     * Apply draft by draft id.
     * @param draftId draft id.
     */

	void applyDraft(String draftId);

	List<NestedEntityDef> getNestedEntitiesList();
}
