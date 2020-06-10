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
package com.unidata.mdm.backend.common.dto.data.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 * Entities and relations view, filtered by side ('to' or 'from').
 */
public class GetEntitiesByRelationSideDTO {

    /**
     * Filtered entities view.
     */
    final Map<EntityDef, Pair<List<NestedEntityDef>, List<RelationDef>>> entities;

    /**
     * Constructor.
     */
    public GetEntitiesByRelationSideDTO(
            Map<EntityDef, Pair<List<NestedEntityDef>, List<RelationDef>>> entities) {
        this.entities = entities;
    }

    /**
     * @return the entities
     */
    public Map<EntityDef, Pair<List<NestedEntityDef>, List<RelationDef>>> getEntities() {
        return entities;
    }

}
