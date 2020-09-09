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

import static com.unidata.mdm.backend.service.cleanse.DQUtils.addSystemRules;
import static com.unidata.mdm.backend.service.cleanse.DQUtils.removeSystemRules;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.Model;

/**
 * @author Mikhail Mikhailov
 * Meta model parser for lookup entity type.
 */
public class LookupEntitiesParser implements ModelParser<LookupEntityWrapper> {

    /**
     * Constructor.
     */
    public LookupEntitiesParser() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, LookupEntityWrapper> parse(Model model){

        final Map<String, LookupEntityWrapper> lookupEntities = new ConcurrentHashMap<>();
        for (LookupEntityDef le : model.getLookupEntities()) {

            Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(le, model.getNestedEntities());
            Map<String, Map<String, Integer>> bvtMap  = ModelUtils.createBvtMap(le, model.getSourceSystems(), attrs);
            LookupEntityWrapper lw = new LookupEntityWrapper(le, le.getName(), attrs, bvtMap);
            removeSystemRules(lw, le.getDataQualities());
            addSystemRules(lw, le.getDataQualities());
            lookupEntities.put(le.getName(), lw);
        }

        return lookupEntities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<LookupEntityWrapper> getValueType() {
        return LookupEntityWrapper.class;
    }

}
