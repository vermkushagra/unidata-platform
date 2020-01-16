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

/**
 * @author Mikhail Mikhailov
 * From side index id.
 */
public class RelationFromIndexId extends RelationIndexId {
    /**
     * Constructor.
     */
    private RelationFromIndexId() {
        super();
    }
    /**
     * Creates a new 'from' relation index id.
     * @param entityName the entity name
     * @param relationName the relation name
     * @param fromEtalonId the from etalon id
     * @param toEtalonId the to etalon id
     * @param periodId the period id
     * @return index id
     */
    public static RelationFromIndexId of(String entityName, String relationName, String fromEtalonId, String toEtalonId, Date to) {

        RelationFromIndexId id = new RelationFromIndexId();

        id.entityName = entityName;
        id.relationName = relationName;
        id.indexId = PeriodIdUtils.childPeriodId(PeriodIdUtils.ensureDateValue(to), fromEtalonId, relationName, toEtalonId);
        id.routing = fromEtalonId;

        return id;
    }
    /**
     * Creates a new 'from' relation index id.
     * @param entityName the entity name
     * @param relationName the relation name
     * @param fromEtalonId the from etalon id
     * @param toEtalonId the to etalon id
     * @param periodId the period id
     * @return index id
     */
    public static RelationFromIndexId of(String entityName, String relationName, String fromEtalonId, String toEtalonId, long periodId) {

        RelationFromIndexId id = new RelationFromIndexId();

        id.entityName = entityName;
        id.relationName = relationName;
        id.indexId = PeriodIdUtils.childPeriodId(periodId, fromEtalonId, relationName, toEtalonId);
        id.routing = fromEtalonId;

        return id;
    }
    /**
     * Creates a new 'from' relation index id.
     * @param entityName the entity name
     * @param relationName the relation name
     * @param fromEtalonId the from etalon id
     * @param toEtalonId the to etalon id
     * @param periodIdAsString the period id in string representation
     * @return index id
     */
    public static RelationFromIndexId of(String entityName, String relationName, String fromEtalonId, String toEtalonId, String periodIdAsString) {

        RelationFromIndexId id = new RelationFromIndexId();

        id.entityName = entityName;
        id.relationName = relationName;
        id.indexId = PeriodIdUtils.childPeriodId(fromEtalonId, relationName, toEtalonId, periodIdAsString);
        id.routing = fromEtalonId;

        return id;
    }
}
