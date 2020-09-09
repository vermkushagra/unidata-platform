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
import com.unidata.mdm.backend.service.cleanse.DQUtils;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.Model;
/**
 * @author Mikhail Mikhailov
 * Entities parser type.
 */
public class EntitiesParser implements ModelParser<EntityWrapper> {

    /**
     * Constructor.
     */
    public EntitiesParser() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, EntityWrapper> parse(Model model){

        final Map<String, EntityWrapper> entities = new ConcurrentHashMap<>();
        for (EntityDef e : model.getEntities()) {

            Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(e, model.getNestedEntities());
            Map<String, Map<String, Integer>> bvtMap = ModelUtils.createBvtMap(e, model.getSourceSystems(), attrs);
            EntityWrapper ew = new EntityWrapper(e, e.getName(), attrs, bvtMap);
            DQUtils.removeSystemRules(ew, e.getDataQualities());
            DQUtils.addSystemRules(ew, e.getDataQualities());
            entities.put(e.getName(), ew);
        }

        return entities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EntityWrapper> getValueType() {
        return EntityWrapper.class;
    }

}
