package org.unidata.mdm.data.service.segments;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.calculables.ModificationBox;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.DataShift;
import org.unidata.mdm.core.type.data.OperationType;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.data.impl.SerializableDataRecord;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.po.data.RecordEtalonPO;
import org.unidata.mdm.data.po.data.RecordOriginPO;
import org.unidata.mdm.data.po.keys.RecordExternalKeysPO;
import org.unidata.mdm.data.type.apply.RecordUpsertChangeSet;
import org.unidata.mdm.data.type.calculables.impl.DataRecordHolder;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.OriginRecordInfoSection;
import org.unidata.mdm.data.type.data.impl.OriginRecordImpl;
import org.unidata.mdm.data.type.keys.RecordEtalonKey;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;
import org.unidata.mdm.data.type.timeline.RecordTimeInterval;
import org.unidata.mdm.data.util.RecordFactoryUtils;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.service.PlatformConfiguration;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;
import org.unidata.mdm.system.util.IdUtils;

/**
 * @author Mikhail Mikhailov
 * Creates MBox and initial objects, if needed.
 */
@Component(RecordUpsertModboxExecutor.SEGMENT_ID)
public class RecordUpsertModboxExecutor extends Point<UpsertRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_UPSERT_MODBOX_INIT]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.upsert.modbox.init.description";
    /**
     * MMS.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * PC.
     */
    @Autowired
    private PlatformConfiguration platformConfiguration;
    /**
     * ORC.
     */
//    @Autowired
//    private OriginRecordsComponent originRecordsComponent;
    /**
     * Constructor.
     */
    public RecordUpsertModboxExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(UpsertRequestContext ctx) {
        MeasurementPoint.start();
        try {

            if (Objects.isNull(ctx.changeSet())) {
                ctx.changeSet(new RecordUpsertChangeSet());
            }

            ensureKeysAndSystemObjects(ctx);
            ensureModificationBox(ctx);
            // ensurePendingState(ctx);

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
    /**
     * Set up initial origin record and return it inside new box.
     * @param ctx the context
     */
    private void ensureModificationBox(UpsertRequestContext ctx) {

        RecordKeys keys = ctx.keys();
        Date ts = ctx.timestamp();
        OperationType operationType = ctx.operationType() == null
                ? OperationType.DIRECT
                : ctx.operationType();

        String user = SecurityUtils.getCurrentUserName();

        DataRecord data = ctx.getRecord();
        Collection<CalculableHolder<OriginRecord>> input = Collections.emptyList();
        if (Objects.nonNull(data)) {

            OriginRecordInfoSection is = new OriginRecordInfoSection()
                    .withCreateDate(ts)
                    .withUpdateDate(ts)
                    .withCreatedBy(user)
                    .withUpdatedBy(user)
                    .withShift(DataShift.PRISTINE)
                    .withStatus(keys.getOriginKey().getStatus())
                    .withApproval(ApprovalState.APPROVED /* originRecordsComponent.calculateVersionState(ctx, keys, ctx.getFromStorage(StorageId.DATA_UPSERT_WF_ASSIGNMENTS)) */ )
                    .withValidFrom(ctx.getValidFrom())
                    .withValidTo(ctx.getValidTo())
                    .withMajor(platformConfiguration.getPlatformMajor())
                    .withMinor(platformConfiguration.getPlatformMinor())
                    .withOperationType(operationType)
                    .withRevision(0)
                    .withOriginKey(keys.getOriginKey());

            OriginRecord origin = new OriginRecordImpl()
                    .withDataRecord(data)
                    .withInfoSection(is);

            input = Collections.singleton(new DataRecordHolder(origin));
        }

        ModificationBox<OriginRecord> box = new RecordTimeInterval(ctx.getValidFrom(), ctx.getValidTo(), input);
        box.setCalculationState(SerializableDataRecord.of(data));

        ctx.modificationBox(box);
    }

    /**
     * Prepare origin upsert between.
     * Creates O/E records if necessary and resets keys in the context.
     * @param ctx the context to prepare
     */
    private void ensureKeysAndSystemObjects(UpsertRequestContext ctx) {

        // 1. Keys
        RecordKeys keys = ctx.keys();
        boolean hasEtalonRecord = keys != null && keys.getEtalonKey() != null && keys.getEtalonKey().getId() != null;
        boolean hasOriginRecord = keys != null && keys.getOriginKey() != null && keys.getOriginKey().getId() != null;

        Date ts = ctx.timestamp();
        String user = SecurityUtils.getCurrentUserName();
        RecordUpsertChangeSet changeSet = ctx.changeSet();

        if (!hasEtalonRecord) {

            RecordEtalonPO etalon = RecordFactoryUtils.createEtalonRecordPO(ctx, keys, RecordStatus.ACTIVE);
            keys = RecordKeys.builder()
                .etalonKey(RecordEtalonKey.builder()
                        .id(etalon.getId())
                        .status(etalon.getStatus())
                        .state(etalon.getApproval())
                        .build())
                .entityName(etalon.getName())
                .shard(etalon.getShard())
                .node(StorageUtils.node(etalon.getShard()))
                .createDate(ts)
                .createdBy(user)
                .updateDate(ts)
                .updatedBy(user)
                .build();

            changeSet.setEtalonRecordInsertPO(etalon);
        }

        if (!hasOriginRecord) {

            RecordOriginPO record = RecordFactoryUtils.createOriginRecordPO(ctx, keys, RecordStatus.ACTIVE);
            RecordExternalKeysPO recordEk = new RecordExternalKeysPO();
            recordEk.setExternalId(record.getExternalId(), record.getName(), record.getSourceSystem());
            recordEk.setEtalonId(UUID.fromString(record.getEtalonId()));

            RecordOriginPO system = null;
            RecordExternalKeysPO systemEk = null;

            // Check for first upsert and create
            // UD origin, if the upsert is not a UD upsert.
            if (!hasEtalonRecord && !metaModelService.getAdminSourceSystem().getName().equals(record.getSourceSystem())) {

                UpsertRequestContext sysContext = UpsertRequestContext.builder()
                        .sourceSystem(metaModelService.getAdminSourceSystem().getName())
                        .entityName(record.getName())
                        .externalId(IdUtils.v1String())
                        .build();

                sysContext.timestamp(ts);

                system = RecordFactoryUtils.createOriginRecordPO(sysContext,
                    RecordKeys.builder().etalonKey(keys.getEtalonKey()).shard(keys.getShard()).build(),
                    RecordStatus.ACTIVE);

                systemEk = new RecordExternalKeysPO();
                systemEk.setExternalId(system.getExternalId(), system.getName(), system.getSourceSystem());
                systemEk.setEtalonId(UUID.fromString(system.getEtalonId()));
            }

            RecordOriginKey uKey = RecordOriginKey.builder()
                    .entityName(record.getName())
                    .externalId(record.getExternalId())
                    .id(record.getId())
                    .initialOwner(record.getInitialOwner())
                    .sourceSystem(record.getSourceSystem())
                    .status(record.getStatus())
                    .enrichment(false)
                    .build();

            RecordOriginKey sKey = system == null
                ? null
                : RecordOriginKey.builder()
                    .entityName(system.getName())
                    .externalId(system.getExternalId())
                    .id(system.getId())
                    .initialOwner(record.getInitialOwner())
                    .sourceSystem(system.getSourceSystem())
                    .status(system.getStatus())
                    .enrichment(false)
                    .build();

            keys = RecordKeys.builder(keys)
                    .originKey(uKey)
                    .supplementaryKeys(sKey == null ? Collections.singletonList(uKey) : Arrays.asList(uKey, sKey))
                    .updateDate(ts)
                    .updatedBy(user)
                    .build();

            changeSet.getOriginRecordInsertPOs().add(record);
            changeSet.getExternalKeysInsertPOs().add(recordEk);
            if (Objects.nonNull(system)) {
                changeSet.getOriginRecordInsertPOs().add(system);
                changeSet.getExternalKeysInsertPOs().add(systemEk);
            }
        }

        if (!hasEtalonRecord || !hasOriginRecord) {
            ctx.keys(keys);
        }
    }

    /**
     * Possibly reset pending state of the keys and generate pending record(s).
     * @param ctx the context
     */
// @Modules
//    private void ensurePendingState(UpsertRequestContext ctx) {
//
//        WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.DATA_UPSERT_WF_ASSIGNMENTS);
//        if (Objects.isNull(assignment)) {
//            return;
//        }
//
//        ModificationBox<OriginRecord> box = ctx.modificationBox();
//        CalculableHolder<OriginRecord> thisUpsert = box.peek(ctx.toBoxKey());
//
//        if (thisUpsert.getValue().getInfoSection().getApproval() != ApprovalState.PENDING) {
//            return;
//        }
//
//        RecordKeys keys = ctx.keys();
//        if (!keys.isPending()) {
//
//            Date ts = ctx.timestamp();
//            String user = SecurityUtils.getCurrentUserName();
//            RecordUpsertChangeSet set = ctx.changeSet();
//
//            RecordEtalonPO result = new RecordEtalonPO();
//            result.setName(keys.getEntityName());
//            result.setStatus(keys.getEtalonKey().getStatus());
//            result.setApproval(ApprovalState.PENDING);
//            result.setId(keys.getEtalonKey().getId());
//            result.setUpdateDate(ts);
//            result.setUpdatedBy(user);
//            result.setOperationId(ctx.getOperationId());
//            set.setEtalonRecordUpdatePO(result);
//
//            RecordKeys newKeys = RecordKeys.builder(keys)
//                .etalonKey(
//                    RecordEtalonKey.builder(keys.getEtalonKey())
//                        .state(ApprovalState.PENDING)
//                        .build())
//                .build();
//
//            ctx.keys(newKeys);
//        }
//    }
}
