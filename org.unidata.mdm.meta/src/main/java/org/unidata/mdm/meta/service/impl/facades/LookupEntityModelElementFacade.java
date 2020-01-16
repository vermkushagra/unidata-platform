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

package org.unidata.mdm.meta.service.impl.facades;

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
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.meta.AbstractSimpleAttributeDef;
import org.unidata.mdm.meta.CodeAttributeDef;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.PeriodBoundaryDef;
import org.unidata.mdm.meta.SourceSystemDef;
import org.unidata.mdm.meta.context.DeleteModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.exception.MetaExceptionIds;
import org.unidata.mdm.meta.service.impl.ModelCache;
import org.unidata.mdm.meta.type.ModelType;
import org.unidata.mdm.meta.type.info.impl.EntitiesGroupWrapper;
import org.unidata.mdm.meta.type.info.impl.LookupInfoHolder;
import org.unidata.mdm.meta.util.MetaJaxbUtils;
import org.unidata.mdm.meta.util.ModelUtils;
import org.unidata.mdm.meta.util.ValidityPeriodUtils;
import org.unidata.mdm.system.exception.PlatformBusinessException;

@Component
public class LookupEntityModelElementFacade extends AbstractModelElementFacade<LookupInfoHolder, LookupEntityDef> {

    @Nonnull
    @Override
    public LookupInfoHolder convertToWrapper(@Nonnull LookupEntityDef modelElement, @Nonnull UpdateModelRequestContext ctx) {
        Map<String, AttributeModelElement> attrs = ModelUtils.createAttributesMap(modelElement, Collections.emptyList());

        List<SourceSystemDef> sourceSystems = metaModelService.getSourceSystemsList();
        Map<String, Map<String, Integer>> bvtMap = ModelUtils.createBvtMap(modelElement, sourceSystems, attrs);
        return new LookupInfoHolder(modelElement, modelElement.getName(), attrs, bvtMap);
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
            throw new PlatformBusinessException("Group is absent. In " + modelElement.getDisplayName(),
                    MetaExceptionIds.EX_META_GROUP_IS_ABSENT, modelElement.getDisplayName());
        }
        if (RESERVED_NAMES.contains(modelElement.getName())) {
            throw new PlatformBusinessException("Lookup entity has reserved name [" + getModelType().getTag() + "]",
                    MetaExceptionIds.EX_META_RESERVED_TOP_LEVEL_NAME, modelElement.getDisplayName());
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
            throw new PlatformBusinessException(
                    "Entity doesn't have a main displayable attribute:" + modelElement.getDisplayName(),
                    MetaExceptionIds.EX_META_MAIN_DISPLAYABLE_ATTR_ABSENT, modelElement.getDisplayName());
        }
        //check timeline
        PeriodBoundaryDef period = modelElement.getValidityPeriod();
        Date start = ValidityPeriodUtils.getGlobalValidityPeriodStart();
        Date end = ValidityPeriodUtils.getGlobalValidityPeriodEnd();
        if (nonNull(start) && nonNull(period) && nonNull(period.getStart()) && start.after(period.getStart().toGregorianCalendar().getTime())) {
            throw new PlatformBusinessException("Period start is not valid",
                    MetaExceptionIds.EX_META_PERIOD_START_BEFORE_GLOBAL_PERIOD, modelElement.getDisplayName());
        }
        if (nonNull(end) && nonNull(period) && nonNull(period.getEnd()) && end.before(period.getEnd().toGregorianCalendar().getTime())) {
            throw new PlatformBusinessException("Period end is not valid",
                    MetaExceptionIds.EX_META_PERIOD_END_AFTER_GLOBAL_PERIOD, modelElement.getDisplayName());
        }

        validateCustomProperties(modelElement.getCustomProperties());
        // TODO: Commented out in scope of UN-11834. Move to DQ.
        // modelElement.getDataQualities().forEach(dq -> validateCustomProperties(dq.getCustomProperties()));
    }

    private void checkCodeAttribute(CodeAttributeDef codeAttr, String lookupEntityName, boolean isAlternative) {

        if (Objects.isNull(codeAttr)) {
            throw new PlatformBusinessException
            ("Code attribute is absent", MetaExceptionIds.EX_META_CODE_ATTRIBUTE_IS_ABSENT, lookupEntityName);
        }

        boolean isCorrectCodeAttr = (!codeAttr.isNullable() || isAlternative)
                && (!codeAttr.isReadOnly() || isAlternative)
                && codeAttr.isUnique()
                && codeAttr.isDisplayable()
                && codeAttr.isSearchable()
                && !codeAttr.isHidden();

        if (!isCorrectCodeAttr) {
            throw new PlatformBusinessException("Code attribute is incorrect",
                    MetaExceptionIds.EX_META_CODE_ATTRIBUTE_IS_INCORRECT, codeAttr.getDisplayName(), lookupEntityName);
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
        return MetaJaxbUtils.marshalLookupEntity(modelElement);
    }

    @Override
    public void changeCacheBeforeUpdate(@Nonnull LookupEntityDef modelElement, @Nonnull UpdateModelRequestContext ctx, @Nonnull ModelCache modelCache) {
        super.changeCacheBeforeUpdate(modelElement, ctx, modelCache);
        //remove yourself from group
        modelCache.getCache().get(LookupInfoHolder.class).values().stream()
                .map(wrapper -> (LookupInfoHolder) wrapper)
                .filter(wrapper -> wrapper.getEntity().getName().equals(modelElement.getName()))
                .map(LookupInfoHolder::getEntity)
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
    public LookupInfoHolder removeFromCache(@Nonnull String uniqueIdentifier, @Nonnull DeleteModelRequestContext deleteModelRequestContext, @Nonnull ModelCache modelCache) {
        LookupInfoHolder entityWrapper = super.removeFromCache(uniqueIdentifier, deleteModelRequestContext, modelCache);
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
