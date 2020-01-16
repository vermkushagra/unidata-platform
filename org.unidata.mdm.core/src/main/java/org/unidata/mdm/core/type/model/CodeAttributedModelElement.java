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

package org.unidata.mdm.core.type.model;

import java.util.Collection;
import java.util.Collections;

/**
 * Code attribute holder element.
 * @author Mikhail Mikhailov on Nov 7, 2019
 */
public interface CodeAttributedModelElement {
    /**
     * Gets the code attribute.
     * @return
     */
    default AttributeModelElement getCodeAttribute() {
        return null;
    }
    /**
     * Gets code alternative attributes.
     * @return attributes collection
     */
    default Collection<AttributeModelElement> getCodeAliases() {
        return Collections.emptyList();
    }
}
