/**
 *
 */
package org.unidata.mdm.meta.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.unidata.mdm.core.type.model.ModelElement;
import org.unidata.mdm.meta.service.impl.ModelCache;

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
    public static <T extends ModelElement> T getValueById(
            Map<Class<? extends ModelElement>, Map<String, ? extends ModelElement>> cache,
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
    public static <T extends ModelElement> T getValueById(
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
    public static <T extends ModelElement> Collection<T> getValues(
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
    public static <T extends ModelElement> Collection<T> getValues(
            Map<Class<? extends ModelElement>, Map<String, ? extends ModelElement>> cache, Class<T> cachedType) {
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
    public static <T extends ModelElement> void putValue(ModelCache cache, String id, T cached,
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
    public static <T extends ModelElement> void putValue(
            Map<Class<? extends ModelElement>, Map<String, ? extends ModelElement>> cache,
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
    public static <T extends ModelElement> void putAllValues(
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
    public static <T extends ModelElement> void putAllValues(
            Map<Class<? extends ModelElement>, Map<String, ? extends ModelElement>> cache,
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
    public static <T extends ModelElement> void removeValueById(
            ModelCache cache, String id, Class<T> cachedType) {
        cache.getCache().get(cachedType).remove(id);
    }
}
