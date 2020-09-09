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

/**
 *
 */
package com.unidata.mdm.backend.service.model.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.unidata.mdm.backend.service.model.util.wrappers.ValueWrapper;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ModelCacheUtils {

    /**
     * Constructor.
     */
    private ModelCacheUtils() {
        super();
    }

    /**
     * Gets value from cache.
     * @param cache
     * @param id
     * @param cachedType
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends ValueWrapper> T getValueById(
            Map<Class<? extends ValueWrapper>, Map<String, ? extends ValueWrapper>> cache,
            String id, Class<T> cachedType) {
        return cache != null && id != null ? (T) cache.get(cachedType).get(id) : null;
    }

    /**
     * Gets the value by id.
     *
     * @param <T>
     *            the generic type
     * @param id
     *            the id
     * @param cacheType
     *            the cache type
     * @return the value by id
     */
    public static <T extends ValueWrapper> T getValueById(
            ModelCache cache, String id, Class<T> type) {
        return cache != null ? getValueById(cache.getCache(), id, type) : null;
    }

    /**
     * Gets all values of a type.
     *
     * @param cachedType
     *            the type
     * @return value list
     */
    public static <T extends ValueWrapper> Collection<T> getValues(
            ModelCache cache, Class<T> cachedType) {
        return cache != null ? getValues(cache.getCache(), cachedType) : Collections.emptyList();
    }

    /**
     * Gets all values of a type.
     *
     * @param cachedType
     *            the type
     * @return value list
     */
    @SuppressWarnings("unchecked")
    public static <T extends ValueWrapper> Collection<T> getValues(
            Map<Class<? extends ValueWrapper>, Map<String, ? extends ValueWrapper>> cache, Class<T> cachedType) {
        return cache != null ? (Collection<T>) cache.get(cachedType).values() : null;
    }

    /**
     * Puts a value.
     *
     * @param id
     *            the id
     * @param cached
     *            wrapper to cache
     * @param cachedType
     *            cached type
     */
    @SuppressWarnings("unchecked")
    public static <T extends ValueWrapper> void putValue(ModelCache cache, String id, T cached,
            Class<T> cachedType) {
        if (cache != null) {
            ((Map<String, T>) cache.getCache().get(cachedType)).put(id, cached);
        }
    }

    /**
     * Puts a value.
     *
     * @param id
     *            the id
     * @param cached
     *            wrapper to cache
     * @param cachedType
     *            cached type
     */
    @SuppressWarnings("unchecked")
    public static <T extends ValueWrapper> void putValue(
            Map<Class<? extends ValueWrapper>, Map<String, ? extends ValueWrapper>> cache,
            String id, T cached,
            Class<T> cachedType) {
        if (cache != null) {
            ((Map<String, T>) cache.get(cachedType)).put(id, cached);
        }
    }

    /**
     * Puts a value.
     *
     * @param id
     *            the id
     * @param cached
     *            wrapper to cache
     * @param cachedType
     *            cached type
     */
    public static <T extends ValueWrapper> void putAllValues(
            ModelCache cache, Map<String, T> chunk, Class<T> cachedType) {
        if (cache != null) {
            putAllValues(cache.getCache(), chunk, cachedType);
        }
    }

    /**
     * Puts a value.
     *
     * @param id
     *            the id
     * @param cached
     *            wrapper to cache
     * @param cachedType
     *            cached type
     */
    @SuppressWarnings("unchecked")
    public static <T extends ValueWrapper> void putAllValues(
            Map<Class<? extends ValueWrapper>, Map<String, ? extends ValueWrapper>> cache,
            Map<String, T> chunk,
            Class<T> cachedType) {
        if (cache != null) {
            ((Map<String, T>) cache.get(cachedType)).putAll(chunk);
        }
    }

    /**
     * Removes by id.
     * @param <T> the cace type
     * @param id the id
     * @param cachedType the cache type
     */
    public static <T extends ValueWrapper> void removeValueById(
            ModelCache cache, String id, Class<T> cachedType) {
        cache.getCache().get(cachedType).remove(id);
    }
}
