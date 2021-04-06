package com.unidata.mdm.backend.service.model.util.facades;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.service.model.util.wrappers.ModelWrapper;
import com.unidata.mdm.meta.VersionedObjectDef;

/**
 * Class for presentation all available functionality related with top level meta model elements.
 *
 * @param <W> related with meta model element, which wrap it and provide addition functionality for simplify work with it.
 * @param <V> marker param , which show that all top level meta model elements allow version control
 */
public interface ModelElementElementFacade<W extends ModelWrapper, V extends VersionedObjectDef>
        extends ConverterToPersistObject<V>, ConverterToWrapper<W, V>, ModelElementVerifier<V>
        , ModelElementVersionController<V>, MetaModelCacheExecutor<W, V> {

    String getModelElementId(@Nonnull V modelElement);
}
