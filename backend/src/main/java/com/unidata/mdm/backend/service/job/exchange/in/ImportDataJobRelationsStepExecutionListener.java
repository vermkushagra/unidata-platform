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

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
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
        final String definitionKey = stepExecution.getJobParameters().getString(JobCommonParameters.PARAM_DEFINITION);
        ExchangeDefinition def = complexParametersHolder.getComplexParameter(definitionKey);
        boolean hasContainmentData = CollectionUtils.isNotEmpty(def.getEntities()) && def.getEntities()
                .stream()
                .anyMatch(entity -> CollectionUtils.isNotEmpty(entity.getContains()) && entity.isProcessRelations());

        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {

                // Containmetnts
                String[] containments = null;
                if (hasContainmentData) {
                    containments = new String[] {
                        ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRecordsInsertsEtalonsTableSQL")),
                        ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRecordsUpdatesEtalonsTableSQL")),
                        ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRecordsInsertsOriginsTableSQL")),
                        ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRecordsUpdatesOriginsTableSQL")),
                        ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRecordsOriginsVistoryTableSQL"))
                    };
                }

                // Relations
                final String[] relations = {

                    // Relations
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRelationsInsertsEtalonsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRelationsUpdatesEtalonsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRelationsInsertsOriginsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRelationsUpdatesOriginsTableSQL")),
                    ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("createRelationsOriginsVistoryTableSQL"))
                };

                for (String statement : relations) {
                    try {
                        jdbcTemplate.execute(statement);
                    } catch (Exception e) {
                        LOGGER.warn("Exception caught, while creating relations tables.", e);
                    }
                }

                if (ArrayUtils.isNotEmpty(containments)) {
                    for (String statement : containments) {
                        try {
                            jdbcTemplate.execute(statement);
                        } catch (Exception e) {
                            LOGGER.warn("Exception caught, while creating containment tables.", e);
                        }
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
        if (dataSetSize == BatchSetSize.SMALL || skipMove) {
            LOGGER.info("Data size is SMALL or 'skipMove' flag is active. Relations AFTER step will be skept.");
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
        copyCollectedContainments();
        copyCollectedRelations();
    }

    private void copyCollectedRelations() {

        final String[] alter = {

            // Alter relations
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("alterRowIdRelationsInsertsEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("alterRowIdRelationsUpdatesEtalonsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("alterRowIdRelationsInsertsOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("alterRowIdRelationsUpdatesOriginsTableSQL")),
            ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("alterRowIdRelationsOriginsVistoryTableSQL"))
        };

        for (String statement : alter) {
            try {
                jdbcTemplate.execute(statement);
            } catch (Exception e) {
                LOGGER.warn("Exception caught, while altering relation data tables. Cannot add row_id counter!", e);
            }
        }

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

        // Rebuild indexes, if needed
        if (initialLoad) {
            buildIndexes("etalons_relations");
            buildIndexes("origins_relations");
            buildIndexes("origins_relations_vistory");
        }
    }

    private void copyCollectedContainments() {

        ExchangeDefinition def = getExchangeDefinition();
        boolean hasContainmentData = Objects.nonNull(def)
                && CollectionUtils.isNotEmpty(def.getEntities())
                && def.getEntities()
                    .stream()
                    .anyMatch(entity -> CollectionUtils.isNotEmpty(entity.getContains()) && entity.isProcessRelations());

        if (!hasContainmentData) {
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
                LOGGER.warn("Exception caught, while altering containment tables. Cannot add row_id counter!", e);
            }
        }

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
                            searchService.setIndexSettings(name, SecurityUtils.getCurrentUserStorageId(), indexParams);
                        }
                    });
            }

            if (metaModelService.isEntity(entity.getName())) {
                searchService.setIndexSettings(entity.getName(), SecurityUtils.getCurrentUserStorageId(), indexParams);
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
                            searchService.setIndexSettings(name, SecurityUtils.getCurrentUserStorageId(), indexParams);
                            searchService.refreshIndex(name, SecurityUtils.getCurrentUserStorageId(), false);
                        }
                    });
            }

            if (metaModelService.isEntity(entity.getName())) {
                searchService.setIndexSettings(entity.getName(), SecurityUtils.getCurrentUserStorageId(), indexParams);
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
