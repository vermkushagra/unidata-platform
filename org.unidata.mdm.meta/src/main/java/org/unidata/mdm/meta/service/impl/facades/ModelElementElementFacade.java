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
