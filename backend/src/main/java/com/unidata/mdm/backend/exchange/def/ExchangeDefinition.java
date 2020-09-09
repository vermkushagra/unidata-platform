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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Exchange definition JSON root.
 */
public class ExchangeDefinition implements Serializable {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -1326611614773865540L;

    /**
     * Lookup entities.
     */
    private List<ExchangeEntity> lookupEntities;

    /**
     * Lookup entities.
     */
    private List<ExchangeEntity> entities;

    /**
     * Constructor.
     */
    public ExchangeDefinition() {
        super();
    }

    /**
     * @return the lookupEntities
     */
    public List<ExchangeEntity> getLookupEntities() {
        if (lookupEntities == null) {
            lookupEntities = new ArrayList<>();
        }
        return lookupEntities;
    }

    /**
     * @param lookupEntities the lookupEntities to set
     */
    public void setLookupEntities(List<ExchangeEntity> lookupEntities) {
        this.lookupEntities = lookupEntities;
    }

    /**
     * @return the entities
     */
    public List<ExchangeEntity> getEntities() {
        if (entities == null) {
            entities = new ArrayList<>();
        }
        return entities;
    }

    /**
     * @param entities the entities to set
     */
    public void setEntities(List<ExchangeEntity> entities) {
        this.entities = entities;
    }

}
