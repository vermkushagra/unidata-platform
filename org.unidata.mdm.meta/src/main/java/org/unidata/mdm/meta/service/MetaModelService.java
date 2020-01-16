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

package org.unidata.mdm.meta.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.EntityModelElement;
import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.core.type.model.ModelElement;
import org.unidata.mdm.meta.AbstractAttributeDef;
import org.unidata.mdm.meta.ComplexAttributeDef;
import org.unidata.mdm.meta.EntitiesGroupDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.EnumerationDataType;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.RelType;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.SourceSystemDef;
import org.unidata.mdm.meta.VersionedObjectDef;
import org.unidata.mdm.meta.context.DeleteModelRequestContext;
import org.unidata.mdm.meta.context.GetModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.dto.GetEntitiesByRelationSideDTO;
import org.unidata.mdm.meta.dto.GetEntitiesGroupsDTO;
import org.unidata.mdm.meta.dto.GetEntityDTO;
import org.unidata.mdm.meta.dto.GetModelDTO;
import org.unidata.mdm.meta.service.impl.facades.AbstractModelElementFacade;
import org.unidata.mdm.meta.type.RelationSide;
import org.unidata.mdm.system.service.AfterContextRefresh;

/**
 * The Interface MetaModelService.
 */
public interface MetaModelService extends AfterContextRefresh {
    /**
     * Upsert model.
     *
     * @param ctx
     *            the ctx
     */
    void upsertModel(UpdateModelRequestContext ctx);
    /**
     * Delete model.
     *
     * @param ctx
     *            the ctx
     */
    void deleteModel(DeleteModelRequestContext ctx);
    /**
     * Gets the model.
     * @param ctx the context
     */
    GetModelDTO getModel(GetModelRequestContext ctx);
    /**
     * Exports meta model.
     *
     * @param storageId the storage ID
     * @return meta model
     */
    Model exportModel(String storageId);

    /**
     * generate empty metamodel
     *
     * @return
     */
    Model exportEmptyModel();

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
     * Checks, whether the name is a lookup entity name.
     *
     * @param entityName the name
     * @return true, if so, false otherwise
     */
    boolean isLookupEntity(String entityName);

    /**
     * Returns true if id denotes a relation.
     *
     * @param id the id
     * @return if the id denotes a relation
     */
    boolean isRelation(String id);

    /**
     * Tells whether this source system is an admin one.
     * @param id the id
     * @return true, if so, false otherwise
     */
    boolean isAdminSourceSystem(String id);

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
     * Gets a source system with the given id.
     *
     * @param id the id
     * @return source system
     */
    SourceSystemDef getSourceSystemById(String id);

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
    Map<LookupEntityDef, Set<AttributeModelElement>> getEntityToLinkedLookups(String entityName);

    /**
     * Get Lookups that are linked for the specified lookup.
     *
     * @param lookupName lookup name
     * @return incomming Lookups that are linked for the specified entity.
     */
    Map<LookupEntityDef, Set<AttributeModelElement>> getLookupEntityToLinkedLookups(String lookupName);

    /**
     * Get entities and their attributes (only attributes that refer) that refer to the specified lookup.
     *
     * @param lookupName lookup name
     * @return entities and their attributes (only attributes that refer) that refer to the specified lookup.
     */
    Map<EntityDef, Set<AttributeModelElement>> getEntitiesReferencingThisLookup(String lookupName);

    /**
     * Get lookups and their attributes (only attributes that refer) that refer to the specified lookup.
     *
     * @param lookupName lookup name
     * @return lookups and their attributes (only attributes that refer) that refer to the specified lookup.
     */
    Map<LookupEntityDef, Set<AttributeModelElement>> getLookupsReferencingThisLookup(String lookupName);

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
    Map<String, AttributeModelElement> getAttributesInfoMap(String id);
    /**
     * Gets an attribute of a top level entity by path.
     *
     * @param <T>
     *            the generic type
     * @param entityName
     *            entity name
     * @param path
     *            path
     * @return attribute info or null
     */
    <T extends AttributeModelElement> T getEntityAttributeInfoByPath(String entityName, String path);
    /**
     * Gets an attribute of a top level relation by path.
     *
     * @param <T>
     *            the generic type
     * @param relationName
     *            relation name
     * @param path
     *            path
     * @return attribute info or null
     */
    <T extends AttributeModelElement> T getRelationAttributeInfoByPath(String relationName, String path);
    /**
     * Gets an attribute of a lookup entity by path.
     *
     * @param <T>
     *            the generic type
     * @param lookupName
     *            lookup entity name
     * @param path
     *            path
     * @return attribute info or null
     */
    <T extends AttributeModelElement> T getLookupAttributeInfoByPath(String lookupName, String path);
    /**
     * Gets an attribute of a nested entity by path.
     *
     * @param <T>
     *            the generic type
     * @param nestedName
     *            nested entity name
     * @param path
     *            path
     * @return attribute info or null
     */
    <T extends AttributeModelElement> T getNestedAttributeInfoByPath(String nestedName, String path);
    /**
     * Gets attribute by path.
     *
     * @param <T>
     *            the generic type
     * @param id
     *            entity or lookup entity id
     * @param path
     *            the path
     * @return attribute info or null
     */
    <T extends AttributeModelElement> T getAttributeInfoByPath(String id, String path);
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

    /**
     * Filter and collect nested entities which used in existing entities
     * @param allNestedEntities
     * @param allEntityDefs
     * @return
     */
    List<NestedEntityDef> filterUsageNestedEntities(final List<NestedEntityDef> allNestedEntities, List<EntityDef> allEntityDefs);
    /**
     * A service method. Reapplies entity indexed content (name, display name etc.)
     * @param entityName the name
     */
    void applyEntitySearchInfo(String entityName);

    Map<String, Float> getBoostScoreForEntity(final String entityName, List<String> searchFields);
    // @Modules
    // FIXME:
    // The proper interface methods - force their usage.
    /**
     * Gets the entity model element, describing either entity,
     * lookup or relation - i. e. some attributed object, possibly BVT capable.
     * @param id the id
     * @return element or null
     */
    @Nullable
    EntityModelElement getEntityModelElementById(String id);
    // @Modules
    // FIXME:
    // - Refactor the below methods to accept ModelType instead of cachedType. Make them "public ready"
    // - Identify not referenced methods and remove them
    // - Replace methods, returning JAXB types with methods returning adapter types
    /**
     * Gets the model facade.
     *
     * @param <W>
     *            the generic type
     * @param <E>
     *            the element type
     * @param processedModelElement
     *            the processed model element
     * @return the model facade
     */
    @Nullable
    <W extends IdentityModelElement, E extends VersionedObjectDef> AbstractModelElementFacade<W, E> getModelFacade(Class<E> processedModelElement);

    /**
     * Gets the value by id.
     *
     * @param <T>        the generic type
     * @param id         the id
     * @param cachedType the cache type
     * @return the value by id
     */
    <T extends ModelElement> T getValueById(String id, Class<T> cachedType);

    /**
     * Remoces object by id.
     *
     * @param <T>
     *            the generic type
     * @param id
     *            the id
     * @param cachedType
     *            cached type
     */
    <T extends ModelElement> void removeValueById(String id, Class<T> cachedType);

    /**
     * Gets all values of a type.
     *
     * @param <T>
     *            the generic type
     * @param cachedType
     *            the type
     * @return value list
     */
    <T extends ModelElement> Collection<T> getValues(Class<T> cachedType);

    /**
     * Puts a value.
     *
     * @param <T>
     *            the generic type
     * @param id
     *            the id
     * @param cached
     *            wrapper to cache
     * @param cachedType
     *            cached type
     */
    <T extends ModelElement> void putValue(String id, T cached, Class<T> cachedType);


}
