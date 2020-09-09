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

package com.unidata.mdm.backend.service.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;
import com.unidata.mdm.backend.service.model.util.facades.AbstractModelElementFacade;
import com.unidata.mdm.backend.service.model.util.wrappers.ModelWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.ValueWrapper;
import com.unidata.mdm.meta.VersionedObjectDef;

/**
 * The Interface MetaModelServiceExt.
 */
public interface MetaModelServiceExt extends MetaModelService, AfterContextRefresh {
    /**
	 * Gets the model facade.
	 *
	 * @param <W>
	 *            the generic type
	 * @param <E>
	 *            the element type
	 * @param processedModelElement
	 *            the processed model element
	 * @return the model facade
	 */
    @Nullable
    <W extends ModelWrapper, E extends VersionedObjectDef> AbstractModelElementFacade<W, E> getModelFacade(Class<E> processedModelElement);

    /**
     * Gets the value by id.
     *
     * @param <T>        the generic type
     * @param id         the id
     * @param cachedType the cache type
     * @return the value by id
     */
    <T extends ValueWrapper> T getValueById(String id, Class<T> cachedType);

    /**
     * Remoces object by id.
     *
     * @param <T>
     *            the generic type
     * @param id
     *            the id
     * @param cachedType
     *            cached type
     */
    <T extends ValueWrapper> void removeValueById(String id, Class<T> cachedType);

    /**
     * Gets all values of a type.
     *
     * @param <T>
     *            the generic type
     * @param cachedType
     *            the type
     * @return value list
     */
    <T extends ValueWrapper> Collection<T> getValues(Class<T> cachedType);

    /**
     * Puts a value.
     *
     * @param <T>
     *            the generic type
     * @param id
     *            the id
     * @param cached
     *            wrapper to cache
     * @param cachedType
     *            cached type
     */
    <T extends ValueWrapper> void putValue(String id, T cached,
                                           Class<T> cachedType);

    Map<String, Float> getBoostScoreForEntity(final String entityName, List<String> searchFields);
}
