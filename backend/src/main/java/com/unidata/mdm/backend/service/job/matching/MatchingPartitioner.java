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

package com.unidata.mdm.backend.service.job.matching;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.dao.util.VendorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.service.job.JobUtil;


public class MatchingPartitioner implements Partitioner {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingPartitioner.class);
    @Qualifier("unidataDataSource")
    @Autowired
    private DataSource unidataDataSource;
    /**
     * Job util
     */
    @Autowired
    private JobUtil jobUtil;
    /**
     * Block size
     */
    private Long blockSize;
    /**
     * Operation id
     */
    private String operationId;
    /**
     * Matching rule name.
     */
    private String matchingName;

    /**
     * Entity name.
     */
    private String entityName;

    /**
     * Split all records as separate partition.
     * For some reason another way to use chunks with few records per partition.
     * Note, that parameter gridSize not used here.
     *
     * @param gridSize
     * @return
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        LOGGER.info("Settings: set the following type and matching group for matching - {}:{} .", entityName, matchingName);
        LOGGER.info("Settings: block size - {}.", blockSize);
        final Map<String, ExecutionContext> result = new HashMap<>();

        final StringBuilder sqlb = new StringBuilder()
                .append("select block_num, start_id, start_gsn, end_id, end_gsn from ud_calc_records_etalon_batch_blocks(null, ")
                .append(blockSize.intValue())
                .append(", ")
                .append(VendorUtils.textArray(Collections.singleton(entityName)))
                .append(" , null, null, ")
                .append(VendorUtils.textArray(Arrays.asList(RecordStatus.ACTIVE.name())))
                .append(") order by block_num");

        result.putAll(collectExecutionContexts(result.size(), sqlb.toString(), entityName));
        LOGGER.info("Finished partitioning of {}.", entityName);

        LOGGER.info("Partitions keys [size={}, keys={}]", result.size(), result.keySet());

        return result;
    }

    private Map<String, ExecutionContext> collectExecutionContexts(int startPartitionNumber, String sql, String type) {

        int number = startPartitionNumber;

        ClusterMetaData clusterMetaData = jobUtil.getMatchingSettings(entityName, matchingName);
        LOGGER.info("Processing entity {} with group {}:{} .", entityName, matchingName);

        final Map<String, ExecutionContext> result = new HashMap<>();
        try (Connection connection = unidataDataSource.getConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
             ResultSet cursor = statement.executeQuery(sql)) {

            while (cursor.next()) {

                final ExecutionContext value = new ExecutionContext();
                result.put(JobUtil.partitionName(number), value);

                String startId = cursor.getString("start_id");
                String endId = cursor.getString("end_id");
                Long startGSN = cursor.getLong("start_gsn");
                Long endGSN = cursor.getLong("end_gsn");

                value.put("startId", startId);
                value.put("endId", endId);
                value.put("startGSN", startGSN);
                value.put("endGSN", endGSN);
                value.putString("entityName", type);
                value.putString("partition", "partition" + number);
                value.putString("operationId", operationId);
                value.put("clusterMetaData", clusterMetaData);
                number++;

                LOGGER.info("Finished block of {}.", type == null ? "not specified type (ALL)" : type + " type");
            }
        } catch (SQLException sqle) {
            LOGGER.error("SQL exception caught.", sqle);
        }

        return result;
    }

    @Required
    public void setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
    }

    @Required
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Required
    public void setMatchingName(String matchingName) {
        this.matchingName = matchingName;
    }

    public void setUnidataDataSource(DataSource unidataDataSource) {
        this.unidataDataSource = unidataDataSource;
    }

}
