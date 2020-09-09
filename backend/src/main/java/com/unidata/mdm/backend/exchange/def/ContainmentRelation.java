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

package com.unidata.mdm.backend.exchange.def;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ContainmentRelation extends ExchangeRelation {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 5402408234136849707L;
    /**
     * Containment entity.
     */
    private ExchangeEntity entity;

    /**
     * Constructor.
     */
    public ContainmentRelation() {
        super();
    }

    /**
     * @return the entity
     */
    public ExchangeEntity getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(ExchangeEntity entity) {
        this.entity = entity;
    }

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    @Override
    public boolean isContainment() {
        return true;
    }
}
