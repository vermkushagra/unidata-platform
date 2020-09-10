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

package com.unidata.mdm.backend.common.search.id;

import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.search.types.SearchType;

/**
 * @author Mikhail Mikhailov
 * managed relation data record index id.
 */
public abstract class RelationIndexId extends AbstractManagedIndexId {
    /**
     * Relation name.
     */
    protected String relationName;
    /**
     * Constructor.
     */
    protected RelationIndexId() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public SearchType getSearchType() {
        return EntitySearchType.ETALON_RELATION;
    }
    /**
     * @return the relationName
     */
    public String getRelationName() {
        return relationName;
    }
}
