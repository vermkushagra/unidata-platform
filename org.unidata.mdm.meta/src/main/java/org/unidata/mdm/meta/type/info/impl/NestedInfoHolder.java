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

/**
 *
 */
package org.unidata.mdm.meta.type.info.impl;

import java.util.Map;

import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.meta.NestedEntityDef;

/**
 * @author Mikhail Mikhailov
 * Nested entities wrapper.
 */
public class NestedInfoHolder extends AbstractAttributesInfoHolder implements IdentityModelElement {
    /**
     * The entity.
     */
    private final NestedEntityDef entity;
    /**
     * Entity name
     */
    private final String id;
    /**
     * Constructor.
     * @param entity nested entity
     * @param id its ID
     * @param attrs attributes
     */
    public NestedInfoHolder(NestedEntityDef entity, String id, Map<String, AttributeModelElement> attrs) {
        super(attrs);
        this.entity = entity;
        this.id = id;
    }

    /**
     * @return the entity
     */
    public NestedEntityDef getEntity() {
        return entity;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Long getVersion() {
        return entity.getVersion();
    }
}
