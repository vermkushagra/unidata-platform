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

/**
 * @author Mikhail Mikhailov
 * To side index id.
 */
public class RelationToIndexId extends RelationIndexId {
    /**
     * Constructor.
     */
    private RelationToIndexId() {
        super();
    }
    /**
     * Creates a new 'to' relation index id.
     * @param entityName the entity name
     * @param relationName the relation name
     * @param fromEtalonId the from etalon id
     * @param toEtalonId the to etalon id
     * @param periodId the period id
     * @return index id
     */
    public static RelationToIndexId of(String entityName, String relationName, String fromEtalonId, String toEtalonId, long periodId) {

        RelationToIndexId id = new RelationToIndexId();

        id.entityName = entityName;
        id.relationName = relationName;
        id.indexId = PeriodIdUtils.childPeriodId(periodId, toEtalonId, relationName, fromEtalonId);
        id.routing = toEtalonId;

        return id;
    }
    /**
     * Creates a new 'to' relation index id.
     * @param entityName the entity name
     * @param relationName the relation name
     * @param fromEtalonId the from etalon id
     * @param toEtalonId the to etalon id
     * @param periodIdAsString the period id in string representation
     * @return index id
     */
    public static RelationToIndexId of(String entityName, String relationName, String fromEtalonId, String toEtalonId, String periodIdAsString) {

        RelationToIndexId id = new RelationToIndexId();

        id.entityName = entityName;
        id.relationName = relationName;
        id.indexId = PeriodIdUtils.childPeriodId(toEtalonId, relationName, fromEtalonId, periodIdAsString);
        id.routing = toEtalonId;

        return id;
    }
}
