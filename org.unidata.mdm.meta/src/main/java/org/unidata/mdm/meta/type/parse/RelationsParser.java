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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.meta.ComplexAttributesHolderEntityDef;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.RelType;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.exception.MetaExceptionIds;
import org.unidata.mdm.meta.type.info.impl.RelationInfoHolder;
import org.unidata.mdm.meta.util.ModelUtils;
import org.unidata.mdm.system.exception.PlatformFailureException;

/**
 * @author Mikhail Mikhailov
 * Relations parser.
 */
public class RelationsParser implements ModelParser<RelationInfoHolder> {
    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationsParser.class);
    /**
     * Constructor.
     */
    public RelationsParser() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, RelationInfoHolder> parse(Model model){
        final Map<String, RelationInfoHolder> relations = new ConcurrentHashMap<>();
        List<RelationDef> defs = model.getRelations();
        for (int i = 0; defs != null && i < defs.size(); i++) {

            RelationDef def = defs.get(i);
            if(def.getName() == null){
            	continue;
            }

            ComplexAttributesHolderEntityDef attrsHolder = def;
            if (def.getRelType() == RelType.CONTAINS) {

                final String message = "The 'to' side containment entity '{}' of the relation '{}' not found in model.";
                attrsHolder = model.getEntities().stream()
                    .filter(e -> def.getToEntity().equals(e.getName()))
                    .findFirst()
                    .orElseThrow(() -> {
                        LOGGER.warn(message, def.getToEntity(), def.getName());
                        return new PlatformFailureException(message,
                                MetaExceptionIds.EX_META_TO_CONTAINMENT_ENTITY_NOT_FOUND_INIT,
                                def.getToEntity(),
                                def.getName());
                    });
            }

            Map<String, AttributeModelElement> attrs = ModelUtils.createAttributesMap(attrsHolder, model.getNestedEntities());
            relations.put(def.getName(), new RelationInfoHolder(def, def.getName(), attrs));
        }
        return relations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<RelationInfoHolder> getValueType() {
        return RelationInfoHolder.class;
    }

}
