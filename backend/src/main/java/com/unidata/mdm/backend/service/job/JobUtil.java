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

package com.unidata.mdm.backend.service.job;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.job.JobEnumType;
import com.unidata.mdm.backend.common.job.JobParameterType;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.po.EtalonRecordPO;
import com.unidata.mdm.backend.service.matching.MatchingRulesService;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;

/**
 * @author Denis Kostovarov
 */
@Component
public class JobUtil {
    public static final String ALL = "ALL";
    public static final String PARTITION = "partition:";
    /**
     * Message tags.
     */
    public static final String MSG_COLLECTED_EXECUTION_FAILURES = "app.job.reporting.collected.failures";

    private static final Logger LOGGER = LoggerFactory.getLogger(JobUtil.class);
    @Autowired
    private MetaModelServiceExt metaModelService;

    @Qualifier("unidataDataSource")
    @Autowired
    private DataSource unidataDataSource;

    @Autowired
    private MatchingRulesService matchingRulesService;

    public JobEnumType getAllEntitiesParamsList() {
        final JobEnumType params = new JobEnumType();
        params.setParameterType(JobParameterType.STRING);
        params.setParameters(Stream.concat(Stream.of(ALL), getAllEntitiesList().stream()).collect(Collectors.toList()));
        return params;
    }

    public JobEnumType getAllEntitiesMultiselectParamsList() {
        final JobEnumType params = new JobEnumType();
        params.setParameterType(JobParameterType.STRING);
        params.setMultiSelect(true);
        params.setParameters(getAllEntitiesList());
        return params;
    }

    public JobEnumType getJustEntitiesParamsList() {
        final JobEnumType params = new JobEnumType();
        params.setParameterType(JobParameterType.STRING);
        final List<String> stringParams = getJustEntitiesList();
        params.setParameters(stringParams);
        return params;
    }

    public List<String> getAllEntitiesList() {
        List<String> entities = getJustEntitiesList();
        List<String> lookupEntities = getJustLookupEntitiesList();
        entities.addAll(lookupEntities);
        return entities;
    }

    private List<String> getJustEntitiesList() {
        return metaModelService.getEntitiesList().stream().map(EntityDef::getName).collect(toList());
    }

    private List<String> getJustLookupEntitiesList() {
        return metaModelService.getLookupEntitiesList().stream().map(LookupEntityDef::getName).collect(toList());
    }

    public List<String> getEtalonIds(final String entityName, final String condition, final long start, final long count) {
        final StringBuilder sqlb = new StringBuilder()
                .append("select ")
                .append(EtalonRecordPO.FIELD_ID)
                .append(" from ")
                .append(EtalonRecordPO.TABLE_NAME)
                .append(" where name = '")
                .append(entityName)
                .append("'");

        if (!StringUtils.isEmpty(condition)) {
            sqlb.append(condition);
        }

        sqlb.append(" order by ").append(EtalonRecordPO.FIELD_ID);
        sqlb.append(" limit ")
                .append(count)
                .append(" offset ")
                .append(start);

        LOGGER.debug("Executing SQL for entities id set query [{}].", sqlb.toString());

        try (Connection connection = unidataDataSource.getConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = statement.executeQuery(sqlb.toString())) {

            List<String> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getString(1));
            }

            return ids;
        } catch (SQLException sqle) {
            LOGGER.error("SQL exception caught.", sqle);
        }

        return null;
    }

    public long getRecordsCount(String entityName) {

        StringBuilder sqlb = new StringBuilder()
                .append("select count(*) as CNT from etalons where name = '")
                .append(entityName)
                .append("'");

        LOGGER.debug("Executing SQL for entities set size query [{}].", sqlb.toString());

        try (final Connection connection = unidataDataSource.getConnection();
             final Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
             final ResultSet rs = statement.executeQuery(sqlb.toString())) {

            if (rs.next()) {
                return rs.getLong("CNT");
            }
        } catch (SQLException sqle) {
            LOGGER.error("SQL exception caught.", sqle);
        }

        return 0L;
    }


    public ClusterMetaData getMatchingSettings(String entityName, String matchingName) {

        Integer ruleId = ofNullable(matchingRulesService.getMatchingRule(entityName, matchingName))
                .map(MatchingRule::getId)
                .orElse(null);


        if (isNull(ruleId)) {
            throw new SystemRuntimeException("Can't find a rule with name " + matchingName, ExceptionId.EX_MATCHING_GROUP_OR_RULE_NOT_FOUND, matchingName);
        }

        return ClusterMetaData.builder()
                .ruleId(ruleId)
                .entityName(entityName)
                .build();
    }

    /**
     * @return collection of name of entities
     */
    public List<String> getEntityList(String entityNames) {

        List<String> reindexTypes = new ArrayList<>();
        boolean reindexAll = StringUtils.contains(entityNames, JobUtil.ALL);
        if (reindexAll) {
            metaModelService.getLookupEntitiesList()
                            .stream()
                            .map(LookupEntityDef::getName)
                            .collect(toCollection(() -> reindexTypes));

            metaModelService.getEntitiesList()
                            .stream()
                            .map(EntityDef::getName)
                            .collect(toCollection(() -> reindexTypes));
        } else {
            if (entityNames != null) {
                String[] tokens = entityNames.split(SearchUtils.COMMA_SEPARATOR);
                Collections.addAll(reindexTypes, tokens);
            }
        }

        return reindexTypes;
    }

    /**
     * Generates partition name. Just an int to string for now.
     * @param i partition number
     * @return name
     */
    public static String partitionName(int i) {
        return PARTITION + Integer.toString(i);
    }
}

