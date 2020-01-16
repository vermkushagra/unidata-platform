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

import org.unidata.mdm.meta.VersionedObjectDef;
import org.unidata.mdm.meta.service.MetaModelValidationComponent;

/**
 * In general, this interface responsible for verifying meta model elements.
 * Be careful, implementations of this interface, responsible for only for one element consistency,
 * For whole model consistency responds {@link MetaModelValidationComponent}
 *
 * @param <V> marker param , which show that all top level meta model elements allow version control
 */
public interface ModelElementVerifier<V extends VersionedObjectDef> {

    /**
     * Verify inner state of model element
     *
     * @param modelElement top level meta model element for verifying.
     */
    void verifyModelElement(V modelElement);

    /**
     * Check model element id for unique over current state of system model
     *
     * @param modelElement top level meta model element for verifying.
     * @return true if unique, otherwise false.
     */
    boolean isUniqueModelElementId(V modelElement);

}
