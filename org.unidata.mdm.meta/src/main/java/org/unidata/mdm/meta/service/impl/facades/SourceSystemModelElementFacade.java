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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.SourceSystemDef;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.type.ModelType;
import org.unidata.mdm.meta.type.info.impl.SourceSystemInfoHolder;
import org.unidata.mdm.meta.util.MetaJaxbUtils;

@Component
public class SourceSystemModelElementFacade extends AbstractModelElementFacade<SourceSystemInfoHolder, SourceSystemDef> {

    @Nonnull
    @Override
    public ModelType getModelType() {
        return ModelType.SOURCE_SYSTEM;
    }

    @Nullable
    @Override
    public String getModelElementId(@Nonnull SourceSystemDef modelElement) {
        return modelElement.getName();
    }

    @Nonnull
    @Override
    protected String getMarshaledData(@Nonnull SourceSystemDef modelElement) {
        return MetaJaxbUtils.marshalSourceSystem(modelElement);
    }


    @Nonnull
    @Override
    public SourceSystemInfoHolder convertToWrapper(@Nonnull SourceSystemDef modelElement, @Nonnull UpdateModelRequestContext ctx) {
        return new SourceSystemInfoHolder(modelElement);
    }
}
