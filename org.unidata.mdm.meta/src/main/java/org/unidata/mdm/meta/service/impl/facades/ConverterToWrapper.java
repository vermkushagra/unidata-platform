package org.unidata.mdm.meta.service.impl.facades;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.meta.VersionedObjectDef;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;

/**
 * General interface for converting meta model elements to wrappers which provide addition functionality.
 *
 * @param <W> related with meta model element, which wrap it and provide addition functionality for simplify work with it.
 * @param <V> marker param , which show that all top level meta model elements allow version control
 */
public interface ConverterToWrapper<W extends IdentityModelElement, V extends VersionedObjectDef> {

    /**
     * @param modelElement -  first citizen model element.(top level model element)
     * @param ctx          -  update execution context
     * @return wrapper related with meta model element, null if object shouldn't have a wrapper.
     */
    @Nullable
    W convertToWrapper(@Nonnull V modelElement, @Nonnull UpdateModelRequestContext ctx);
}
