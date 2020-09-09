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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.ModelType;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.NestedEntityWrapper;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.NestedEntityDef;

@Component
public class NestedEntityModelElementFacade extends AbstractModelElementFacade<NestedEntityWrapper, NestedEntityDef> {

    @Nonnull
    @Override
    public ModelType getModelType() {
        return ModelType.NESTED_ENTITY;
    }

    @Nullable
    @Override
    public String getModelElementId(@Nonnull NestedEntityDef modelElement) {
        return modelElement.getName();
    }

    @Nonnull
    @Override
    protected String getMarshaledData(@Nonnull NestedEntityDef modelElement) {
        return JaxbUtils.marshalNestedEntity(modelElement);
    }

    @Override
    public void verifyModelElement(NestedEntityDef modelElement) {
        super.verifyModelElement(modelElement);
        modelElement.getSimpleAttribute().stream().forEach(attr-> checkSimpleAttribute(attr, modelElement.getDisplayName()));
        validateCustomProperties(modelElement.getCustomProperties());
        modelElement.getDataQualities().forEach(dq -> validateCustomProperties(dq.getCustomProperties()));
    }

    @Nonnull
    @Override
    public NestedEntityWrapper convertToWrapper(@Nonnull NestedEntityDef modelElement, @Nonnull UpdateModelRequestContext ctx) {
        Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(modelElement, ctx.getNestedEntityUpdate());
        return new NestedEntityWrapper(modelElement, modelElement.getName(), attrs);
    }
}
