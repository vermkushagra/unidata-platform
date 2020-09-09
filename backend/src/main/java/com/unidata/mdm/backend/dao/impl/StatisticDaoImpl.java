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

package com.unidata.mdm.backend.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.unidata.mdm.backend.common.dto.statistic.dq.StatisticInfoDTO;
import com.unidata.mdm.backend.service.job.exchange.in.AbstractRowMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
     * insert new statistic.
     */
    private final String insertStatistic;
    /**
     * update statistic.
     */
    private final String updateStatistic;
    /**
     * find statistic.
     */
    private final String findStatisticByTypeEntityAtDate;
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
        insertStatistic = sql.getProperty("insertStatistic");
        updateStatistic = sql.getProperty("updateStatistic");
        findStatisticByTypeEntityAtDate = sql.getProperty("findStatisticByTypeEntityAtDate");
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
        paramMap.put("types", Arrays.stream(StatisticType.values()).map(t -> t.name()).collect(Collectors.toList()));
        List<Map<String, Object>> results = namedJdbcTemplate.queryForList(getSlice, paramMap);
        Map<StatisticType, List<TimeSerieDTO>> stMap = new HashMap<>();
        for (Map<String, Object> map : results) {
            StatisticType type = StatisticType.fromValue((String) map.get("type"));
            if (type == null) {
                continue;
            }
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
        paramMap.put("types", Arrays.stream(StatisticType.values()).map(t -> t.name()).collect(Collectors.toList()));
        List<Map<String, Object>> results;
        if (StringUtils.isEmpty(entityName)) {
            results = namedJdbcTemplate.queryForList(getLastSliceForAllEntities, paramMap);
        } else {
            paramMap.put("entityName", entityName);
            results = namedJdbcTemplate.queryForList(getLastSlice, paramMap);
        }

        for (Map<String, Object> map : results) {

            StatisticType type = StatisticType.fromValue((String) map.get("type"));
            if (type == null) {
                continue;
            }
            StatisticDTO statistic = new StatisticDTO();
            statistic.setType(type);
            statistic.setEntityName((String) map.get("entity"));

            TimeSerieDTO serie = new TimeSerieDTO();
            serie.setTime((Date) map.get("at_date"));
            serie.setValue((int) map.get("count"));
            statistic.setSeries(Collections.singletonList(serie));
            statistics.add(statistic);
        }

        return statistics;
    }

    @Override
    public List<StatisticInfoDTO> getStatistic(String type, Date from, Date to, List<String> entities, Map<String, List<String>> dimensions) {

        String dateRestrictions = from != null && to != null ? " and a.at_date between :startDate and :endDate "
                : from != null ? " and a.at_date > :startDate "
                : to != null ? " and a.at_date < :endDate "
                : "";

        StringBuilder sql = new StringBuilder("select a.id, a.type, a.at_date, a.count, a.entity, a.dimension1, a.dimension2, a.dimension3 \n" +
                "            from\n" +
                "                statistic_counters a\n" +
                "            where a.type = :type " +
                                dateRestrictions);

        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("startDate", from);
        params.put("endDate", to);

        if (CollectionUtils.isNotEmpty(entities)) {
            sql.append(" and a.entity in (:entities)");
            params.put("entities", entities);
        }

        if (dimensions != null) {
            dimensions.forEach((k, v) -> {
                if (k != null && CollectionUtils.isNotEmpty(v)) {
                    sql.append(" and ").append(k).append(" in (:").append(k).append(")");
                    params.put(k, v);
                }
            });
        }

        sql.append(" order by a.at_date desc");

        return namedJdbcTemplate.query(sql.toString(), params, new AbstractRowMapper<StatisticInfoDTO>() {
            @Override
            public StatisticInfoDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                StatisticInfoDTO dto = new StatisticInfoDTO();
                dto.setEntityName(rs.getString("entity"));
                dto.setCount(rs.getLong("count"));
                dto.setAtDate(rs.getDate("at_date"));
                dto.setTypeName(rs.getString("type"));

                dto.setDimension1(rs.getString("dimension1"));
                dto.setDimension2(rs.getString("dimension2"));
                dto.setDimension3(rs.getString("dimension3"));

                return dto;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void persist(StatisticInfoDTO statistic, boolean updateIfExists) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", statistic.getTypeName());
        params.put("entity", statistic.getEntityName());
        params.put("at_date", statistic.getAtDate());
        params.put("count", statistic.getCount());
        params.put("dimension1", statistic.getDimension1());
        params.put("dimension2", statistic.getDimension2());
        params.put("dimension3", statistic.getDimension3());
        params.put("created_at", new Date());
        params.put("updated_at", new Date());
        params.put("created_by", SecurityUtils.getCurrentUserName());
        params.put("updated_by", SecurityUtils.getCurrentUserName());

        if (updateIfExists) {
            List<Long> id = namedJdbcTemplate.query(findStatisticByTypeEntityAtDate, params, new AbstractRowMapper<Long>() {
                @Override
                public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getLong("id");

                }
            });
            if (!id.isEmpty()) {
                params.put("id", id.get(0));
                namedJdbcTemplate.update(updateStatistic, params);
                return;
            }
        }

        namedJdbcTemplate.update(insertStatistic, params);
    }

}
