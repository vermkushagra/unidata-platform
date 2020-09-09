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
        if (dataSetSize == BatchSetSize.SMALL || skipMove) {
            LOGGER.info("Data size is SMALL or 'skipMove' flag is active. Records AFTER step wll be skipped.");
            return stepExecution.getExitStatus();
        }

        copyCollectedData();

        // 3. Restore indexing, initialLoad == true and records were indexed inplace
        if (initialLoad) {
            super.resetSearchIndexesAfterInitialLoad(stepExecution);
        }

        // 4. Drop tables
        // Records and classifiers. Drop all except ref table.
        // Record tables can be recreated for containments in rel. processing
        final String[] statements = {

            // Records
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRecordsInsertsEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRecordsUpdatesEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRecordsInsertsOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRecordsUpdatesOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRecordsOriginsVistoryTableSQL")),

            // Classifiers
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropClassifiersInsertsEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropClassifiersUpdatesEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropClassifiersInsertsOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropClassifiersUpdatesOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropClassifiersOriginsVistoryTableSQL"))
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

    private void copyCollectedRecordData() {

        // Check
        IAtomicLong iCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId,
                        ImportDataJobConstants.IMPORT_JOB_RECORDS_INSERT_COUNTER));

        IAtomicLong uCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId,
                        ImportDataJobConstants.IMPORT_JOB_RECORDS_UPDATE_COUNTER));

        IAtomicLong dCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId,
                        ImportDataJobConstants.IMPORT_JOB_RECORDS_DELETE_COUNTER));

        if ((iCounter.get() + uCounter.get() + dCounter.get()) <= 0) {
            LOGGER.info("No inserts, updates or deletes discovered. COPY DATA for records will not be excuted.");
            return;
        }

        final String[] alter = {
            // Alter records
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("alterRowIdRecordsInsertsEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("alterRowIdRecordsUpdatesEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("alterRowIdRecordsInsertsOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("alterRowIdRecordsUpdatesOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("alterRowIdRecordsOriginsVistoryTableSQL")),
        };

        for (String statement : alter) {
            try {
                jdbcTemplate.execute(statement);
            } catch (Exception e) {
                LOGGER.warn("Exception caught, while altering records data tables. Cannot add row_id counter!", e);
            }
        }

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
    }

    private void copyCollectedClassifiersData() {

        // Check
        IAtomicLong iCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId,
                        ImportDataJobConstants.IMPORT_JOB_CLASSIFIERS_INSERT_COUNTER));

        IAtomicLong uCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId,
                        ImportDataJobConstants.IMPORT_JOB_CLASSIFIERS_UPDATE_COUNTER));

        IAtomicLong dCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId,
                        ImportDataJobConstants.IMPORT_JOB_CLASSIFIERS_DELETE_COUNTER));

        if ((iCounter.get() + uCounter.get() + dCounter.get()) <= 0) {
            LOGGER.info("No inserts, updates or deletes discovered. COPY DATA for classifiers will not be excuted.");
            return;
        }

        final String[] alter = {
            // Alter classifiers
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("alterRowIdClassifiersInsertsEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("alterRowIdClassifiersUpdatesEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("alterRowIdClassifiersInsertsOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("alterRowIdClassifiersUpdatesOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("alterRowIdClassifiersOriginsVistoryTableSQL"))
        };

        for (String statement : alter) {
            try {
                jdbcTemplate.execute(statement);
            } catch (Exception e) {
                LOGGER.warn("Exception caught, while altering classifiers tables. Cannot add row_id counter!", e);
            }
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
    /**
     * Copies collected data.
     */
    @Override
    protected void copyCollectedData() {
        copyCollectedRecordData();
        copyCollectedClassifiersData();
    }
}
