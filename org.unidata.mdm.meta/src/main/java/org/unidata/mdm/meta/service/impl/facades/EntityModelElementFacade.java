package org.unidata.mdm.meta.service.impl.facades;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.meta.AbstractSimpleAttributeDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.PeriodBoundaryDef;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.SourceSystemDef;
import org.unidata.mdm.meta.context.DeleteModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.exception.MetaExceptionIds;
import org.unidata.mdm.meta.service.impl.ModelCache;
import org.unidata.mdm.meta.type.ModelType;
import org.unidata.mdm.meta.type.info.impl.EntitiesGroupWrapper;
import org.unidata.mdm.meta.type.info.impl.EntityInfoHolder;
import org.unidata.mdm.meta.type.info.impl.RelationInfoHolder;
import org.unidata.mdm.meta.util.MetaJaxbUtils;
import org.unidata.mdm.meta.util.ModelUtils;
import org.unidata.mdm.meta.util.ValidityPeriodUtils;
import org.unidata.mdm.system.exception.PlatformBusinessException;

@Component
public class EntityModelElementFacade extends AbstractModelElementFacade<EntityInfoHolder, EntityDef> {

    @Nonnull
    @Override
    public ModelType getModelType() {
        return ModelType.ENTITY;
    }

    @Nullable
    @Override
    public String getModelElementId(@Nonnull EntityDef modelElement) {
        return modelElement.getName();
    }

    @Nonnull
    @Override
    protected String getMarshaledData(@Nonnull EntityDef modelElement) {
        return MetaJaxbUtils.marshalEntity(modelElement);
    }

    @Override
    public void verifyModelElement(EntityDef modelElement) {
        super.verifyModelElement(modelElement);
        if (isBlank(modelElement.getGroupName())) {
            throw new PlatformBusinessException("Group is absent. In " + modelElement.getDisplayName(),
                    MetaExceptionIds.EX_META_GROUP_IS_ABSENT, modelElement.getDisplayName());
        }
        if (RESERVED_NAMES.contains(modelElement.getName())) {
            throw new PlatformBusinessException("Lookup entity has reserved name [" + getModelType().getTag() + "]",
                    MetaExceptionIds.EX_META_RESERVED_TOP_LEVEL_NAME, modelElement.getDisplayName());
        }
        modelElement.getSimpleAttribute().forEach(attr -> checkSimpleAttribute(attr, modelElement.getDisplayName()));
        boolean isMainPresent = modelElement.getSimpleAttribute()
                                            .stream()
                                            .anyMatch(AbstractSimpleAttributeDef::isMainDisplayable);
        if (!isMainPresent) {
            throw new PlatformBusinessException(
                    "Entity doesn't have a main displayable attribute:" + modelElement.getDisplayName(),
                    MetaExceptionIds.EX_META_MAIN_DISPLAYABLE_ATTR_ABSENT, modelElement.getDisplayName());
        }

        PeriodBoundaryDef period = modelElement.getValidityPeriod();
        Date start = ValidityPeriodUtils.getGlobalValidityPeriodStart();
        Date end = ValidityPeriodUtils.getGlobalValidityPeriodEnd();

        if (nonNull(start) && nonNull(period) && nonNull(period.getStart())
         && start.after(MetaJaxbUtils.xmlGregorianCalendarToDate(period.getStart()))) {
            throw new PlatformBusinessException("Period start is not valid",
                    MetaExceptionIds.EX_META_PERIOD_START_BEFORE_GLOBAL_PERIOD, modelElement.getDisplayName());
        }

        if (nonNull(end) && nonNull(period) && nonNull(period.getEnd())
         && end.before(MetaJaxbUtils.xmlGregorianCalendarToDate(period.getEnd()))) {
            throw new PlatformBusinessException("Period end is not valid",
                    MetaExceptionIds.EX_META_PERIOD_END_AFTER_GLOBAL_PERIOD, modelElement.getDisplayName());
        }

        // TODO: Commented out in scope of UN-11834. Move to CLSF.
        /*
        List<String> classifiers = modelElement.getClassifiers();
        List<String> complexNames = modelElement.getComplexAttribute().stream().map(AbstractAttributeDef::getName).collect(Collectors.toList());
        complexNames.retainAll(classifiers);
        if (!complexNames.isEmpty()) {
            throw new BusinessException("Complex attribute has the same name as classifier", ExceptionId.EX_META_IDENTICAL_NAMES, complexNames);
        }
        */
		validateCustomProperties(modelElement.getCustomProperties());
		// TODO: Commented out in scope of UN-11834. Move to DQ.
		// modelElement.getDataQualities().forEach(dq -> validateCustomProperties(dq.getCustomProperties()));
    }

    @Nonnull
    @Override
    public EntityInfoHolder convertToWrapper(@Nonnull EntityDef modelElement, @Nonnull UpdateModelRequestContext ctx) {

        Map<String, AttributeModelElement> attrs = ModelUtils.createAttributesMap(modelElement, ctx.getNestedEntityUpdate());

        List<SourceSystemDef> sourceSystems = metaModelService.getSourceSystemsList();
        Map<String, Map<String, Integer>> bvtMap = ModelUtils.createBvtMap(modelElement, sourceSystems, attrs);
        return new EntityInfoHolder(modelElement, modelElement.getName(), attrs, bvtMap);
    }

    @Override
    public void changeCacheBeforeUpdate(@Nonnull EntityDef modelElement, @Nonnull UpdateModelRequestContext ctx, @Nonnull ModelCache modelCache) {
        super.changeCacheBeforeUpdate(modelElement, ctx, modelCache);

        //remove relations FROM
        modelCache.getCache().get(RelationInfoHolder.class).values().stream()
                .map(wrapper -> (RelationInfoHolder) wrapper)
                .map(RelationInfoHolder::getRelation)
                .filter(el -> modelElement.getName().equals(el.getFromEntity()))
                .map(RelationDef::getName)
                .forEach(id -> modelCache.getCache().get(RelationInfoHolder.class).remove(id));

        //remove yourself from group
        modelCache.getCache().get(EntityInfoHolder.class).values().stream()
                .map(wrapper -> (EntityInfoHolder) wrapper)
                .filter(wrapper -> wrapper.getEntity().getName().equals(modelElement.getName()))
                .map(EntityInfoHolder::getEntity)
                .filter(entity -> !isBlank(entity.getGroupName()))
                .forEach(entity -> {
                    EntitiesGroupWrapper entitiesGroupWrapper = getGroup(entity.getGroupName(), modelCache);
                    if (entitiesGroupWrapper != null) {
                        entitiesGroupWrapper.removeEntity(entity.getName());
                    }
                });

    }

    @Override
    public void changeCacheAfterUpdate(@Nonnull EntityDef modelElement, @Nonnull UpdateModelRequestContext ctx, @Nonnull ModelCache modelCache) {
        super.changeCacheAfterUpdate(modelElement, ctx, modelCache);
        //add yourself to group
        Optional<EntitiesGroupWrapper> wrapperOptional = modelCache.getCache().get(EntitiesGroupWrapper.class).values().stream()
                .map(wrapper -> (EntitiesGroupWrapper) wrapper)
                .filter(wrapper -> wrapper.getWrapperId().equals(modelElement.getGroupName()))
                .findFirst();
        if (wrapperOptional.isPresent()) {
            wrapperOptional.get().addEntityToGroup(modelElement);
        }
    }

    @Nullable
    @Override
    public EntityInfoHolder removeFromCache(@Nonnull String uniqueIdentifier, @Nonnull DeleteModelRequestContext deleteModelRequestContext, @Nonnull ModelCache modelCache) {
        EntityInfoHolder entityWrapper = super.removeFromCache(uniqueIdentifier, deleteModelRequestContext, modelCache);
        if (entityWrapper == null) return null;
        String groupName = entityWrapper.getEntity().getGroupName();
        EntitiesGroupWrapper entityGroup = (EntitiesGroupWrapper) modelCache.getCache().get(EntitiesGroupWrapper.class).get(groupName);
        entityGroup.removeEntity(uniqueIdentifier);
        return entityWrapper;
    }

    private EntitiesGroupWrapper getGroup(String groupName, ModelCache modelCache) {
        return (EntitiesGroupWrapper) modelCache.getCache().get(EntitiesGroupWrapper.class).get(groupName);
    }
}
