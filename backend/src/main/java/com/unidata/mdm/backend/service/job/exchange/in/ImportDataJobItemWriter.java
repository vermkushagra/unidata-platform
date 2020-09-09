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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.unidata.mdm.backend.common.context.DeleteClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationsRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.DeleteClassifiersDTO;
import com.unidata.mdm.backend.common.dto.DeleteRecordDTO;
import com.unidata.mdm.backend.common.dto.DeleteRelationsDTO;
import com.unidata.mdm.backend.common.dto.UpsertClassifiersDTO;
import com.unidata.mdm.backend.common.dto.UpsertRecordDTO;
import com.unidata.mdm.backend.common.dto.UpsertRelationsDTO;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.RelatesToRelation;
import com.unidata.mdm.backend.service.data.RecordsServiceComponent;
import com.unidata.mdm.backend.service.data.batch.BatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.BatchSetIterationType;
import com.unidata.mdm.backend.service.data.batch.BatchSetSize;
import com.unidata.mdm.backend.service.data.batch.BatchTarget;
import com.unidata.mdm.backend.service.data.batch.ClassifierUpsertBatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.ClassifiersDeleteBatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.RecordDeleteBatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.RecordUpsertBatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.RelationDeleteBatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.RelationUpsertBatchSetAccumulator;
import com.unidata.mdm.backend.service.data.classifiers.ClassifiersServiceComponent;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.service.job.AbstractUnidataWriter;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportDataSet;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportRecordSet;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportRelationSet;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

@StepScope
public class ImportDataJobItemWriter extends AbstractUnidataWriter<ImportDataSet> implements InitializingBean {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportDataJobConstants.IMPORT_JOB_LOGGER_NAME);
    /**
     * Run origin phase only.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_SKIP_INDEXING + "]}")
    private boolean skipIndexing;
    /**
     * No updates expected (TEST).
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_INITIAL_LOAD + "] ?: false}")
    private boolean initialLoad;
    /**
     * This run id.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_RUN_ID + "]}")
    private String runId;
    /**
     * The set size hint.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_DATA_SET_SIZE + "] ?: 'SMALL'}")
    private BatchSetSize batchSetSize;
    /**
     * The block (portion) size.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_BLOCK_SIZE + "]}")
    private long blockSize;
    /**
     * Processing batch crashes.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PROCESSING_BATCH_CRASHES + "]}")
    private boolean processingBatchCrashes;
    /**
     * Records component.
     */
    @Autowired
    private RecordsServiceComponent recordsComponent;
    /**
     * Relations component.
     */
    @Autowired
    private RelationsServiceComponent relationsComponent;
    /**
     * Classifiers component.
     */
    @Autowired
    private ClassifiersServiceComponent classifiersComponent;
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * Search service.
     */
    @Autowired
    private SearchServiceExt searchServiceExt;
    /**
     * Unidata data source.
     */
    @Qualifier("unidataDataSource")
    @Autowired
    protected DataSource unidataDataSource;
    /**
     * Data records service.
     */
    @Autowired
    private DataRecordsService dataRecordsService;
    /**
     * Accumulators.
     */
    private Map<AccumulatorType, BatchSetAccumulator<?>> accumulators = new EnumMap<>(AccumulatorType.class);
    /**
     * Records targets.
     */
    private Map<BatchTarget, String> recordTargets = null;
    /**
     * Relation targets.
     */
    private Map<BatchTarget, String> relationTargets = null;
    /**
     * Classifier targets.
     */
    private Map<BatchTarget, String> classifierTargets = null;

    /**
     * @author Mikhail Mikhailov
     * Map keys.
     */
    private enum AccumulatorType {
        UPSERT_RECORDS,
        DELETE_RECORDS,
        UPSERT_RELATIONS,
        DELETE_RELATIONS,
        UPSERT_CLASSIFIERS,
        DELETE_CLASSIFIERS
    }

    /**
     * @author Mikhail Mikhailov
     * Type of collected relation contexts.
     */
    private enum CollectedRelationContexts {
        UPSERT_CONTEXTS,
        DELETE_CONTEXTS
    }

    /**
     * @author Mikhail Mikhailov
     * Type of collected relation contexts.
     */
    private enum CollectedRecordContexts {
        UPSERT_RECORD_CONTEXTS,
        DELETE_RECORD_CONTEXTS,
        UPSERT_CLASSIFIER_CONTEXTS,
        DELETE_CLASSIFIER_CONTEXTS
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        recordTargets = new EnumMap<>(BatchTarget.class);
        recordTargets.put(BatchTarget.ETALON_INSERTS, ImportDataJobUtils.prepareTargetTableName(runId, "_etalons_i"));
        recordTargets.put(BatchTarget.ETALON_UPDATES, ImportDataJobUtils.prepareTargetTableName(runId, "_etalons_u"));
        recordTargets.put(BatchTarget.ORIGIN_INSERTS, ImportDataJobUtils.prepareTargetTableName(runId, "_origins_i"));
        recordTargets.put(BatchTarget.ORIGIN_UPDATES, ImportDataJobUtils.prepareTargetTableName(runId, "_origins_u"));
        recordTargets.put(BatchTarget.VISTORY, ImportDataJobUtils.prepareTargetTableName(runId, "_origins_v"));

        classifierTargets = new EnumMap<>(BatchTarget.class);
        classifierTargets.put(BatchTarget.ETALON_INSERTS, ImportDataJobUtils.prepareTargetTableName(runId, "_etalons_classifiers_i"));
        classifierTargets.put(BatchTarget.ETALON_UPDATES, ImportDataJobUtils.prepareTargetTableName(runId, "_etalons_classifiers_u"));
        classifierTargets.put(BatchTarget.ORIGIN_INSERTS, ImportDataJobUtils.prepareTargetTableName(runId, "_origins_classifiers_i"));
        classifierTargets.put(BatchTarget.ORIGIN_UPDATES, ImportDataJobUtils.prepareTargetTableName(runId, "_origins_classifiers_u"));
        classifierTargets.put(BatchTarget.VISTORY, ImportDataJobUtils.prepareTargetTableName(runId, "_origins_classifiers_v"));

        relationTargets = new EnumMap<>(BatchTarget.class);
        relationTargets.put(BatchTarget.ETALON_INSERTS, ImportDataJobUtils.prepareTargetTableName(runId, "_etalons_relations_i"));
        relationTargets.put(BatchTarget.ETALON_UPDATES, ImportDataJobUtils.prepareTargetTableName(runId, "_etalons_relations_u"));
        relationTargets.put(BatchTarget.ORIGIN_INSERTS, ImportDataJobUtils.prepareTargetTableName(runId, "_origins_relations_i"));
        relationTargets.put(BatchTarget.ORIGIN_UPDATES, ImportDataJobUtils.prepareTargetTableName(runId, "_origins_relations_u"));
        relationTargets.put(BatchTarget.VISTORY, ImportDataJobUtils.prepareTargetTableName(runId, "_origins_relations_v"));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends ImportDataSet> items) throws Exception {

        ImportDataJobStepExecutionState parameters = ImportDataJobUtils.getStepState();
        if (parameters.exchangeObjectIsRelation()) {

            Map<CollectedRelationContexts, List<?>> relations = repackageRelationsResult((List<ImportRelationSet>) items);
            processRelations(parameters,
                    (List<UpsertRelationsRequestContext>) relations.get(CollectedRelationContexts.UPSERT_CONTEXTS),
                    (List<DeleteRelationsRequestContext>) relations.get(CollectedRelationContexts.DELETE_CONTEXTS));
        } else {

            Map<CollectedRecordContexts, List<?>> records = repackageRecordsResult((List<ImportRecordSet>) items);
            processRecords(
                    parameters,
                    (List<UpsertRequestContext>) records.get(CollectedRecordContexts.UPSERT_RECORD_CONTEXTS),
                    (List<DeleteRequestContext>) records.get(CollectedRecordContexts.DELETE_RECORD_CONTEXTS));

            // Post process records for resolved + classifiers
            items.stream()
                    .map(item -> (ImportRecordSet) item)
                    .filter(item -> Objects.nonNull(item.getClassifiersUpsert()))
                    .forEach(item -> {
                        if (Objects.nonNull(item.getRecordUpsert())
                                && item.getRecordUpsert().getFromStorage(StorageId.DATA_BATCH_ACCEPT) == Boolean.TRUE) {
                            item.getClassifiersUpsert().putToStorage(item.getClassifiersUpsert().keysId(), item.getRecordUpsert().keys());
                        }
                    });

            processClassifiers(parameters,
                    (List<UpsertClassifiersDataRequestContext>) records.get(CollectedRecordContexts.UPSERT_CLASSIFIER_CONTEXTS),
                    (List<DeleteClassifiersDataRequestContext>) records.get(CollectedRecordContexts.DELETE_CLASSIFIER_CONTEXTS));
        }
    }

    /**
     * Process records.
     *
     * @param parameters this thread parameters
     * @param upserts upsert contexts
     * @param deletes delete contexts
     */
    private void processRecords(ImportDataJobStepExecutionState parameters,
                                List<UpsertRequestContext> upserts, List<DeleteRequestContext> deletes) {

        ExchangeEntity ee = parameters.getExchangeObject();
        boolean isLookup = metaModelService.isLookupEntity(ee.getName());

        // Divide processing (separate data put in the first step and idexing in the second)
        // to acheave accurate timeline in index, if this import is:
        // - NOT initial (updates), nor is this a lookup or historical records are processed
        boolean divideProcessing = batchSetSize == BatchSetSize.LARGE && (!isLookup && (!initialLoad || ee.isMultiVersion()));

        if (!CollectionUtils.isEmpty(upserts)) {

            BatchSetAccumulator<UpsertRequestContext> upsertAccumulator = getRecordsUpsertAccumulator(parameters, isLookup, divideProcessing);
            upsertAccumulator.charge(upserts);

            boolean batchCrashed = false;
            try {
                List<UpsertRecordDTO> result = recordsComponent.batchUpsertRecords(upsertAccumulator);

                long inserted = result.stream().filter(dto -> dto.getAction() == UpsertAction.INSERT).count();
                long updated = result.stream().filter(dto -> dto.getAction() == UpsertAction.UPDATE).count();
                long skept = upserts.size() - (inserted + updated);

                parameters.incrementInserted(inserted);
                parameters.incrementUpdated(updated);
                parameters.incrementSkept(skept);

            } catch (Exception e) {
                String message = getErrorMessage(e);
                if (!processingBatchCrashes) {
                    parameters.incrementFailed(upserts.size());
                } else {
                    batchCrashed = true;
                }
                LOGGER.error("Error during upsert records. {}", message, e);
            } finally {
                upsertAccumulator.discharge();
            }

            if (batchCrashed) {
                LOGGER.warn("Batch UPSERT crashed. Trying processing batch items one by one, batch size [{}].", upserts.size());
                int errorsCount = 0;
                int upsertsSize = 1;

                for (UpsertRequestContext crashedUpsert : upserts) {
                    UpsertRequestContext upsert = UpsertRequestContext.builder(crashedUpsert).batchUpsert(false).build();
                    upsert.repeatNotificationBehavior(crashedUpsert);
                    upsert.setOperationId(crashedUpsert.getOperationId());
                    try {
                        UpsertRecordDTO result = dataRecordsService.upsertRecord(upsert);

                        long inserted = result.getAction() == UpsertAction.INSERT ? 1 : 0;
                        long updated = result.getAction() == UpsertAction.UPDATE ? 1 : 0;
                        long skept = upsertsSize - (inserted + updated);

                        parameters.incrementInserted(inserted);
                        parameters.incrementUpdated(updated);
                        parameters.incrementSkept(skept);

                    } catch (Exception e) {
                        parameters.incrementFailed(upsertsSize);
                        String message = getErrorMessage(e);
                        errorsCount++;
                        LOGGER.error("Error during upsert record. {}", message);
                    }
                }
                LOGGER.warn("Batch UPSERT crash processed successfully. Processed [{}] errors in [{}] upserts.", errorsCount, upserts.size());
            }
        }

        if (!CollectionUtils.isEmpty(deletes)) {

            BatchSetAccumulator<DeleteRequestContext> deleteAccumulator = getRecordsDeleteAccumulator(parameters, divideProcessing);
            deleteAccumulator.charge(deletes);

            try {

                List<DeleteRecordDTO> result = recordsComponent.batchDeleteRecords(deleteAccumulator);
                parameters.incrementDeleted(result.size());
                parameters.incrementSkept(deletes.size() - (long) result.size());

            } catch (Exception e) {
                parameters.incrementFailed(deletes.size());
                String message = getErrorMessage(e);

                LOGGER.error("Error during delete records. {}", message);
            } finally {
                deleteAccumulator.discharge();
            }
        }

        // Special case - lookups must be refreshed asap
        if (isLookup && (!CollectionUtils.isEmpty(upserts) || !CollectionUtils.isEmpty(deletes))) {
            searchServiceExt.refreshIndex(ee.getName(), SecurityUtils.getCurrentUserStorageId(), false);
        }
    }

    /**
     * Does classifiers processing.
     *
     * @param parameters
     * @param upserts
     * @param deletes
     */
    private void processClassifiers(ImportDataJobStepExecutionState parameters,
                                    List<UpsertClassifiersDataRequestContext> upserts,
                                    List<DeleteClassifiersDataRequestContext> deletes) {

        ExchangeEntity ee = parameters.getExchangeObject();
        boolean divideProcessing = batchSetSize == BatchSetSize.LARGE && (!initialLoad || ee.isMultiVersion());

        if (CollectionUtils.isNotEmpty(upserts)) {

            BatchSetAccumulator<UpsertClassifiersDataRequestContext> upsertAccumulator = getClassifiersUpsertAccumulator(parameters, divideProcessing);
            upsertAccumulator.charge(upserts);

            try {

                List<UpsertClassifiersDTO> result = classifiersComponent.batchUpsertClassifiers(upsertAccumulator);

                long inserted = result.stream()
                            .flatMap(dto -> dto.getClassifiers().values().stream())
                            .flatMap(Collection::stream)
                            .filter(dto -> dto.getAction() == UpsertAction.INSERT)
                            .count();

                long updated = result.stream()
                        .flatMap(dto -> dto.getClassifiers().values().stream())
                        .flatMap(Collection::stream)
                        .filter(dto -> dto.getAction() == UpsertAction.UPDATE)
                        .count();

                long skipped = upserts.size() - (inserted + updated);

                parameters.incrementClassifiersInserted(inserted);
                parameters.incrementClassifiersUpdated(updated);
                parameters.incrementClassifiersSkipped(skipped);

            } catch (Exception e) {
                parameters.incrementClassifiersFailed(upserts.size());
                String message = getErrorMessage(e);

                LOGGER.error("Error during upsert classifier records. {}", message);
            } finally {
                upsertAccumulator.discharge();
            }
        }

        if (CollectionUtils.isNotEmpty(deletes)) {

            BatchSetAccumulator<DeleteClassifiersDataRequestContext> deleteAccumulator = getClassifiersDeleteAccumulator(parameters, divideProcessing);
            deleteAccumulator.charge(deletes);

            try {

                List<DeleteClassifiersDTO> result = classifiersComponent.batchDeleteClassifiers(deleteAccumulator);
                parameters.incrementClassifiersDeleted(result.size());
                parameters.incrementClassifiersSkipped(deletes.size() - (long) result.size());

            } catch (Exception e) {
                parameters.incrementClassifiersFailed(deletes.size());
                String message = getErrorMessage(e);

                LOGGER.error("Error during delete records. {}", message);
            } finally {
                deleteAccumulator.discharge();
            }
        }
    }

    /**
     * Does relation processing.
     *
     * @param parameters
     * @param upserts
     */
    private void processRelations(ImportDataJobStepExecutionState parameters,
                                  List<UpsertRelationsRequestContext> upserts,
                                  List<DeleteRelationsRequestContext> deletes) {

        if (!CollectionUtils.isEmpty(upserts)) {

            // Write digest for later post-processing, if this import is:
            // - of size LARGE and the load is NOT initial (updates), nor is this a lookup or historical records being processed
            boolean isMultiVersion = false;
            if (parameters.exchangeObjectIsContainmentRelation()) {
                ContainmentRelation containment = parameters.getExchangeObject();
                isMultiVersion = containment.getEntity().isMultiVersion();
            } else {
                RelatesToRelation relTo = parameters.getExchangeObject();
                isMultiVersion = relTo.isMultiVersion();
            }

            boolean divideProcessing = batchSetSize == BatchSetSize.LARGE && (!initialLoad || isMultiVersion);

            long submitted = upserts.stream()
                    .flatMap(ctx -> ctx.getRelations().values().stream())
                    .mapToLong(Collection::size)
                    .sum();

            BatchSetAccumulator<UpsertRelationsRequestContext> upsertAccumulator = getRelationsUpsertAccumulator(parameters, divideProcessing);
            upsertAccumulator.charge(upserts);

            try {
                List<UpsertRelationsDTO> result = relationsComponent.batchUpsertRelations(upsertAccumulator);

                long inserted = result.stream()
                        .flatMap(dto -> dto.getRelations().values().stream())
                        .flatMap(Collection::stream)
                        .filter(dto -> dto.getAction() == UpsertAction.INSERT)
                        .count();

                long updated = result.stream()
                        .flatMap(dto -> dto.getRelations().values().stream())
                        .flatMap(Collection::stream)
                        .filter(dto -> dto.getAction() == UpsertAction.UPDATE)
                        .count();

                long skept = submitted - (inserted + updated);

                parameters.incrementInserted(inserted);
                parameters.incrementUpdated(updated);
                parameters.incrementSkept(skept);

            } catch (Exception e) {

                String message = getErrorMessage(e);
                parameters.incrementFailed(submitted);

                LOGGER.error("Error during upsert records. {}", message);
            } finally {
                upsertAccumulator.discharge();
            }
        }

        if (CollectionUtils.isNotEmpty(deletes)) {

            boolean divideProcessing = batchSetSize == BatchSetSize.LARGE;

            BatchSetAccumulator<DeleteRelationsRequestContext> deleteAccumulator = getRelationsDeleteAccumulator(parameters, divideProcessing);
            deleteAccumulator.charge(deletes);

            try {
                List<DeleteRelationsDTO> result = relationsComponent.batchDeleteRelations(deleteAccumulator);
            } catch (Exception e) {
                String message = getErrorMessage(e);
                LOGGER.error("Error during upsert records. {}", message);
            } finally {
                deleteAccumulator.discharge();
            }
        }
    }

    /**
     * Gets accumulator instance.
     *
     * @param parameters parameters
     * @param isLookup
     * @param divideProcessing
     * @return
     */
    private BatchSetAccumulator<UpsertRequestContext> getRecordsUpsertAccumulator(
            ImportDataJobStepExecutionState parameters, boolean isLookup, boolean divideProcessing) {
        // No separate tables required for small sets
        // Those go to working tables directly
        RecordUpsertBatchSetAccumulator recordsAccumulator
                = (RecordUpsertBatchSetAccumulator) accumulators.computeIfAbsent(AccumulatorType.UPSERT_RECORDS, k ->
                new RecordUpsertBatchSetAccumulator((int) blockSize, batchSetSize == BatchSetSize.LARGE ? recordTargets : null, parameters.getIdCache()));

        // Lookups must be inserted, indexed and refreshed inplace
        recordsAccumulator.setBatchSetSize(isLookup ? BatchSetSize.SMALL : batchSetSize);
        recordsAccumulator.setSupportedIterationTypes(divideProcessing || skipIndexing
                ? Collections.singletonList(BatchSetIterationType.UPSERT_ORIGINS)
                : Arrays.asList(BatchSetIterationType.UPSERT_ORIGINS, BatchSetIterationType.UPSERT_ETALONS));

        return recordsAccumulator;
    }

    /**
     * Gets accumulator instance.
     *
     * @param parameters parameters
     * @param isLookup
     * @param divideProcessing
     * @return
     */
    private BatchSetAccumulator<DeleteRequestContext> getRecordsDeleteAccumulator(
            ImportDataJobStepExecutionState parameters, boolean divideProcessing) {
        // No separate tables required for small sets
        // Those go to working tables directly
        RecordDeleteBatchSetAccumulator recordsAccumulator
                = (RecordDeleteBatchSetAccumulator) accumulators.computeIfAbsent(AccumulatorType.DELETE_RECORDS, k ->
                new RecordDeleteBatchSetAccumulator((int) blockSize, batchSetSize == BatchSetSize.LARGE ? recordTargets : null));

        recordsAccumulator.setBatchSetSize(batchSetSize);
        recordsAccumulator.setSupportedIterationTypes(divideProcessing || skipIndexing
                ? Collections.singletonList(BatchSetIterationType.DELETE_ORIGINS)
                : Arrays.asList(BatchSetIterationType.DELETE_ORIGINS, BatchSetIterationType.DELETE_ETALONS));

        return recordsAccumulator;
    }

    /**
     * Gets classifiers upsert accumulator.
     *
     * @param parameters parameters
     * @return accumulator
     */
    private BatchSetAccumulator<UpsertClassifiersDataRequestContext> getClassifiersUpsertAccumulator(
            ImportDataJobStepExecutionState parameters, boolean divideProcessing) {
        // No separate tables required for small sets
        // Those go to working tables directly
        ClassifierUpsertBatchSetAccumulator classifiersAccumulator
                = (ClassifierUpsertBatchSetAccumulator) accumulators.computeIfAbsent(AccumulatorType.UPSERT_CLASSIFIERS, k ->
                new ClassifierUpsertBatchSetAccumulator((int) blockSize, batchSetSize == BatchSetSize.LARGE ? classifierTargets : null));

        classifiersAccumulator.setBatchSetSize(batchSetSize);
        classifiersAccumulator.setSupportedIterationTypes(divideProcessing || skipIndexing
                ? Collections.singletonList(BatchSetIterationType.UPSERT_ORIGINS)
                : Arrays.asList(BatchSetIterationType.UPSERT_ORIGINS, BatchSetIterationType.UPSERT_ETALONS));

        return classifiersAccumulator;
    }

    /**
     * Gets classifiers delete accumulator.
     *
     * @param parameters parameters
     * @return accumulator
     */
    private BatchSetAccumulator<DeleteClassifiersDataRequestContext> getClassifiersDeleteAccumulator(
            ImportDataJobStepExecutionState parameters, boolean divideProcessing) {
        // No separate tables required for small sets
        // Those go to working tables directly
        ClassifiersDeleteBatchSetAccumulator classifiersAccumulator
                = (ClassifiersDeleteBatchSetAccumulator) accumulators.computeIfAbsent(AccumulatorType.DELETE_CLASSIFIERS, k ->
                new ClassifiersDeleteBatchSetAccumulator((int) blockSize, batchSetSize == BatchSetSize.LARGE ? classifierTargets : null));

        classifiersAccumulator.setBatchSetSize(batchSetSize);
        classifiersAccumulator.setSupportedIterationTypes(divideProcessing || skipIndexing
                ? Collections.singletonList(BatchSetIterationType.DELETE_ORIGINS)
                : Arrays.asList(BatchSetIterationType.DELETE_ORIGINS, BatchSetIterationType.DELETE_ETALONS));

        return classifiersAccumulator;
    }

    /**
     * Creates relations accumulator, if necessary, and does basic setup.
     *
     * @param parameters the parameters
     * @param divideProcessing divide processing
     * @return accumulator
     */
    private BatchSetAccumulator<UpsertRelationsRequestContext> getRelationsUpsertAccumulator(
            ImportDataJobStepExecutionState parameters, boolean divideProcessing) {

        boolean isContainment = parameters.exchangeObjectIsContainmentRelation();
        List<BatchSetIterationType> iterationTypes = divideProcessing || skipIndexing
                ? Collections.singletonList(BatchSetIterationType.UPSERT_ORIGINS)
                : Arrays.asList(BatchSetIterationType.UPSERT_ORIGINS, BatchSetIterationType.UPSERT_ETALONS);

        // No separate tables required for small sets
        // Those go to working tables directly
        // Set run id null for small sets causing writes to working tables
        RelationUpsertBatchSetAccumulator relationsAccumulator
                = (RelationUpsertBatchSetAccumulator) accumulators.computeIfAbsent(AccumulatorType.UPSERT_RELATIONS, k ->
                new RelationUpsertBatchSetAccumulator((int) blockSize,
                        batchSetSize == BatchSetSize.LARGE ? relationTargets : null,
                        parameters.getIdCache(),
                        isContainment,
                        batchSetSize == BatchSetSize.LARGE ? recordTargets : null));

        relationsAccumulator.setBatchSetSize(batchSetSize);
        relationsAccumulator.setSupportedIterationTypes(iterationTypes);

        // UDSUE-377
        if (isContainment) {
            relationsAccumulator
                    .getRecordBatchSetAccumulator()
                    .setSupportedIterationTypes(iterationTypes);
        }

        return relationsAccumulator;
    }

    /**
     * Creates relations accumulator, if necessary, and does basic setup.
     *
     * @param parameters the parameters
     * @param divideProcessing divide processing
     * @return accumulator
     */
    private BatchSetAccumulator<DeleteRelationsRequestContext> getRelationsDeleteAccumulator(
            ImportDataJobStepExecutionState parameters, boolean divideProcessing) {
        // No separate tables required for small sets
        // Those go to working tables directly
        // Set run id null for small sets causing writes to working tables
        RelationDeleteBatchSetAccumulator relationsAccumulator
                = (RelationDeleteBatchSetAccumulator) accumulators.computeIfAbsent(AccumulatorType.DELETE_RELATIONS, k ->
                new RelationDeleteBatchSetAccumulator((int) blockSize,
                        batchSetSize == BatchSetSize.LARGE ? relationTargets : null,
                        parameters.exchangeObjectIsContainmentRelation(),
                        batchSetSize == BatchSetSize.LARGE ? recordTargets : null));

        relationsAccumulator.setBatchSetSize(batchSetSize);
        relationsAccumulator.setSupportedIterationTypes(divideProcessing || skipIndexing
                ? Collections.singletonList(BatchSetIterationType.DELETE_ORIGINS)
                : Arrays.asList(BatchSetIterationType.DELETE_ORIGINS, BatchSetIterationType.DELETE_ETALONS));

        return relationsAccumulator;
    }

    /**
     * Collects relations contexts
     *
     * @param sets the sets to process
     * @return result map
     */
    @SuppressWarnings("unchecked")
    private Map<CollectedRelationContexts, List<?>> repackageRelationsResult(List<ImportRelationSet> sets) {

        Map<CollectedRelationContexts, List<?>> result = new EnumMap<>(CollectedRelationContexts.class);
        for (ImportRelationSet set : sets) {

            if (Objects.nonNull(set.getRelationsDelete())) {
                ((List<DeleteRelationsRequestContext>) result
                        .computeIfAbsent(CollectedRelationContexts.DELETE_CONTEXTS,
                                key -> new ArrayList<DeleteRelationsRequestContext>(sets.size())))
                        .add(set.getRelationsDelete());

            } else if (Objects.nonNull(set.getRelationsUpsert())) {
                ((List<UpsertRelationsRequestContext>) result
                        .computeIfAbsent(CollectedRelationContexts.UPSERT_CONTEXTS,
                                key -> new ArrayList<UpsertRelationsRequestContext>(sets.size())))
                        .add(set.getRelationsUpsert());
            }
        }

        return result;
    }

    /**
     * Collects records contexts
     *
     * @param sets the sets to process
     * @return result map
     */
    @SuppressWarnings("unchecked")
    private Map<CollectedRecordContexts, List<?>> repackageRecordsResult(List<ImportRecordSet> sets) {

        Map<CollectedRecordContexts, List<?>> result = new EnumMap<>(CollectedRecordContexts.class);
        for (ImportRecordSet set : sets) {

            if (Objects.nonNull(set.getRecordDelete())) {
                ((List<DeleteRequestContext>) result
                        .computeIfAbsent(CollectedRecordContexts.DELETE_RECORD_CONTEXTS,
                                key -> new ArrayList<DeleteRequestContext>(sets.size())))
                        .add(set.getRecordDelete());
            } else if (Objects.nonNull(set.getRecordUpsert())) {
                ((List<UpsertRequestContext>) result
                        .computeIfAbsent(CollectedRecordContexts.UPSERT_RECORD_CONTEXTS,
                                key -> new ArrayList<UpsertRequestContext>(sets.size())))
                        .add(set.getRecordUpsert());
            }

            if (Objects.nonNull(set.getClassifiersDelete())) {
                ((List<DeleteClassifiersDataRequestContext>) result
                        .computeIfAbsent(CollectedRecordContexts.DELETE_CLASSIFIER_CONTEXTS,
                                key -> new ArrayList<DeleteClassifiersDataRequestContext>(sets.size())))
                        .add(set.getClassifiersDelete());
            }

            // Will be set after records processing
            if (Objects.nonNull(set.getClassifiersUpsert())) {
                ((List<UpsertClassifiersDataRequestContext>) result
                        .computeIfAbsent(CollectedRecordContexts.UPSERT_CLASSIFIER_CONTEXTS,
                                key -> new ArrayList<UpsertClassifiersDataRequestContext>(sets.size())))
                        .add(set.getClassifiersUpsert());
            }
        }

        return result;
    }
}
