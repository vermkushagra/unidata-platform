package com.unidata.mdm.backend.service.model.util.facades;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.backend.service.search.impl.IndexComponent;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;
import com.unidata.mdm.meta.AbstractSimpleAttributeDef;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.PeriodBoundaryDef;
import com.unidata.mdm.meta.SourceSystemDef;

@Component
public class LookupEntityModelElementFacade extends AbstractModelElementFacade<LookupEntityWrapper, LookupEntityDef> {

    @Nonnull
    @Override
    public LookupEntityWrapper convertToWrapper(@Nonnull LookupEntityDef modelElement, @Nonnull UpdateModelRequestContext ctx) {
        Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(modelElement, Collections.emptyList());

        List<SourceSystemDef> sourceSystems = metaModelService.getSourceSystemsList();
        Map<String, Map<String, Integer>> bvtMap = ModelUtils.createBvtMap(modelElement, sourceSystems, attrs);
        return new LookupEntityWrapper(modelElement, modelElement.getName(), attrs, bvtMap);
    }

    @Nonnull
    @Override
    public ModelType getModelType() {
        return ModelType.LOOKUP_ENTITY;
    }

    @Override
    public void verifyModelElement(LookupEntityDef modelElement) {
        super.verifyModelElement(modelElement);
        //check group
        if (isBlank(modelElement.getGroupName())) {
            throw new BusinessException("Group is absent. In " + modelElement.getDisplayName(),
                    ExceptionId.EX_META_GROUP_IS_ABSENT, modelElement.getDisplayName());
        }
        if (IndexComponent.RESERVED_INDEX_NAMES.contains(modelElement.getName())) {
            throw new BusinessException("Lookup entity has reserved name [" + getModelType().getTag() + "]",
                    ExceptionId.EX_META_RESERVED_TOP_LEVEL_NAME, modelElement.getDisplayName());
        }
        //check code attrs
        final String lookupEntityName = modelElement.getDisplayName();
        checkCodeAttribute(modelElement.getCodeAttribute(), lookupEntityName, false);
        modelElement.getAliasCodeAttributes().forEach(code -> this.checkCodeAttribute(code, lookupEntityName, true));
        //check simple attrs
        modelElement.getSimpleAttribute().forEach(attr-> checkSimpleAttribute(attr, modelElement.getDisplayName()));
        boolean isMainPresentInSimple = modelElement.getSimpleAttribute()
                                            .stream()
                                            .anyMatch(AbstractSimpleAttributeDef::isMainDisplayable);
        boolean isMainPresentInCode = modelElement.getAliasCodeAttributes()
                                 .stream()
                                 .anyMatch(AbstractSimpleAttributeDef::isMainDisplayable);
        if (!isMainPresentInSimple && !isMainPresentInCode && !modelElement.getCodeAttribute().isMainDisplayable()) {
            throw new BusinessException(
                    "Entity doesn't have a main displayable attribute:" + modelElement.getDisplayName(),
                    ExceptionId.EX_META_MAIN_DISPLAYABLE_ATTR_ABSENT, modelElement.getDisplayName());
        }
        //check timeline
        PeriodBoundaryDef period = modelElement.getValidityPeriod();
        Date start = ValidityPeriodUtils.getGlobalValidityPeriodStart();
        Date end = ValidityPeriodUtils.getGlobalValidityPeriodEnd();
        if (nonNull(start) && nonNull(period) && nonNull(period.getStart()) && start.after(period.getStart().toGregorianCalendar().getTime())) {
            throw new BusinessException("Period start is not valid", ExceptionId.EX_META_PERIOD_START_BEFORE_GLOBAL_PERIOD, modelElement.getDisplayName());
        }
        if (nonNull(end) && nonNull(period) && nonNull(period.getEnd()) && end.before(period.getEnd().toGregorianCalendar().getTime())) {
            throw new BusinessException("Period end is not valid", ExceptionId.EX_META_PERIOD_END_AFTER_GLOBAL_PERIOD, modelElement.getDisplayName());
        }

        validateCustomProperties(modelElement.getCustomProperties());
    }

    private void checkCodeAttribute(CodeAttributeDef codeAttr, String lookupEntityName, boolean isAlternative) {
        if (Objects.isNull(codeAttr)) {
            throw new BusinessException("Code attribute is absent", ExceptionId.EX_META_CODE_ATTRIBUTE_IS_ABSENT, lookupEntityName);
        }

        boolean isCorrectCodeAttr = (!codeAttr.isNullable() || isAlternative)
                && (!codeAttr.isReadOnly() || isAlternative)
                && codeAttr.isUnique()
                && codeAttr.isDisplayable()
                && codeAttr.isSearchable()
                && !codeAttr.isHidden();

        if (!isCorrectCodeAttr) {
            throw new BusinessException("Code attribute is incorrect", ExceptionId.EX_META_CODE_ATTRIBUTE_IS_INCORRECT, codeAttr.getDisplayName(), lookupEntityName);
        }
    }

    @Nullable
    @Override
    public String getModelElementId(@Nonnull LookupEntityDef modelElement) {
        return modelElement.getName();
    }

    @Nonnull
    @Override
    protected String getMarshaledData(@Nonnull LookupEntityDef modelElement) {
        return JaxbUtils.marshalLookupEntity(modelElement);
    }

    @Override
    public void changeCacheBeforeUpdate(@Nonnull LookupEntityDef modelElement, @Nonnull UpdateModelRequestContext ctx, @Nonnull ModelCache modelCache) {
        super.changeCacheBeforeUpdate(modelElement, ctx, modelCache);
        //remove yourself from group
        modelCache.getCache().get(LookupEntityWrapper.class).values().stream()
                .map(wrapper -> (LookupEntityWrapper) wrapper)
                .filter(wrapper -> wrapper.getEntity().getName().equals(modelElement.getName()))
                .map(LookupEntityWrapper::getEntity)
                .filter(entity -> !isBlank(entity.getGroupName()))
                .forEach(entity -> {
                    EntitiesGroupWrapper entitiesGroupWrapper = getGroup(entity.getGroupName(), modelCache);
                    if (entitiesGroupWrapper != null) {
                        entitiesGroupWrapper.removeLookupEntity(entity.getName());
                    }
                });
    }

    @Override
    public void changeCacheAfterUpdate(@Nonnull LookupEntityDef modelElement, @Nonnull UpdateModelRequestContext ctx, @Nonnull ModelCache modelCache) {
        super.changeCacheAfterUpdate(modelElement, ctx, modelCache);
        //add yourself to group
        Optional<EntitiesGroupWrapper> wrapperOptional = modelCache.getCache().get(EntitiesGroupWrapper.class).values().stream()
                .map(wrapper -> (EntitiesGroupWrapper) wrapper)
                .filter(wrapper -> wrapper.getWrapperId().equals(modelElement.getGroupName()))
                .findFirst();
        wrapperOptional.ifPresent(entitiesGroupWrapper -> entitiesGroupWrapper.addLookupEntityToGroup(modelElement));
    }

    @Nullable
    @Override
    public LookupEntityWrapper removeFromCache(@Nonnull String uniqueIdentifier, @Nonnull DeleteModelRequestContext deleteModelRequestContext, @Nonnull ModelCache modelCache) {
        LookupEntityWrapper entityWrapper = super.removeFromCache(uniqueIdentifier, deleteModelRequestContext, modelCache);
        if (entityWrapper == null) return null;
        String groupName = entityWrapper.getEntity().getGroupName();
        EntitiesGroupWrapper entityGroup = (EntitiesGroupWrapper) modelCache.getCache().get(EntitiesGroupWrapper.class).get(groupName);
        entityGroup.removeLookupEntity(uniqueIdentifier);
        return entityWrapper;
    }

    private EntitiesGroupWrapper getGroup(String groupName, ModelCache modelCache) {
        return (EntitiesGroupWrapper) modelCache.getCache().get(EntitiesGroupWrapper.class).get(groupName);
    }
}
