package org.unidata.mdm.meta.service.impl.facades;

import javax.annotation.Nonnull;

import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.meta.VersionedObjectDef;

/**
 * Class for presentation all available functionality related with top level meta model elements.
 *
 * @param <W> related with meta model element, which wrap it and provide addition functionality for simplify work with it.
 * @param <V> marker param , which show that all top level meta model elements allow version control
 */
public interface ModelElementElementFacade<W extends IdentityModelElement, V extends VersionedObjectDef>
        extends
            ConverterToPersistObject<V>, ConverterToWrapper<W, V>, ModelElementVerifier<V>,
            ModelElementVersionController<V>,
            MetaModelCacheExecutor<W, V> {

    String getModelElementId(@Nonnull V modelElement);
}
