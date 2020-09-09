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

package com.unidata.mdm.backend.service.job.exchange.out;

import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.ExchangeRelation;

/**
 * @author Denis Kostovarov
 *         Not really a 'reader', but rather a preprocessor.
 */
@Component
@Scope("step")
public class ExportDataItemReader extends JdbcCursorItemReader<Long> {
    /**
     * This run id.
     */
    @Value("#{stepExecutionContext[runId]}")
    private String runId;
    /**
     * This partition.
     */
    @Value("#{stepExecutionContext[partition]}")
    private String partition;
    /**
     * Start of chunk to process.
     */
    @Value("#{stepExecutionContext[startGSN]}")
    private Long startGsn;
    /**
     * Start of chunk to process.
     */
    @Value("#{stepExecutionContext[endGSN]}")
    private Long endGsn;
    /**
     * Skip deleted or not.
     */
    @Value("#{jobParameters[skipDeleted]}")
    private boolean skipDeleted;
    /**
     * Data source.
     */
    @Qualifier("unidataDataSource")
    @Autowired
    private DataSource unidataDataSource;
    /**
     * HZ innstance.
     */
    @Autowired
    private HazelcastInstance hazelcastInstance;
    /**
     * Row mapper.
     */
    private static final RowMapper<Long> NEXT_GSN_ROW_MAPPER = (rs, rowNum) -> rs.getLong(1);
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        // Provoke NPE, if something went wrong.
        final String objectId = new StringBuilder()
                .append(ExportDataConstants.EXCHANGE_OBJECTS_PREFIX)
                .append("_")
                .append(runId)
                .append("_")
                .append(partition)
                .toString();

        String tableName = null;
        String entityName = null;
        Object obj = hazelcastInstance.getMap(ExportDataConstants.EXCHANGE_OBJECTS_MAP_NAME).get(objectId);
        if (obj instanceof ExchangeEntity) {
            entityName = ((ExchangeEntity) obj).getName();
            tableName = "etalons";
        } else if (obj instanceof ExchangeRelation) {
            entityName = ((ExchangeRelation) obj).getRelation();
            tableName = "etalons_relations";
        }

        final String sql = new StringBuilder()
                .append("select gsn from ")
                .append(tableName)
                .append(" where name = '" + entityName + "' and ")
                .append("gsn >= ")
                .append(startGsn)
                .append(" and gsn <= ")
                .append(endGsn)
                .append(skipDeleted ? " and status = 'ACTIVE'" : "")
                .toString();

        super.setDataSource(unidataDataSource);
        super.setSaveState(true);
        super.setDriverSupportsAbsolute(true);
        super.setRowMapper(NEXT_GSN_ROW_MAPPER);
        super.setSql(sql);
        super.afterPropertiesSet();
    }

    /**
     * @return the runId
     */
    public String getRunId() {
        return runId;
    }

    /**
     * @param runId the runId to set
     */
    public void setRunId(String runId) {
        this.runId = runId;
    }

    /**
     * @param partition the partition to set
     */
    public void setPartition(String partition) {
        this.partition = partition;
    }

    /**
     * @return the startGsn
     */
    public Long getStartGsn() {
        return startGsn;
    }

    /**
     * @param startGsn the startGsn to set
     */
    public void setStartGsn(Long startGsn) {
        this.startGsn = startGsn;
    }

    /**
     * @return the endGsn
     */
    public Long getEndGsn() {
        return endGsn;
    }

    /**
     * @param endGsn the endGsn to set
     */
    public void setEndGsn(Long endGsn) {
        this.endGsn = endGsn;
    }

    /**
     * @param unidataDataSource the unidataDataSource to set
     */
    public void setUnidataDataSource(DataSource unidataDataSource) {
        this.unidataDataSource = unidataDataSource;
    }

    /**
     * @param skipDeleted the skipDeleted to set
     */
    public void setSkipDeleted(boolean skipDeleted) {
        this.skipDeleted = skipDeleted;
    }
}
