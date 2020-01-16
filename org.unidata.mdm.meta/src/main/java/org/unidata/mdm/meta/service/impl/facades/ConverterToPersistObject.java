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
