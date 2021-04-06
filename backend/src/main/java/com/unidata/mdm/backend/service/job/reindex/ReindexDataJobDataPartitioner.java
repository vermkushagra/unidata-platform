package com.unidata.mdm.backend.service.job.reindex;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.dao.util.VendorUtils;
import com.unidata.mdm.backend.service.job.JobUtil;

/**
 * @author Denis Kostovarov
 */
@Component
@JobScope
public class ReindexDataJobDataPartitioner implements Partitioner {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReindexDataJobDataPartitioner.class);
    /**
     * Reindex entity names
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_TYPES + "]}")
    private String reindexTypes;
    /**
     * Block size
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_BLOCK_SIZE + "]}")
    private Long blockSize;
    /**
     * If true, record's data will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_RECORDS + "] ?: false}")
    private Boolean reindexRecords;
    /**
     * If true, rels will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_RELATIONS + "] ?: false}")
    private Boolean reindexRelations;
    /**
     * If true, maching data will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_MATCHING + "] ?: false}")
    private Boolean reindexMatching;
    /**
     * If true, classifiers data will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_CLASSIFIERS + "] ?: false}")
    private Boolean reindexClassifiers;
    /**
     * Unidata data source
     */
    @Qualifier("unidataDataSource")
    @Autowired
    private DataSource unidataDataSource;
    /**
     * Job util
     */
    @Autowired
    private JobUtil jobUtil;

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

        if (!reindexRecords && !reindexRelations && !reindexMatching && !reindexClassifiers) {
            LOGGER.info("No data kind specified for reindexing [reindexRecords, reindexRelations, reindexMatching, reindexClassifiers are all false]. Exiting.");
            return Collections.emptyMap();
        }

        try {

            List<String> types = jobUtil.getEntityList(reindexTypes);
            types.sort(String.CASE_INSENSITIVE_ORDER);
            LOGGER.info("Settings: set the following types for re-indexing - {}.", types);
            LOGGER.info("Settings: block size - {}.", blockSize);
            final Map<String, ExecutionContext> result = new HashMap<>();
            boolean reindexAll = reindexTypes.contains(JobUtil.ALL);
            if (reindexAll) {

                final StringBuilder sqlb = new StringBuilder()
                        .append("select block_num, start_id, start_gsn, end_id, end_gsn, name from ud_calc_records_etalon_batch_blocks(null, ")
                        .append(blockSize.intValue())
                        .append(", null, null, null) order by block_num");

                result.putAll(collectExecutionContexts(result.size(), sqlb.toString()));
                LOGGER.info("Finished partitioning of all types.");
            } else {

                String typesAsArray = VendorUtils.textArray(types);
                final StringBuilder sqlb = new StringBuilder()
                        .append("select block_num, start_id, start_gsn, end_id, end_gsn, name from ud_calc_records_etalon_batch_blocks(null, ")
                        .append(blockSize.intValue())
                        .append(", ")
                        .append(typesAsArray)
                        .append(", null, null) order by block_num");

                result.putAll(collectExecutionContexts(result.size(), sqlb.toString()));
                LOGGER.info("Finished partitioning of {}.", types);
            }

            LOGGER.info("Partitions keys [size={}, keys={}]", result.size(), result.keySet());
            return result;
        } catch (Exception e) {
            LOGGER.warn("Partitioner caught an exception.", e);
            throw e;
        }
    }

    private Map<String, ExecutionContext> collectExecutionContexts(int startPartitionNumber, String sql) {

        int number = startPartitionNumber;
        final Map<String, ExecutionContext> result = new HashMap<>();
        try (Connection connection = unidataDataSource.getConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
             ResultSet cursor = statement.executeQuery(sql)) {

            while (cursor.next()) {

                final ExecutionContext value = new ExecutionContext();
                result.put(JobUtil.partitionName(number), value);

                Long startGSN = cursor.getLong("start_gsn");
                Long endGSN = cursor.getLong("end_gsn");
                String type = cursor.getString("name");

                value.put(ReindexDataJobConstants.PARAM_START_GSN, startGSN);
                value.put(ReindexDataJobConstants.PARAM_END_GSN, endGSN);
                value.putString(ReindexDataJobConstants.PARAM_ENTITY_NAME, type);
                value.putString(ReindexDataJobConstants.PARAM_PARTITION_ID, "partition" + number);
                number++;

                LOGGER.info("Finished block of {}.", type == null ? "not specified type (ALL)" : type + " type");
            }
        } catch (SQLException sqle) {
            LOGGER.error("SQL exception caught.", sqle);
        }

        return result;
    }

    public void setReindexTypes(String reindexTypes) {
        this.reindexTypes = reindexTypes;
    }

    public void setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
    }

    public void setReindexRecords(Boolean reindexRecords) {
        this.reindexRecords = reindexRecords;
    }

    public void setReindexRelations(Boolean reindexRelations) {
        this.reindexRelations = reindexRelations;
    }
}
