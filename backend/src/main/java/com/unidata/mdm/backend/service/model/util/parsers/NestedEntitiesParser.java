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
package com.unidata.mdm.backend.service.model.util.parsers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.NestedEntityWrapper;
import com.unidata.mdm.meta.Model;
import com.unidata.mdm.meta.NestedEntityDef;

/**
 * @author Mikhail Mikhailov
 *
 */
public class NestedEntitiesParser implements ModelParser<NestedEntityWrapper> {

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
    public Map<String, NestedEntityWrapper> parse(Model model){
        final Map<String, NestedEntityWrapper> entities = new ConcurrentHashMap<>();
        for (NestedEntityDef e : model.getNestedEntities()) {
        	
            Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(e, model.getNestedEntities());
            entities.put(e.getName(), new NestedEntityWrapper(e, e.getName(), attrs));
        }

        return entities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<NestedEntityWrapper> getValueType() {
        return NestedEntityWrapper.class;
    }

}
