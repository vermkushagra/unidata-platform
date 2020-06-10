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

package com.unidata.mdm.backend.common.service;

import com.unidata.mdm.backend.common.types.DataRecord;

/**
 * @author Dmitry Kopin on 01.12.2017.
 */
public interface ValidationService {

    /**
     * Validate entity record data and name
     * @param record record for check
     * @param id the id
     */
    void checkEntityDataRecord(DataRecord record, String id);

    /**
     * Validate lookup record data and name
     * @param record record for check
     * @param id the id
     */
    void checkLookupDataRecord(DataRecord record, String id);

    /**
     * Validate relation record data and name
     * @param record record for check
     * @param id the id
     */
    void checkRelationDataRecord(DataRecord record, String id);
}