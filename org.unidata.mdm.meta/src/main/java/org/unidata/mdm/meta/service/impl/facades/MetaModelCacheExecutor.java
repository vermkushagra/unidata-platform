package org.unidata.mdm.meta.service.impl.facades;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.meta.VersionedObjectDef;
import org.unidata.mdm.meta.context.DeleteModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.service.impl.ModelCache;

/**
 * Interface which provide ability modify cache before applying new changes and after
 *
 * @param <V> marker param , which show that all top level meta model elements allow version control
 */
public interface MetaModelCacheExecutor<W extends IdentityModelElement, V extends VersionedObjectDef> {

    /**
     * Cache still contains old values, what allow to make some changes for consistency.
     * Before new elements of cache will be put in cache
     *
     * @param modelElement element from context which was processed
     * @param ctx          context whom modify cache
     * @param modelCache   updated cache
     */
    default void changeCacheBeforeUpdate(@Nonnull V modelElement, @Nonnull UpdateModelRequestContext ctx, @Nonnull ModelCache modelCache){    }

    /**
     * Cache just was updated by new model elements, what allow to make some changes with addition information.
     *
     * @param modelElement element from context which was processed
     * @param ctx          context whom modify cache
     * @param modelCache   updated cache
     */
    default void changeCacheAfterUpdate(@Nonnull V modelElement, @Nonnull UpdateModelRequestContext ctx, @Nonnull ModelCache modelCache){}

    /**
     * Action which remove model elements from cache.
     *
     * @param uniqueIdentifier          unique identifier
     * @param deleteModelRequestContext whole delete context
     * @param modelCache                updated cache
     */
    @Nullable
    W removeFromCache(@Nonnull String uniqueIdentifier, @Nonnull DeleteModelRequestContext deleteModelRequestContext, @Nonnull ModelCache modelCache);

}
