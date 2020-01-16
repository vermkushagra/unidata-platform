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

package org.unidata.mdm.data.service.job;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.unidata.mdm.core.dao.template.QueryTemplate;
import org.unidata.mdm.core.dao.vendor.VendorUtils;
import org.unidata.mdm.core.service.job.JobCommonParameters;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.util.JobUtils;
import org.unidata.mdm.data.dao.DataStorageDAO;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.po.storage.DataClusterPO;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.service.job.ModelJobSupport;
import org.unidata.mdm.system.exception.PlatformFailureException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Dmitrii Kopin
 */
public abstract class AbstractRecordPartitioner implements Partitioner, ModelJobSupport {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRecordPartitioner.class);
    /**
     * Processed entity names
     */
    protected String processedTypes;
    /**
     * Max threads for calculate partitions
     */
    protected Integer partitionThreads;
    /**
     * Block size
     */
    @Value("#{jobParameters[" + JobCommonParameters.PARAM_BLOCK_SIZE + "]}")
    protected Long blockSize;
    /**
     * Operation id
     */
    @Value("#{jobParameters[" + JobCommonParameters.PARAM_OPERATION_ID + "]}")
    protected String operationId;
    /**
     * If true, classifiers data will be reindexed
     */
    @Value("#{jobParameters[" + JobCommonParameters.PARAM_FILTERS + "] ?: null}")
    protected String filters;
    /**
     * Record statuses to consider.
     */
    protected List<RecordStatus> statuses = Arrays.asList(RecordStatus.ACTIVE, RecordStatus.INACTIVE);
    /**
     * Storage DAO.
     */
    @Autowired
    protected DataStorageDAO dataStorageDAO;
    /**
     * MMS.
     */
    @Autowired
    protected MetaModelService metaModelService;
    /**
     * OM.
     */
    @Autowired
    ObjectMapper objectMapper;
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

        List<String> types = getEntityList(processedTypes);
        types.sort(String.CASE_INSENSITIVE_ORDER);
        final String typesAsArray = VendorUtils.textArray(types);

        boolean processAll = processedTypes.contains(JobUtils.JOB_ALL);

        ExecutorService fixedExecutor = Executors.newFixedThreadPool(partitionThreads);


        ConcurrentMap<String, ExecutionContext> result = new ConcurrentHashMap<>();

        DataClusterPO dataCluster = dataStorageDAO.load();
        int numberOfShards = dataCluster.getNumberOfShards();
        final AtomicInteger partitionNumber = new AtomicInteger(0);
        List<Integer> shards = IntStream.range(0, numberOfShards)
                .boxed()
                .collect(Collectors.toList());
        Collections.shuffle(shards);

        final CountDownLatch latch = new CountDownLatch(shards.size());
        for (final Integer shardNumber : shards) {
            fixedExecutor.execute(() -> {
                try (Connection c = dataStorageDAO.shardSelect(shardNumber).bareConnection();
                     Statement s = c.createStatement()) {
                    s.executeUpdate(createTempTableQuery());
                    // prepare statement;
                    String execBlockPrepared = collectBlocksQuery();
                    execBlockPrepared = execBlockPrepared.replace("?block", blockSize.toString());
                    execBlockPrepared = execBlockPrepared.replace("?shard", shardNumber.toString());
                    execBlockPrepared = execBlockPrepared.replace("?names", processAll ? "NULL" : typesAsArray);
                    execBlockPrepared = execBlockPrepared.replace("?statuses", CollectionUtils.isEmpty(statuses)
                            ? "NULL"
                            : VendorUtils.textArray(statuses.stream()
                                    .map(RecordStatus::value)
                                    .collect(Collectors.toList())));

                    execBlockPrepared = execBlockPrepared.replace(QueryTemplate.PARTITION_MARK, shardNumber.toString());
                    s.execute(execBlockPrepared);

                    try (ResultSet rs = s.executeQuery(selectCollectedBlocksQuery())) {
                        while (rs.next()) {

                            final ExecutionContext value = new ExecutionContext();
                            result.put(JobUtils.partitionName(partitionNumber.get()), value);

                            Long startLSN = rs.getLong("start_lsn");
                            Long endLSN = rs.getLong("end_lsn");
                            String type = rs.getString("name");
                            Long shard = rs.getLong("shard");

                            value.put(JobCommonParameters.PARAM_START_LSN, startLSN);
                            value.put(JobCommonParameters.PARAM_END_LSN, endLSN);
                            value.putString(JobCommonParameters.PARAM_ENTITY_NAME, type);
                            value.putLong(JobCommonParameters.PARAM_SHARD_NUMBER, shard);
                            value.put(JobCommonParameters.PARAM_OPERATION_ID, operationId);
                            value.putString(JobCommonParameters.PARAM_PARTITION_ID, "partition" + partitionNumber);
                            fillExecutionContext(value);
                            partitionNumber.incrementAndGet();

                            LOGGER.info("Finished {} block of LOGGED ids.", partitionNumber);
                        }
                    }

                    s.executeUpdate(dropTempTableQuery());
                } catch (SQLException e) {
                    LOGGER.warn("SQLE caught.", e);
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
            LOGGER.info("Finished partitioning");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Interrupted while partitioning  in parallel.", e);
        }
        return result;
    }

    protected String createTempTableQuery() {
        return "CREATE TEMPORARY TABLE __result (" +
                "    block_num INT NOT NULL PRIMARY KEY," +
                "    start_id  CHARACTER(36)," +
                "    start_lsn BIGINT," +
                "    end_id    CHARACTER(36)," +
                "    end_lsn   BIGINT," +
                "    shard     INT," +
                "    name      TEXT " +
                ")";
    }

    protected String collectBlocksQuery() {
        return "do " +
                "$$ " +
                "DECLARE " +
                "    exec_sql TEXT; " +
                "    cur_lsn BIGINT := -9223372036854775808; " +
                "    cur_block INT := 0; " +
                "    cur_name TEXT; " +
                "    cur_shard INT := ?shard; " +
                "    block_sz INT := COALESCE (?block, 5000); " +
                "    names TEXT []:= ?names:: TEXT []; " +
                "    statuses TEXT []:= ?statuses:: TEXT []; " +
                "BEGIN " +
                "    exec_sql := ' WITH _block AS (SELECT id, lsn, shard, name, status " +
                "                       FROM com_unidata_mdm_data.record_etalons_p" + QueryTemplate.PARTITION_MARK +
                "                       WHERE lsn >= $1 and shard = $2 " +
                "                       ORDER BY lsn LIMIT $3), '; " +
                "    exec_sql := exec_sql || ' _block_start AS (( SELECT id, lsn FROM _block '; " +
                "    IF (array_length(names, 1) > 0) THEN " +
                "        exec_sql := exec_sql || ' WHERE name = $5 '; " +
                "    END IF; " +
                "    IF (array_length(statuses, 1) > 0) THEN " +
                "        exec_sql := exec_sql || ' and status = ANY ( ARRAY [$6]::record_status[]) '; " +
                "    END IF; " +
                "    exec_sql := exec_sql || ' ORDER BY lsn ASC LIMIT 1) union select null,null limit 1)'; " +
                "    exec_sql := exec_sql || ', _block_end AS ( SELECT id, lsn FROM _block ORDER BY lsn DESC LIMIT 1) " +
                "                     INSERT INTO __result (block_num, start_id, start_lsn, end_id, end_lsn, shard, name) " +
                "                       SELECT $4, _block_start.id, _block_start.lsn, _block_end.id, _block_end.lsn, $2, $5 " +
                "                       FROM _block_start, _block_end'; " +
                "    IF (array_length(names, 1) > 0) THEN " +
                "        FOREACH cur_name IN ARRAY names LOOP " +
                "            cur_lsn := -9223372036854775808; " +
                "            WHILE TRUE LOOP " +
                "                EXECUTE exec_sql USING  cur_lsn, cur_shard, block_sz, cur_block, cur_name, statuses; " +
                "                cur_lsn := ( SELECT __result.end_lsn FROM __result WHERE __result.block_num = cur_block); " +
                "                IF cur_lsn IS NULL THEN " +
                "                    EXIT; " +
                "                END IF; " +
                "                cur_lsn := cur_lsn + 1; " +
                "                cur_block := cur_block + 1; " +
                "            END LOOP; " +
                "        END LOOP; " +
                "    ELSE " +
                "        WHILE TRUE LOOP " +
                "            EXECUTE exec_sql USING cur_lsn, cur_shard, block_sz, cur_block, cur_name, statuses; " +
                "            cur_lsn := (SELECT __result.end_lsn FROM __result WHERE __result.block_num = cur_block); " +
                "            IF cur_lsn IS NULL THEN " +
                "                EXIT; " +
                "            END IF; " +
                "            cur_lsn := cur_lsn + 1; " +
                "            cur_block := cur_block + 1; " +
                "        END LOOP; " +
                "    END IF; " +
                "    DELETE  from __result where start_lsn ISNULL; " +
                "end " +
                "$$; ";
    }

    protected String selectCollectedBlocksQuery() {
        return "select block_num, start_id, start_lsn, end_id, end_lsn, shard, name from __result";
    }

    protected String dropTempTableQuery() {
        return "drop table __result";
    }

    protected Map<String, String> getFilters(String filters) {

        if (filters != null) {
            try {
                return objectMapper.readValue(filters, new TypeReference<Map<String, String>>(){});
            } catch (IOException e) {
                final String message = "Input filters cannot be processed while partitioning.";
                throw new PlatformFailureException(message, e, DataExceptionIds.EX_DATA_PARTITION_FILTERS_FAILED);
            }
        } else {
            return Collections.emptyMap();
        }
    }

    protected void fillExecutionContext(ExecutionContext value){
    }

    public void setProcessedTypes(String processedTypes) {
        this.processedTypes = processedTypes;
    }

    public void setPartitionThreads(Integer partitionThreads) {
        this.partitionThreads = partitionThreads;
    }

    public void setStatuses(List<RecordStatus> statuses) {
        this.statuses = statuses;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public MetaModelService metaModelService() {
        return metaModelService;
    }
}
