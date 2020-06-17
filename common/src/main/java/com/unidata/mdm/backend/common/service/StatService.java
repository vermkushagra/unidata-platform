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

import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.common.context.StatisticRequestContext;
import com.unidata.mdm.backend.common.dto.statistic.ErrorsStatDTO;
import com.unidata.mdm.backend.common.dto.statistic.StatisticDTO;
import com.unidata.mdm.backend.common.dto.statistic.StatisticResponseDTO;


/**
 * @author Mikhail Mikhailov
 * Statistic stuff.
 */
public interface StatService {

    /**
     * Gets the statistic.
     *
     * @param request
     *            the request
     * @return the statistic
     * @throws Exception
     *             the exception
     */
    StatisticResponseDTO getStatistic(StatisticRequestContext request) throws Exception;

    /**
     * Gather 'real' statistic.
     *
     * @param fromDate
     *            from date.
     * @param toDate
     *            to date.
     * @param entityName
     *            entity name.
     * @return list with 'real' statistic.
     */
    List<StatisticDTO> gatherStatisticForDates(Date fromDate, Date toDate, String entityName);

    /**
     * Gets the errors stat.
     *
     * @param entityName
     *            the entity name
     * @param sourceSystemName
     *            the source system name
     * @return the errors stat
     * @throws Exception
     *             the exception
     */
    ErrorsStatDTO getErrorsStat(String entityName, String sourceSystemName) throws Exception;

    /**
     * Get statistic slice.
     *
     * @param startDate
     *            start date.
     * @param endDate
     *            end date.
     * @param entityName
     *            entity name.
     * @return statistic slice.
     */
    List<StatisticDTO> gatherHistoricalStatistic(Date startDate, Date endDate, String entityName);

    /**
     * Get last statistic slice.
     * @param entityName
     *            entity name.
     * @return statistic slice.
     */
    List<StatisticDTO> gatherLastAvailableStatistic(String entityName);

    /**
     * Export statistic to user event
     */
    void exportStatistic();

    /**
     * Gets the cache ttl.
     *
     * @return the cache ttl
     */
    int getCacheTTL();

}
