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

package com.unidata.mdm.backend.service.statistic;

import com.unidata.mdm.backend.api.rest.dto.table.SearchableTable;
import com.unidata.mdm.backend.common.dto.statistic.dq.TypedStatisticDTO;
import com.unidata.mdm.backend.common.service.StatService;
import com.unidata.mdm.backend.common.types.SeverityType;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StatServiceExt extends StatService, AfterContextRefresh {

    /**
     * Persist current statistic for all entities.
     */
    void persistStatistic(Date fromDate, Date toDate) ;

    /**
     * Sets the cache ttl.
     *
     * @param cacheTTL
     *            the new cache ttl
     */
    void setCacheTTL(int cacheTTL);

    SearchableTable getErrorStatisticAggregation(String entityName);

    TypedStatisticDTO getStatistic(String type, Date from, Date to, List<String> entities, Map<String, List<String>> dimensions);
}