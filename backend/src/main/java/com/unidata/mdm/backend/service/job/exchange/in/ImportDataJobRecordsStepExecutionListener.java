package com.unidata.mdm.backend.service.job.exchange.in;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.hazelcast.core.IAtomicLong;
import com.unidata.mdm.backend.service.data.batch.BatchSetSize;

/**
 * @author Mikhail Mikhailov
 * Prepares storage. Creates objects if neseccary.
 */
@JobScope
public class ImportDataJobRecordsStepExecutionListener extends ImportDataJobDataStepExecutionListener implements StepExecutionListener {
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {

        // 1. Data set is large
        if (dataSetSize == BatchSetSize.SMALL) {
            LOGGER.info("Data size is SMALL. Records BEFORE step will be skept.");
            return;
        }

        // 2. Prepare import tables
        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {

                final String[] statements = {

                    // Records
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRecordsInsertsEtalonsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRecordsUpdatesEtalonsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRecordsInsertsOriginsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRecordsUpdatesOriginsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRecordsOriginsVistoryTableSQL")),

                    // Classifiers
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createClassifiersInsertsEtalonsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createClassifiersUpdatesEtalonsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createClassifiersInsertsOriginsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createClassifiersUpdatesOriginsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createClassifiersOriginsVistoryTableSQL"))
                };

                // Insert reference table
                for (String statement : statements) {
                    try {
                        jdbcTemplate.execute(statement);
                    } catch (Exception e) {
                        LOGGER.warn("Exception caught, while creating tables.", e);
                    }
                }
            }
        });

        // 3. Prepare indexing for initialLoad == true (records will be indexed inplace)
        // Otherwise this will be done in the indexing step
        if (initialLoad) {
            super.resetSearchIndexesBeforeInitialLoad(stepExecution);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        // 1. Data set is large and skipOriginPhase is false
        if (dataSetSize == BatchSetSize.SMALL) {
            LOGGER.info("Data size is SMALL. Records AFTER step wll be skept.");
            return stepExecution.getExitStatus();
        }

        // 2. Copy data
        IAtomicLong iCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId,
                        ImportDataJobConstants.IMPORT_JOB_RECORDS_INSERT_COUNTER));

        IAtomicLong uCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId,
                        ImportDataJobConstants.IMPORT_JOB_RECORDS_UPDATE_COUNTER));

        if ((iCounter.get() + uCounter.get()) <= 0) {
            LOGGER.info("No inserts or updates discovered. Records COPY DATA will not be excuted.");
        } else {
            copyCollectedData();
        }

        // 3. Restore indexing, initialLoad == true and records were indexed inplace
        if (initialLoad) {
            super.resetSearchIndexesAfterInitialLoad(stepExecution);
        }

        // 4. Truncate tables
        // Records and classifiers. Truncate all except ref table.
        // Drop will be done in the job listener.
        final String[] statements = {

            // Truncate records
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("truncateRecordsInsertsEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("truncateRecordsUpdatesEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("truncateRecordsInsertsOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("truncateRecordsUpdatesOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("truncateRecordsOriginsVistoryTableSQL")),

            // Truncate classifiers
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("truncateClassifiersInsertsEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("truncateClassifiersUpdatesEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("truncateClassifiersInsertsOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("truncateClassifiersUpdatesOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("truncateClassifiersOriginsVistoryTableSQL"))
        };

        for (String statement : statements) {
            try {
                jdbcTemplate.execute(statement);
            } catch (Exception e) {
                LOGGER.warn("Exception caught, while truncating tables.", e);
            }
        }

        return stepExecution.getExitStatus();
    }
    /**
     * Copies collected data.
     */
    @Override
    protected void copyCollectedData() {

        // Records
        final String bulkInsertEtalons = ImportDataJobUtils.prepareTargetTableName(runId, "_etalons_i");
        final String bulkInsertOrigins = ImportDataJobUtils.prepareTargetTableName(runId, "_origins_i");
        final String bulkInsertVistory = ImportDataJobUtils.prepareTargetTableName(runId, "_origins_v");
        final String bulkUpdateEtalons = ImportDataJobUtils.prepareTargetTableName(runId, "_etalons_u");
        final String bulkUpdateOrigins = ImportDataJobUtils.prepareTargetTableName(runId, "_origins_u");

        // 1. Etalons
        insertTableData("etalons", bulkInsertEtalons,
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("insertRecordsEtalonsSQL")), initialLoad);
        // 2. Origins
        insertTableData("origins", bulkInsertOrigins,
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("insertRecordsOriginsSQL")), initialLoad);
        // 3. Origins vistory
        insertTableData("origins_vistory", bulkInsertVistory,
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("insertRecordsOriginsVistorySQL")), initialLoad);
        // 4. Update etalons
        updateTableData("etalons", bulkUpdateEtalons,
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("updateRecordsEtalonsSQL")));
        // 5. Update origins
        updateTableData("origins", bulkUpdateOrigins,
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("updateRecordsOriginsSQL")));

        // 6. Rebuild indexes, if needed
        if (initialLoad) {
            buildIndexes("etalons");
            buildIndexes("origins");
            buildIndexes("origins_vistory");
        }

        // Classifiers
        final String bulkInsertClassifierEtalons = ImportDataJobUtils.prepareTargetTableName(runId, "_etalons_classifiers_i");
        final String bulkInsertClassifierOrigins = ImportDataJobUtils.prepareTargetTableName(runId, "_origins_classifiers_i");
        final String bulkInsertClassifierVistory = ImportDataJobUtils.prepareTargetTableName(runId, "_origins_classifiers_v");
        final String bulkUpdateClassifierEtalons = ImportDataJobUtils.prepareTargetTableName(runId, "_etalons_classifiers_u");
        final String bulkUpdateClassifierOrigins = ImportDataJobUtils.prepareTargetTableName(runId, "_origins_classifiers_u");

        // 1. Etalons
        insertTableData("etalons_classifiers", bulkInsertClassifierEtalons,
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("insertClassifiersEtalonsSQL")), initialLoad);
        // 2. Origins
        insertTableData("origins_classifiers", bulkInsertClassifierOrigins,
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("insertClassifiersOriginsSQL")), initialLoad);
        // 3. Origins vistory
        insertTableData("origins_classifiers_vistory", bulkInsertClassifierVistory,
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("insertClassifiersOriginsVistorySQL")), initialLoad);
        // 4. Update etalons
        updateTableData("etalons_classifiers", bulkUpdateClassifierEtalons,
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("updateClassifiersEtalonsSQL")));
        // 5. Update origins
        updateTableData("origins_classifiers", bulkUpdateClassifierOrigins,
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("updateClassifiersOriginsSQL")));

        // 6. Rebuild indexes, if needed
        if (initialLoad) {
            buildIndexes("etalons_classifiers");
            buildIndexes("origins_classifiers");
            buildIndexes("origins_classifiers_vistory");
        }
    }
}
