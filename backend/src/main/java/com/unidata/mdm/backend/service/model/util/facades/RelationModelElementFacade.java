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

package com.unidata.mdm.backend.service.model.util.facades;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.MetadataException;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.ModelType;
import com.unidata.mdm.backend.service.model.util.ModelCache;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.RelationWrapper;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.ComplexAttributesHolderEntityDef;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

@Component
public class RelationModelElementFacade extends AbstractModelElementFacade<RelationWrapper, RelationDef> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelationModelElementFacade.class);

    @Nonnull
    @Override
    public ModelType getModelType() {
        return ModelType.RELATION;
    }

    @Nullable
    @Override
    public String getModelElementId(@Nonnull RelationDef modelElement) {
        return modelElement.getName();
    }

    @Nonnull
    @Override
    protected String getMarshaledData(@Nonnull RelationDef modelElement) {
        return JaxbUtils.marshalRelation(modelElement);
    }

    @Override
    public boolean isUniqueModelElementId(RelationDef modelElement) {
        boolean isUnique = super.isUniqueModelElementId(modelElement);
        if (!isUnique) return false;
        //check has cache other relations with the same name and if has check that they from the same entity otherwise return false!
        RelationDef cachedRel = metaModelService.getRelationsList().stream().filter(rel -> rel.getName().equals(modelElement.getName())).findAny().orElse(null);
        String from = modelElement.getFromEntity();
        return cachedRel == null || cachedRel.getFromEntity().equals(from);
    }

    @Override
    public void updateVersion(RelationDef modelElement) {
        //because relations everytime removed during entity update.
        modelElement.setVersion(INITIAL_VERSION);
    }

    @Override
    public void verifyModelElement(RelationDef modelElement) {
        super.verifyModelElement(modelElement);
        if (StringUtils.isBlank(modelElement.getFromEntity())
                || StringUtils.isBlank(modelElement.getToEntity())) {
            throw new BusinessException("From and to entities of a relation must be defined.", ExceptionId.EX_META_RELATION_SIDE_IS_ABSENT);
        }
        validateCustomProperties(modelElement.getCustomProperties());
        modelElement.getDataQualities().forEach(dq -> validateCustomProperties(dq.getCustomProperties()));
    }

    @Nonnull
    @Override
    public RelationWrapper convertToWrapper(@Nonnull RelationDef modelElement, @Nonnull UpdateModelRequestContext ctx) {

        ComplexAttributesHolderEntityDef attrsHolder = modelElement;
        if (modelElement.getRelType() == RelType.CONTAINS) {

            attrsHolder = ctx.getEntityUpdate().stream()
                .filter(e -> modelElement.getToEntity().equals(e.getName()))
                .findFirst()
                .orElseThrow(() -> {
                    final String message = "The 'to' side containment entity '{}' of the relation '{}' not found in update.";
                    LOGGER.warn(message, modelElement.getToEntity(), modelElement.getName());
                    return new MetadataException(
                            message,
                            ExceptionId.EX_META_TO_CONTAINMENT_ENTITY_NOT_FOUND_UPDATE,
                            modelElement.getToEntity(),
                            modelElement.getName()
                    );
                });
        }

        Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(attrsHolder, ctx.getNestedEntityUpdate());
        return new RelationWrapper(modelElement, modelElement.getName(), attrs);
    }

    @Nullable
    @Override
    public RelationWrapper removeFromCache(@Nonnull String uniqueIdentifier, @Nonnull DeleteModelRequestContext deleteModelRequestContext, @Nonnull ModelCache modelCache) {
        RelationWrapper relation = super.removeFromCache(uniqueIdentifier, deleteModelRequestContext, modelCache);
        if (relation != null) {
            String from = relation.getRelation().getFromEntity();
            String to = relation.getRelation().getToEntity();
            EntityWrapper fromEntity = (EntityWrapper) modelCache.getCache().get(EntityWrapper.class).get(from);
            if (fromEntity != null) {
                fromEntity.getRelationsFrom().remove(relation);
            }
            EntityWrapper toEntity = (EntityWrapper) modelCache.getCache().get(EntityWrapper.class).get(to);
            if (toEntity != null) {
                toEntity.getRelationsTo().remove(relation);
            }
        }
        return relation;
    }
}
