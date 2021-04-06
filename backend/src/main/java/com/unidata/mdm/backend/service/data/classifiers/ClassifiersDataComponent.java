package com.unidata.mdm.backend.service.data.classifiers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.context.DeleteClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.GetClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.dto.DeleteClassifierDTO;
import com.unidata.mdm.backend.common.dto.GetClassifierDTO;
import com.unidata.mdm.backend.common.dto.UpsertClassifierDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.EtalonClassifierInfoSection;
import com.unidata.mdm.backend.common.types.OriginClassifier;
import com.unidata.mdm.backend.common.types.OriginClassifierInfoSection;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.common.types.impl.EtalonClassifierImpl;
import com.unidata.mdm.backend.common.types.impl.OriginClassifierImpl;
import com.unidata.mdm.backend.dao.ClassifiersDAO;
import com.unidata.mdm.backend.po.EtalonClassifierPO;
import com.unidata.mdm.backend.po.OriginClassifierPO;
import com.unidata.mdm.backend.po.OriginsVistoryClassifierPO;
import com.unidata.mdm.backend.service.data.batch.ClassifierBatchSet;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.driver.CalculableHolder;
import com.unidata.mdm.backend.service.data.driver.EtalonComposer;
import com.unidata.mdm.backend.service.data.driver.EtalonCompositionDriverType;
import com.unidata.mdm.backend.service.data.driver.OriginClassifierRecordHolder;
import com.unidata.mdm.backend.service.data.listener.DataRecordLifecycleListener;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * @author Mikhail Mikhailov
 * Classifiers data manipulations.
 */
@Component
public class ClassifiersDataComponent {
    /**
     * Delete classifier listener.
     */
    private static final String DELETE_CLASSIFIER_ACTION_LISTENER_QUALIFIER = "deleteClassifierActionListener";
    /**
     * Get classifier listener.
     */
    private static final String GET_CLASSIFIER_ACTION_LISTENER_QUALIFIER = "getClassifierActionListener";
    /**
     * Upsert classifier listener.
     */
    private static final String UPSERT_CLASSIFIER_ACTION_LISTENER_QUALIFIER = "upsertClassifierActionListener";
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifiersDataComponent.class);
    /**
     * Classifier data DAO.
     */
    @Autowired
    private ClassifiersDAO classifierDAO;
    /**
     * Etalon composer.
     */
    @Autowired
    private EtalonComposer etalonComposer;
    /**
     * MMS.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * Search service.
     */
    @Autowired
    private SearchServiceExt searcheService;
    /**
     * CRC.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * Delete single classifier executors.
     */
    @Autowired
    @Qualifier(DELETE_CLASSIFIER_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<DeleteClassifierDataRequestContext> deleteClassifierActionListener;
    /**
     * Get multiple classifiers executors.
     */
    @Autowired
    @Qualifier(GET_CLASSIFIER_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<GetClassifierDataRequestContext> getClassifierActionListener;
    /**
     * Upsert single classifier executors.
     */
    @Autowired
    @Qualifier(UPSERT_CLASSIFIER_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<UpsertClassifierDataRequestContext> upsertClassifierActionListener;
    /**
     * Does classifier data upsert.
     * @param ctx the context
     * @return result
     */
    public UpsertClassifierDTO upsert(UpsertClassifierDataRequestContext ctx) {

        // 1. Run pre-check
        upsertClassifierActionListener.before(ctx);

        // 2. Upsert
        upsertOrigin(ctx);
        upsertEtalon(ctx);

        // 3. Run after-check
        upsertClassifierActionListener.after(ctx);

        return upsertContextToResult(ctx);
    }
    /**
     * Deletes a classifier data record.
     * @param ctx the context
     * @return result
     */
    public DeleteClassifierDTO delete(DeleteClassifierDataRequestContext ctx) {

        // 1. Run pre-check
        deleteClassifierActionListener.before(ctx);

        // 2. Delete
        deleteOrigin(ctx);
        deleteEtalon(ctx);

        // 3. Run post-check
        deleteClassifierActionListener.after(ctx);

        return deleteContextToResult(ctx);
    }
    /**
     * Gets classifier data.
     * @param ctx the context
     * @return data
     */
    public GetClassifierDTO get(GetClassifierDataRequestContext ctx) {

        MeasurementPoint.start();
        try {

            boolean isOk = getClassifierActionListener.before(ctx);
            if (!isOk) {
                return null;
            }

            ClassifierKeys keys = ctx.classifierKeys();
            GetClassifierDTO result = null;
            Pair<EtalonClassifier, List<CalculableHolder<OriginClassifier>>> calculation
                = loadClassifierDataFull(keys, ctx.getForDate(), null, false);
            if (calculation != null) {

                result = new GetClassifierDTO();
                result.setClassifierKeys(keys);
                result.setEtalon(calculation.getKey());

                if (ctx.isFetchOrigins() && calculation.getValue() != null ) {
                    result.setOrigins(calculation.getValue().stream()
                            .map(CalculableHolder::getValue)
                            .collect(Collectors.toList()));
                }
            }

            getClassifierActionListener.after(ctx);
            return result;

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Deletes classifier origin record.
     * @param ctx the context
     * @return result
     */
    public void deleteOrigin(DeleteClassifierDataRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Get keys
            ClassifierKeys keys = ctx.classifierKeys();

            // 3. Delete
            boolean success = false;
            if (ctx.isWipe()) {
                // 3.1. Wipe record
                success = classifierDAO.wipeClassifierEtalon(ctx.getClassifierEtalonKey());
            } else {
                // 3.2. Relation etalon ID. Deactivate etalon and origins
                //    or Keys present (found by from <-> to). Deactivate etalon and origins.
                if (ctx.isInactivateEtalon()) {
                    if (ctx.isBatchUpsert()) {

                        ClassifierBatchSet set = ctx.getFromStorage(StorageId.DATA_BATCH_CLASSIFIERS);
                        Date ts = new Date(System.currentTimeMillis());
                        String user = SecurityUtils.getCurrentUserName();

                        EtalonClassifierPO epo
                            = DataRecordUtils.newEtalonClassifierPO(
                                    keys.getName(), keys.getRecord().getEtalonKey().getId(), RecordStatus.INACTIVE,
                                    ts, ctx.getOperationId());

                        epo.setId(keys.getEtalonId());
                        epo.setApproval(ApprovalState.APPROVED);
                        epo.setUpdateDate(ts);
                        epo.setUpdatedBy(user);

                        set.setEtalonClassifierUpdatePO(epo);

                        success = true;
                    } else {
                        success = classifierDAO.deactivateClassifierEtalon(keys.getEtalonId(), ApprovalState.APPROVED);
                    }
                // 3.3. Relation origin ID. Deactivate relation origin only
                } else if (ctx.isInactivateOrigin()) {
                    success = classifierDAO.deactivateClassifierOrigin(keys.getOriginId());
                // 3.4. Inactivate period
                } else if (ctx.isInactivatePeriod()) {

                    OriginsVistoryClassifierPO version
                        = DataRecordUtils.newOriginsVistoryClassifierPO(
                            keys.getOriginId(),
                            ctx.getOperationId(),
                            ctx.getValidFrom(),
                            ctx.getValidTo(),
                            null,
                            RecordStatus.INACTIVE);

                    if (ctx.isBatchUpsert()) {
                        ClassifierBatchSet set = ctx.getFromStorage(StorageId.DATA_BATCH_CLASSIFIERS);
                        set.getOriginsVistoryClassifierPO().add(version);
                    } else {
                        success = classifierDAO.putVersion(version);
                    }
                }
            }

            ctx.putToStorage(StorageId.CLASSIFIERS_RESULT, new DeleteClassifierDTO(success ? keys : null));

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Deletes classifier data from elastic or prepares such a delete.
     * @param ctx the context
     */
    public void deleteEtalon(DeleteClassifierDataRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Get keys
            ClassifierKeys keys = ctx.classifierKeys();
            // No support for restore so far. No status for classifier records in the index
            IndexRequestContext iCtx = IndexRequestContext.builder()
                    .classifierToDelete(SearchUtils.childPeriodId(keys.getRecord().getEtalonKey().getId(), keys.getName()))
                    .entity(keys.getRecord().getEntityName())
                    .routing(keys.getRecord().getEtalonKey().getId())
                    .drop(true)
                    .build();
            if (ctx.isBatchUpsert()) {
                ClassifierBatchSet set = ctx.getFromStorage(StorageId.DATA_BATCH_CLASSIFIERS);
                set.setIndexRequestContext(iCtx);
            } else {
                searcheService.index(iCtx);
            }

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Extracts delete data to result DTO.
     * @param ctx the context
     * @return result
     */
    public DeleteClassifierDTO deleteContextToResult(DeleteClassifierDataRequestContext ctx) {
        return ctx.getFromStorage(StorageId.CLASSIFIERS_RESULT);
    }
    /**
     * Does origin upsert.
     * @param ctx the context
     */
    public void upsertOrigin(UpsertClassifierDataRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Possibly upsert E|O records
            ClassifierKeys classifierKeys = ctx.classifierKeys();
            RecordKeys recordKeys = ctx.keys();
            UpsertAction action = ctx.getFromStorage(StorageId.CLASSIFIERS_UPSERT_EXACT_ACTION);
            String resolvedNodeId = Objects.nonNull(ctx.getClassifierNodeId())
                ? ctx.getClassifierNodeId()
                : ctx.getFromStorage(StorageId.CLASSIFIERS_UPSERT_RESOLVED_NODE_ID);
            Date ts = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);

            if (classifierKeys == null) {

                // Fail upsert. Incomplete identity.
                if (recordKeys == null) {
                    final String message = "Cannot identify record by given origin id [{}], external id [{}, {}, {}], etalon id [{}]. Stopping.";
                    LOGGER.warn(message, ctx.getOriginKey(), ctx.getExternalId(), ctx.getSourceSystem(), ctx.getEntityName(), ctx.getEtalonKey());
                    throw new BusinessException(message, ExceptionId.EX_DATA_CLASSIFIER_UPSERT_RECORD_NOT_FOUND,
                            ctx.getOriginKey(), ctx.getExternalId(), ctx.getSourceSystem(), ctx.getEntityName(), ctx.getEtalonKey());
                }

                // Check parent status
                if (recordKeys.getEtalonStatus() != RecordStatus.ACTIVE) {
                    final String message = "Parent etalon record is in inactive state. Stopping.";
                    LOGGER.warn(message);
                    throw new BusinessException(message, ExceptionId.EX_DATA_CLASSIFIER_UPSERT_RECORD_INACTIVE);
                }

                // Create new etalon
                EtalonClassifierPO etalon
                    = DataRecordUtils.newEtalonClassifierPO(ctx.getClassifierName(),
                            recordKeys.getEtalonKey().getId(), RecordStatus.ACTIVE, ts, ctx.getOperationId());

                if (ctx.isBatchUpsert()) {

                    ClassifierBatchSet set = ctx.getFromStorage(StorageId.DATA_BATCH_CLASSIFIERS);
                    set.setEtalonClassifierInsertPO(etalon);

                } else if (!classifierDAO.upsertEtalonClassifier(etalon, true)) {
                    final String message = "Relation etalon record upsert failed. Stopping";
                    LOGGER.warn(message);
                    throw new DataProcessingException(message, ExceptionId.EX_DATA_CLASSIFIER_UPSERT_ETALON_FAILED);
                }

                classifierKeys = ClassifierKeys.builder()
                        .etalonId(etalon.getId())
                        .etalonState(etalon.getApproval())
                        .etalonStatus(etalon.getStatus())
                        .name(etalon.getName())
                        .record(recordKeys)
                        .build();
            }

            boolean existingRecordNodeIdChange = isSameRecordNodeIdChange(classifierKeys, resolvedNodeId);
            if (classifierKeys.getOriginId() == null || existingRecordNodeIdChange) {

                if (classifierKeys.getRecord().getOriginKey() == null) {
                    OriginKey fromOriginKey
                        = commonRecordsComponent
                            .createSystemOriginRecord(
                                    classifierKeys.getRecord().getEtalonKey().getId(),
                                    classifierKeys.getRecord().getEntityName());
                    recordKeys = RecordKeys.builder(classifierKeys.getRecord())
                            .originKey(fromOriginKey)
                            .originStatus(RecordStatus.ACTIVE)
                            .build();
                }

                // Node id reset
                if (existingRecordNodeIdChange) {
                    classifierDAO.wipeClassifierOrigin(classifierKeys.getOriginId());
                }

                OriginClassifierPO system = null;
                OriginClassifierPO origin
                    = DataRecordUtils.newOriginsClassifierPO(
                            classifierKeys.getEtalonId(),
                            classifierKeys.getName(),
                            resolvedNodeId,
                            recordKeys.getOriginKey().getId(),
                            recordKeys.getOriginKey().getSourceSystem(),
                            RecordStatus.ACTIVE, ts);

                // Check for non-system origin being inserted. Create one if needed
                String adminSourceSystem = metaModelService.getAdminSourceSystem().getName();
                if (action == UpsertAction.INSERT && !adminSourceSystem.equals(origin.getSourceSystem())) {

                    OriginKey systemKey = recordKeys.getKeyBySourceSystem(adminSourceSystem);
                    if (systemKey != null) {
                        system
                            = DataRecordUtils.newOriginsClassifierPO(
                                classifierKeys.getEtalonId(),
                                classifierKeys.getName(),
                                resolvedNodeId,
                                systemKey.getId(),
                                adminSourceSystem,
                                RecordStatus.ACTIVE, ts);
                    } else {
                        LOGGER.warn("Cannot create system origin classifier! Record system key is missing.");
                    }
                }

                if (ctx.isBatchUpsert()) {
                    ClassifierBatchSet set = ctx.getFromStorage(StorageId.DATA_BATCH_CLASSIFIERS);
                    set.getOriginClassifierInsertPOs().addAll(system == null
                            ? Collections.singletonList(origin)
                            : Arrays.asList(origin, system));

                } else if (!classifierDAO.upsertOriginClassifier(origin, true)
                 || (system != null && !classifierDAO.upsertOriginClassifier(system, true))) {
                    final String message = "Relation origin record upsert failed. Stopping.";
                    LOGGER.warn(message);
                    throw new DataProcessingException(message, ExceptionId.EX_DATA_RELATIONS_UPSERT_ORIGIN_FAILED);
                }

                classifierKeys = ClassifierKeys.builder(classifierKeys)
                        .originId(origin.getId())
                        .originStatus(origin.getStatus())
                        .originSourceSystem(origin.getSourceSystem())
                        .nodeId(origin.getNodeId())
                        .record(recordKeys)
                        .build();
            }

            ctx.putToStorage(ctx.classifierKeysId(), classifierKeys);

            // 2. Check etalon status, re-enable, if inactive
            ClassifierKeys keys = ctx.classifierKeys();
            if (keys.getEtalonStatus() == RecordStatus.INACTIVE) {

                EtalonClassifierPO po
                    = DataRecordUtils.newEtalonClassifierPO(
                        keys.getName(),
                        keys.getRecord().getEtalonKey().getId(),
                        RecordStatus.ACTIVE, ts, ctx.getOperationId());

                po.setId(keys.getEtalonId());

                classifierDAO.upsertEtalonClassifier(po, false);
            }

            // 5. Check origin status, re-enable, if inactive
            if (keys.getOriginStatus() == RecordStatus.INACTIVE) {

                OriginClassifierPO origin
                    = DataRecordUtils.newOriginsClassifierPO(
                            keys.getEtalonId(),
                            keys.getName(),
                            keys.getNodeId(),
                            keys.getRecord().getOriginKey().getId(),
                            keys.getOriginSourceSystem(),
                            RecordStatus.ACTIVE, ts);

                origin.setId(keys.getOriginId());

                classifierDAO.upsertOriginClassifier(origin, false);
            }

            // 6. Put data
            OriginsVistoryClassifierPO version
                = DataRecordUtils.newOriginsVistoryClassifierPO(keys.getOriginId(),
                        ctx.getOperationId(),
                        ctx.getValidFrom(),
                        ctx.getValidTo(),
                        ctx.getClassifier(), RecordStatus.ACTIVE);

            if (ctx.isBatchUpsert()) {
                ClassifierBatchSet set = ctx.getFromStorage(StorageId.DATA_BATCH_CLASSIFIERS);
                set.getOriginsVistoryClassifierPO().add(version);
            } else {

                boolean success = classifierDAO.putVersion(version);
                if (!success) {
                    final String message = "Cannot insert classifier data version record [etalon id = {}, origin id = {}, rel name = {}, source system = {}].";
                    LOGGER.warn(message, keys.getEtalonId(), keys.getOriginId(), keys.getName(), keys.getOriginSourceSystem());
                    throw new DataProcessingException(message,
                            ExceptionId.EX_DATA_CLASSIFIER_UPSERT_VERSION_FAILED,
                            keys.getEtalonId(), keys.getOriginId(), keys.getName(), keys.getOriginSourceSystem());
                } else {
                    LOGGER.info("Upserted classifier data version record [etalon id = {}, origin id = {}, rel name = {}, source system = {}].",
                            keys.getEtalonId(), keys.getOriginId(), keys.getName(), keys.getOriginSourceSystem());
                }
            }

            // 7. Save result
            UpsertClassifierDTO result = new UpsertClassifierDTO();
            result.setClassifierKeys(keys);

            ctx.putToStorage(StorageId.CLASSIFIERS_RESULT, result);

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Does etalon upsert.
     * @param ctx the context
     */
    public void upsertEtalon(UpsertClassifierDataRequestContext ctx) {

        ClassifierKeys keys = ctx.classifierKeys();
        UpsertAction action = ctx.getFromStorage(StorageId.CLASSIFIERS_UPSERT_EXACT_ACTION);
        Date ts = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);

        EtalonClassifier etalon = null;

        // 1. Choice - generate inplace
        if (action == UpsertAction.INSERT) {

            EtalonClassifierInfoSection infoSection = new EtalonClassifierInfoSection()
                    .withApproval(keys.getEtalonState())
                    .withClassifierEtalonKey(keys.getEtalonId())
                    .withClassifierName(keys.getName())
                    .withCreateDate(ts)
                    .withCreatedBy(SecurityUtils.getCurrentUserName())
                    .withNodeId(keys.getNodeId())
                    .withRecordEntityName(keys.getRecord().getEntityName())
                    .withRecordEtalonKey(keys.getRecord().getEtalonKey())
                    .withStatus(keys.getEtalonStatus())
                    .withUpdateDate(ts)
                    .withUpdatedBy(SecurityUtils.getCurrentUserName());

            etalon = new EtalonClassifierImpl()
                    .withInfoSection(infoSection)
                    .withDataRecord(ctx.getClassifier() == null ? new SerializableDataRecord(0) : ctx.getClassifier());
        } else {
        // 2. Or calculate
            Pair<EtalonClassifier, List<CalculableHolder<OriginClassifier>>> calculation = null;
            calculation = loadClassifierDataFull(keys,
                    ctx.getValidFrom() != null ? ctx.getValidFrom() : ctx.getValidTo(), null, false);
            etalon = calculation != null ? calculation.getKey() : null;
        }

        if (etalon != null) {

            UpsertClassifierDTO result = ctx.getFromStorage(StorageId.CLASSIFIERS_RESULT);
            if (result != null) {
                result.setEtalon(etalon);
            // Batch
            } else {
                result = new UpsertClassifierDTO();
                result.setClassifierKeys(keys);
            }

            IndexRequestContext iCtx = IndexRequestContext.builder()
                    .entity(keys.getRecord().getEntityName())
                    .classifiers(Collections.singletonList(etalon))
                    .classifiersToDelete(action == UpsertAction.INSERT
                        ? Collections.emptyList()
                        : Collections.singletonList(SearchUtils.childPeriodId(keys.getRecord().getEtalonKey().getId(), keys.getName())))
                    .drop(action == UpsertAction.UPDATE)
                    .routing(etalon.getInfoSection().getRecordEtalonKey().getId())
                    .refresh(!ctx.isBatchUpsert())
                    .build();

            if (ctx.isBatchUpsert()) {
                ClassifierBatchSet set = ctx.getFromStorage(StorageId.DATA_BATCH_CLASSIFIERS);
                set.setIndexRequestContext(iCtx);
            } else {
                searcheService.index(iCtx);
            }
        }
    }
    /**
     * Extracts upsert data to result DTO.
     * @param ctx the context
     * @return result
     */
    public UpsertClassifierDTO upsertContextToResult(UpsertClassifierDataRequestContext ctx) {
        return ctx.getFromStorage(StorageId.CLASSIFIERS_RESULT);
    }
    /**
     * Metarialize etalon classifier record.
     * @param keys the keys
     * @param asOf as of date
     * @param operationId the operation id
     * @param includeDraftVersions see drafts or not
     * @return classifier etalon and its calculable versions
     */
    private Pair<EtalonClassifier, List<CalculableHolder<OriginClassifier>>>
        loadClassifierDataFull(ClassifierKeys keys, Date asOf, String operationId, boolean includeDraftVersions) {

        MeasurementPoint.start();
        try {

            List<OriginsVistoryClassifierPO> versions;
            if (Objects.nonNull(operationId)) {
                versions = classifierDAO.loadClassifierVersions(keys.getEtalonId(), asOf, operationId, includeDraftVersions);
            } else {
                versions = classifierDAO.loadClassifierVersions(keys.getEtalonId(), asOf, includeDraftVersions);
            }

            if (CollectionUtils.isEmpty(versions)) {
                return null;
            }

            List<CalculableHolder<OriginClassifier>> calculables = new ArrayList<>(versions.size());
            for (OriginsVistoryClassifierPO po : versions) {

                OriginClassifier ori = new OriginClassifierImpl()
                        .withDataRecord(po.getData())
                        .withInfoSection(new OriginClassifierInfoSection()
                                .withApproval(po.getApproval())
                                .withClassifierName(po.getName())
                                .withClassifierOriginKey(po.getOriginId())
                                .withClassifierSourceSystem(po.getSourceSystem())
                                .withCreateDate(po.getCreateDate())
                                .withCreatedBy(po.getCreatedBy())
                                .withNodeId(po.getNodeId())
                                .withRecordEntityName(po.getOriginRecordName())
                                .withRecordOriginKey(OriginKey.builder()
                                        .entityName(po.getOriginRecordName())
                                        .externalId(po.getOriginRecordExternalId())
                                        .sourceSystem(po.getOriginRecordSourceSystem())
                                        .status(po.getOriginRecordStatus())
                                        .id(po.getOriginIdRecord())
                                        .build())
                                .withRevision(po.getRevision())
                                .withMajor(po.getMajor())
                                .withMinor(po.getMinor())
                                .withStatus(po.getStatus())
                                .withUpdateDate(po.getUpdateDate())
                                .withUpdatedBy(po.getUpdatedBy())
                                .withValidFrom(po.getValidFrom())
                                .withValidTo(po.getValidTo()));

                calculables.add(new OriginClassifierRecordHolder(ori));
            }

            OriginClassifier record = etalonComposer.compose(EtalonCompositionDriverType.BVR, calculables, false, false);
            if (record != null) {
                return new ImmutablePair<>(new EtalonClassifierImpl()
                        .withDataRecord(record)
                        .withInfoSection(new EtalonClassifierInfoSection()
                                .withApproval(record.getInfoSection().getApproval())
                                .withClassifierEtalonKey(keys.getEtalonId())
                                .withClassifierName(keys.getName())
                                .withCreateDate(record.getInfoSection().getCreateDate())
                                .withCreatedBy(record.getInfoSection().getCreatedBy())
                                .withNodeId(record.getInfoSection().getNodeId())
                                .withRecordEntityName(keys.getRecord().getEntityName())
                                .withRecordEtalonKey(keys.getRecord().getEtalonKey())
                                .withStatus(record.getInfoSection().getStatus())
                                .withUpdateDate(record.getInfoSection().getUpdateDate())
                                .withUpdatedBy(record.getInfoSection().getUpdatedBy())
                                .withValidFrom(record.getInfoSection().getValidFrom())
                                .withValidTo(record.getInfoSection().getValidTo())),
                        calculables);
            }

            return new ImmutablePair<>(null, calculables);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Detects node id change condition for the same classifier etalon id.
     * @param classifierKeys current keys
     * @param resolvedNodeId the node id
     * @return true, for node id change, false otherwise
     */
    private boolean isSameRecordNodeIdChange(ClassifierKeys classifierKeys, String resolvedNodeId) {
        return Objects.nonNull(classifierKeys.getOriginId())
                && Objects.nonNull(resolvedNodeId)
                && !StringUtils.equals(classifierKeys.getNodeId(), resolvedNodeId);
    }
    /**
     * @return the deleteClassifierActionListener
     */
    public DataRecordLifecycleListener<DeleteClassifierDataRequestContext> getDeleteClassifierActionListener() {
        return deleteClassifierActionListener;
    }
    /**
     * @return the getClassifierActionListener
     */
    public DataRecordLifecycleListener<GetClassifierDataRequestContext> getGetClassifierActionListener() {
        return getClassifierActionListener;
    }
    /**
     * @return the upsertClassifierActionListener
     */
    public DataRecordLifecycleListener<UpsertClassifierDataRequestContext> getUpsertClassifierActionListener() {
        return upsertClassifierActionListener;
    }
}
