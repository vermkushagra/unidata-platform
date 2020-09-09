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
package com.unidata.mdm.backend.service.model.util.wrappers;

import java.util.Map;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 *         Relation wrapper.
 */
public class RelationWrapper extends AttributesWrapper implements AbstractEntityWrapper {

    /**
     * Relation.
     */
    private final RelationDef relation;

    /**
     * Constructor.
     */
    public RelationWrapper(RelationDef relation, final String id, final Map<String, AttributeInfoHolder> attrs) {
        super(id, attrs);
        this.relation = relation;
    }

    /**
     * @return the relation
     */
    public RelationDef getRelation() {
        return relation;
    }

    @Override
    public String getUniqueIdentifier() {
        return getId();
    }

    @Override
    public Long getVersionOfWrappedElement() {
        return relation.getVersion();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractEntityDef getAbstractEntity() {
        return relation;
    }
}
