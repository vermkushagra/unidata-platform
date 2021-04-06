package com.unidata.mdm.backend.service.job.exchange.in;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.unidata.mdm.backend.dao.util.VendorUtils;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.service.data.batch.BatchSetSize;
import com.unidata.mdm.backend.service.job.ComplexJobParameterHolder;
import com.unidata.mdm.backend.service.job.JobCommonParameters;
import com.unidata.mdm.backend.service.job.JobUtil;
import com.unidata.mdm.backend.service.job.reindex.ReindexDataJobConstants;
import com.unidata.mdm.backend.service.job.reindex.ReindexDataJobExecutionMode;

/**
 * @author Mikhail Mikhailov
 * Reindex records partitioner.
 */
public class ImportDataJobReindexRecordsPartitioner implements Partitioner {
    /**
     * Logger
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(ImportDataJobConstants.IMPORT_JOB_LOGGER_NAME);
    /**
     * Definition key.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_DEFINITION + "]}")
    private String definitionKey;
    /**
     * This run id.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_RUN_ID + "]}")
    private String runId;
    /**
     * The set size hint.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_DATA_SET_SIZE + "]}")
    private BatchSetSize dataSetSize;
    /**
     * New objects only.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_INITIAL_LOAD + "]}")
    private boolean initialLoad;
    /**
     * The block (portion) size.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_BLOCK_SIZE + "]}")
    private long blockSize;
    /**
     * Operation ID.
     */
    @Value("#{jobParameters[" + JobCommonParameters.PARAM_OPERATION_ID + "]}")
    private String operationId;
    /**
     * Complex parameters holder.
     */
    @Autowired
    private ComplexJobParameterHolder jobParameterHolder;
    /**
     * Unidata data source.
     */
    @Qualifier("unidataDataSource")
    @Autowired
    private DataSource unidataDataSource;
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        if (dataSetSize == BatchSetSize.SMALL) {
            LOGGER.info("Records were indexed in place. Skip separate reindex step.");
            return Collections.emptyMap();
        }

        Collection<String> types = collectEntitiesNames();
        if (CollectionUtils.isEmpty(types)) {
            LOGGER.info("No entity names collected for post-indexing. Exiting.");
            return Collections.emptyMap();
        }

        ReindexDataJobExecutionMode mode = initialLoad
                ? ReindexDataJobExecutionMode.IMPORT_RECORDS_INITIAL_MULTIVERSIONS
                : ReindexDataJobExecutionMode.IMPORT_RECORDS_UPDATE;

        int partition = 0;
        final Map<String, ExecutionContext> result = new HashMap<>();
        String query = createQuery(types, mode);
        try (Connection connection = unidataDataSource.getConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
             ResultSet cursor = statement.executeQuery(query)) {

            while (cursor.next()) {

                final ExecutionContext value = new ExecutionContext();
                result.put(JobUtil.partitionName(partition), value);

                Long startGSN = cursor.getLong("start_gsn");
                Long endGSN = cursor.getLong("end_gsn");
                String entityName = cursor.getString("name");

                value.put(ReindexDataJobConstants.PARAM_START_GSN, startGSN);
                value.put(ReindexDataJobConstants.PARAM_END_GSN, endGSN);
                value.put(ReindexDataJobConstants.PARAM_REINDEX_RECORDS, Boolean.TRUE);
                value.put(ReindexDataJobConstants.PARAM_REINDEX_MATCHING, Boolean.TRUE);
                value.put(ReindexDataJobConstants.PARAM_REINDEX_CLASSIFIERS, Boolean.TRUE);
                value.put(ReindexDataJobConstants.PARAM_REINDEX_RELATIONS, Boolean.FALSE);
                value.putString(ReindexDataJobConstants.PARAM_EXECUTION_MODE, mode.name());
                value.putString(ReindexDataJobConstants.PARAM_ENTITY_NAME, entityName);
                value.putString(ReindexDataJobConstants.PARAM_PARTITION_ID, "partition" + partition);
                partition++;

                LOGGER.info("Finished block of records to reindex.");
            }
        } catch (SQLException sqle) {
            LOGGER.error("SQL exception caught.", sqle);
        }

        return result;
    }
    /**
     * Collects relevant entitie names.
     * @return collection.
     */
    private Collection<String> collectEntitiesNames() {

        ExchangeDefinition definition = jobParameterHolder.getComplexParameter(definitionKey);

        if (CollectionUtils.isEmpty(definition.getEntities())) {
            return Collections.emptyList();
        }

        // Sort order is not relevant for reindex.
        // 1. If current load is initial load,
        // only multiversioned (historical) entities should be reindexed
        if (initialLoad) {
            return definition.getEntities().stream()
                .filter(ExchangeEntity::isMultiVersion)
                .filter(e -> e.isProcessRecords() || e.isProcessClassifiers())
                .map(ExchangeEntity::getName)
                .collect(Collectors.toList());
        } else {
            return definition.getEntities().stream()
                .filter(e -> e.isProcessRecords() || e.isProcessClassifiers())
                .map(ExchangeEntity::getName)
                .collect(Collectors.toList());
        }
    }

    /**
     * Creates blocks query for an entity.
     * @param entityName the entity name
     * @return query string
     */
    private String createQuery(Collection<String> entityNames, ReindexDataJobExecutionMode mode) {

        String typesAsArray = VendorUtils.textArray(entityNames);
        final StringBuilder sqlb = new StringBuilder()
                .append("select block_num, start_id, start_gsn, end_id, end_gsn, name from ud_calc_records_etalon_batch_blocks(null, ")
                .append(blockSize)
                .append(", ")
                .append(typesAsArray)
                .append(mode == ReindexDataJobExecutionMode.IMPORT_RECORDS_UPDATE ? ", '" : ", ")
                .append(mode == ReindexDataJobExecutionMode.IMPORT_RECORDS_UPDATE ? operationId : "null")
                .append(mode == ReindexDataJobExecutionMode.IMPORT_RECORDS_UPDATE ? "', 'RECORDS') order by block_num" : ", null) order by block_num");

        return sqlb.toString();
    }
}
