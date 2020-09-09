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

/**
 *
 */

package com.unidata.mdm.backend.service.job.republishregistry;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.service.job.JobUtil;
import com.unidata.mdm.backend.util.IdUtils;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class RepublishRegistryPartitioner implements Partitioner {
    private static final Logger LOGGER = LoggerFactory.getLogger("ud-republish-partitioner");

    private Long blockSize;
    private String type;
    private boolean allPeriods;

    @Qualifier("unidataDataSource")
    @Autowired
    private DataSource unidataDataSource;

    @Autowired
    private JobUtil jobUtil;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        final Map<String, ExecutionContext> result = new HashMap<>();

        final List<String> types;
        final Date asOf = new Date();
        if (JobUtil.ALL.equals(type)) {
            types = jobUtil.getAllEntitiesList();
        } else {
            types = new ArrayList<>();
            types.add(type);
        }

        final String operationId = IdUtils.v1String();
        LOGGER.info("Settings: [blockSize={}, types={}], generated operationId={}", blockSize, types, operationId);

        int number = 0;
        for (final String type : types) {
            LOGGER.info("Processing {}.", type);

            long count = getRecordsCount(type);
            LOGGER.info("Found {} etalon records in the DB. {}.", count, count == 0 ? "Skipping" : "Starting");
            if (count <= 0) {
                LOGGER.info("Finished processing {}.", type);
                continue;
            }

            long process = count;
            long offset = 0;

            while (process > 0) {
                final List<String> ids = jobUtil.getEtalonIds(type, makeCondition(), offset, blockSize);
                if (ids == null || ids.isEmpty()) {
                    LOGGER.warn("No more ids for type {}. Breaking up.", type);
                    break;
                }

                final ExecutionContext value = new ExecutionContext();
                result.put(JobUtil.partitionName(number), value);

                value.put("asOf", asOf);
                value.put("ids", ids);
                value.putString("entityName", type);
                value.putString("operationId", operationId);
                value.put("allPeriods", allPeriods);
                value.putString("partition", "partition" + number);
                number++;

                if (count == offset) {
                    LOGGER.info("No more records for {}. Breaking up.", type);
                    break;
                }

                process -= blockSize;
                offset += blockSize;
            }

            LOGGER.info("Finished processing {}.", type);
        }

        LOGGER.info("Partitions keys [size={}, keys={}]", result.size(), result.keySet());

        return result;
    }

    private long getRecordsCount(final String entityName) {
        final StringBuilder sqlb = new StringBuilder()
                .append("select count(*) as CNT from etalons where name = '")
                .append(entityName).append("' ").append(makeCondition());

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

    private String makeCondition() {
        return " and status = '" + RecordStatus.ACTIVE + '\'';
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBlockSize(String blockSize) {
        this.blockSize = Long.valueOf(blockSize);
    }

    public void setAllPeriods(String allPeriods) {
        this.allPeriods = Boolean.valueOf(allPeriods);
    }
}