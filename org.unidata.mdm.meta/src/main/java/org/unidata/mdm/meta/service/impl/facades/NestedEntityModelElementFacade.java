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

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.type.ModelType;
import org.unidata.mdm.meta.type.info.impl.NestedInfoHolder;
import org.unidata.mdm.meta.util.MetaJaxbUtils;
import org.unidata.mdm.meta.util.ModelUtils;

@Component
public class NestedEntityModelElementFacade extends AbstractModelElementFacade<NestedInfoHolder, NestedEntityDef> {

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
        return MetaJaxbUtils.marshalNestedEntity(modelElement);
    }

    @Override
    public void verifyModelElement(NestedEntityDef modelElement) {
        super.verifyModelElement(modelElement);
        modelElement.getSimpleAttribute().stream().forEach(attr-> checkSimpleAttribute(attr, modelElement.getDisplayName()));
        validateCustomProperties(modelElement.getCustomProperties());
        // TODO: Commented out in scope of UN-11834. Move to DQ.
        //modelElement.getDataQualities().forEach(dq -> validateCustomProperties(dq.getCustomProperties()));
    }

    @Nonnull
    @Override
    public NestedInfoHolder convertToWrapper(@Nonnull NestedEntityDef modelElement, @Nonnull UpdateModelRequestContext ctx) {
        Map<String, AttributeModelElement> attrs = ModelUtils.createAttributesMap(modelElement, ctx.getNestedEntityUpdate());
        return new NestedInfoHolder(modelElement, modelElement.getName(), attrs);
    }
}
