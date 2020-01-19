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
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.type.info.impl.LookupInfoHolder;
import org.unidata.mdm.meta.util.ModelUtils;

/**
 * @author Mikhail Mikhailov
 * Meta model parser for lookup entity type.
 */
public class LookupEntitiesParser implements ModelParser<LookupInfoHolder> {

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
    public Map<String, LookupInfoHolder> parse(Model model){

        final Map<String, LookupInfoHolder> lookupEntities = new ConcurrentHashMap<>();
        for (LookupEntityDef le : model.getLookupEntities()) {

            Map<String, AttributeModelElement> attrs = ModelUtils.createAttributesMap(le, model.getNestedEntities());
            Map<String, Map<String, Integer>> bvtMap  = ModelUtils.createBvtMap(le, model.getSourceSystems(), attrs);
            LookupInfoHolder lw = new LookupInfoHolder(le, le.getName(), attrs, bvtMap);
            // TODO: Commented out in scope of UN-11834. Reenable ASAP.
            // removeSystemRules(lw, le.getDataQualities());
            // addSystemRules(lw, le.getDataQualities());
            lookupEntities.put(le.getName(), lw);
        }

        return lookupEntities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<LookupInfoHolder> getValueType() {
        return LookupInfoHolder.class;
    }

}
