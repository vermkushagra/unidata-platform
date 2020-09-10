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

import com.unidata.mdm.backend.common.search.PeriodIdUtils;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.search.types.SearchType;

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
    public SearchType getSearchType() {
        return EntitySearchType.ETALON_DATA;
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
