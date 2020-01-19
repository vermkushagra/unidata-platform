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

package org.unidata.mdm.core.service;

import java.util.Map;

import javax.annotation.Nonnull;

import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.upath.UPath;
import org.unidata.mdm.core.type.upath.UPathApplicationMode;
import org.unidata.mdm.core.type.upath.UPathExecutionContext;
import org.unidata.mdm.core.type.upath.UPathResult;

/**
 * @author Mikhail Mikhailov
 * UPath service.
 */
public interface UPathService {
    /**
     * Creates UPath chain.
     * @param entity the name of the entity, must not be null
     * @param path UPath expression, must not be null
     * @param attributes the attributes map to use
     * @return UPath instance
     * @throws UPathException
     */
    UPath upathCreate(@Nonnull String entity, @Nonnull String path, Map<String, AttributeModelElement> attributes);
    /**
     * Evaluates the given UPath against the {@linkplain DataRecord}
     * using {@link UPathExecutionContext#FULL_TREE} context and {@link UPathApplicationMode#MODE_ALL}.
     * @param upath the UPath instance
     * @param record the record
     * @return result
     */
    UPathResult upathGet(UPath upath, DataRecord record);
    /**
     * Evaluates the given UPath against the {@linkplain DataRecord}
     * using {@link UPathExecutionContext#FULL_TREE} context and specified application mode.
     * @param upath the UPath instance, must not be null
     * @param record the record, must not be null
     * @param mode the application mode
     * @return result
     * @throws UPathException
     */
    UPathResult upathGet(@Nonnull UPath upath, @Nonnull DataRecord record, UPathApplicationMode mode);
    /**
     * Evaluates the given UPath against the {@linkplain DataRecord}.
     * @param upath the UPath instance, must not be null
     * @param record the record, must not be null
     * @param context the context to apply
     * @param mode the application mode
     * @return result
     * @throws UPathException
     */
    UPathResult upathGet(@Nonnull UPath upath, @Nonnull DataRecord record, UPathExecutionContext context, UPathApplicationMode mode);
    /**
     * Evaluates the given UPath against the {@linkplain DataRecord} and applies target.
     * @param upath the UPath instance, must not be null
     * @param record the record, must not be null
     * @param target target attribute
     * @param context the context
     * @param mode application mode
     * @return true, if target attribute was applied successfullly at least once
     * @throws UPathException
     */
    boolean upathSet(@Nonnull UPath upath, @Nonnull DataRecord record, Attribute target, UPathExecutionContext context, UPathApplicationMode mode);
    /**
     * Evaluates the given UPath against the {@linkplain DataRecord} and applies target.
     * @param upath the UPath instance, must not be null
     * @param record the record, must not be null
     * @param target target attribute
     * @param mode application mode
     * @return true, if target attribute was applied successfullly at least once
     * @throws UPathException
     */
    boolean upathSet(@Nonnull UPath upath, @Nonnull DataRecord record, Attribute target, UPathApplicationMode mode);
    /**
     * Evaluates the given UPath against the {@linkplain DataRecord} and applies target.
     * @param upath the UPath instance, must not be null
     * @param record the record, must not be null
     * @param target target attribute
     * @return true, if target attribute was applied successfullly at least once
     * @throws UPathException
     */
    boolean upathSet(@Nonnull UPath upath, @Nonnull DataRecord record, Attribute target);
    /**
     * Combines previous two.
     * @param entity the name of the entity, must not be null
     * @param path UPath expression, must not be null
     * @param info attributes map
     * @param record the record, must not be null
     * @return result
     * @throws UPathException
     */
    UPathResult upathResult(@Nonnull String entity, @Nonnull String path, @Nonnull Map<String, AttributeModelElement> info, @Nonnull DataRecord record);
}
