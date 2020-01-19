package org.unidata.mdm.data.service.job;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.unidata.mdm.core.dao.template.QueryTemplate;
import org.unidata.mdm.core.dao.vendor.VendorUtils;
import org.unidata.mdm.core.service.job.JobCommonParameters;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.util.JobUtils;
import org.unidata.mdm.data.dao.DataStorageDAO;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.service.job.ModelJobSupport;
import org.unidata.mdm.system.exception.PlatformFailureException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Dmitrii Kopin
 * TODO: remove @Value autowire for the sake of purity.
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
     * Job suport SQL queries.
     */
    @Autowired
    @Qualifier("job-support-sql")
    protected Properties sql;
    /**
     * OM.
     */
    @Autowired
    protected ObjectMapper objectMapper;
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
        int numberOfShards = StorageUtils.numberOfShards();
        final AtomicInteger partitionNumber = new AtomicInteger(0);

        List<Integer> shards = IntStream.range(0, numberOfShards)
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(shards);

        final Map<String, ExecutionContext> result = new HashMap<>();

        // Unchecked CompletionException will be thrown in case of an error
        CompletableFuture.allOf(shards.stream()
                .map(shardNumber -> partitionShard(shardNumber, partitionNumber.getAndIncrement(), result, typesAsArray, processAll))
                .toArray(k -> new CompletableFuture[k]))
            .join();

        LOGGER.info("Finished partitioning");
        return result;
    }

    protected CompletableFuture<Void> partitionShard(
            int shard, int partition, Map<String, ExecutionContext> result, String typesAsArray, boolean processAll) {

        return CompletableFuture.runAsync(() -> {

            String shardAsString = Integer.toString(shard);
            try (Connection c = dataStorageDAO.shardSelect(shard).bareConnection();
                 Statement s = c.createStatement()) {

                s.executeUpdate(createTempTableQuery());

                String execBlockPrepared = collectBlocksQuery();
                execBlockPrepared = execBlockPrepared.replace("?block", blockSize.toString());
                execBlockPrepared = execBlockPrepared.replace("?shard", shardAsString);
                execBlockPrepared = execBlockPrepared.replace("?names", processAll ? "NULL::text[]" : typesAsArray);
                execBlockPrepared = execBlockPrepared.replace("?statuses", CollectionUtils.isEmpty(statuses)
                       ? "NULL::record_status[]"
                       : VendorUtils.typeArray(statuses.stream()
                               .map(RecordStatus::value)
                               .collect(Collectors.toList()), "record_status"));

                execBlockPrepared = execBlockPrepared.replace(QueryTemplate.PARTITION_MARK, shardAsString);
                try {

                    s.execute(execBlockPrepared);
                    try (ResultSet rs = s.executeQuery(selectCollectedBlocksQuery())) {

                        while (rs.next()) {

                            final ExecutionContext value = new ExecutionContext();

                            Long startLSN = rs.getLong("start_lsn");
                            Long endLSN = rs.getLong("end_lsn");
                            String type = rs.getString("name");

                            value.put(JobCommonParameters.PARAM_START_LSN, startLSN);
                            value.put(JobCommonParameters.PARAM_END_LSN, endLSN);
                            value.putString(JobCommonParameters.PARAM_ENTITY_NAME, type);
                            value.putLong(JobCommonParameters.PARAM_SHARD_NUMBER, shard);
                            value.put(JobCommonParameters.PARAM_OPERATION_ID, operationId);
                            value.putString(JobCommonParameters.PARAM_PARTITION_ID, "partition" + partition);

                            fillExecutionContext(value);

                            result.put(JobUtils.partitionName(partition), value);

                            LOGGER.info("Finished collecting {} block of ids.", partition);
                        }
                    }
                } finally {
                    s.executeUpdate(dropTempTableQuery());
                }

            } catch (SQLException e) {
                throw new DataProcessingException("Failed to partition shard [{}]",
                        e, DataExceptionIds.EX_DATA_FAILED_TO_PARTITION_RECORDS, shard);
            }
        });
    }

    protected String createTempTableQuery() {
        return sql.getProperty("createTemporaryTableSQL");
    }

    protected String collectBlocksQuery() {
        return sql.getProperty("collectRecordBlocksSQL");
    }

    protected String selectCollectedBlocksQuery() {
        return sql.getProperty("selectRecordBlocksSQL");
    }

    protected String dropTempTableQuery() {
        return sql.getProperty("dropTemporaryTableSQL");
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
