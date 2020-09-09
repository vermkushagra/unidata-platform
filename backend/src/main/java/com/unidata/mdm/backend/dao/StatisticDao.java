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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.dto.statistic.StatisticDTO;
import com.unidata.mdm.backend.common.dto.statistic.dq.StatisticInfoDTO;


/**
 * The Interface StatisticDao.
 */
public interface StatisticDao {

    /**
     * Count errors.
     *
     * @param entityName            the entity name
     * @param sourceSystemName the source system name
     * @param startLocalDateTime            the start local date time
     * @param endLocalDateTime            the end local date time
     * @return the int
     */
    int countErrors(String entityName, String sourceSystemName, LocalDateTime startLocalDateTime,
            LocalDateTime endLocalDateTime);

    /**
     * Count new.
     *
     * @param entityName            the entity name
     * @param sourceSystemName the source system name
     * @param startLocalDateTime            the start local date time
     * @param endLocalDateTime            the end local date time
     * @return the int
     */
    int countNew(String entityName, String sourceSystemName, LocalDateTime startLocalDateTime,
            LocalDateTime endLocalDateTime);

    /**
     * Count total.
     *
     * @param entityName            the entity name
     * @param sourceSystemName the source system name
     * @param startLocalDateTime            the start local date time
     * @param endLocalDateTime            the end local date time
     * @return the int
     */
    int countTotal(String entityName, String sourceSystemName, LocalDateTime startLocalDateTime,
            LocalDateTime endLocalDateTime);

    /**
     * Count updated.
     *
     * @param entityName            the entity name
     * @param sourceSystemName the source system name
     * @param startLocalDateTime            the start local date time
     * @param endLocalDateTime            the end local date time
     * @return the int
     */
    int countUpdated(String entityName, String sourceSystemName, LocalDateTime startLocalDateTime,
            LocalDateTime endLocalDateTime);

    /**
     * Count updated.
     *
     * @param entityName            the entity name
     * @param startLocalDateTime            the start local date time
     * @param endLocalDateTime            the end local date time
     * @return the int
     */
    int countDuplicates(String entityName, LocalDateTime startLocalDateTime,
            LocalDateTime endLocalDateTime);

    /**
     * Count v errors.
     *
     * @param entityName            the entity name
     * @param sourceSystemName the source system name
     * @param startLocalDateTime            the start local date time
     * @param endLocalDateTime            the end local date time
     * @return the int
     */
    int countVErrors(String entityName, String sourceSystemName, LocalDateTime startLocalDateTime,
            LocalDateTime endLocalDateTime);

    /**
     * Count merged.
     *
     * @param entityName            the entity name
     * @param startLocalDateTime            the start local date time
     * @param endLocalDateTime            the end local date time
     * @return the int
     */
    int countMerged(String entityName, LocalDateTime startLocalDateTime,
            LocalDateTime endLocalDateTime);

    /**
     * Count errors by severity and entity.
     *
     * @param entityName
     *            the entity name
     * @param severity
     *            the severity
     * @return the int
     */
    int countErrorsBySeverityAndEntity(String entityName, String severity);
    /**
     * Count errors by severity and source system name.
     *
     * @param sourceSystemName
     *            the source system name
     * @param severity
     *            the severity
     * @return the int
     */
    int countErrorsBySeverityAndSourceSystem(String sourceSystemName, String severity);
    /**
     * Count errors by severity.
     *
     * @param severity
     *            the severity
     * @return the int
     */
    int countErrorsBySeverity(String severity);
    /**
     * Persist statistic slice to database.
     * @param statistics slice.
     * @param entity entity name.
     */
	void persistSlice(List<StatisticDTO> statistics, String entity);
	/**
	 * Return statistic slice from history.
	 * @param startDate start date.
	 * @param endDate end date
	 * @param entityName entity name.
	 * @return statistic slice.
	 */
	List<StatisticDTO> getSlice(Date startDate, Date endDate, String entityName);

    /**
     * Get statistic slice for last available date from history
     * @param entityName entity name
     * @return statistic slice
     */
    List<StatisticDTO> getLastSlice(String entityName);

    List<StatisticInfoDTO> getStatistic(String type, Date from, Date to, List<String> entities, Map<String, List<String>> dimensions);
    void persist(StatisticInfoDTO statistic, boolean updateIfExists);
}
