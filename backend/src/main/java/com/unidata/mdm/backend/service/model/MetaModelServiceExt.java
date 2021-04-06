package com.unidata.mdm.backend.service.model;

import java.util.Collection;

import javax.annotation.Nullable;

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
}
