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

import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.service.model.ModelType;
import com.unidata.mdm.backend.service.model.util.wrappers.SourceSystemWrapper;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.SourceSystemDef;

@Component
public class SourceSystemModelElementFacade extends AbstractModelElementFacade<SourceSystemWrapper, SourceSystemDef> {

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
        return JaxbUtils.marshalSourceSystem(modelElement);
    }


    @Nonnull
    @Override
    public SourceSystemWrapper convertToWrapper(@Nonnull SourceSystemDef modelElement, @Nonnull UpdateModelRequestContext ctx) {
        return new SourceSystemWrapper(modelElement);
    }
}
