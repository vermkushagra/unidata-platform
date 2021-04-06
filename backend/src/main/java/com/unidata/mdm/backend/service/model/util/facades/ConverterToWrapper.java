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
