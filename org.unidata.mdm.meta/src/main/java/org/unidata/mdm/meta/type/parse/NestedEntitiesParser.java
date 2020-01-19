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
package org.unidata.mdm.meta.type.parse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.type.info.impl.NestedInfoHolder;
import org.unidata.mdm.meta.util.ModelUtils;

/**
 * @author Mikhail Mikhailov
 *
 */
public class NestedEntitiesParser implements ModelParser<NestedInfoHolder> {

    /**
     * Constructor.
     */
    public NestedEntitiesParser() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, NestedInfoHolder> parse(Model model){
        final Map<String, NestedInfoHolder> entities = new ConcurrentHashMap<>();
        for (NestedEntityDef e : model.getNestedEntities()) {

            Map<String, AttributeModelElement> attrs = ModelUtils.createAttributesMap(e, model.getNestedEntities());
            entities.put(e.getName(), new NestedInfoHolder(e, e.getName(), attrs));
        }

        return entities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<NestedInfoHolder> getValueType() {
        return NestedInfoHolder.class;
    }

}
