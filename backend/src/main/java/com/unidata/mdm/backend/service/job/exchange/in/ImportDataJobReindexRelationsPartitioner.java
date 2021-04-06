package com.unidata.mdm.backend.service.job.exchange.in;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
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
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.RelatesToRelation;
import com.unidata.mdm.backend.service.data.batch.BatchSetSize;
import com.unidata.mdm.backend.service.job.ComplexJobParameterHolder;
import com.unidata.mdm.backend.service.job.JobCommonParameters;
import com.unidata.mdm.backend.service.job.JobUtil;
import com.unidata.mdm.backend.service.job.reindex.ReindexDataJobConstants;
import com.unidata.mdm.backend.service.job.reindex.ReindexDataJobExecutionMode;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 * Reindex relations partitioner.
 */
public class ImportDataJobReindexRelationsPartitioner implements Partitioner {
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
     * The block (portion) size.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_BLOCK_SIZE + "]}")
    private long blockSize;
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
     * MMS instance.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
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
            LOGGER.info("Relations were indexed in place. Skip separate reindex step.");
            return Collections.emptyMap();
        }

        ExchangeDefinition definition = jobParameterHolder.getComplexParameter(definitionKey);
        if (CollectionUtils.isEmpty(definition.getEntities())) {
            return Collections.emptyMap();
        }

        return partition(definition.getEntities());
    }

    /**
     * Collects relevant relations.
     * @return collection of relation definitions
     */
    private Map<String, ExecutionContext> partition(@Nonnull Collection<ExchangeEntity> entities) {

        // Sort order is not relevant for reindex.
        // 1. Select only multiversion/historical relations + containments
        final Map<String, ExecutionContext> result = new HashMap<>();
        if (initialLoad) {

            for (ExchangeEntity ee : entities) {

                if (!ee.isProcessRelations()) {
                    continue;
                }

                if (CollectionUtils.isNotEmpty(ee.getContains())) {
                    for (ContainmentRelation cr : ee.getContains()) {
                        if (cr.getEntity().isMultiVersion()) {

                            RelationDef relation = metaModelService.getRelationById(cr.getRelation());
                            result.putAll(partition(result.size(), relation.getToEntity(), false, true,
                                    ReindexDataJobExecutionMode.IMPORT_RECORDS_INITIAL_MULTIVERSIONS));

                            result.putAll(partition(result.size(), relation.getFromEntity(), true, false,
                                    ReindexDataJobExecutionMode.IMPORT_RELATIONS_INITIAL_MULTIVERSIONS));
                        }
                    }
                }

                if (CollectionUtils.isNotEmpty(ee.getRelates())) {
                    for (RelatesToRelation rtr : ee.getRelates()) {
                        if (rtr.isMultiVersion()) {

                            RelationDef relation = metaModelService.getRelationById(rtr.getRelation());
                            result.putAll(partition(result.size(), relation.getFromEntity(), true, false,
                                    ReindexDataJobExecutionMode.IMPORT_RELATIONS_INITIAL_MULTIVERSIONS));
                        }
                    }
                }
            }

        // 2. Was an update
        } else {

            entities.stream()
                .filter(entity -> entity.isProcessRelations()
                        && (CollectionUtils.isNotEmpty(entity.getContains()) || CollectionUtils.isNotEmpty(entity.getRelates())))
                .forEach(entity -> {

                    if (CollectionUtils.isNotEmpty(entity.getContains())) {
                        for (ContainmentRelation cr : entity.getContains()) {

                            RelationDef relation = metaModelService.getRelationById(cr.getRelation());
                            result.putAll(partition(result.size(), relation.getToEntity(), false, true,
                                    ReindexDataJobExecutionMode.IMPORT_RECORDS_UPDATE));

                            result.putAll(partition(result.size(), relation.getFromEntity(), true, false,
                                    ReindexDataJobExecutionMode.IMPORT_RELATIONS_UPDATE));
                        }
                    }

                    if (CollectionUtils.isNotEmpty(entity.getRelates())) {
                        for (RelatesToRelation rtr : entity.getRelates()) {

                            RelationDef relation = metaModelService.getRelationById(rtr.getRelation());
                            result.putAll(partition(result.size(), relation.getFromEntity(), true, false,
                                    ReindexDataJobExecutionMode.IMPORT_RELATIONS_UPDATE));
                        }
                    }
                });
        }

        return result;
    }
    /**
     * Creates execution contexts.
     * @param partition
     * @param entityName
     * @param reindexRelations
     * @param reindexRecords
     * @param mode
     * @return
     */
    private Map<String, ExecutionContext> partition(
            int partition, String entityName, boolean reindexRelations, boolean reindexRecords, ReindexDataJobExecutionMode mode) {

        final Map<String, ExecutionContext> result = new HashMap<>();
        try (Connection connection = unidataDataSource.getConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
             ResultSet cursor = statement.executeQuery(createQuery(entityName, mode))) {

            while (cursor.next()) {

                final ExecutionContext value = new ExecutionContext();
                result.put(JobUtil.partitionName(partition), value);

                Long startGSN = cursor.getLong("start_gsn");
                Long endGSN = cursor.getLong("end_gsn");

                value.put(ReindexDataJobConstants.PARAM_START_GSN, startGSN);
                value.put(ReindexDataJobConstants.PARAM_END_GSN, endGSN);
                value.put(ReindexDataJobConstants.PARAM_REINDEX_RECORDS, reindexRecords);
                value.put(ReindexDataJobConstants.PARAM_REINDEX_RELATIONS, reindexRelations);
                value.putString(ReindexDataJobConstants.PARAM_EXECUTION_MODE, mode.name());
                value.putString(ReindexDataJobConstants.PARAM_ENTITY_NAME, entityName);
                value.putString(ReindexDataJobConstants.PARAM_PARTITION_ID, "partition" + partition);
                partition++;

                LOGGER.info("Finished block of relations to reindex.");
            }
        } catch (SQLException sqle) {
            LOGGER.error("SQL exception caught.", sqle);
        }

        return result;
    }

    /**
     * Creates blocks query for an entity.
     * @param entityName the entity name
     * @return query string
     */
    private String createQuery(String entityName, ReindexDataJobExecutionMode mode) {

        final StringBuilder sqlb = new StringBuilder()
                .append("select block_num, start_id, start_gsn, end_id, end_gsn, name from ud_calc_records_etalon_batch_blocks(null, ")
                .append(blockSize)
                .append(", ")
                .append(VendorUtils.textArray(Collections.singleton(entityName)));

        switch (mode) {
        case IMPORT_RECORDS_UPDATE:
            sqlb
                .append(", '")
                .append(operationId)
                .append("', 'RECORDS') order by block_num");
            break;
        case IMPORT_RELATIONS_UPDATE:
            sqlb
                .append(", '")
                .append(operationId)
                .append("', 'RELATIONS') order by block_num");
            break;
        default:
            sqlb.append(", null, null) order by block_num");
            break;
        }

        return sqlb.toString();
    }
}
