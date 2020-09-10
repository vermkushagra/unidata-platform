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

/**
 * @author Mikhail Mikhailov
 * Common part of managed index id.
 */
public abstract class AbstractManagedIndexId implements ManagedIndexId {
    /**
     * Entity name.
     */
    protected String entityName;
    /**
     * Generated index id.
     */
    protected String indexId;
    /**
     * Routing.
     */
    protected String routing;
    /**
     * Constructor.
     */
    protected AbstractManagedIndexId() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getIndexId() {
        return indexId;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRouting() {
        return routing;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntityName() {
        return entityName;
    }
}
