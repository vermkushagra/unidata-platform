package org.unidata.mdm.data.service.segments.records;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.CodeAttribute;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.data.impl.IntegerCodeAttributeImpl;
import org.unidata.mdm.core.type.data.impl.StringCodeAttributeImpl;
import org.unidata.mdm.core.type.model.EntityModelElement;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.GetRecordTimelineRequestContext;
import org.unidata.mdm.data.context.RecordIdentityContextSupport;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.po.data.RecordEtalonPO;
import org.unidata.mdm.data.po.data.RecordOriginPO;
import org.unidata.mdm.data.po.keys.RecordExternalKeysPO;
import org.unidata.mdm.data.service.impl.CommonRecordsComponent;
import org.unidata.mdm.data.service.segments.IdGenerationStrategySupport;
import org.unidata.mdm.data.type.apply.RecordUpsertChangeSet;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.UpsertAction;
import org.unidata.mdm.data.type.keys.RecordEtalonKey;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;
import org.unidata.mdm.data.type.timeline.RecordTimeline;
import org.unidata.mdm.data.util.RecordFactoryUtils;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.meta.AbstractExternalIdGenerationStrategyDef;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.type.info.impl.EntityInfoHolder;
import org.unidata.mdm.meta.type.info.impl.LookupInfoHolder;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;
import org.unidata.mdm.system.util.IdUtils;

/**
 * @author Mikhail Mikhailov
 *         Old 'ensure before' part of the ORC.
 */
@Component(RecordUpsertStartExecutor.SEGMENT_ID)
public class RecordUpsertStartExecutor
    extends Start<UpsertRequestContext>
    implements RecordIdentityContextSupport, IdGenerationStrategySupport {
    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordUpsertStartExecutor.class);
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_UPSERT_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.upsert.start.description";
    /**
     * Common functionality.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Constructor.
     */
    public RecordUpsertStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, UpsertRequestContext.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(UpsertRequestContext ctx) {
        MeasurementPoint.start();
        try {
            setup(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(UpsertRequestContext ctx) {
        MeasurementPoint.start();
        try {
            setup(ctx);
            RecordKeys keys = ctx.keys();
            return keys.getEntityName();
        } finally {
            MeasurementPoint.stop();
        }
    }

    private void setup(UpsertRequestContext ctx) {

        if (ctx.setUp()) {
            return;
        }

        // 0. Run autogeneration.
        // Entity name must exist on the context or batch insert keys for this to work.
        setupAutogeneration(ctx);

        // 1. Prepare change set
        setupChangeSet(ctx);

        // 2. Pre-process, fetch identity and TL, verify
        setupKeys(ctx);

        RecordKeys keys = ctx.keys();
        Timeline<OriginRecord> current = ctx.currentTimeline();

        // 3. Set record timestamp
        Date ts = ctx.localTimestamp();
        if (ts == null) {
            ts = new Date(System.currentTimeMillis());
        }
        ctx.timestamp(ts);

        // 4. Set action type
        UpsertAction action = keys.isNew() ? UpsertAction.INSERT : UpsertAction.UPDATE;
        ctx.upsertAction(action);

        // 5. Check for mergeWithPreviousVersion flag and re-construct the record if possible
        if (action == UpsertAction.UPDATE && ctx.isMergeWithPreviousVersion()) {
            setupMergeWithPreviousVersion(ctx, keys, current);
        }

        ctx.setUp(true);
    }

    private void setupAutogeneration(UpsertRequestContext ctx) {

        // 1. Select model element
        String entityName = selectEntityName(ctx);
        EntityModelElement info = metaModelService.getEntityModelElementById(entityName);

        // 2. Check for code attributes generation
        setupCodeAttributeAutogeneration(info, ctx);

        // 3. Check for "ext id autogeneration" rules turned on and apply them
        setupExternalIdAutoGeneration(info, ctx);
    }

    /**
     * Applies autogeneration rules for external ids.
     *
     * @param wrapper the wrapper
     * @param ctx the context
     * @return true, if applied, false otherwise
     */
    private boolean setupExternalIdAutoGeneration(EntityModelElement wrapper, UpsertRequestContext ctx) {

        if (!ctx.isEtalonRecordKey()
         && !ctx.isOriginRecordKey()
         && !ctx.isEnrichmentKey()
         && !ctx.isLsnKey()
         && (Objects.isNull(ctx.getExternalId()) && Objects.nonNull(ctx.getEntityName()) && Objects.nonNull(ctx.getSourceSystem()))) {

            AbstractExternalIdGenerationStrategyDef strategy = wrapper.isEntity()
                    ? ((EntityInfoHolder) wrapper).getEntity().getExternalIdGenerationStrategy()
                    : ((LookupInfoHolder) wrapper).getEntity().getExternalIdGenerationStrategy();

            if (strategy == null) {
                return false;
            }

            Object externalId = applyAutogenerationStrategy(ctx, ctx.getRecord(), strategy);
            if (externalId == null) {
                return false;
            }

            // 2.1 Check key length
            if (externalId.toString().length() > 512) {
                final String message = "Unable to generate externalId, using autogeneration strategy for entity {}. "
                        + "Generated key length exceeds the limit of 512 characters.";
                LOGGER.warn(message, ctx.getEntityName());
                throw new DataProcessingException(message,
                        DataExceptionIds.EX_DATA_UPSERT_ID_GENERATION_STRATEGY_KEY_LENGTH,
                        ctx.getEntityName());
            }

            ctx.setExternalId(externalId.toString());

            // 2.2 Batch insert with keys
            if (ctx.isBatchOperation() && Objects.nonNull(ctx.keys())) {

                RecordKeys keys = RecordKeys.builder(ctx.keys())
                        .originKey(RecordOriginKey.builder(ctx.keys().getOriginKey())
                                .externalId(externalId.toString())
                                .build())
                        .build();
                ctx.keys(keys);
            }

            return true;
        }

        return false;
    }

    /**
     * Applies autogeneration rules to lookup , if some defined.
     *
     * @param wrapper the wrapper
     * @param ctx the context
     * @return true, if applied, false otherwise
     */
    private boolean setupCodeAttributeAutogeneration(EntityModelElement wrapper, UpsertRequestContext ctx) {

        if (Objects.nonNull(wrapper) && wrapper.isLookup() && Objects.nonNull(ctx.getRecord())) {

            LookupEntityDef lookup = ((LookupInfoHolder) wrapper).getEntity();
            AbstractExternalIdGenerationStrategyDef strategy
                    = lookup.getCodeAttribute().getExternalIdGenerationStrategy();

            if (strategy == null) {
                return false;
            }

            // 1.1 Current value is set. Do not overwrite
            CodeAttribute<?> current = ctx.getRecord().getCodeAttribute(lookup.getCodeAttribute().getName());
            if (current != null && current.getValue() != null) {
                return false;
            }

            Object codeAttributeValue = applyAutogenerationStrategy(ctx, ctx.getRecord(), strategy);
            if (codeAttributeValue == null) {
                return false;
            }

            switch (lookup.getCodeAttribute().getSimpleDataType()) {
                case STRING:

                    if (current != null) {
                        current.castValue(codeAttributeValue.toString());
                    } else {
                        ctx.getRecord()
                                .addAttribute(new StringCodeAttributeImpl(
                                        lookup.getCodeAttribute().getName(), codeAttributeValue.toString()));
                    }
                    break;
                case INTEGER:

                    if (current != null) {
                        current.castValue(codeAttributeValue);
                    } else {
                        ctx.getRecord()
                                .addAttribute(new IntegerCodeAttributeImpl(
                                        lookup.getCodeAttribute().getName(), (Long) codeAttributeValue));
                    }
                    break;
                default:
                    break;
            }

            return true;
        }

        return false;
    }

    /**
     * Loads previous version of the data and merges the input into it.
     *
     * @param ctx current context
     * @param current current timeline
     * @return merged record
     */
    private void setupMergeWithPreviousVersion(UpsertRequestContext ctx, RecordKeys keys, Timeline<OriginRecord> current) {

        if (Objects.isNull(keys) || Objects.isNull(keys.getOriginKey())) {
            return;
        }

        Date recordFrom = ctx.getValidFrom();
        Date recordTo = ctx.getValidTo();
        OriginRecord prevOrigin = null;
        TimeInterval<OriginRecord> selected = current.selectAsOf(nonNull(recordFrom) ? recordFrom : recordTo);
        if (Objects.nonNull(selected)) {
            for (CalculableHolder<OriginRecord> ch : selected) {
                if (ch.toBoxKey().equals(keys.getOriginKey().toBoxKey())) {
                    prevOrigin = ch.getValue();
                    break;
                }
            }
        }

        if (isNull(prevOrigin)) {
            return;
        }

        // 1st level only
        DataRecord upsert = ctx.getRecord();
        for (Attribute attr : prevOrigin.getAllAttributes()) {
            if (upsert.getAttribute(attr.getName()) != null) {
                continue;
            }
            upsert.addAttribute(attr);
        }
    }

    private void setupChangeSet(UpsertRequestContext ctx) {
        if (Objects.isNull(ctx.changeSet())) {
            ctx.changeSet(new RecordUpsertChangeSet());
        }
    }

    private void setupTimeline(UpsertRequestContext uCtx) {

        MeasurementPoint.start();
        try {

            RecordKeys keys = uCtx.keys();
            boolean batchInsert = uCtx.isBatchOperation()
                    && Objects.nonNull(keys)
                    && keys.getOriginKey() != null
                    && keys.getOriginKey().getRevision() == 0;

            // 1. In cases, other then batch insert try to load current timeline
            if (!batchInsert) {

                GetRecordTimelineRequestContext tlCtx = GetRecordTimelineRequestContext.builder(uCtx)
                        .fetchData(true)
                        .build();

                tlCtx.keys(keys);

                Timeline<OriginRecord> current = commonRecordsComponent.loadTimeline(tlCtx);

                uCtx.currentTimeline(current);
                uCtx.keys(current.getKeys());

            } else {
                Timeline<OriginRecord> current = new RecordTimeline(keys);
                uCtx.currentTimeline(current);
            }

        } finally {
            MeasurementPoint.stop();
        }
    }

    private void setupVerify(UpsertRequestContext ctx) {

        // 1. Check input (presence of records themselves)
        if (!ctx.isRecalculateWholeTimeline() && !ctx.isEtalon() && !ctx.isOrigin()) {
            final String message = "Invalid upsert request context. Either etalon data or origin data or keys invalid / missing. Upsert rejected.";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_NO_INPUT, ctx);
        }

        RecordKeys keys = ctx.keys();

        // 2. Check supplied keys validity.
        if (keys == null && ((ctx.isOrigin() && ctx.isOriginRecordKey()) || ctx.isEtalonRecordKey())) {
            final String message = "Record can not be identified by supplied keys. Upsert rejected.";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_INVALID_KEYS);
        }

        // 3. Origin is inactive, discard updates
        if (keys != null && keys.getOriginKey() != null && keys.getOriginKey().getStatus() == RecordStatus.INACTIVE) {
            final String message = "Origin [Ext. ID: {}, Source system: {}, Entity name: {}] is inactive. Upsert rejected.";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_ORIGIN_INACTIVE,
                    keys.getOriginKey().getExternalId(),
                    keys.getOriginKey().getSourceSystem(),
                    keys.getOriginKey().getEntityName());
        } else if (keys != null && keys.getEtalonKey() != null && keys.getEtalonKey().getStatus() == RecordStatus.INACTIVE) {
            final String message = "Etalon [ID: {}] is inactive. Upsert rejected.";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_ETALON_INACTIVE, keys.getEtalonKey().getId());
        }

        // 4. Check key combination validity
        if (ctx.isOrigin() && !ctx.isOriginExternalId() && !ctx.isOriginRecordKey()) {
            final String message = "Cannot upsert origin record. Neither valid external id nor origin record key has been supplied. Upsert rejected.";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_INVALID_ORIGIN_INPUT);
        }

        // 5. Check source system.
        if (ctx.isOrigin() && ctx.isOriginExternalId() && metaModelService.getSourceSystemById(ctx.getSourceSystem()) == null) {
            String message = "Valid source system should be defined.";
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_NO_SOURCE_SYSTEM);
        }
    }

    /**
     * Prepare origin upsert between.
     * Creates O/E records if necessary and resets keys in the context.
     * @param ctx the context to prepare
     */
    private void setupKeys(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. In cases, other then batch insert try to load current timeline
            setupTimeline(ctx);

            // 2. Run verify
            setupVerify(ctx);

            // 3. Process keys actually
            RecordKeys keys = ctx.keys();
            Date ts = ctx.timestamp();
            String user = SecurityUtils.getCurrentUserName();
            RecordUpsertChangeSet changeSet = ctx.changeSet();

            boolean hasEtalonRecord = keys != null && keys.getEtalonKey() != null && keys.getEtalonKey().getId() != null;
            boolean hasOriginRecord = keys != null && keys.getOriginKey() != null && keys.getOriginKey().getId() != null;

            // 3.1. Create etalon
            if (!hasEtalonRecord) {

                RecordEtalonPO etalon = RecordFactoryUtils.createRecordEtalonPO(ctx, keys, RecordStatus.ACTIVE);
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

            // 3.2. Create origin
            if (!hasOriginRecord) {

                RecordOriginPO record = RecordFactoryUtils.createRecordOriginPO(ctx, keys, RecordStatus.ACTIVE);
                RecordExternalKeysPO recordEk = new RecordExternalKeysPO();
                recordEk.setExternalId(record.getExternalId(), record.getName(), record.getSourceSystem());
                recordEk.setEtalonId(UUID.fromString(record.getEtalonId()));

                RecordOriginPO system = null;
                RecordExternalKeysPO systemEk = null;

                // Check for first upsert and create
                // UD origin, if the upsert is not a UD upsert.
                if (!hasEtalonRecord && !metaModelService.getAdminSourceSystem().getName().equals(record.getSourceSystem())) {

                    UpsertRequestContext sysCtx = UpsertRequestContext.builder()
                            .sourceSystem(metaModelService.getAdminSourceSystem().getName())
                            .entityName(record.getName())
                            .externalId(IdUtils.v1String())
                            .build();

                    RecordKeys sysKeys = RecordKeys.builder()
                            .etalonKey(keys.getEtalonKey())
                            .shard(keys.getShard())
                            .build();

                    sysCtx.timestamp(ts);

                    system = RecordFactoryUtils.createRecordOriginPO(sysCtx, sysKeys, RecordStatus.ACTIVE);

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

        } finally {
            MeasurementPoint.stop();
        }
    }
}
