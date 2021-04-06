package com.unidata.mdm.backend.service.job.exchange.in;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.hazelcast.core.IAtomicLong;
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.service.data.batch.BatchSetSize;
import com.unidata.mdm.backend.service.job.JobCommonParameters;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * @author Mikhail Mikhailov
 * Relations block lstener.
 */
@JobScope
public class ImportDataJobRelationsStepExecutionListener extends ImportDataJobDataStepExecutionListener
        implements StepExecutionListener {
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {

        // 1. Data set is small and skipOriginPhase is false
        if (dataSetSize == BatchSetSize.SMALL) {
            LOGGER.info("Data size is SMALL. Relations BEFORE step will be skept.");
            return;
        }

        // 2. Ensure tables
        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {

                final String[] statements = {
                    // Relations
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRelationsInsertsEtalonsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRelationsUpdatesEtalonsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRelationsInsertsOriginsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRelationsUpdatesOriginsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRelationsOriginsVistoryTableSQL"))
                };

                for (String statement : statements) {
                    try {
                        jdbcTemplate.execute(statement);
                    } catch (Exception e) {
                        LOGGER.warn("Exception caught, while creating relations tables.", e);
                    }
                }
            }
        });

        // 3. Turn off refresh, etc.
        if (initialLoad) {
            resetSearchIndexesBeforeInitialLoad(stepExecution);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        // 1. Data set is large and skipOriginPhase is false
        if (dataSetSize == BatchSetSize.SMALL) {
            LOGGER.info("Data size is SMALL. Relations AFTER step will be skept.");
            return stepExecution.getExitStatus();
        }

        // 2. Copy data
        IAtomicLong iCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId,
                        ImportDataJobConstants.IMPORT_JOB_RELATIONS_INSERT_COUNTER));
        IAtomicLong uCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId,
                        ImportDataJobConstants.IMPORT_JOB_RELATIONS_UPDATE_COUNTER));

        if ((iCounter.get() + uCounter.get()) <= 0) {
            LOGGER.info("No inserts or updates discovered. Relations COPY DATA will not be excuted.");
        } else {

            final String definitionKey = stepExecution.getJobParameters().getString(JobCommonParameters.PARAM_DEFINITION);
            ExchangeDefinition def = complexParametersHolder.getComplexParameter(definitionKey);
            boolean copyContainmentData = CollectionUtils.isEmpty(def.getEntities())
                ? false
                : def.getEntities().stream()
                    .filter(entity -> CollectionUtils.isNotEmpty(entity.getContains()))
                    .count() > 0;

            // Possibly re-run records insert for containmemnts
            if (copyContainmentData) {

                final String bulkInsertEtalons = ImportDataJobUtils.prepareTargetTableName(runId, "_etalons_i");
                final String bulkInsertOrigins = ImportDataJobUtils.prepareTargetTableName(runId, "_origins_i");
                final String bulkInsertVistory = ImportDataJobUtils.prepareTargetTableName(runId, "_origins_v");
                final String bulkUpdateEtalons = ImportDataJobUtils.prepareTargetTableName(runId, "_etalons_u");
                final String bulkUpdateOrigins = ImportDataJobUtils.prepareTargetTableName(runId, "_origins_u");

                // 1. Etalons
                insertTableData("etalons", bulkInsertEtalons,
                        ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("insertRecordsEtalonsSQL")), false);
                // 2. Origins
                insertTableData("origins", bulkInsertOrigins,
                        ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("insertRecordsOriginsSQL")), false);
                // 3. Origins vistory
                insertTableData("origins_vistory", bulkInsertVistory,
                        ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("insertRecordsOriginsVistorySQL")), false);
                // 4. Update etalons
                updateTableData("etalons", bulkUpdateEtalons,
                        ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("updateRecordsEtalonsSQL")));
                // 5. Update origins
                updateTableData("origins", bulkUpdateOrigins,
                        ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("updateRecordsOriginsSQL")));
            }

            copyCollectedData();
        }

        // 3. Restore indexing, initialLoad == true and records were indexed inplace
        if (initialLoad) {
            resetSearchIndexesAfterInitialLoad(stepExecution);
        }

        // 4. Truncate tables
        final String[] statements = {
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRelationsInsertsEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRelationsUpdatesEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRelationsInsertsOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRelationsUpdatesOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRelationsOriginsVistoryTableSQL"))
        };

        for (String statement : statements) {
            try {
                jdbcTemplate.execute(statement);
            } catch (Exception e) {
                LOGGER.warn("Exception caught, while truncating relations tables.", e);
            }
        }

        return stepExecution.getExitStatus();
    }

    /**
     * Copies collected data.
     */
    @Override
    protected void copyCollectedData() {

        final String bulkInsertEtalons = ImportDataJobUtils.prepareTargetTableName(runId, "_etalons_relations_i");
        final String bulkInsertOrigins = ImportDataJobUtils.prepareTargetTableName(runId, "_origins_relations_i");
        final String bulkInsertVistory = ImportDataJobUtils.prepareTargetTableName(runId, "_origins_relations_v");
        final String bulkUpdateEtalons = ImportDataJobUtils.prepareTargetTableName(runId, "_etalons_relations_u");
        final String bulkUpdateOrigins = ImportDataJobUtils.prepareTargetTableName(runId, "_origins_relations_u");

        // 1. Etalons
        insertTableData("etalons_relations", bulkInsertEtalons,
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("insertRelationsEtalonsSQL")), initialLoad);
        // 2. Origins
        insertTableData("origins_relations", bulkInsertOrigins,
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("insertRelationsOriginsSQL")), initialLoad);
        // 3. Origins vistory
        insertTableData("origins_relations_vistory", bulkInsertVistory,
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("insertRelationsOriginsVistorySQL")), initialLoad);
        // 4. Update etalons
        updateTableData("etalons_relations", bulkUpdateEtalons,
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("updateRelationsEtalonsSQL")));
        // 5. Update origins
        updateTableData("origins_relations", bulkUpdateOrigins,
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("updateRelationsOriginsSQL")));
        // 6. Rebuild indexes, if needed
        if (initialLoad) {
            buildIndexes("etalons_relations");
            buildIndexes("origins_relations");
            buildIndexes("origins_relations_vistory");
        }
    }

    /**
     * Do before mass indexing.
     * @param stepExecution the step execution
     */
    @Override
    public void resetSearchIndexesBeforeInitialLoad(StepExecution stepExecution) {

        final String definitionKey = stepExecution.getJobParameters().getString(JobCommonParameters.PARAM_DEFINITION);
        ExchangeDefinition def = complexParametersHolder.getComplexParameter(definitionKey);

        /*
         * ("index.refresh_interval", "-1"); // Disable refresh
         * ("index.warmer.enabled", Boolean.FALSE); // Disable warmers
         */
        Map<String, Object> indexParams = Collections.singletonMap("index.refresh_interval", "-1");
        for (ExchangeEntity entity : def.getEntities()) {

            if (CollectionUtils.isEmpty(entity.getContains())
             && CollectionUtils.isEmpty(entity.getRelates())) {
                continue;
            }

            if (CollectionUtils.isNotEmpty(entity.getContains())) {
                entity.getContains().stream()
                    .map(ContainmentRelation::getEntity)
                    .map(ExchangeEntity::getName)
                    .forEach(name -> {

                        if (metaModelService.isEntity(name)) {
                            // searchService.closeIndex(name, SecurityUtils.getCurrentUserStorageId());
                            searchService.setIndexSettings(name, SecurityUtils.getCurrentUserStorageId(), indexParams);
                            // searchService.openIndex(name, SecurityUtils.getCurrentUserStorageId());
                        }
                    });
            }

            if (metaModelService.isEntity(entity.getName())) {
                // searchService.closeIndex(entity.getName(), SecurityUtils.getCurrentUserStorageId());
                searchService.setIndexSettings(entity.getName(), SecurityUtils.getCurrentUserStorageId(), indexParams);
                // searchService.openIndex(entity.getName(), SecurityUtils.getCurrentUserStorageId());
            }
        }

        /*
         * ("indices.memory.index_buffer_size", "40%"); // Increase indexing buffer size
         * ("indices.store.throttle.type", "none"); // None throttling.
         */
        searchService.setClusterSettings(Collections.singletonMap("indices.store.throttle.type", "none"), false);
    }
    /**
     * Do after mass indexing.
     * @param stepExecution the step execution
     */
    @Override
    public void resetSearchIndexesAfterInitialLoad(StepExecution stepExecution) {

        final String definitionKey = stepExecution.getJobParameters().getString(JobCommonParameters.PARAM_DEFINITION);
        ExchangeDefinition def = complexParametersHolder.getComplexParameter(definitionKey);

        /*
         * ("index.refresh_interval", "1s"); // Enable refresh
         * ("index.warmer.enabled", Boolean.TRUE); // Enable warmers
         */
        Map<String, Object> indexParams = Collections.singletonMap("index.refresh_interval", "1s");
        for (ExchangeEntity entity : def.getEntities()) {

            if (CollectionUtils.isEmpty(entity.getContains())
             && CollectionUtils.isEmpty(entity.getRelates())) {
               continue;
            }

            if (CollectionUtils.isNotEmpty(entity.getContains())) {
                entity.getContains().stream()
                    .map(ContainmentRelation::getEntity)
                    .map(ExchangeEntity::getName)
                    .forEach(name -> {

                        if (metaModelService.isEntity(name)) {
                            // searchService.closeIndex(name, SecurityUtils.getCurrentUserStorageId());
                            searchService.setIndexSettings(name, SecurityUtils.getCurrentUserStorageId(), indexParams);
                            // searchService.openIndex(name, SecurityUtils.getCurrentUserStorageId());
                            searchService.refreshIndex(name, SecurityUtils.getCurrentUserStorageId(), false);
                        }
                    });
            }

            if (metaModelService.isEntity(entity.getName())) {
                // searchService.closeIndex(entity.getName(), SecurityUtils.getCurrentUserStorageId());
                searchService.setIndexSettings(entity.getName(), SecurityUtils.getCurrentUserStorageId(), indexParams);
                // searchService.openIndex(entity.getName(), SecurityUtils.getCurrentUserStorageId());
                searchService.refreshIndex(entity.getName(), SecurityUtils.getCurrentUserStorageId(), false);
            }
        }

        /*
         * ("indices.memory.index_buffer_size", "10%"); // Decrease indexing buffer size
         * ("indices.store.throttle.type", "merge"); // Merge throttling.
         */
        searchService.setClusterSettings(Collections.singletonMap("indices.store.throttle.type", "merge"), false);
    }
}
