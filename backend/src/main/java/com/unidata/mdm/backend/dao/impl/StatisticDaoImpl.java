package com.unidata.mdm.backend.dao.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.unidata.mdm.backend.common.dto.statistic.StatisticDTO;
import com.unidata.mdm.backend.common.dto.statistic.TimeSerieDTO;
import com.unidata.mdm.backend.common.statistic.StatisticType;
import com.unidata.mdm.backend.dao.StatisticDao;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * The Class StatisticDaoImpl.
 */
@Repository
public class StatisticDaoImpl extends AbstractDaoImpl implements StatisticDao {

    /**
     * The Constant ORIGIN_NAME.
     */
    private static final String ORIGIN_NAME = "origin_name";

    /**
     * The Constant SEVERITY.
     */
    private static final String SEVERITY = "severity";

    /**
     * The Constant ENTITY_NAME.
     */
    private static final String ENTITY_NAME = "entityName";

    /**
     * The Constant END_DATE.
     */
    private static final String END_DATE = "endDate";

    /**
     * The Constant START_DATE.
     */
    private static final String START_DATE = "startDate";

    /**
     * The Constant ENTITY_ID.
     */
    private static final String ENTITY_ID = "entity_id";


    /**
     * The Constant countTotalOrigins.
     */
    private final String countTotalOrigins;

    /**
     * The Constant countTotalEtalons.
     */
    private final String countTotalEtalons;

    /**
     * The Constant countNewOrigins.
     */
    private final String countNewOrigins;

    /**
     * The Constant countNewEtalons.
     */
    private final String countNewEtalons;

    /**
     * The Constant countUpdatedOrigins.
     */
    private final String countUpdatedOrigins;

    /**
     * The Constant countUpdatedEtalons.
     */
    private final String countUpdatedEtalons;

    /**
     * The Constant countErrorsOrigins.
     */
    private final String countErrorsOrigins;

    /**
     * The Constant countErrorsEtalons.
     */

    private final String countErrorsEtalons;

    // /** The Constant COUNT_MERGED_ORIGINS. */
    // private static final String COUNT_MERGED_ORIGINS =
    // "select count(id) as cnt from origins where version=1 and name=:entityName and update_date between :startDate and :endDate and status='MERGED'";

    /**
     * The Constant countMergedEtalons.
     */
    private final String countMergedEtalons;

    /**
     * The Constant countErrorsBySeverityAndEntity.
     */
    private final String countErrorsBySeverityAndEntity;

    /**
     * The Constant countErrorsBySeverityAndSourceSystem.
     */
    private final String countErrorsBySeverityAndSourceSystem;

    /**
     * The Constant countErrorsBySeverity.
     */
    private final String countErrorsBySeverity;

    /**
     * The constant countDuplicates.
     */
    private final String countDuplicates;

    /**
     * The Constant insertNew.
     */
    private final String insertNew;
    /**
     * Select slice.
     */
    private final String getSlice;
    /**
     * Select last slice.
     */
    private final String getLastSlice;
    /**
     * Select last slice.
     */
    private final String getLastSliceForAllEntities;

    /**
     * Instantiates a new statistic dao impl.
     *
     * @param dataSource the data source
     */
    @Autowired
    public StatisticDaoImpl(DataSource dataSource, @Qualifier("statistic-sql") Properties sql) {
        super(dataSource);
        countTotalOrigins = sql.getProperty("countTotalOrigins");
        countTotalEtalons = sql.getProperty("countTotalEtalons");
        countNewOrigins = sql.getProperty("countNewOrigins");
        countNewEtalons = sql.getProperty("countNewEtalons");
        countUpdatedOrigins = sql.getProperty("countUpdatedOrigins");
        countUpdatedEtalons = sql.getProperty("countUpdatedEtalons");
        countErrorsOrigins = sql.getProperty("countErrorsOrigins");
        countErrorsEtalons = sql.getProperty("countErrorsEtalons");
        countMergedEtalons = sql.getProperty("countMergedEtalons");
        countErrorsBySeverityAndEntity = sql.getProperty("countErrorsBySeverityAndEntity");
        countErrorsBySeverityAndSourceSystem = sql.getProperty("countErrorsBySeverityAndSourceSystem");
        countErrorsBySeverity = sql.getProperty("countErrorsBySeverity");
        countDuplicates = sql.getProperty("countDuplicates");
        insertNew = sql.getProperty("insertNew");
        getSlice = sql.getProperty("getSlice");
        getLastSlice = sql.getProperty("getLastSlice");
        getLastSliceForAllEntities = sql.getProperty("getLastSliceForAllEntities");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.StatisticDao#countErrors(java.time.LocalDateTime
     * , java.time.LocalDateTime)
     */
    @Override
    public int countErrors(String entityName, String sourceSystemName, LocalDateTime startLocalDateTime,
                           LocalDateTime endLocalDateTime) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(START_DATE, Timestamp.valueOf(startLocalDateTime));
        paramMap.put(END_DATE, Timestamp.valueOf(endLocalDateTime));
        paramMap.put(ENTITY_NAME, entityName);
        paramMap.put(ORIGIN_NAME, sourceSystemName);
        if (entityName != null) {
            return namedJdbcTemplate.queryForObject(countErrorsEtalons, paramMap, Integer.class);
        } else {
            return namedJdbcTemplate.queryForObject(countErrorsOrigins, paramMap, Integer.class);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.StatisticDao#countErrorsBySeverityAndEntity
     * (java.lang.String, java.lang.String)
     */
    @Override
    public int countErrorsBySeverityAndEntity(String entityName, String severity) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(SEVERITY, severity);
        paramMap.put(ENTITY_NAME, entityName);
        return namedJdbcTemplate.queryForObject(countErrorsBySeverityAndEntity, paramMap, Integer.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.StatisticDao#countNew(java.time.LocalDateTime
     * , java.time.LocalDateTime)
     */
    @Override
    @Deprecated
    public int countNew(String entityName, String sourceSystemName, LocalDateTime startLocalDateTime,
                        LocalDateTime endLocalDateTime) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(START_DATE, startLocalDateTime == null ? null : Timestamp.valueOf(startLocalDateTime));
        paramMap.put(END_DATE, endLocalDateTime == null ? null : Timestamp.valueOf(endLocalDateTime));
        paramMap.put(ENTITY_NAME, entityName);
        paramMap.put(ORIGIN_NAME, sourceSystemName);
        if (entityName != null) {
            return namedJdbcTemplate.queryForObject(countNewEtalons, paramMap, Integer.class);
        } else {
            return namedJdbcTemplate.queryForObject(countNewOrigins, paramMap, Integer.class);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.unidata.mdm.backend.dao.StatisticDao#countTotal(java.time.LocalDateTime
     * , java.time.LocalDateTime)
     */
    @Deprecated
    @Override
    public int countTotal(String entityName, String sourceSystemName, LocalDateTime startLocalDateTime,
                          LocalDateTime endLocalDateTime) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(END_DATE, Timestamp.valueOf(endLocalDateTime));
        paramMap.put(ENTITY_NAME, entityName);
        paramMap.put(ORIGIN_NAME, sourceSystemName);
        if (entityName != null) {
            return namedJdbcTemplate.queryForObject(countTotalEtalons, paramMap, Integer.class);
        } else {
            return namedJdbcTemplate.queryForObject(countTotalOrigins, paramMap, Integer.class);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.StatisticDao#countUpdated(java.time.
     * LocalDateTime, java.time.LocalDateTime)
     */
    @Deprecated
    @Override
    public int countUpdated(String entityName, String sourceSystemName, LocalDateTime startLocalDateTime,
                            LocalDateTime endLocalDateTime) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(START_DATE, startLocalDateTime == null ? null : Timestamp.valueOf(startLocalDateTime));
        paramMap.put(END_DATE, endLocalDateTime == null ? null : Timestamp.valueOf(endLocalDateTime));
        paramMap.put(ENTITY_NAME, entityName);
        paramMap.put(ORIGIN_NAME, sourceSystemName);
        if (entityName != null) {
            return namedJdbcTemplate.queryForObject(countUpdatedEtalons, paramMap, Integer.class);
        } else {
            return namedJdbcTemplate.queryForObject(countUpdatedOrigins, paramMap, Integer.class);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.StatisticDao#countVErrors(java.time.
     * LocalDateTime, java.time.LocalDateTime)
     */
    @Override
    public int countVErrors(String entityName, String sourceSystemName, LocalDateTime startLocalDateTime,
                            LocalDateTime endLocalDateTime) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(START_DATE, startLocalDateTime == null ? null : Timestamp.valueOf(startLocalDateTime));
        paramMap.put(END_DATE, endLocalDateTime == null ? null : Timestamp.valueOf(endLocalDateTime));
        paramMap.put(ENTITY_NAME, entityName);
        paramMap.put(ORIGIN_NAME, sourceSystemName);
        if (entityName != null) {
            return namedJdbcTemplate.queryForObject(countErrorsEtalons, paramMap, Integer.class);
        } else {
            return namedJdbcTemplate.queryForObject(countErrorsOrigins, paramMap, Integer.class);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.unidata.mdm.backend.dao.StatisticDao#countMerged(java.time.LocalDateTime
     * , java.time.LocalDateTime)
     */
    @Deprecated
    @Override
    public int countMerged(String entityName, LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(START_DATE, startLocalDateTime == null ? null : Timestamp.valueOf(startLocalDateTime));
        paramMap.put(END_DATE, endLocalDateTime == null ? null : Timestamp.valueOf(endLocalDateTime));
        paramMap.put(ENTITY_NAME, entityName);
        if (entityName != null) {
            return namedJdbcTemplate.queryForObject(countMergedEtalons, paramMap, Integer.class);
        } else {
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.StatisticDao#countErrorsBySeverity(java.
     * lang.String)
     */
    @Override
    public int countErrorsBySeverity(String severity) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(SEVERITY, severity);
        return namedJdbcTemplate.queryForObject(countErrorsBySeverity, paramMap, Integer.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.StatisticDao#countDuplicates(java.lang.String
     * , java.time.LocalDateTime, java.time.LocalDateTime)
     */
    @SuppressWarnings("serial")
    @Override
    public int countDuplicates(String entityName, LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
        Map<String, Object> paramMap = new HashMap<String, Object>() {
            {
                put(ENTITY_ID, entityName);
            }
        };
        return namedJdbcTemplate.queryForObject(countDuplicates, paramMap, Integer.class);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.StatisticDao#countErrorsBySeverityAndSourceSystem(java.lang.String, java.lang.String)
     */
    @Override
    public int countErrorsBySeverityAndSourceSystem(String sourceSystemName, String severity) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(SEVERITY, severity);
        paramMap.put(ORIGIN_NAME, sourceSystemName);
        return namedJdbcTemplate.queryForObject(countErrorsBySeverityAndSourceSystem, paramMap, Integer.class);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.StatisticDao#persistSlice(java.util.List, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void persistSlice(List<StatisticDTO> statistics, String entity) {
        if (CollectionUtils.isNotEmpty(statistics)) {
            List<Map<String, Object>> batchArgs = new ArrayList<>();
            for (StatisticDTO statistic : statistics) {
                String type = statistic.getType().name();
                List<TimeSerieDTO> series = statistic.getSeries();
                for (TimeSerieDTO serie : series) {
                    Map<String, Object> arg = new HashMap<>();
                    arg.put("type", type);
                    arg.put("entity", entity);
                    arg.put("at_date", serie.getTime());
                    arg.put("count", serie.getValue());
                    arg.put("created_at", new Date());
                    arg.put("updated_at", new Date());
                    arg.put("created_by", SecurityUtils.getCurrentUserName());
                    arg.put("updated_by", SecurityUtils.getCurrentUserName());
                    batchArgs.add(arg);
                }
            }
            namedJdbcTemplate.batchUpdate(insertNew, batchArgs.toArray(new Map[batchArgs.size()]));
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.StatisticDao#getSlice(java.util.Date, java.util.Date, java.lang.String)
     */
    @Override
    public List<StatisticDTO> getSlice(Date startDate, Date endDate, String entityName) {
        List<StatisticDTO> statistics = new ArrayList<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("startDate", startDate);
        paramMap.put("endDate", endDate);
        paramMap.put("entityName", entityName);
        List<Map<String, Object>> results = namedJdbcTemplate.queryForList(getSlice, paramMap);
        Map<StatisticType, List<TimeSerieDTO>> stMap = new HashMap<>();
        for (Map<String, Object> map : results) {
            StatisticType type = StatisticType.fromValue((String) map.get("type"));
            if (!stMap.containsKey(type)) {
                stMap.put(type, new ArrayList<>());
            }
            TimeSerieDTO serie = new TimeSerieDTO();
            serie.setTime((Date) map.get("at_date"));
            serie.setValue((int) map.get("count"));
            stMap.get(type).add(serie);
        }
        for (StatisticType type : stMap.keySet()) {
            StatisticDTO statistic = new StatisticDTO();
            statistic.setType(type);
            statistic.setSeries(stMap.get(type));
            statistics.add(statistic);
        }
        return statistics;
    }

    @Override
    public List<StatisticDTO> getLastSlice(String entityName) {
        List<StatisticDTO> statistics = new ArrayList<>();
        Map<String, Object> paramMap = new HashMap<>();
        List<Map<String, Object>> results;
        if(StringUtils.isEmpty(entityName)){
            results =  namedJdbcTemplate.queryForList(getLastSliceForAllEntities, paramMap);
        } else {
            paramMap.put("entityName", entityName);
            results = namedJdbcTemplate.queryForList(getLastSlice, paramMap);
        }

        for (Map<String, Object> map : results) {
            StatisticDTO statistic = new StatisticDTO();
            StatisticType type = StatisticType.fromValue((String) map.get("type"));
            statistic.setType(type);
            statistic.setEntityName((String)map.get("entity"));

            TimeSerieDTO serie = new TimeSerieDTO();
            serie.setTime((Date) map.get("at_date"));
            serie.setValue((int) map.get("count"));
            statistic.setSeries(Collections.singletonList(serie));
            statistics.add(statistic);
        }

        return statistics;
    }
}
