package com.unidata.mdm.backend.service.job.exchange.in;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.context.ClassifierIdentityContext;
import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.CommonSendableContext;
import com.unidata.mdm.backend.common.context.DeleteClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.DeleteClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationsRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.keys.ReferenceAliasKey;
import com.unidata.mdm.backend.common.types.OriginClassifier;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.ExchangeRelation;
import com.unidata.mdm.backend.exchange.def.RelatesToRelation;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.exchange.def.db.DbRelatesToRelation;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportDataSet;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportRecordSet;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportRelationSet;

/**
 * Map record to upsert context
 */
@StepScope
public class ImportDataJobItemProcessor implements ItemProcessor<ImportDataSet, ImportDataSet> {
    /**
     * Operation id
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_OPERATION_ID + "]}")
    private String operationId;
    /**
     * Audit level.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_AUDIT_LEVEL + "]}")
    private Long auditLevel;
    /**
     * Offset.
     */
    @Value("#{stepExecutionContext[" + ImportDataJobConstants.PARAM_OFFSET + "]}")
    private Integer offset;
    /**
     * Skip DQ.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_SKIP_DQ + "]}")
    private boolean skipDQ;
    /**
     * Skip matching.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_SKIP_MATCHING + "]}")
    private boolean skipMatching;
    /**
     * Skip notifications.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_SKIP_NOTIFICATIONS + "]}")
    private boolean skipNotifications;
    /**
     * No updates expected (TEST).
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_INITIAL_LOAD + "]}")
    private boolean initialLoad;
    /**
     * Merge with prev. version parameter.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_MERGE_WITH_PREVIOUS_VERSION + "]}")
    private boolean mergeWithPreviousVersion;

    /**
     * Parameter used to set createDate for all imported items.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_START_TIMESTAMP + "]}")
    private String jobStartTimestamp;

    @Override
    public ImportDataSet process(ImportDataSet item) throws Exception {

        if (item == null || item == ImportDataJobConstants.DUMMY_RECORD) {
            return null;
        }

        ImportDataJobStepExecutionState state = ImportDataJobUtils.getStepState();
        if (item.isRecord()) {
            processRecord((ImportRecordSet) item, state);
        } else {
            processRelation((ImportRelationSet) item, state);
        }

        return item;
    }
    /**
     * Creates data upsert context.
     * @param item the item
     * @param entity the exchange entity
     * @return context
     */
    private void processRecord(@Nonnull ImportRecordSet item, ImportDataJobStepExecutionState parameters) {

        DbExchangeEntity entity = parameters.getExchangeObject();
        if (entity.isProcessRecords()) {
            processRecordData(item, parameters);
        }

        if (entity.isProcessClassifiers()) {
            processClassifiersData(item, parameters);
        }
    }

    /**
     * Process standalone classifiers data.
     * @param item
     * @param parameters
     * @return
     */
    private void processClassifiersData(@Nonnull ImportRecordSet item, ImportDataJobStepExecutionState parameters) {

        DbExchangeEntity entity = parameters.getExchangeObject();

        RecordKeys keys = null;
        if (initialLoad) {

            keys = RecordKeys.builder()
                .etalonKey(item.getEtalonKey())
                .originKey(item.getOriginKey())
                .entityName(entity.getName())
                .etalonStatus(RecordStatus.ACTIVE)
                .originStatus(RecordStatus.ACTIVE)
                .build();
        }

        Map<String, List<UpsertClassifierDataRequestContext>> upserts = new HashMap<>();
        Map<String, List<DeleteClassifierDataRequestContext>> deletes = new HashMap<>();
        if (CollectionUtils.isNotEmpty(item.getClassifiers())) {

            item.getClassifiers().stream()
                .map(cls -> processSingleClassifierRecord(cls, entity, item))
                .filter(Objects::nonNull)
                .forEach(result -> {
                    if (result instanceof UpsertClassifierDataRequestContext) {
                        UpsertClassifierDataRequestContext uCtx = (UpsertClassifierDataRequestContext) result;
                        uCtx.putToStorage(StorageId.IMPORT_RECORD_SOURCE, entity.getTables().toString());
                        upserts.computeIfAbsent(uCtx.getClassifierName(), k -> new ArrayList<>()).add(uCtx);
                    } else {

                        DeleteClassifierDataRequestContext dCtx = (DeleteClassifierDataRequestContext) result;
                        dCtx.putToStorage(StorageId.IMPORT_RECORD_SOURCE, entity.getTables().toString());
                        deletes.computeIfAbsent(dCtx.getClassifierName(), k -> new ArrayList<>()).add(dCtx);
                    }
                });
        }

        if (MapUtils.isNotEmpty(upserts)) {

            UpsertClassifiersDataRequestContext ctx = UpsertClassifiersDataRequestContext.builder()
                    .originKey(item.getOriginKey())
                    .etalonKey(item.getEtalonKey())
                    .classifiers(upserts)
                    .build();

            ctx.putToStorage(ctx.keysId(), keys);
            item.setClassifiersUpsert(ctx);
        } else if (MapUtils.isNotEmpty(deletes)) {

            DeleteClassifiersDataRequestContext ctx = DeleteClassifiersDataRequestContext.builder()
                    .originKey(item.getOriginKey())
                    .etalonKey(item.getEtalonKey())
                    .classifiers(deletes)
                    .build();

            ctx.putToStorage(ctx.keysId(), keys);
            item.setClassifiersDelete(ctx);
        }
    }
    /**
     * Processes record data specifically.
     * @param item record item
     * @param parameters parameters
     */
    private void processRecordData(ImportRecordSet item, ImportDataJobStepExecutionState parameters) {

        DbExchangeEntity entity = parameters.getExchangeObject();

        Date fromDate = getFrom(item, parameters);
        Date toDate = getTo(item, parameters);

        RecordStatus versionStatus = item.getStatus() == null ? RecordStatus.ACTIVE : item.getStatus();

        // Was actually a period delete
        CommonSendableContext context;
        if (versionStatus == RecordStatus.INACTIVE) {

            item.setRecordDelete(DeleteRequestContext.builder()
                .originKey(item.getOriginKey())
                .etalonKey(item.getEtalonKey())
                .sourceSystem(entity.getSourceSystem())
                .entityName(entity.getName())
                .validFrom(fromDate)
                .validTo(toDate)
                .inactivatePeriod(true)
                .auditLevel(auditLevel != null ? auditLevel.shortValue() : AuditLevel.AUDIT_SUCCESS)
                .batchUpsert(true)
                .build());

            context = item.getRecordDelete();

        } else {

            UpsertRequestContext uCtx = UpsertRequestContext.builder()
                    .record(item.getData())
                    .originKey(item.getOriginKey())
                    .etalonKey(item.getEtalonKey())
                    .validFrom(fromDate)
                    .validTo(toDate)
                    .sourceSystem(entity.getSourceSystem())
                    .entityName(entity.getName())
                    .skipCleanse(entity.isSkipCleanse() || skipDQ)
                    .mergeWithPrevVersion(mergeWithPreviousVersion)
                    .initialLoad(initialLoad)
                    .skipMatching(skipMatching) // In-line matching is not supported in batch mode.
                    .auditLevel(auditLevel != null ? auditLevel.shortValue() : AuditLevel.AUDIT_SUCCESS)
                    .codeAttributeAliases(parameters.getCodeAttributeAliases())
                    .returnEtalon(true)
                    .batchUpsert(true)
                    .build();

            // Use one timestamp for all imported records.
            uCtx.putToStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP, new Date(Long.parseLong(jobStartTimestamp)));

            item.setRecordUpsert(uCtx);

            // Add keys to skip resolution
            if (initialLoad) {

                RecordKeys keys = RecordKeys.builder()
                    .etalonKey(item.getEtalonKey())
                    .originKey(item.getOriginKey())
                    .entityName(entity.getName())
                    .etalonStatus(RecordStatus.ACTIVE)
                    .originStatus(RecordStatus.ACTIVE)
                    .build();

                uCtx.putToStorage(uCtx.keysId(), keys);
            }

            context = item.getRecordUpsert();
        }

        if (skipNotifications || entity.isSkipNotifications()) {
            context.skipNotification();
        }

        Integer numberInSet = offset + item.getImportRowNum();
        context.putToStorage(StorageId.IMPORT_ROW_NUM, numberInSet);
        context.putToStorage(StorageId.IMPORT_RECORD_SOURCE, entity.getTables().toString());
        context.setOperationId(operationId);
    }
    /**
     * Processes relation data specifically.
     * @param item relation item
     * @param parameters parameters
     */
    private void processRelation(ImportRelationSet item, ImportDataJobStepExecutionState parameters) {

        ExchangeRelation relation = parameters.getExchangeObject();

        Date fromDate = getFrom(item, parameters);
        Date toDate = getTo(item, parameters);

        ReferenceAliasKey referenceAliasKey = null;
        String toSourceSystem;
        String toSourceTables;

        if (relation.isContainment()) {

            DbExchangeEntity containment = (DbExchangeEntity) ((ContainmentRelation) relation).getEntity();
            toSourceSystem = containment.getSourceSystem();
            toSourceTables = containment.getTables().toString();

        } else {

            RelatesToRelation relTo = (RelatesToRelation) relation;
            toSourceSystem = relTo.getToSourceSystem();
            toSourceTables = ((DbRelatesToRelation) relTo).getTables().toString();

            if (StringUtils.isNotBlank(relTo.getToEntityAttributeName())) {
               referenceAliasKey = Objects.nonNull(item.getToOriginKey()) && Objects.nonNull(item.getToOriginKey().getExternalId())
                       ? ReferenceAliasKey.builder()
                               .value(item.getToOriginKey().getExternalId())
                               .entityAttributeName(relTo.getToEntityAttributeName())
                               .build()
                       : null;
            }
        }

        Integer numberInSet = offset + item.getImportRowNum();
        RecordStatus versionStatus = item.getStatus() == null ? RecordStatus.ACTIVE : item.getStatus();
        CommonRequestContext context;
        if (versionStatus == RecordStatus.INACTIVE) {

            DeleteRelationRequestContext dCtx = DeleteRelationRequestContext.builder()
                    .sourceSystem(toSourceSystem)
                    .originKey(item.getToOriginKey())
                    .etalonKey(item.getToEtalonKey())
                    .validFrom(fromDate)
                    .validTo(toDate)
                    .batchUpsert(true)
                    .inactivatePeriod(true)
                    .auditLevel(auditLevel != null ? auditLevel.shortValue() : AuditLevel.AUDIT_SUCCESS)
                    .build();

            dCtx.putToStorage(StorageId.IMPORT_ROW_NUM, numberInSet);
            dCtx.putToStorage(StorageId.IMPORT_RECORD_SOURCE, toSourceTables);
            dCtx.setOperationId(operationId);
            if (skipNotifications) {
                dCtx.skipNotification();
            }

            // Instantiate normal list to support remove iterator
            List<DeleteRelationRequestContext> values = new ArrayList<>(1);
            values.add(dCtx);

            DeleteRelationsRequestContext rCtx = DeleteRelationsRequestContext.builder()
                    .originKey(item.getFromOriginKey())
                    .etalonKey(item.getFromEtalonKey())
                    .entityName(parameters.getFromEntityName()) // Reset from definition
                    .sourceSystem(parameters.getFromSourceSystem()) // Reset from definition
                    .relations(Collections.singletonMap(item.getRelationName(), values))
                    .build();

            item.setRelationsDelete(rCtx);
            context = rCtx;

        } else {

            UpsertRelationRequestContext uCtx = UpsertRelationRequestContext.builder()
                    .relation(item.getData())
                    .sourceSystem(toSourceSystem)
                    .originKey(item.getToOriginKey())
                    .etalonKey(item.getToEtalonKey())
                    .validFrom(fromDate)
                    .validTo(toDate)
                    .referenceAliasKey(referenceAliasKey)
                    .batchUpsert(true)
                    .initialLoad(initialLoad)
                    .auditLevel(auditLevel != null ? auditLevel.shortValue() : AuditLevel.AUDIT_SUCCESS)
                    .build();

            uCtx.putToStorage(StorageId.IMPORT_ROW_NUM, numberInSet);
            uCtx.putToStorage(StorageId.IMPORT_RECORD_SOURCE, toSourceTables);
            uCtx.setOperationId(operationId);
            if (skipNotifications) {
                uCtx.skipNotification();
            }

            // Instantiate normal list to support remove iterator
            List<UpsertRelationRequestContext> values = new ArrayList<>(1);
            values.add(uCtx);

            UpsertRelationsRequestContext rCtx = UpsertRelationsRequestContext.builder()
                    .originKey(item.getFromOriginKey())
                    .etalonKey(item.getFromEtalonKey())
                    .entityName(parameters.getFromEntityName()) // Reset from definition
                    .sourceSystem(parameters.getFromSourceSystem()) // Reset from definition
                    .relations(Collections.singletonMap(item.getRelationName(), values))
                    .build();

            item.setRelationsUpsert(rCtx);
            context = rCtx;
        }

        context.setOperationId(operationId);
    }
    /**
     * Gets from date either from the import set or from parameters.
     * @param set the set
     * @param parameters the parameters
     * @return date
     */
    private Date getFrom(ImportDataSet set, ImportDataJobStepExecutionState parameters) {

        Date result;
        if(set.getValidFrom() == null){
            result = parameters.getFrom();
        } else {
            if(parameters.getFrom() == null){
                result = set.getValidFrom();
            } else {
                result = set.getValidFrom().before(parameters.getFrom()) ? parameters.getFrom() : set.getValidFrom();
            }
        }
        return result;
    }
    /**
     * Gets to date either from the import set or from parameters.
     * @param set the set
     * @param parameters the parameters
     * @return date
     */
    private Date getTo(ImportDataSet set, ImportDataJobStepExecutionState parameters) {

        Date result;
        if(set.getValidTo() == null){
            result = parameters.getTo();
        } else {
            if(parameters.getTo() == null){
                result = set.getValidTo();
            } else {
                result = set.getValidTo().after(parameters.getTo()) ? parameters.getTo() : set.getValidTo();
            }
        }
        return result;
    }
    /**
     * Does process a classifier data record.
     * @param classifier the record
     * @param entity exchange entity
     * @param item the item
     * @return context
     */
    private ClassifierIdentityContext processSingleClassifierRecord(OriginClassifier classifier, DbExchangeEntity entity, ImportRecordSet item) {

        RecordStatus status = classifier.getInfoSection().getStatus() == null
                ? RecordStatus.ACTIVE
                : classifier.getInfoSection().getStatus();

        ClassifierIdentityContext result = null;
        if (status == RecordStatus.ACTIVE) {
            result = UpsertClassifierDataRequestContext.builder()
                .classifier(classifier)
                .classifierName(classifier.getInfoSection().getClassifierName())
                .classifierNodeId(classifier.getInfoSection().getNodeId())
                .status(status)
                .initialLoad(initialLoad)
                .auditLevel(auditLevel != null ? auditLevel.shortValue() : AuditLevel.AUDIT_SUCCESS)
                .batchUpsert(true)
                .build();
        } else if (status == RecordStatus.INACTIVE) {
            result = DeleteClassifierDataRequestContext.builder()
                .batchUpsert(true)
                .classifierName(classifier.getInfoSection().getClassifierName())
                .classifierNodeId(classifier.getInfoSection().getNodeId())
                .auditLevel(auditLevel != null ? auditLevel.shortValue() : AuditLevel.AUDIT_SUCCESS)
                .inactivateEtalon(true)
                .build();
        }

        if (Objects.nonNull(result)) {

            CommonSendableContext context = (CommonSendableContext) result;
            if (skipNotifications || entity.isSkipNotifications()) {
                context.skipNotification();
            }

            Integer numberInSet = offset + item.getImportRowNum();
            context.putToStorage(StorageId.IMPORT_ROW_NUM, numberInSet);
            context.setOperationId(operationId);
        }

        return result;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
    /**
     * @param offset the offset to set
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
