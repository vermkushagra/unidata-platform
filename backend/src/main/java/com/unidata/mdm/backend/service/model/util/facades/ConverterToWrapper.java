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

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.service.model.util.wrappers.ModelWrapper;
import com.unidata.mdm.meta.VersionedObjectDef;

/**
 * General interface for converting meta model elements to wrappers which provide addition functionality.
 *
 * @param <W> related with meta model element, which wrap it and provide addition functionality for simplify work with it.
 * @param <V> marker param , which show that all top level meta model elements allow version control
 */
public interface ConverterToWrapper<W extends ModelWrapper, V extends VersionedObjectDef> {

    /**
     * @param modelElement -  first citizen model element.(top level model element)
     * @param ctx          -  update execution context
     * @return wrapper related with meta model element, null if object shouldn't have a wrapper.
     */
    @Nullable
    W convertToWrapper(@Nonnull V modelElement, @Nonnull UpdateModelRequestContext ctx);
}
