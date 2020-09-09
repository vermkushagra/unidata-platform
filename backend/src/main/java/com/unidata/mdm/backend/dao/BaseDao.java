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

package com.unidata.mdm.backend.dao;

import java.sql.Connection;

/**
 * Dao which contain common request to DB
 */
public interface BaseDao {
    /**
     * Gets entity name by etalon id.
     *
     * @param etalonId the etalon id
     * @return name
     */
    String getEntityNameByEtalonId(String etalonId);

    /**
     * Gets entity name by etalon id.
     *
     * @param originId the etalon id
     * @return name
     */
    String getEntityNameByOriginId(String originId);

    /**
     * Gets entity name by etalon id.
     *
     * @param relationEtalonId the etalon id
     * @return name
     */
    String getEntityNameByRelationFromEtalonId(String relationEtalonId);

    /**
     * Gets entity name by etalon id.
     *
     * @param relationOriginId the etalon id
     * @return name
     */
    String getEntityNameByRelationFromOriginId(String relationOriginId);
    /**
     * Gets the bare connection from the data source.
     * @return connection
     */
    Connection getBareConnection();
}
