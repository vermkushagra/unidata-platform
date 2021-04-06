package com.unidata.mdm.backend.service.cleanse.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.dto.data.model.GetEntitiesByRelationSideDTO;
import com.unidata.mdm.backend.common.dto.data.model.GetEntitiesGroupsDTO;
import com.unidata.mdm.backend.common.dto.data.model.GetEntityDTO;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.types.RelationSide;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.facades.AbstractModelElementFacade;
import com.unidata.mdm.backend.service.model.util.parsers.CleanseParser;
import com.unidata.mdm.backend.service.model.util.wrappers.ModelWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.ValueWrapper;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.cleanse.common.CleanseFunctionWrapper;
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
import com.unidata.mdm.meta.VersionedObjectDef;

public class MetaModelServiceDQMock implements MetaModelServiceExt {
    private CleanseFunctionGroupDef cleanseFunctionGroupDef = ModelUtils.createDefaultCleanseFunctionGroup();
    private CleanseParser cleanseParser = new CleanseParser();
    private Map<String, CleanseFunctionWrapper> cleanseFunctions;
    private Model model;

    /**
     * Instantiates a new meta model service DQ mock.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public MetaModelServiceDQMock() throws IOException {
        model = JaxbUtils.createModelFromInputStream(ClassLoader.getSystemResourceAsStream("model/dq/model.xml"));
        cleanseFunctions = cleanseParser.parse(model);
    }

    @Override
    public Model exportModel(String storageId) {
        // TODO Auto-generated method stub
        return model;
    }

    @Override
    public List<String> getStorageIdsList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EntityDef> getEntitiesList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Nonnull
    @Override
    public List<EntityDef> getUnfilteredEntitiesList() {
        return null;
    }

    @Override
    public EntitiesGroupDef getRootGroup(String storageId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GetEntityDTO getEntityById(String id) {

        return new GetEntityDTO(
                model.getEntities().stream().filter(en -> StringUtils.equals(id, en.getName())).findAny().orElse(null),
                null, null);
    }

    @Override
    public GetEntitiesByRelationSideDTO getEntitiesFilteredByRelationSide(String id, RelationSide side) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntityDef getEntityByIdNoDeps(String entityName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends AbstractAttributeDef> T getEntityAttributeByPath(String entityName, String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends AbstractAttributeDef> T getAttributeByPath(String id, String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isEntity(String entityId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isNestedEntity(String entityId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<LookupEntityDef> getLookupEntitiesList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LookupEntityDef> getUnfilteredLookupEntitiesList() {
        return null;
    }

    @Override
    public LookupEntityDef getLookupEntityById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GetEntitiesGroupsDTO getEntitiesGroups() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends AbstractAttributeDef> T getLookupEntityAttributeByPath(String entityName, String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isLookupEntity(String entityName) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<NestedEntityDef> getNestedEntitiesByTopLevelId(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<NestedEntityDef, List<NestedEntityDef>> getNestedEntityById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NestedEntityDef getNestedEntityByNoDeps(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends AbstractAttributeDef> T getRelationAttributeByPath(String relationName, String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RelationDef getRelationById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RelationDef> getRelationsByFromEntityName(String entityName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<RelationDef> getRelationsByToEntityName(String entityName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getClassifiersForEntity(String entityName) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractEntityDef> getClassifiedEntities(String classifierName) {
        return Collections.emptyList();
    }

    @Override
    public boolean isRelation(String id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<RelationDef> getRelationsList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EnumerationDataType getEnumerationById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EnumerationDataType> getEnumerationsList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<SourceSystemDef> getSourceSystemsList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Integer> getReversedSourceSystems() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Integer> getStraightSourceSystems() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SourceSystemDef getAdminSourceSystem() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAdminSourceSystem(String id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public SourceSystemDef getSourceSystemById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CleanseFunctionGroupDef getCleanseFunctionRootGroup() {
        // TODO Auto-generated method stub
        return cleanseFunctionGroupDef;
    }

    @Override
    public void upsertModel(UpdateModelRequestContext ctx) {
        // TODO Auto-generated method stub

    }

    @Override
    public void synchronizationUpsertModel(UpdateModelRequestContext ctx) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteModel(DeleteModelRequestContext ctx) {
        // TODO Auto-generated method stub

    }

    @Override
    public void synchronizationDeleteModel(DeleteModelRequestContext ctx) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<RelationDef, EntityDef> getEntityRelations(String entityName, boolean includeTo, boolean includeFrom) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<RelationDef, EntityDef> getEntityRelationsByType(String entityName, List<RelType> types,
                                                                boolean includeTo, boolean includeFrom) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<LookupEntityDef, Set<AttributeInfoHolder>> getEntityToLinkedLookups(String entityName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<LookupEntityDef, Set<AttributeInfoHolder>> getLookupEntityToLinkedLookups(String lookupName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<EntityDef, Set<AttributeInfoHolder>> getEntitiesReferencingThisLookup(String lookupName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<LookupEntityDef, Set<AttributeInfoHolder>> getLookupsReferencingThisLookup(String lookupName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<AbstractAttributeDef> getSimpleAttributes(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ComplexAttributeDef> getComplexAttributes(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, AttributeInfoHolder> getAttributesInfoMap(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void afterContextRefresh() {
        // TODO Auto-generated method stub

    }

    @Override
    public <W extends ModelWrapper, E extends VersionedObjectDef> AbstractModelElementFacade<W, E> getModelFacade(
            Class<E> processedModelElement) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends ValueWrapper> T getValueById(String id, Class<T> cachedType) {
        if (cachedType == CleanseFunctionWrapper.class) {
            return (T) cleanseFunctions.get(id);
        }
        return null;
    }

    @Override
    public <T extends ValueWrapper> void removeValueById(String id, Class<T> cachedType) {
        if (cachedType == CleanseFunctionWrapper.class) {
            cleanseFunctions.remove(id);
        }

    }

    @Override
    public <T extends ValueWrapper> Collection<T> getValues(Class<T> cachedType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends ValueWrapper> void putValue(String id, T cached, Class<T> cachedType) {
        if (cachedType == CleanseFunctionWrapper.class) {
            cleanseFunctions.put(id, (CleanseFunctionWrapper) cached);
        }

    }

    @Override
    public List<String> findMainDisplayableAttrNamesSorted(String entityName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void applyDraft(String draftId) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<NestedEntityDef> getNestedEntitiesList() {
        // TODO Auto-generated method stub
        return null;
    }

}
