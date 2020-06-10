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

package com.unidata.mdm.backend.common.integration.exits;

import java.util.Date;

import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;

/**
 * @author Mikhail Mikhailov
 * Simple data service interface, suitable for straight simple operations.
 */
public interface DataService {

    /**
     * Find an etalon record by id.
     * @param etalonId the id
     * @param forDate date
     * @return record or null
     */
    public EtalonRecord findEtalonRecord(String etalonId, Date forDate);
    /**
     * Find origin record by origin id.
     * @param originId the origin id
     * @return record or null
     */
    public OriginRecord findOriginRecord(String originId);
    /**
     * Find origin record by external id.
     * @param externalId the external id
     * @param entityName the entity name
     * @param sourceSystem the source system
     * @return record or null
     */
    public OriginRecord findOriginRecord(String externalId, String entityName, String sourceSystem);
    /**
     * Upsert a new origin record.
     * @param record the record to upsert
     * @return true if successful, false otherwise
     */
    public boolean upsertOriginRecord(OriginRecord record);
}
