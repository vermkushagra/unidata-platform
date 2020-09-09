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

package com.unidata.mdm.backend.service.model.draft;

import static com.unidata.mdm.backend.service.measurement.MeasurementValueXmlConverter.convert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ISet;
import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext.UpdateModelRequestContextBuilder;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext.UpsertType;
import com.unidata.mdm.backend.common.dto.data.model.GetEntitiesByRelationSideDTO;
import com.unidata.mdm.backend.common.dto.data.model.GetEntitiesGroupsDTO;
import com.unidata.mdm.backend.common.dto.data.model.GetEntityDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.integration.exits.MetaModelListener;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.types.RelationSide;
import com.unidata.mdm.backend.conf.impl.ModelImpl;
import com.unidata.mdm.backend.dao.MetaDraftDao;
import com.unidata.mdm.backend.po.MetaDraftPO;
import com.unidata.mdm.backend.po.MetaDraftPO.Type;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.cleanse.CleanseFunctionServiceExt;
import com.unidata.mdm.backend.service.configuration.ConfigurationServiceExt;
import com.unidata.mdm.backend.service.maintenance.MaintenanceService;
import com.unidata.mdm.backend.service.maintenance.dto.SystemMode;
import com.unidata.mdm.backend.service.maintenance.dto.SystemMode.ModeEnum;
import com.unidata.mdm.backend.service.measurement.MeasurementValueXmlConverter;
import com.unidata.mdm.backend.service.measurement.MetaMeasurementService;
import com.unidata.mdm.backend.service.measurement.data.MeasurementUnit;
import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.ie.dto.MetaType;
import com.unidata.mdm.backend.service.model.util.facades.EntitiesGroupModelElementFacade;
import com.unidata.mdm.backend.service.model.util.wrappers.EntitiesGroupWrapper;
import com.unidata.mdm.backend.service.registration.RegistrationService;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.ArrayValueType;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.ListOfCleanseFunctions;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.MeasurementUnitDef;
import com.unidata.mdm.meta.MeasurementValueDef;
import com.unidata.mdm.meta.MeasurementValues;
import com.unidata.mdm.meta.Model;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleAttributesHolderEntityDef;
import com.unidata.mdm.meta.SourceSystemDef;



/**
 * The Class MetaDraftServiceImpl.
 */
@Component
public class MetaDraftServiceImpl implements  MetaDraftService {

    /** The meta model service. */
    @Autowired
    public MetaModelService metaModelService;

    /** The measurement service. */
    @Autowired
    private MetaMeasurementService measurementService;
    /**
     * Cleanse function service.
     */
    @Autowired
    private CleanseFunctionServiceExt cleanseFunctionService;
    
    /** Configuration Service. */
    @Autowired
    private ConfigurationServiceExt configurationService;
    /** The tx manager. */
    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager txManager;
    /** The validation component. */
    @Autowired
    private MetaDraftValidationComponent validationComponent;

    /** The meta draft dao. */
    @Autowired
    private MetaDraftDao metaDraftDao;

    /** The hazelcast instance. */
    @Autowired
    private HazelcastInstance hazelcastInstance;

    /** The maintenance service. */
    @Autowired
    private MaintenanceService maintenanceService;

    /** The registration service. */
    @Autowired
    private RegistrationService registrationService;
    
    /** The audit events writer. */
    @Autowired
    private AuditEventsWriter auditEventsWriter;

    /** The ents. */
    private IMap<String, EntityDef> ents;

    /** The lookups. */
    private IMap<String, LookupEntityDef> lookups;

    /** The rels. */
    private IMap<String, RelationDef> rels;

    /** The nested entities. */
    private IMap<String, NestedEntityDef> nestedEntities;

    /** The enums. */
    private IMap<String, EnumerationDataType> enums;

    /** The ss. */
    private IMap<String, SourceSystemDef> ss;

    /** The ents to delete. */
    private ISet<String> entsToDelete;

    /** The lookups to delete. */
    private ISet<String> lookupsToDelete;

    /** The rels to delete. */
    private ISet<String> relsToDelete;

    /** The nested entities to delete. */
    private ISet<String> nestedEntitiesToDelete;

    /** The enums to delete. */
    private ISet<String> enumsToDelete;

    /** The ss to delete. */
    private ISet<String> ssToDelete;

    /** The eg to delete. */
    private ISet<String> egToDelete;
    /** The eg to delete. */
    private ISet<EntitiesGroupDef> entitiesGroup;

    /** The measurement values. */
    private ISet<MeasurementValues> measurementValues;

    /** The name. */
    private String name;
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDraftServiceImpl.class);

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.model.MetaDraftService#apply()
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void apply() {
        boolean isExc = false;
        try {
            maintenanceService
                    .transferTo(new SystemMode().withModeEnum(ModeEnum.MAINTENANCE).withMessage(MessageUtils.getMessage(
                            ExceptionId.EX_MAINTENANCE_IMPORT_MODEL.getCode(), SecurityUtils.getCurrentUserName())));
            UpdateModelRequestContext uctx = new UpdateModelRequestContextBuilder()
                    .entityUpdate(new ArrayList<>(ents.values()))
                    .nestedEntityUpdate(new ArrayList<>(nestedEntities.values()))
                    .cleanseFunctionsUpdate(metaModelService.getCleanseFunctionRootGroup())
                    .entitiesGroupsUpdate(entitiesGroup.stream().findFirst().orElse(null))
                    .enumerationsUpdate(new ArrayList<>(enums.values()))
                    .sourceSystemsUpdate(new ArrayList<>(ss.values()))
                    .lookupEntityUpdate(new ArrayList<>(lookups.values()))
                    .relationsUpdate(new ArrayList<>(rels.values())).isForceRecreate(UpsertType.PARTIAL_UPDATE).build();
            uctx = executeUEApply(uctx);
            validationComponent.validateUpdateModelContext(uctx);
            DeleteModelRequestContext dctx = new DeleteModelRequestContext.DeleteModelRequestContextBuilder()
                    .entitiesIds(new ArrayList<>(entsToDelete)).lookupEntitiesIds(new ArrayList<>(lookupsToDelete))
                    .sourceSystemIds(new ArrayList<>(ssToDelete)).relationIds(new ArrayList<>(relsToDelete)).build();

            metaModelService.deleteModel(dctx);

            if (measurementValues != null && measurementValues.stream().findFirst().isPresent()
                    && measurementValues.stream().findFirst().orElse(null).getValue() != null) {
                measurementService.batchRemove(measurementService.getAllValues().stream().map(MeasurementValue::getId)
                        .collect(Collectors.toSet()), false, true);
                List<MeasurementValueDef> valueDefs = measurementValues.stream().findFirst().orElse(null).getValue();
                List<MeasurementValue> values = new ArrayList<>();
                for (MeasurementValueDef value : valueDefs) {
                    values.add(convert(value));
                }
                measurementService.saveValues(values);
            }
            metaModelService.upsertModel(uctx);
            addVersion(false);
        }catch(Exception e) {
            isExc = true;
            throw e;
        }finally {
            // end of maintenance mode
            try {
                refresh(isExc);
            } finally {
                if(!isExc) {
                    removeDraft();
                }
                maintenanceService.transferTo(new SystemMode().withModeEnum(ModeEnum.NORMAL));
            }

        }
        auditEventsWriter.writeSuccessEvent(AuditActions.META_DRAFT_APPLY);
    }

    /**
     * Refresh.
     *
     * @param isExc the is exc
     */
    public void refresh(boolean isExc) {
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        txTemplate.setPropagationBehavior(
                isExc ? TransactionDefinition.PROPAGATION_REQUIRES_NEW : TransactionDefinition.PROPAGATION_REQUIRED);
        txTemplate.execute(new TransactionCallback<Object>() {

            @Override
            public Object doInTransaction(TransactionStatus status) {
                registrationService.cleanup();
                measurementService.afterContextRefresh();
                ((MetaModelServiceExt) metaModelService).afterContextRefresh();
                cleanseFunctionService.afterContextRefresh();
                return null;
            }
        });

    }

    /**
     * Load active draft.
     *
     */
    public synchronized void loadActiveDraft() {
        if (!metaDraftDao.isDraftExist("DEFAULT")) {
            removeDraft();
            addVersion(true);
        } else {
            List<MetaDraftPO> currentDraft = metaDraftDao.currentDraft("DEFAULT");
            for (MetaDraftPO metaDraftPO : currentDraft) {
                if (metaDraftPO.getType() == Type.MODEL) {
                    try (ByteArrayInputStream bas = new ByteArrayInputStream(metaDraftPO.getValue())) {
                        Model model = JaxbUtils.createModelFromInputStream(bas);
                        this.ents.clear();
                        this.lookups.clear();
                        this.rels.clear();
                        this.ss.clear();
                        this.nestedEntities.clear();
                        this.enums.clear();
                        this.entitiesGroup.clear();                        
                        this.entsToDelete.clear();
                        this.ssToDelete.clear();
                        this.lookupsToDelete.clear();
                        this.relsToDelete.clear();
                        this.measurementValues.clear();
                        this.enumsToDelete.clear();
                        model.getLookupEntities().forEach(s -> this.lookups.put(s.getName(), s));
                        model.getEntities().forEach(s -> this.ents.put(s.getName(), s));
                        model.getRelations().forEach(s -> this.rels.put(s.getName(), s));
                        model.getNestedEntities().forEach(s -> this.nestedEntities.put(s.getName(), s));
                        model.getEnumerations().forEach(s -> this.enums.put(s.getName(), s));
                        model.getSourceSystems().forEach(s -> this.ss.put(s.getName(), s));
                        
                    
                        this.entitiesGroup.add(model.getEntitiesGroup());
                        this.measurementValues.add(model.getMeasurementValues());
                    } catch (IOException e) {
						throw new SystemRuntimeException("Unable to load draft.", ExceptionId.EX_META_CANNOT_UNMARSHAL_MODEL);
					}
                }
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.model.MetaDraftService#removeDraft()
     */
    @Override
    public synchronized void removeDraft() {
 
        this.ents.clear();
        this.lookups.clear();
        this.rels.clear();
        this.ss.clear();
        this.nestedEntities.clear();
        this.enums.clear();
        this.entsToDelete.clear();
        this.ssToDelete.clear();
        this.lookupsToDelete.clear();
        this.relsToDelete.clear();
        this.enumsToDelete.clear();
        metaModelService.getLookupEntitiesList().forEach(s -> lookups.put(s.getName(), s));
        metaModelService.getEntitiesList().forEach(s -> ents.put(s.getName(), s));
        metaModelService.getRelationsList().forEach(s -> rels.put(s.getName(), s));
        metaModelService.getNestedEntitiesList().forEach(s -> nestedEntities.put(s.getName(), s));
        metaModelService.getEnumerationsList().forEach(s -> enums.put(s.getName(), s));
        metaModelService.getSourceSystemsList().forEach(s -> ss.put(s.getName(), s));
        entitiesGroup.clear();
        measurementValues.clear();
        entitiesGroup.add(metaModelService.getRootGroup(null));
        Collection<MeasurementValueDef> measurementValue = measurementService.getAllValues().stream()
                .map(MeasurementValueXmlConverter::convert).collect(Collectors.toList());
        measurementValues.add(new MeasurementValues().withValue(measurementValue));
        metaDraftDao.delete(new MetaDraftPO());
        auditEventsWriter.writeSuccessEvent(AuditActions.META_DRAFT_REMOVE);
    }

    /**
     * Adds the version.
     *
     * @param isActive
     *            the is active
     */
    private synchronized void addVersion(boolean isActive) {

        Model modelToSave = null;
        if (isActive) {
            modelToSave = new Model()
                    .withCleanseFunctions(
                            new ListOfCleanseFunctions().withGroup(metaModelService.getCleanseFunctionRootGroup()))
                    .withEntities(this.ents.values())
                    .withEnumerations(this.enums.values())
                    .withMeasurementValues(this.measurementValues.stream().findFirst().orElse(null))
                    .withLookupEntities(this.lookups.values())
                    .withNestedEntities(this.nestedEntities.values())
                    .withEntitiesGroup(this.entitiesGroup.stream().findFirst().orElse(null))
                    .withRelations(this.rels.values())
                    .withSourceSystems(this.ss.values());
        } else {
            modelToSave = metaModelService.exportModel(null);
            modelToSave.setMeasurementValues(measurementValues.stream().findFirst().orElse(null));
        }
        byte[] modelAsByte = JaxbUtils.marshalMetaModel(modelToSave).getBytes(StandardCharsets.UTF_8);
        MetaDraftPO modelDraft = new MetaDraftPO();
        modelDraft.setActive(isActive);
        modelDraft.setCreatedAt(new java.sql.Date(new Date().getTime()));
        modelDraft.setCreatedBy(SecurityUtils.getCurrentUserName());
        modelDraft.setUpdatedAt(new java.sql.Date(new Date().getTime()));
        modelDraft.setType(Type.MODEL);
        modelDraft.setValue(modelAsByte);
        modelDraft.setName(name);
        if (isActive) {
            metaDraftDao.delete(modelDraft);
            metaDraftDao.create(modelDraft);
        } else {
            long version = metaDraftDao.getLastVersion("DEFAULT");
            modelDraft.setVersion(version + 1);
            metaDraftDao.create(modelDraft);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.configuration.AfterContextRefresh#
     * afterContextRefresh()
     */
    @Override
    public void afterContextRefresh() {
        this.ents = hazelcastInstance.getMap("draft_ents");
        this.lookups = hazelcastInstance.getMap("draft_lookups");
        this.rels = hazelcastInstance.getMap("draft_rels");
        this.nestedEntities = hazelcastInstance.getMap("draft_nestedEntities");
        this.enums = hazelcastInstance.getMap("draft_enums");
        this.ss = hazelcastInstance.getMap("draft_ss");

        this.entsToDelete = hazelcastInstance.getSet("draft_entsToDelete");
        this.nestedEntitiesToDelete = hazelcastInstance.getSet("draft_nestedEntitiesToDelete");
        this.ssToDelete = hazelcastInstance.getSet("draft_ssToDelete");
        this.lookupsToDelete = hazelcastInstance.getSet("draft_lookupsToDelete");
        this.relsToDelete = hazelcastInstance.getSet("draft_relsToDelete");
        this.enumsToDelete = hazelcastInstance.getSet("draft_enumsToDelete");
        this.egToDelete = hazelcastInstance.getSet("draft_egToDelete");
        this.entitiesGroup = hazelcastInstance.getSet("draft_entitiesGroup");
        this.measurementValues = hazelcastInstance.getSet("draft_measurementValues");

        if (this.ss.size() == 0) {
            try {
                loadActiveDraft();
            } catch (Exception e) {            	
                removeDraft();
                LOGGER.error("Exception while loading draft from database.",e);
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.model.MetaDraftService#getEntitiesGroups()
     */
    @Override
    public GetEntitiesGroupsDTO getEntitiesGroups() {

        Map<String, EntitiesGroupWrapper> map = parse();
        Collection<EntitiesGroupWrapper> entitiesGroupWrappers = map.values();
        Collection<String> entitiesNames = new ArrayList<>();

        entitiesGroupWrappers.stream().map(EntitiesGroupWrapper::getNestedEntites).flatMap(Collection::stream)
                .map(EntityDef::getName).collect(Collectors.toCollection(() -> entitiesNames));

        entitiesGroupWrappers.stream().map(EntitiesGroupWrapper::getNestedLookupEntities).flatMap(Collection::stream)
                .map(LookupEntityDef::getName).collect(Collectors.toCollection(() -> entitiesNames));

        Map<String, EntitiesGroupDef> defs = new HashMap<>(entitiesGroupWrappers.size());
        Map<EntitiesGroupDef, Pair<List<EntityDef>, List<LookupEntityDef>>> nested = new HashMap<>(
                entitiesGroupWrappers.size());
        for (EntitiesGroupWrapper entitiesGroupWrapper : entitiesGroupWrappers) {

            List<EntityDef> entities = entitiesGroupWrapper.getNestedEntites().stream().collect(Collectors.toList());

            List<LookupEntityDef> lookupEntities = entitiesGroupWrapper.getNestedLookupEntities().stream()
                    .collect(Collectors.toList());

            defs.put(entitiesGroupWrapper.getWrapperId(), entitiesGroupWrapper.getEntitiesGroupDef());
            nested.put(entitiesGroupWrapper.getEntitiesGroupDef(),
                    new ImmutablePair<List<EntityDef>, List<LookupEntityDef>>(entities, lookupEntities));
        }
        GetEntitiesGroupsDTO result = new GetEntitiesGroupsDTO(defs, nested);
        return result;
    }

    /**
     * Parses the.
     *
     * @return the map
     */
    private Map<String, EntitiesGroupWrapper> parse() {
        if (this.entitiesGroup == null) {
            this.entitiesGroup.clear();
            this.entitiesGroup.add(EntitiesGroupModelElementFacade.DEFAULT_ROOT_GROUP);
        }

        Map<String, EntitiesGroupWrapper> groups = recursiveParse(
                entitiesGroup.stream().findFirst().orElse(null).getInnerGroups(),
                entitiesGroup.stream().findFirst().orElse(null).getGroupName());
        EntitiesGroupWrapper rootWrapper = new EntitiesGroupWrapper(entitiesGroup.stream().findFirst().orElse(null),
                entitiesGroup.stream().findFirst().orElse(null).getGroupName());
        groups.put(entitiesGroup.stream().findFirst().orElse(null).getGroupName(), rootWrapper);

        this.ents.values().stream()
                .filter(entity -> entity.getGroupName() != null && groups.get(entity.getGroupName()) != null)
                .forEach(entity -> {
                    EntitiesGroupWrapper wrapper = groups.get(entity.getGroupName());
                    wrapper.addEntityToGroup(entity);
                });

        this.lookups.values().stream()
                .filter(entity -> entity.getGroupName() != null && groups.get(entity.getGroupName()) != null)
                .forEach(entity -> {
                    EntitiesGroupWrapper wrapper = groups.get(entity.getGroupName());
                    wrapper.addLookupEntityToGroup(entity);
                });
        return groups;
    }

    /**
     * Recursive parse.
     *
     * @param groups
     *            the groups
     * @param parentPath
     *            the parent path
     * @return the map
     */
    private Map<String, EntitiesGroupWrapper> recursiveParse(List<EntitiesGroupDef> groups, String parentPath) {
        Map<String, EntitiesGroupWrapper> result = new ConcurrentHashMap<>();
        if (groups.isEmpty())
            return result;
        for (EntitiesGroupDef entitiesGroup : groups) {
            String wrapperId = EntitiesGroupModelElementFacade.getFullPath(parentPath, entitiesGroup.getGroupName());
            EntitiesGroupWrapper wrapper = new EntitiesGroupWrapper(entitiesGroup, wrapperId);
            result.put(wrapperId, wrapper);
            result.putAll(recursiveParse(entitiesGroup.getInnerGroups(), wrapperId));
        }
        return result;
    }

    /**
     * Check duplicates.
     *
     * @param ctx the ctx
     */
    private void checkDuplicates(UpdateModelRequestContext ctx) {
        if(ctx.hasEntityUpdate()) {
            ctx.getEntityUpdate().stream().forEach(e->{
                if(lookups.containsKey(e.getName())) {
                    throw new BusinessException("Duplicate found", ExceptionId.EX_META_IMPORT_MODEL_EL_DUPLICATE, e.getName(),MetaType.ENTITY,e.getName(),MetaType.LOOKUP);
                }
                if(rels.containsKey(e.getName())) {
                    throw new BusinessException("Duplicate found", ExceptionId.EX_META_IMPORT_MODEL_EL_DUPLICATE, e.getName(),MetaType.ENTITY,e.getName(),MetaType.RELATION);
                }
                if(enums.containsKey(e.getName())) {
                    throw new BusinessException("Duplicate found", ExceptionId.EX_META_IMPORT_MODEL_EL_DUPLICATE, e.getName(),MetaType.ENTITY,e.getName(),MetaType.ENUM);
                }
            });
        }
        if(ctx.hasLookupEntityUpdate()) {
            ctx.getLookupEntityUpdate().stream().forEach(e->{
                if(ents.containsKey(e.getName())) {
                    throw new BusinessException("Duplicate found", ExceptionId.EX_META_IMPORT_MODEL_EL_DUPLICATE, e.getName(),MetaType.LOOKUP,e.getName(),MetaType.ENTITY);
                }
                if(rels.containsKey(e.getName())) {
                    throw new BusinessException("Duplicate found", ExceptionId.EX_META_IMPORT_MODEL_EL_DUPLICATE, e.getName(),MetaType.LOOKUP,e.getName(),MetaType.RELATION);
                }
                if(enums.containsKey(e.getName())) {
                    throw new BusinessException("Duplicate found", ExceptionId.EX_META_IMPORT_MODEL_EL_DUPLICATE, e.getName(),MetaType.RELATION,e.getName(),MetaType.ENUM);
                }
            });
        }
            if(ctx.hasRelationsUpdate()) {
                ctx.getRelationsUpdate().stream().forEach(e->{
                    if(ents.containsKey(e.getName())) {
                        throw new BusinessException("Duplicate found", ExceptionId.EX_META_IMPORT_MODEL_EL_DUPLICATE, e.getName(),MetaType.RELATION,e.getName(),MetaType.ENTITY);
                    }
                    if(lookups.containsKey(e.getName())) {
                        throw new BusinessException("Duplicate found", ExceptionId.EX_META_IMPORT_MODEL_EL_DUPLICATE, e.getName(),MetaType.RELATION,e.getName(),MetaType.LOOKUP);
                    }
                    if(enums.containsKey(e.getName())) {
                        throw new BusinessException("Duplicate found", ExceptionId.EX_META_IMPORT_MODEL_EL_DUPLICATE, e.getName(),MetaType.RELATION,e.getName(),MetaType.ENUM);
                    }
                });
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.model.MetaDraftService#update(com.unidata.mdm
     * .backend.common.context.UpdateModelRequestContext)
     */
    @Override
    public synchronized void update(UpdateModelRequestContext ctx) {
        ctx = executeUEUpsert(ctx);
        checkDuplicates(ctx);
		if (ctx.hasEntityUpdate()) {
			ctx.getEntityUpdate().forEach(s -> {
				rebuildLookupReferences(s);
			});
        }
        if (ctx.hasNestedEntityUpdate()) {
            ctx.getNestedEntityUpdate().forEach(s -> {
                rebuildLookupReferences(s);
            });
        }
        if (ctx.hasLookupEntityUpdate()) {
            ctx.getLookupEntityUpdate().forEach(s -> {
                rebuildLookupReferences(s);
            });
        }
        if (ctx.hasRelationsUpdate()) {
            ctx.getRelationsUpdate().forEach(s -> {
                rebuildLookupReferences(s);
            });
        }
        validationComponent.validateUpdateModelContext(ctx);

        if (ctx.hasEntitiesGroupUpdate()) {
            this.entitiesGroup.clear();
            this.entitiesGroup.add(ctx.getEntitiesGroupsUpdate());
        }

		if (ctx.hasEntityUpdate()) {
			boolean isSsUpdate = ctx.hasSourceSystemsUpdate();
			ctx.getEntityUpdate().forEach(s -> {
				rebuildLookupReferences(s);
				ents.put(s.getName(), s);
				if (!isSsUpdate) {
					Set<String> relsTemp = new HashSet<>();
					rels.forEach((k, v) -> {
						if (StringUtils.equals(v.getFromEntity(), s.getName())) {
							relsTemp.add(k);
						}
					});
					relsToDelete.addAll(relsTemp);
					relsTemp.forEach(key -> rels.remove(key));
				}
			});

        }
        if (ctx.hasNestedEntityUpdate()) {
            ctx.getNestedEntityUpdate().forEach(s -> {
                rebuildLookupReferences(s);
                nestedEntities.put(s.getName(), s);
            });
        }
        if (ctx.hasLookupEntityUpdate()) {
            ctx.getLookupEntityUpdate().forEach(s -> {
                rebuildLookupReferences(s);
                lookups.put(s.getName(), s);
            });
        }
        if (ctx.hasRelationsUpdate()) {

            Set<String> relsTemp = new HashSet<>();
            ctx.getRelationsUpdate().forEach(s -> rels.forEach((k, v) -> {
                if (StringUtils.equals(v.getFromEntity(), s.getFromEntity())) {
                    relsTemp.add(k);
                }
            }));
            ctx.getRelationsUpdate().forEach(s -> {
                rebuildLookupReferences(s);
                relsTemp.remove(s.getName());
                relsToDelete.remove(s.getName());
                rels.put(s.getName(), s);
            });
            relsTemp.forEach(key -> rels.remove(key));

        }
        if (ctx.hasSourceSystemsUpdate()) {
            ctx.getSourceSystemsUpdate().forEach(s -> ss.put(s.getName(), s));
        }
        auditEventsWriter.writeSuccessEvent(AuditActions.META_DRAFT_UPSERT, ctx);
        addVersion(true);
    }

    /**
     * Select lookup by name.
     *
     * @param name the name
     * @return the lookup entity def
     */
    private LookupEntityDef selectLookupByName(String name) {

        // try draft first
        LookupEntityDef toLookup = lookups.get(name);
        if (Objects.nonNull(toLookup)) {
            return toLookup;
        }

        // Get from stationary
        return metaModelService.getLookupEntityById(name);
    }

    /**
     * Rebuild references in cache.
     *
     * @param def the def
     */
    private void rebuildLookupReferences(SimpleAttributesHolderEntityDef def) {

        List<ArrayAttributeDef> arrayLinks = def.getArrayAttribute()
                .stream().filter(attr -> StringUtils.isNotBlank(attr.getLookupEntityType()))
                .collect(Collectors.toList());

        arrayLinks.forEach(attr -> {

            LookupEntityDef toLookup = selectLookupByName(attr.getLookupEntityType());
            if (Objects.isNull(toLookup)) {
                return;
            }

            // fix ref type
            CodeAttributeDef cAttrDef = toLookup.getCodeAttribute();
            attr.setLookupEntityCodeAttributeType(ArrayValueType.fromValue(cAttrDef.getSimpleDataType()));
        });

        List<SimpleAttributeDef> simpleLinks = def.getSimpleAttribute()
                .stream().filter(attr -> StringUtils.isNotBlank(attr.getLookupEntityType()))
                .collect(Collectors.toList());

        simpleLinks.forEach(attr -> {

            LookupEntityDef toLookup = selectLookupByName(attr.getLookupEntityType());
            if (Objects.isNull(toLookup)) {
                return;
            }

            // fix ref type
            CodeAttributeDef cAttrDef = toLookup.getCodeAttribute();
            attr.setLookupEntityCodeAttributeType(cAttrDef.getSimpleDataType());
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.model.MetaDraftService#getEnumerationsList()
     */
    @Override
    public List<EnumerationDataType> getEnumerationsList() {

        return enums.values().stream().collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.model.MetaDraftService#getEntitiesList()
     */
    @Override
    public List<EntityDef> getEntitiesList() {
        return ents.values().stream().collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.model.MetaDraftService#getEntityById(java.
     * lang.String)
     */
    @Override
    public GetEntityDTO getEntityById(String id) {
        List<RelationDef> rels = this.rels.values().stream().filter(s -> StringUtils.equals(s.getFromEntity(), id))
                .collect(Collectors.toList());
        Set<NestedEntityDef> nes = new HashSet<>();
        EntityDef def = ents.get(id);
        if (def == null) {
            return null;
        }
        def.getComplexAttribute().stream().forEach(ca -> {
            NestedEntityDef ne = nestedEntities.get(ca.getNestedEntityName());
            nes.add(ne);
            nestedParse(ne, nes);
        });

        return new GetEntityDTO(ents.get(id), nes.stream().collect(Collectors.toList()), rels);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityDef getEntityByIdNoDeps(String id) {

        MeasurementPoint.start();
        try {
            return ents.get(id);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Nested parse.
     *
     * @param ne
     *            the ne
     * @param nes
     *            the nes
     */
    private void nestedParse(NestedEntityDef ne, Set<NestedEntityDef> nes) {
        if (ne == null) {
            return;
        }
        ne.getComplexAttribute().stream().forEach(ca -> {
            NestedEntityDef nei = nestedEntities.get(ca.getNestedEntityName());
            nes.add(nei);
            nestedParse(nei, nes);

        });
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.model.MetaDraftService#remove(com.unidata.mdm
     * .backend.common.context.DeleteModelRequestContext)
     */
    @Override
    public synchronized void remove(DeleteModelRequestContext ctx) {
        UpdateModelRequestContext uctx = new UpdateModelRequestContextBuilder()
                .entityUpdate(ents.values().stream().filter(s -> !ctx.getEntitiesIds().contains(s.getName()))
                        .collect(Collectors.toList()))
                .nestedEntityUpdate(nestedEntities.values().stream()
                        .filter(s -> !ctx.getNestedEntitiesIds().contains(s.getName())).collect(Collectors.toList()))
                .cleanseFunctionsUpdate(metaModelService.getCleanseFunctionRootGroup())
                .entitiesGroupsUpdate(entitiesGroup.stream().findFirst().orElse(null))
                .enumerationsUpdate(enums.values().stream().collect(Collectors.toList()))
                .sourceSystemsUpdate(ss.values().stream().filter(s -> !ctx.getSourceSystemIds().contains(s.getName()))
                        .collect(Collectors.toList()))
                .lookupEntityUpdate(lookups.values().stream()
                        .filter(s -> !ctx.getLookupEntitiesIds().contains(s.getName())).collect(Collectors.toList()))
                .relationsUpdate(rels.values().stream().filter(s -> !ctx.getRelationIds().contains(s.getName()))
                        .collect(Collectors.toList()))
                .isForceRecreate(UpsertType.FULLY_NEW).build();
        validationComponent.validateUpdateModelContext(uctx);
        if (ctx.getEntitiesIds() != null) {
            ctx.getEntitiesIds().stream().forEach(s -> {
                if (metaModelService.isEntity(s)) {
                    entsToDelete.add(s);
                    rels.forEach((k, v) -> {
                        if (StringUtils.equals(v.getFromEntity(), s) || StringUtils.equals(v.getToEntity(), s)) {
                            relsToDelete.add(v.getName());

                        }
                    });
                    relsToDelete.forEach(key -> rels.remove(key));
                }
                ents.remove(s);
            });
        }
        if (ctx.getLookupEntitiesIds() != null) {
            ctx.getLookupEntitiesIds().stream().forEach(s -> {
                if (metaModelService.isLookupEntity(s)) {
                    lookupsToDelete.add(s);
                }
                lookups.remove(s);
            });
        }
        if (ctx.getRelationIds() != null) {
            ctx.getRelationIds().stream().forEach(s -> {
                if (metaModelService.isRelation(s)) {
                    relsToDelete.add(s);
                }
                rels.remove(s);
            });
        }
        if (ctx.getSourceSystemIds() != null) {
            ctx.getSourceSystemIds().stream().forEach(s -> {
                if (metaModelService.getSourceSystemById(s) != null) {
                    ssToDelete.add(s);
                }
                ss.remove(s);
            });
        }
        if (ctx.getNestedEntitiesIds() != null) {
            ctx.getNestedEntitiesIds().stream().forEach(s -> {
                if (metaModelService.isNestedEntity(s)) {
                    nestedEntitiesToDelete.add(s);
                }
                nestedEntities.remove(s);
            });
        }
        if (ctx.getEnumerationIds() != null) {
            ctx.getEnumerationIds().stream().forEach(s -> {
                if (metaModelService.getEnumerationById(s) != null) {
                    enumsToDelete.add(s);
                }
                enums.remove(s);
            });
        }
        auditEventsWriter.writeSuccessEvent(AuditActions.META_DRAFT_DELETE, ctx);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.model.MetaDraftService#getLookupEntitiesList(
     * )
     */
    @Override
    public List<LookupEntityDef> getLookupEntitiesList() {
        return lookups.values().stream().collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.model.MetaDraftService#getLookupEntityById(
     * java.lang.String)
     */
    @Override
    public LookupEntityDef getLookupEntityById(String id) {
        MeasurementPoint.start();
        try {
            return lookups.get(id);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.model.MetaDraftService#getRelationsList()
     */
    @Override
    public List<RelationDef> getRelationsList() {
        return rels.values().stream().collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.model.MetaDraftService#
     * getEntitiesFilteredByRelationSide(java.lang.String,
     * com.unidata.mdm.backend.common.types.RelationSide)
     */
    @Override
    public GetEntitiesByRelationSideDTO getEntitiesFilteredByRelationSide(String entityName, RelationSide to) {

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.model.MetaDraftService#getSourceSystemsList()
     */
    @Override
    public List<SourceSystemDef> getSourceSystemsList() {
        return this.ss.values().stream().collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.model.MetaDraftService#getAllValues()
     */
    @Override
    public List<MeasurementValue> getAllValues() {
        List<MeasurementValue> result = new ArrayList<>();

        if (measurementValues == null || measurementValues.stream().findFirst().orElse(null).getValue() == null) {
            return result;
        }
        measurementValues.stream().findFirst().orElse(null).getValue().forEach(s -> {
            MeasurementValue value = new MeasurementValue();
            value.setBaseUnitId(s.getUnit().stream().filter(MeasurementUnitDef::isBase).findFirst().get().getId());

            value.setId(s.getId());
            value.setShortName(s.getShortName());
            value.setName(s.getDisplayName());
            Map<String, MeasurementUnit> units = new HashMap<>();
            s.getUnit().forEach(u -> {
                MeasurementUnit unit = new MeasurementUnit();
                unit.setBase(u.isBase());
                unit.setConvectionFunction(u.getConvectionFunction());
                unit.setId(u.getId());
                unit.setName(u.getDisplayName());
                unit.setShortName(u.getShortName());
                unit.setValueId(value.getId());
                units.put(unit.getId(), unit);
            });
            value.setMeasurementUnits(units);
            result.add(value);
        });
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.model.MetaDraftService#getValueById(java.lang
     * .String)
     */
    @Override
    public MeasurementValue getValueById(String id) {
        MeasurementValue value = new MeasurementValue();
        measurementValues.stream().findFirst().orElse(null).getValue().stream()
                .filter(s -> StringUtils.equals(id, s.getId())).forEach(s -> {

            value.setBaseUnitId(s.getUnit().stream().filter(u -> u.isBase()).findFirst().get().getId());
            value.setId(s.getId());
            value.setShortName(s.getShortName());
            value.setName(s.getDisplayName());
            Map<String, MeasurementUnit> units = new HashMap<>();
            s.getUnit().stream().forEach(u -> {
                MeasurementUnit unit = new MeasurementUnit();
                unit.setBase(u.isBase());
                unit.setConvectionFunction(u.getConvectionFunction());
                unit.setId(u.getId());
                unit.setName(u.getDisplayName());
                unit.setShortName(u.getShortName());
                unit.setValueId(value.getId());
                units.put(unit.getId(), unit);
            });
            value.setMeasurementUnits(units);
        });
        return value;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.model.MetaDraftService#removeValue(java.lang.
     * String)
     */
    @Override
    public boolean removeValue(String measureValueId) {
        validationComponent.checkReferencesToMeasurementValues(Collections.singletonList(measureValueId));
        MeasurementValues mv = measurementValues.stream().findFirst().orElse(null);
        List<MeasurementValueDef> values = mv.getValue();
        for (Iterator<MeasurementValueDef> iterator = values.iterator(); iterator.hasNext();) {
            MeasurementValueDef value = iterator.next();
            if (StringUtils.equals(value.getId(), measureValueId)) {
                iterator.remove();
                return true;
            }
        }
        measurementValues.clear();
        measurementValues.add(mv);
        return false;

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.model.MetaDraftService#batchRemove(java.util.
     * List, boolean, boolean)
     */
    @Override
    public synchronized boolean batchRemove(List<String> measureValueIds, boolean b, boolean c) {
        validationComponent.checkReferencesToMeasurementValues(measureValueIds);
        MeasurementValues mv = measurementValues.stream().findFirst().orElse(null);
        List<MeasurementValueDef> values = mv.getValue();
        boolean result = false;
        for (Iterator<MeasurementValueDef> iterator = values.iterator(); iterator.hasNext();) {
            MeasurementValueDef value = iterator.next();
            if (measureValueIds.contains(value.getId())) {
                iterator.remove();
                result = true;
            }

        }
        measurementValues.clear();
        measurementValues.add(mv);
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.model.MetaDraftService#saveValue(com.unidata.
     * mdm.backend.service.measurement.data.MeasurementValue)
     */
    @Override
    public void saveValue(MeasurementValue value) {
        measurementService.validateValue(value);
        MeasurementValues mv = measurementValues.stream().findFirst().orElse(null);
        Optional<MeasurementValueDef> existValue = mv.getValue().stream()
                .filter(measurementValueDef -> measurementValueDef.getId().equals(value.getId())).findAny();
        if (existValue.isPresent()) {
            validationComponent.checkReferencesToMeasurementUnits(value);
            MeasurementValueDef forUpdate = existValue.get();
            forUpdate.setDisplayName(value.getName());
            forUpdate.setShortName(value.getShortName());
            forUpdate.getUnit().clear();
            value.getMeasurementUnits().forEach(s -> {
                MeasurementUnitDef unit = new MeasurementUnitDef();
                unit.setBase(s.isBase());
                unit.setConvectionFunction(s.getConvectionFunction());
                unit.setDisplayName(s.getName());
                unit.setId(s.getId());
                unit.setShortName(s.getShortName());
                forUpdate.getUnit().add(unit);
            });
        } else {
            MeasurementValueDef result = new MeasurementValueDef();
            result.setDisplayName(value.getName());
            result.setShortName(value.getShortName());
            result.setId(value.getId());
            value.getMeasurementUnits().forEach(s -> {
                MeasurementUnitDef unit = new MeasurementUnitDef();
                unit.setBase(s.isBase());
                unit.setConvectionFunction(s.getConvectionFunction());
                unit.setDisplayName(s.getName());
                unit.setId(s.getId());
                unit.setShortName(s.getShortName());
                result.getUnit().add(unit);
            });
            mv.getValue().add(result);
        }
        measurementValues.clear();
        measurementValues.add(mv);

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.common.service.MetaDraftService#getRootGroup(java.
     * lang.String)
     */
    @Override
    public EntitiesGroupDef getRootGroup(String storageId) {

        return entitiesGroup.stream().findFirst().orElse(null);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.common.service.MetaDraftService#getRelationById(java.
     * lang.String)
     */
    @Override
    public RelationDef getRelationById(String name) {

        return rels.get(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.common.service.MetaDraftService#exportModel(java.lang
     * .String)
     */
    @Override
    public Model exportModel(String storageId) {
        return new Model()
                .withCleanseFunctions(
                        new ListOfCleanseFunctions().withGroup(metaModelService.getCleanseFunctionRootGroup()))
                .withEntities(this.ents.values()).withEnumerations(this.enums.values())
                .withMeasurementValues(this.measurementValues.stream().findFirst().orElse(null))
                .withLookupEntities(this.lookups.values()).withNestedEntities(this.nestedEntities.values())
                .withRelations(this.rels.values()).withSourceSystems(this.ss.values())
                .withMeasurementValues(this.measurementValues.stream().findFirst().orElse(null))
                .withEntitiesGroup(this.entitiesGroup.stream().findFirst().orElse(null));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.common.service.MetaDraftService#getEnumerationById(
     * java.lang.String)
     */
    @Override
    public EnumerationDataType getEnumerationById(String enumDataType) {
        return enums.get(enumDataType);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.common.service.MetaDraftService#getSourceSystemById(
     * java.lang.String)
     */
    @Override
    public SourceSystemDef getSourceSystemById(String sourceSystem) {

        return ss.get(sourceSystem);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.model.MetaDraftService#getNestedEntitiesList(
     * )
     */
    @Override
    public List<NestedEntityDef> getNestedEntitiesList() {
        return new ArrayList<>(nestedEntities.values());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.common.service.MetaDraftService#getNestedEntityById(
     * java.lang.String)
     */
    @Override
    public NestedEntityDef getNestedEntityById(String elementName) {
        MeasurementPoint.start();
        try {
            return nestedEntities.get(elementName);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.common.service.MetaDraftServiceExt#removeRefsToClsf(java.lang.String)
     */
    @Override
    public void removeRefsToClsf(String clsfName) {
        Set<LookupEntityDef> lookupsToUpdate = new HashSet<>();
        lookups.forEach((k, v) -> {
            if(v.getClassifiers().contains(clsfName)) {
                List<String> classifiers = v.getClassifiers().stream().filter(c->!StringUtils.equals(clsfName, c)).collect(Collectors.toList());
                v.getClassifiers().clear();
                v.withClassifiers(classifiers);
                lookupsToUpdate.add(v);
            }
        });
        Set<EntityDef> entsToUpdate = new HashSet<>();
        ents.forEach((k, v) -> {
            if(v.getClassifiers().contains(clsfName)) {
                List<String> classifiers = v.getClassifiers().stream().filter(c->!StringUtils.equals(clsfName, c)).collect(Collectors.toList());
                v.getClassifiers().clear();
                v.withClassifiers(classifiers);
                entsToUpdate.add(v);
            }
        });
        Set<NestedEntityDef> nesToUpdate = new HashSet<>();
        nestedEntities.forEach((k, v) -> {
            if(v.getClassifiers().contains(clsfName)) {
                List<String> classifiers = v.getClassifiers().stream().filter(c->!StringUtils.equals(clsfName, c)).collect(Collectors.toList());
                v.getClassifiers().clear();
                v.withClassifiers(classifiers);
                nesToUpdate.add(v);
            }
        });
        lookupsToUpdate.forEach(v -> lookups.put(v.getName(), v));
        entsToUpdate.forEach(v -> ents.put(v.getName(), v));
        nesToUpdate.forEach(v -> nestedEntities.put(v.getName(), v));
    }

    /**
     * Execute UE apply.
     *
     * @param ctx the ctx
     * @return the update model request context
     */
    public UpdateModelRequestContext executeUEApply(UpdateModelRequestContext ctx) {
        ModelImpl upsert = configurationService.getModel();
        if (upsert != null&&ctx!=null ) {
            Collection<MetaModelListener> listeners = upsert.getApplyModelInstances().values();
            if(CollectionUtils.isNotEmpty(listeners)){
                for (MetaModelListener listener : listeners) {
                    ctx = listener.applyModel(ctx);
                }

            }
        }
        return ctx;
    }
    
    /**
     * Execute UE upsert.
     *
     * @param ctx the ctx
     * @return the update model request context
     */
    public UpdateModelRequestContext executeUEUpsert(UpdateModelRequestContext ctx) {
        ModelImpl upsert = configurationService.getModel();
        if (upsert != null &&ctx!=null) {
            Collection<MetaModelListener> listeners = upsert.getUpsertDraftInstances().values();
            if(CollectionUtils.isNotEmpty(listeners)){
                for (MetaModelListener listener : listeners) {
                    ctx =    listener.upsertDraft(ctx);
                }

            }
        }
        return ctx;
    }
}
