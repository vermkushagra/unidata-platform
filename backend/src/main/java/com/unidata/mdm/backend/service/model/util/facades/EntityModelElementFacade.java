package com.unidata.mdm.backend.service.model.util.facades;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.ModelType;
import com.unidata.mdm.backend.service.model.util.ModelCache;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.EntitiesGroupWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.RelationWrapper;
import com.unidata.mdm.backend.service.search.impl.IndexComponent;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.AbstractSimpleAttributeDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.PeriodBoundaryDef;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SourceSystemDef;

@Component
public class EntityModelElementFacade extends AbstractModelElementFacade<EntityWrapper, EntityDef> {

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
        return JaxbUtils.marshalEntity(modelElement);
    }

    @Override
    public void verifyModelElement(EntityDef modelElement) {
        super.verifyModelElement(modelElement);
        if (isBlank(modelElement.getGroupName())) {
            throw new BusinessException("Group is absent. In " + modelElement.getDisplayName(),
                    ExceptionId.EX_META_GROUP_IS_ABSENT, modelElement.getDisplayName());
        }
        if (IndexComponent.RESERVED_INDEX_NAMES.contains(modelElement.getName())) {
            throw new BusinessException("Lookup entity has reserved name [" + getModelType().getTag() + "]",
                    ExceptionId.EX_META_RESERVED_TOP_LEVEL_NAME, modelElement.getDisplayName());
        }
        modelElement.getSimpleAttribute().forEach(attr -> checkSimpleAttribute(attr, modelElement.getDisplayName()));
        boolean isMainPresent = modelElement.getSimpleAttribute()
                                            .stream()
                                            .anyMatch(AbstractSimpleAttributeDef::isMainDisplayable);
        if (!isMainPresent) {
            throw new BusinessException(
                    "Entity doesn't have a main displayable attribute:" + modelElement.getDisplayName(),
                    ExceptionId.EX_META_MAIN_DISPLAYABLE_ATTR_ABSENT, modelElement.getDisplayName());
        }
        PeriodBoundaryDef period = modelElement.getValidityPeriod();
        Date start = ValidityPeriodUtils.getGlobalValidityPeriodStart();
        Date end = ValidityPeriodUtils.getGlobalValidityPeriodEnd();
        if (nonNull(start) && nonNull(period) && nonNull(period.getStart()) && start.after(period.getStart().toGregorianCalendar().getTime())) {
            throw new BusinessException("Period start is not valid", ExceptionId.EX_META_PERIOD_START_BEFORE_GLOBAL_PERIOD, modelElement.getDisplayName());
        }
        if (nonNull(end) && nonNull(period) && nonNull(period.getEnd()) && end.before(period.getEnd().toGregorianCalendar().getTime())) {
            throw new BusinessException("Period end is not valid", ExceptionId.EX_META_PERIOD_END_AFTER_GLOBAL_PERIOD, modelElement.getDisplayName());
        }

        List<String> classifiers = modelElement.getClassifiers();
        List<String> complexNames = modelElement.getComplexAttribute().stream().map(AbstractAttributeDef::getName).collect(Collectors.toList());
        complexNames.retainAll(classifiers);
        if (!complexNames.isEmpty()) {
            throw new BusinessException("Complex attribute has the same name as classifier", ExceptionId.EX_META_IDENTICAL_NAMES, complexNames);
        }

        validateCustomProperties(modelElement.getCustomProperties());
    }

    @Nonnull
    @Override
    public EntityWrapper convertToWrapper(@Nonnull EntityDef modelElement, @Nonnull UpdateModelRequestContext ctx) {

        Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(modelElement, ctx.getNestedEntityUpdate());

        List<SourceSystemDef> sourceSystems = metaModelService.getSourceSystemsList();
        Map<String, Map<String, Integer>> bvtMap = ModelUtils.createBvtMap(modelElement, sourceSystems, attrs);
        return new EntityWrapper(modelElement, modelElement.getName(), attrs, bvtMap);
    }

    @Override
    public void changeCacheBeforeUpdate(@Nonnull EntityDef modelElement, @Nonnull UpdateModelRequestContext ctx, @Nonnull ModelCache modelCache) {
        super.changeCacheBeforeUpdate(modelElement, ctx, modelCache);

        //remove relations FROM
        modelCache.getCache().get(RelationWrapper.class).values().stream()
                .map(wrapper -> (RelationWrapper) wrapper)
                .map(RelationWrapper::getRelation)
                .filter(el -> modelElement.getName().equals(el.getFromEntity()))
                .map(RelationDef::getName)
                .forEach(id -> modelCache.getCache().get(RelationWrapper.class).remove(id));

        //remove yourself from group
        modelCache.getCache().get(EntityWrapper.class).values().stream()
                .map(wrapper -> (EntityWrapper) wrapper)
                .filter(wrapper -> wrapper.getEntity().getName().equals(modelElement.getName()))
                .map(EntityWrapper::getEntity)
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
    public EntityWrapper removeFromCache(@Nonnull String uniqueIdentifier, @Nonnull DeleteModelRequestContext deleteModelRequestContext, @Nonnull ModelCache modelCache) {
        EntityWrapper entityWrapper = super.removeFromCache(uniqueIdentifier, deleteModelRequestContext, modelCache);
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
