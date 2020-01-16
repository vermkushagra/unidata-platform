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

import java.util.Date;

import org.unidata.mdm.core.util.PeriodIdUtils;
import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.search.type.id.AbstractManagedIndexId;

/**
 * @author Mikhail Mikhailov
 * Record period index id.
 */
public class RecordIndexId extends AbstractManagedIndexId {
    /**
     * Constructor.
     */
    private RecordIndexId() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public IndexType getSearchType() {
        return EntityIndexType.RECORD;
    }
    /**
     * Creates a new record index id.
     * @param entityName the entity name
     * @param etalonId the to etalon id
     * @param to the period to date - the source of the id
     * @return index id
     */
    public static RecordIndexId of(String entityName, String etalonId, Date to) {

        RecordIndexId id = new RecordIndexId();

        id.entityName = entityName;
        id.indexId = PeriodIdUtils.childPeriodId(PeriodIdUtils.ensureDateValue(to), etalonId);
        id.routing = etalonId;

        return id;
    }
    /**
     * Creates a new record index id.
     * @param entityName the entity name
     * @param etalonId the to etalon id
     * @param periodId the period id
     * @return index id
     */
    public static RecordIndexId of(String entityName, String etalonId, long periodId) {

        RecordIndexId id = new RecordIndexId();

        id.entityName = entityName;
        id.indexId = PeriodIdUtils.childPeriodId(periodId, etalonId);
        id.routing = etalonId;

        return id;
    }
    /**
     * Creates a new record index id.
     * @param entityName the entity name
     * @param etalonId the to etalon id
     * @param periodIdAsString the period id in string representation
     * @return index id
     */
    public static RecordIndexId of(String entityName, String etalonId, String periodIdAsString) {

        RecordIndexId id = new RecordIndexId();

        id.entityName = entityName;
        id.indexId = PeriodIdUtils.childPeriodId(etalonId, periodIdAsString);
        id.routing = etalonId;

        return id;
    }
}
