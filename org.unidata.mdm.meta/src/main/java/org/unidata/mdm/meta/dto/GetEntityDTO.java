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

package org.unidata.mdm.meta.dto;

import java.util.List;

import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 * Entity metadata container.
 */
public class GetEntityDTO {
    /**
     * Entity.
     */
    private final EntityDef entity;
    /**
     * References.
     */
    private final List<NestedEntityDef> refs;
    /**
     * Relations.
     */
    private final List<RelationDef> relations;
    /**
     * Constructor.
     */
    public GetEntityDTO(EntityDef entity, List<NestedEntityDef> refs, List<RelationDef> relations) {
        super();
        this.entity = entity;
        this.refs = refs;
        this.relations = relations;
    }
    /**
     * @return the entity
     */
    public EntityDef getEntity() {
        return entity;
    }
    /**
     * @return the refs
     */
    public List<NestedEntityDef> getRefs() {
        return refs;
    }
    /**
     * @return the relations
     */
    public List<RelationDef> getRelations() {
        return relations;
    }
}
