package com.unidata.mdm.backend.service.data.classifiers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.context.ContextUtils;
import com.unidata.mdm.backend.common.context.DeleteClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.DeleteClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.GetClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.GetClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.dto.DeleteClassifierDTO;
import com.unidata.mdm.backend.common.dto.DeleteClassifiersDTO;
import com.unidata.mdm.backend.common.dto.GetClassifierDTO;
import com.unidata.mdm.backend.common.dto.GetClassifiersDTO;
import com.unidata.mdm.backend.common.dto.UpsertClassifierDTO;
import com.unidata.mdm.backend.common.dto.UpsertClassifiersDTO;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.dao.ClassifiersDAO;
import com.unidata.mdm.backend.po.EtalonClassifierPO;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.data.batch.BatchIterator;
import com.unidata.mdm.backend.service.data.batch.BatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.BatchSetIterationType;
import com.unidata.mdm.backend.service.data.batch.BatchSetSize;
import com.unidata.mdm.backend.service.data.batch.ClassifierBatchSetProcessor;
import com.unidata.mdm.backend.service.data.batch.ClassifierUpsertBatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.ClassifiersDeleteBatchSetAccumulator;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;

/**
 * @author Mikhail Mikhailov
 * Classifiers _data_ service component.
 */
@Component
public class ClassifiersServiceComponent {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifiersServiceComponent.class);
    /**
     * Classifier data DAO.
     */
    @Autowired
    private ClassifiersDAO classifierDAO;
    /**
     * CVC.
     */
    @Autowired
    private ClassifiersValidationComponent classifiersValidationComponent;
    /**
     * CDC.
     */
    @Autowired
    private ClassifiersDataComponent classifiersDataComponent;
    /**
     * MMS.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * Audit writer.
     */
    @Autowired
    private AuditEventsWriter auditEventsWriter;
    /**
     * Classifiers batch set processor.
     */
    @Autowired
    private ClassifierBatchSetProcessor batchSetProcessor;
    /**
     * Constructor.
     */
    public ClassifiersServiceComponent() {
        super();
    }

    /**
     * Merge support.
     *
     * @param master
     * @param duplicates
     * @param operationId the operation id
     */
    @Transactional
    public void mergeClassifiers(RecordKeys master, List<RecordKeys> duplicates, String operationId) {

        List<String> duplicateIds = duplicates.stream()
                .map(RecordKeys::getEtalonKey)
                .map(EtalonKey::getId)
                .collect(Collectors.toList());

        // 1. Set status 'MERGED' to etalon relation records, having IDs in 'from_record'
        int count = classifierDAO.markEtalonClassifiersMerged(duplicateIds, operationId);
        LOGGER.debug("Merged {} number of classifier records, merged to {} etalon id.", count,
                master.getEtalonKey().getId());
    }

    /**
     * Deletes a classified data by single context.
     * @param ctx the context
     * @return dto
     */
    @Transactional
    public DeleteClassifierDTO deleteClassifier(DeleteClassifierDataRequestContext ctx) {
        MeasurementPoint.start();
        try {
            return classifiersDataComponent.delete(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Deletes a classifier data set.
     * @param ctxts the contexts
     * @return dtos
     */
    @Transactional
    public DeleteClassifiersDTO deleteClassifiers(List<DeleteClassifierDataRequestContext> ctxts) {
        MeasurementPoint.start();
        try {

            Map<String, List<DeleteClassifierDTO>> deleted = new HashMap<>();
            if (!CollectionUtils.isEmpty(ctxts)) {
                for (DeleteClassifierDataRequestContext ctx : ctxts) {

                    DeleteClassifierDTO result = deleteClassifier(ctx);
                    if (Objects.isNull(result)) {
                        continue;
                    }

                    deleted.computeIfAbsent(result.getClassifierKeys().getName(), k -> new ArrayList<>()).add(result);
                }
            }

            return new DeleteClassifiersDTO(deleted);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Deletes a classifier data set.
     * @param ctx the classifiers context
     * @return dtos
     */
    @Transactional
    public DeleteClassifiersDTO deleteClassifiers(DeleteClassifiersDataRequestContext ctx) {
        MeasurementPoint.start();
        try {

            // 1. Ensure from key and possibly do other stuff, common to collecting conetxts
            classifiersValidationComponent.before(ctx);

            // 2. Delete classifiers by classifier name
            Map<String, List<DeleteClassifierDTO>> deleted = new HashMap<>();
            if (!MapUtils.isEmpty(ctx.getClassifiers())) {
                for (Entry<String, List<DeleteClassifierDataRequestContext>> entry : ctx.getClassifiers().entrySet()) {

                    for (DeleteClassifierDataRequestContext i : entry.getValue()) {

                        ContextUtils.storageCopy(ctx, i, ctx.keysId());
                        DeleteClassifierDTO result = deleteClassifier(i);
                        if (Objects.isNull(result)) {
                            continue;
                        }

                        deleted.computeIfAbsent(entry.getKey(), k -> new ArrayList<>(entry.getValue().size())).add(result);
                    }
                }
            } else if (!CollectionUtils.isEmpty(ctx.getClassifierNames())) {

                RecordKeys keys = ctx.keys();
                if (keys != null) {

                    for (String name : ctx.getClassifierNames()) {
                        // TODO implement mass key fetch
                        List<EtalonClassifierPO> toEtalons = classifierDAO.loadClassifierEtalons(keys.getEtalonKey().getId(), name, null);
                        if (CollectionUtils.isEmpty(toEtalons)) {
                            continue;
                        }

                        List<DeleteClassifierDataRequestContext> dCtxts = toEtalons.stream()
                                .map(po -> {
                                    DeleteClassifierDataRequestContext dCtx = DeleteClassifierDataRequestContext.builder()
                                            .classifierEtalonKey(po.getId())
                                            .approvalState(ctx.getApprovalState())
                                            .auditLevel(ctx.getAuditLevel())
                                            .batchUpsert(ctx.isBatchUpsert())
                                            .inactivateEtalon(ctx.isInactivateEtalon())
                                            .inactivateOrigin(ctx.isInactivateOrigin())
                                            .suppressAudit(ctx.isSuppressAudit())
                                            .wipe(ctx.isWipe())
                                            .workflowAction(ctx.isWorkflowAction())
                                            .build();

                                    dCtx.setOperationId(ctx.getOperationId());
                                    ContextUtils.storageCopy(ctx, dCtx, ctx.keysId());
                                    return dCtx;
                                })
                                .collect(Collectors.toList());

                        DeleteClassifiersDTO result = deleteClassifiers(dCtxts);
                        if (Objects.nonNull(result)) {
                            deleted.putAll(result.getClassifiers());
                        }
                    }
                }
            }

            // 3. Run post-check
            classifiersValidationComponent.after(ctx);

            return new DeleteClassifiersDTO(deleted);
        } finally {
            MeasurementPoint.stop();
        }
    }
    public List<DeleteClassifiersDTO> batchDeleteClassifiers(List<DeleteClassifiersDataRequestContext> ctxs){
        BatchSetAccumulator<DeleteClassifiersDataRequestContext> accumulator = getDefaultClassifierDeleteAccumulator();
        accumulator.charge(ctxs);
        List<DeleteClassifiersDTO> result;
        try {
            result = batchDeleteClassifiers(accumulator);
        } finally {
            accumulator.discharge();
        }
        return result;
    }

    /**
     * Does batch delete for classifier data records.
     * @param accumulator the accumulator
     * @return result
     */
    public List<DeleteClassifiersDTO> batchDeleteClassifiers(BatchSetAccumulator<DeleteClassifiersDataRequestContext> accumulator) {

        MeasurementPoint.start();
        try {

            if (accumulator.getSupportedIterationTypes().contains(BatchSetIterationType.DELETE_ORIGINS)) {
                batchDeleteOrigins(accumulator);
            }

            if (accumulator.getSupportedIterationTypes().contains(BatchSetIterationType.DELETE_ETALONS)) {
                batchDeleteEtalons(accumulator);
            }

            List<DeleteClassifiersDataRequestContext> workingCopy = accumulator.workingCopy();
            List<DeleteClassifiersDTO> result = new ArrayList<>(workingCopy.size());

            workingCopy.stream()
                .map(ctx -> {
                    Map<String, List<DeleteClassifierDTO>> collected = ctx.getClassifiers().entrySet().stream()
                        .filter(entry -> CollectionUtils.isNotEmpty(entry.getValue()))
                        .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().stream()
                                .map(uCtx -> classifiersDataComponent.deleteContextToResult(uCtx))
                                .filter(Objects::nonNull)
                                .filter(dto -> dto.getClassifierKeys() != null)
                                .collect(Collectors.toList())));

                    return MapUtils.isEmpty(collected) ? null : new DeleteClassifiersDTO(collected);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(() -> result));

            return result;

        } catch (Exception exc) {
            LOGGER.warn("Batch UPSERT caught an exception", exc);
            throw exc;
        } finally {
            MeasurementPoint.stop();
        }
    }

    public List<UpsertClassifiersDTO> batchUpsertClassifiers(List<UpsertClassifiersDataRequestContext> ctxs){
        BatchSetAccumulator<UpsertClassifiersDataRequestContext> accumulator = getDefaultClassifierUpsertAccumulator();
        accumulator.charge(ctxs);
        List<UpsertClassifiersDTO> result;
        try {
            result = batchUpsertClassifiers(accumulator);
        } finally {
            accumulator.discharge();
        }
        return result;
    }

    /**
     * Doas batch upsert for classifier data records.
     * @param accumulator the accumulator
     * @return result
     */
    public List<UpsertClassifiersDTO> batchUpsertClassifiers(BatchSetAccumulator<UpsertClassifiersDataRequestContext> accumulator) {

        MeasurementPoint.start();
        try {

            if (accumulator.getSupportedIterationTypes().contains(BatchSetIterationType.UPSERT_ORIGINS)) {
                batchUpsertOrigins(accumulator);
            }

            if (accumulator.getSupportedIterationTypes().contains(BatchSetIterationType.UPSERT_ETALONS)) {
                batchUpsertEtalons(accumulator);
            }

            List<UpsertClassifiersDataRequestContext> workingCopy = accumulator.workingCopy();
            List<UpsertClassifiersDTO> result = new ArrayList<>(workingCopy.size());

            workingCopy.stream()
                .map(ctx -> {
                    Map<String, List<UpsertClassifierDTO>> collected = ctx.getClassifiers().entrySet().stream()
                        .filter(entry -> CollectionUtils.isNotEmpty(entry.getValue()))
                        .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().stream()
                                .map(uCtx -> classifiersDataComponent.upsertContextToResult(uCtx))
                                .filter(Objects::nonNull)
                                .filter(dto -> dto.getClassifierKeys() != null)
                                .collect(Collectors.toList())));

                    return MapUtils.isEmpty(collected) ? null : new UpsertClassifiersDTO(collected);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(() -> result));

            return result;

        } catch (Exception exc) {
            LOGGER.warn("Batch UPSERT caught an exception", exc);
            throw exc;
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Does origins batch upsert.
     * @param accumulator the accumulator
     */
    private void batchUpsertOrigins(BatchSetAccumulator<UpsertClassifiersDataRequestContext> accumulator) {

        // 1. Collect data
        for (BatchIterator<UpsertClassifiersDataRequestContext> bi = accumulator.iterator(BatchSetIterationType.UPSERT_ORIGINS); bi.hasNext(); ) {

            UpsertClassifiersDataRequestContext ctx = bi.next();
            try {

                classifiersValidationComponent.before(ctx);
                for (Entry<String, List<UpsertClassifierDataRequestContext>> entry : ctx.getClassifiers().entrySet()) {

                    for (Iterator<UpsertClassifierDataRequestContext> li = entry.getValue().iterator();  li.hasNext(); ) {

                        UpsertClassifierDataRequestContext uCtx = li.next();
                        try {

                            ContextUtils.storageCopy(ctx, uCtx, uCtx.keysId());
                            classifiersDataComponent.getUpsertClassifierActionListener().before(uCtx);
                            classifiersDataComponent.upsertOrigin(uCtx);
                            if (!uCtx.isSuppressAudit() && AuditLevel.AUDIT_SUCCESS <= uCtx.getAuditLevel() && !uCtx.isInitialLoad()) {
                                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_UPSERT_CLASSIFIER, uCtx);
                            }

                        } catch (Exception e) {
                            if (!uCtx.isSuppressAudit() && AuditLevel.AUDIT_ERRORS <= uCtx.getAuditLevel()) {
                                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_UPSERT_CLASSIFIER, e, uCtx);
                            }
                            li.remove();
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Batch upsert classifiers BEFORE exception caught.", e);
                bi.remove();
            }
        }

        // 2. Apply
        batchSetProcessor.applyOrigins(accumulator);
    }
    /**
     * Does etalons batch upsert.
     * @param accumulator the accumulator
     */
    private void batchUpsertEtalons(BatchSetAccumulator<UpsertClassifiersDataRequestContext> accumulator) {

        // 1. Collect data
        for (BatchIterator<UpsertClassifiersDataRequestContext> bi = accumulator.iterator(BatchSetIterationType.UPSERT_ETALONS); bi.hasNext(); ) {

            UpsertClassifiersDataRequestContext ctx = bi.next();
            try {

                for (Entry<String, List<UpsertClassifierDataRequestContext>> entry : ctx.getClassifiers().entrySet()) {
                    for (Iterator<UpsertClassifierDataRequestContext> li = entry.getValue().iterator(); li.hasNext(); ) {

                        UpsertClassifierDataRequestContext uCtx = li.next();
                        try {

                            classifiersDataComponent.upsertEtalon(uCtx);
                            if (!uCtx.isSuppressAudit() && AuditLevel.AUDIT_SUCCESS <= uCtx.getAuditLevel() && !uCtx.isInitialLoad()) {
                                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_UPSERT_CLASSIFIER, uCtx);
                            }
                        } catch (Exception e) {
                            if (!uCtx.isSuppressAudit() && AuditLevel.AUDIT_ERRORS <= uCtx.getAuditLevel()) {
                                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_UPSERT_CLASSIFIER, e, uCtx);
                            }
                            li.remove();
                        }
                    }
                }

                classifiersValidationComponent.after(ctx);
            } catch (Exception e) {
                LOGGER.warn("Batch upsert classifiers AFTER exception caught.", e);
                bi.remove();
            }
        }

        // 2. Apply
        batchSetProcessor.applyEtalons(accumulator);
    }
    /**
     * Does origins batch delete.
     * @param accumulator the accumulator
     */
    private void batchDeleteOrigins(BatchSetAccumulator<DeleteClassifiersDataRequestContext> accumulator) {

        // 1. Collect data
        for (BatchIterator<DeleteClassifiersDataRequestContext> bi = accumulator.iterator(BatchSetIterationType.DELETE_ORIGINS); bi.hasNext(); ) {

            DeleteClassifiersDataRequestContext ctx = bi.next();
            try {

                classifiersValidationComponent.before(ctx);
                for (Entry<String, List<DeleteClassifierDataRequestContext>> entry : ctx.getClassifiers().entrySet()) {

                    for (Iterator<DeleteClassifierDataRequestContext> li = entry.getValue().iterator();  li.hasNext(); ) {

                        DeleteClassifierDataRequestContext dCtx = li.next();
                        try {

                            ContextUtils.storageCopy(ctx, dCtx, dCtx.keysId());
                            classifiersDataComponent.getDeleteClassifierActionListener().before(dCtx);
                            classifiersDataComponent.deleteOrigin(dCtx);
                            if (!dCtx.isSuppressAudit() && AuditLevel.AUDIT_SUCCESS <= dCtx.getAuditLevel()) {
                                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_DELETE_CLASSIFIER, dCtx);
                            }

                        } catch (Exception e) {
                            if (!dCtx.isSuppressAudit() && AuditLevel.AUDIT_ERRORS <= dCtx.getAuditLevel()) {
                                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_DELETE_CLASSIFIER, e, dCtx);
                            }
                            li.remove();
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Batch delete classifiers BEFORE exception caught.", e);
                bi.remove();
            }
        }

        // 2. Apply
        batchSetProcessor.applyOrigins(accumulator);
    }
    /**
     * Does etalons batch delete.
     * @param accumulator the accumulator
     */
    private void batchDeleteEtalons(BatchSetAccumulator<DeleteClassifiersDataRequestContext> accumulator) {

        // 1. Collect
        for (BatchIterator<DeleteClassifiersDataRequestContext> bi = accumulator.iterator(BatchSetIterationType.DELETE_ETALONS); bi.hasNext(); ) {

            DeleteClassifiersDataRequestContext ctx = bi.next();
            try {

                for (Entry<String, List<DeleteClassifierDataRequestContext>> entry : ctx.getClassifiers().entrySet()) {
                    for (Iterator<DeleteClassifierDataRequestContext> li = entry.getValue().iterator(); li.hasNext(); ) {

                        DeleteClassifierDataRequestContext dCtx = li.next();
                        try {

                            classifiersDataComponent.deleteEtalon(dCtx);
                            if (!dCtx.isSuppressAudit() && AuditLevel.AUDIT_SUCCESS <= dCtx.getAuditLevel()) {
                                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_DELETE_CLASSIFIER, dCtx);
                            }
                        } catch (Exception e) {
                            if (!dCtx.isSuppressAudit() && AuditLevel.AUDIT_ERRORS <= dCtx.getAuditLevel()) {
                                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_DELETE_CLASSIFIER, e, dCtx);
                            }
                            li.remove();
                        }
                    }
                }

                classifiersValidationComponent.after(ctx);
            } catch (Exception e) {
                LOGGER.warn("Batch delete classifiers AFTER exception caught.", e);
                bi.remove();
            }
        }

        // 2. Apply
        batchSetProcessor.applyEtalons(accumulator);
    }
    /**
     * Upsert single relation call.
     *
     * @param ctx the context
     * @return result (inserted/updated record)
     */
    @Transactional
    public UpsertClassifierDTO upsertClassifier(UpsertClassifierDataRequestContext ctx) {
        MeasurementPoint.start();
        try {
            return classifiersDataComponent.upsert(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Upsert relations call.
     *
     * @param ctxts the context's
     * @return result (inserted/updated records)
     */
    @Transactional
    public UpsertClassifiersDTO upsertClassifiers(List<UpsertClassifierDataRequestContext> ctxts) {
        MeasurementPoint.start();
        try {

            Map<String, List<UpsertClassifierDTO>> upserted = new HashMap<>();
            if (!CollectionUtils.isEmpty(ctxts)) {
                for (UpsertClassifierDataRequestContext ctx : ctxts) {
                    UpsertClassifierDTO result = classifiersDataComponent.upsert(ctx);
                    if (Objects.isNull(result)) {
                        continue;
                    }

                    if (!upserted.containsKey(result.getClassifierKeys().getName())) {
                        upserted.put(result.getClassifierKeys().getName(), new ArrayList<>());
                    }

                    upserted.get(result.getClassifierKeys().getName()).add(result);
                }
            }

            return new UpsertClassifiersDTO(upserted);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Upsert relations call.
     *
     * @param ctx the context
     * @return result (inserted/updated records)
     */
    @Transactional
    public UpsertClassifiersDTO upsertClassifiers(UpsertClassifiersDataRequestContext ctx) {
        MeasurementPoint.start();
        try {

            // 1. Ensure from key and possibly do other stuff, common to collecting conetxts
            classifiersValidationComponent.before(ctx);

            // 3. Upsert classifiers by classifier name
            Map<String, List<UpsertClassifierDTO>> upserted = new HashMap<>();
            for (Entry<String, List<UpsertClassifierDataRequestContext>> entry : ctx.getClassifiers().entrySet()) {

                upserted.put(entry.getKey(), new ArrayList<>(entry.getValue().size()));
                for (UpsertClassifierDataRequestContext i : entry.getValue()) {

                    ContextUtils.storageCopy(ctx, i, ctx.keysId());
                    UpsertClassifierDTO result = classifiersDataComponent.upsert(i);
                    if (Objects.isNull(result)) {
                        continue;
                    }

                    upserted.get(entry.getKey()).add(result);
                }
            }

            // 4. Run possible future 'after' actions.
            classifiersValidationComponent.after(ctx);

            return new UpsertClassifiersDTO(upserted);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets a classifier by simple request context.
     *
     * @param ctx the context
     * @return classifier DTO
     */
    public GetClassifierDTO getClassifier(GetClassifierDataRequestContext ctx) {
        MeasurementPoint.start();
        try {
            return classifiersDataComponent.get(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets the classifiers.
     *
     * @param ctxts the contexts
     * @return classifiers DTO
     */
    public GetClassifiersDTO getClassifiers(List<GetClassifierDataRequestContext> ctxts) {

        MeasurementPoint.start();
        try {

            Map<String, List<GetClassifierDTO>> fetched = new HashMap<>();
            if (!CollectionUtils.isEmpty(ctxts)) {
                for (GetClassifierDataRequestContext ctx : ctxts) {

                    GetClassifierDTO result = getClassifier(ctx);
                    if (Objects.isNull(result)) {
                        continue;
                    }

                    fetched.computeIfAbsent(result.getClassifierKeys().getName(), k -> new ArrayList<>()).add(result);
                }
            }

            return new GetClassifiersDTO(fetched);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Gets the classifiers.
     *
     * @param ctx the context
     * @return classifiers DTO
     */
    public GetClassifiersDTO getClassifiers(GetClassifiersDataRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Ensure from key and possibly run other stuff, relevant for collection context
            classifiersValidationComponent.before(ctx);

            // 2. Get relations by relation type
            Map<String, List<GetClassifierDTO>> collected = new HashMap<>();
            if (!ctx.getClassifiers().isEmpty()) {

                for (Entry<String, List<GetClassifierDataRequestContext>> entry : ctx.getClassifiers().entrySet()) {
                    for (GetClassifierDataRequestContext i : entry.getValue()) {

                        ContextUtils.storageCopy(ctx, i, ctx.keysId());
                        GetClassifierDTO result = getClassifier(i);
                        if(result != null){
                            collected.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(result);
                        }
                    }
                }
            } else if (!ctx.getClassifierNames().isEmpty()) {

                RecordKeys keys = ctx.keys();
                if (keys != null) {

                    for (String name : ctx.getClassifierNames()) {
                        // TODO implement mass key fetch
                        List<EtalonClassifierPO> toEtalons = classifierDAO.loadClassifierEtalons(keys.getEtalonKey().getId(), name, null);
                        if (CollectionUtils.isEmpty(toEtalons)) {
                            continue;
                        }

                        List<GetClassifierDataRequestContext> requestList = toEtalons.stream().map(
                                po -> {
                                    GetClassifierDataRequestContext gCtx = GetClassifierDataRequestContext.builder()
                                        .classifierEtalonKey(po.getId())
                                        .forDate(ctx.getForDate())
                                        .forOperationId(ctx.getForOperationId())
                                        .fetchOrigins(ctx.isFetchOrigins())
                                        .build();

                                    gCtx.setOperationId(ctx.getOperationId());
                                    ContextUtils.storageCopy(ctx, gCtx, ctx.keysId());
                                    return gCtx;
                                })
                                .collect(Collectors.toList());

                        GetClassifiersDTO result = getClassifiers(requestList);
                        if (Objects.nonNull(result) && MapUtils.isNotEmpty(result.getClassifiers())) {
                            collected.putAll(result.getClassifiers());
                        }
                    }
                }
            }

            // 3. Run possible future 'after' actions.
            classifiersValidationComponent.after(ctx);
            return new GetClassifiersDTO(collected);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Loads all active classifier
     * @param etalonId
     * @param asOf
     * @return
     */
    public Map<String, List<GetClassifierDTO>> loadActiveEtalonsClassifiers(EtalonRecord record, Date asOf) {

        MeasurementPoint.start();
        try {

            List<String> availableClassifiers = metaModelService.isLookupEntity(record.getInfoSection().getEntityName())
                    ? metaModelService.getValueById(record.getInfoSection().getEntityName(), LookupEntityWrapper.class).getEntity().getClassifiers()
                    : metaModelService.getValueById(record.getInfoSection().getEntityName(), EntityWrapper.class).getEntity().getClassifiers();

            if (!CollectionUtils.isEmpty(availableClassifiers)) {

                GetClassifiersDataRequestContext clsfCtx = GetClassifiersDataRequestContext.builder()
                        .etalonKey(record.getInfoSection().getEtalonKey().getId())
                        .forDate(asOf)
                        .classifierNames(availableClassifiers)
                        .build();

                clsfCtx.putToStorage(clsfCtx.keysId(),
                    RecordKeys.builder()
                        .etalonKey(record.getInfoSection().getEtalonKey())
                        .entityName(record.getInfoSection().getEntityName())
                        .etalonState(record.getInfoSection().getApproval())
                        .etalonStatus(record.getInfoSection().getStatus())
                        .build());

                GetClassifiersDTO classifiers = getClassifiers(clsfCtx);
                return classifiers.getClassifiers();
            }

            return Collections.emptyMap();
        } finally {
            MeasurementPoint.stop();
        }
    }

    private  BatchSetAccumulator<DeleteClassifiersDataRequestContext> getDefaultClassifierDeleteAccumulator(){
        ClassifiersDeleteBatchSetAccumulator accumulator
                = new ClassifiersDeleteBatchSetAccumulator(500, null);
        accumulator.setBatchSetSize(BatchSetSize.SMALL);
        accumulator.setSupportedIterationTypes(Arrays.asList(BatchSetIterationType.DELETE_ETALONS,
                BatchSetIterationType.DELETE_ORIGINS));
        return accumulator;
    }

    private  BatchSetAccumulator<UpsertClassifiersDataRequestContext> getDefaultClassifierUpsertAccumulator(){
        ClassifierUpsertBatchSetAccumulator accumulator
                = new ClassifierUpsertBatchSetAccumulator(500, null);
        accumulator.setBatchSetSize(BatchSetSize.SMALL);
        accumulator.setSupportedIterationTypes(Arrays.asList(BatchSetIterationType.UPSERT_ORIGINS,
                BatchSetIterationType.UPSERT_ETALONS));
        return accumulator;
    }
}