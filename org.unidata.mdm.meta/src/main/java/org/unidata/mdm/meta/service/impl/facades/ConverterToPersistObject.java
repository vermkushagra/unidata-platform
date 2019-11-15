package org.unidata.mdm.meta.service.impl.facades;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.unidata.mdm.meta.VersionedObjectDef;
import org.unidata.mdm.meta.po.MetaModelPO;

/**
 * General interface for converting meta model elements to persist objects.
 *
 * @param <V> Class of meta model element which support version control on server side
 */
public interface ConverterToPersistObject<V extends VersionedObjectDef> {

    /**
     * @param modelElement -  first citizen model element.(top level model element)
     * @param storageId    - special identifier, in general related with user.
     * @param user         - how create a changes in model
     * @return Object for persisting in DB , null if object shouldn't be persist.
     */
    @Nullable
    MetaModelPO convertToPersistObject(@Nonnull V modelElement, @Nonnull String storageId, @Nonnull String user);
}
