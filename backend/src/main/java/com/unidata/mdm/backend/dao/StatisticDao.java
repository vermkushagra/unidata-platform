package com.unidata.mdm.backend.dao;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.common.dto.statistic.StatisticDTO;


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
     * @param entityName
     *            the entity name
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

}
