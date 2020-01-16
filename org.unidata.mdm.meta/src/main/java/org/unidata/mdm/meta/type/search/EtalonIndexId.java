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

package org.unidata.mdm.meta.type.search;

import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.search.type.id.AbstractManagedIndexId;

/**
 * @author Dmitry Kopin on 26.10.2018.
 */
public class EtalonIndexId extends AbstractManagedIndexId {
    /**
     * Constructor.
     */
    private EtalonIndexId() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public IndexType getSearchType() {
        return EntityIndexType.ETALON;
    }
    /**
     * Creates a new record index id.
     * @param entityName the entity name
     * @param etalonId the to etalon id
     * @return index id
     */
    public static EtalonIndexId of(String entityName, String etalonId) {

        EtalonIndexId id = new EtalonIndexId();

        id.entityName = entityName;
        id.indexId = etalonId;
        id.routing = etalonId;

        return id;
    }
}
