package org.unidata.mdm.data.service.segments;

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
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.service.PipelineService;
import org.unidata.mdm.system.service.PlatformConfiguration;
import org.unidata.mdm.system.type.pipeline.Pipeline;
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
     * Pipeline service
     */
    @Autowired
    private PipelineService pipelineService;
    /**
     * PC.
     */
    @Autowired
    private PlatformConfiguration platformConfiguration;
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

            boolean batchInsertWithKeys = ctx.isBatchUpsert()
                    && Objects.nonNull(ctx.keys())
                    && ctx.keys().getOriginKey().getRevision() == 0;

            RecordKeys keys = null;
            Timeline<OriginRecord> current = null;

            // 1. Pre-process, fetch identity and TL
            if (batchInsertWithKeys || Objects.isNull(ctx.keys())) {

                ensureAutogeneration(ctx);

                if (batchInsertWithKeys) {

                    // 1.1 Set up dummies for batch insert
                    current =  new RecordTimeline(keys);
                    keys = ctx.keys();
                } else {

                    // 1.2 Identify record and load TL, if possible
                    current = ensureCurrentTimeline(ctx);
                    keys = current.getKeys();
                }

                ctx.keys(keys);
                ctx.currentTimeline(current);

            // 2 Keys and TL might have been resolved and loaded by #select()
            } else {

                keys = ctx.keys();
                current = ctx.currentTimeline();
            }

            // 3. Set record timestamp
            Date ts = ctx.localTimestamp();
            if (ts == null) {
                ts = new Date(System.currentTimeMillis());
            }

            // 4. Set action type
            UpsertAction action = keys == null || batchInsertWithKeys
                    ? UpsertAction.INSERT
                    : UpsertAction.UPDATE;

            // 5. Check for mergeWithPreviousVersion flag and re-construct the record if possible
            if (action == UpsertAction.UPDATE && ctx.isMergeWithPreviousVersion()) {
                ensureMergeWithPreviousVersion(ctx, keys, current);
            }

            ctx.upsertAction(action);
            ctx.timestamp(ts);

            // 6. Finally, create keys, change set, system objects and init timeline
            if (Objects.isNull(ctx.changeSet())) {
                ctx.changeSet(new RecordUpsertChangeSet());
            }

        } finally {
            MeasurementPoint.stop();
        }
    }

    private void ensureAutogeneration(UpsertRequestContext ctx) {

        // 1. Select model element
        String entityName = selectEntityName(ctx);
        EntityModelElement info = metaModelService.getEntityModelElementById(entityName);

        // 2. Check for code attributes generation
        ensureCodeAttributeAutogeneration(info, ctx);

        // 3. Check for "ext id autogeneration" rules turned on and apply them
        ensureExternalIdAutoGeneration(info, ctx);
    }

    /**
     * Applies autogeneration rules for external ids.
     *
     * @param wrapper the wrapper
     * @param ctx the context
     * @return true, if applied, false otherwise
     */
    private boolean ensureExternalIdAutoGeneration(EntityModelElement wrapper, UpsertRequestContext ctx) {

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
            if (ctx.isBatchUpsert() && Objects.nonNull(ctx.keys())) {

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
    private boolean ensureCodeAttributeAutogeneration(EntityModelElement wrapper, UpsertRequestContext ctx) {

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
    private void ensureMergeWithPreviousVersion(UpsertRequestContext ctx, RecordKeys keys, Timeline<OriginRecord> current) {

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

    private Timeline<OriginRecord> ensureCurrentTimeline(UpsertRequestContext uCtx) {

        MeasurementPoint.start();
        try {

            GetRecordTimelineRequestContext tCtx = GetRecordTimelineRequestContext.builder(uCtx)
                        .fetchData(true)
                        .build();

            return commonRecordsComponent.loadTimeline(tCtx);
        } finally {
            MeasurementPoint.stop();
        }
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

                system = RecordFactoryUtils.createOriginRecordPO(sysCtx, sysKeys, RecordStatus.ACTIVE);

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
     * {@inheritDoc}
     */
    @Override
    public Pipeline select(UpsertRequestContext ctx) {

        Pipeline result = null;

        // 1. Check for entity name, being present.
        String entityName = selectEntityName(ctx);
        if (Objects.nonNull(entityName)) {
            result = pipelineService.getPipeline(entityName);
        // 2. Do resolve (load keys and timeline), if entity name is not present.
        } else {
            // 2.1 This is either etalon id/origin id update or invalid request
            Timeline<OriginRecord> current = ensureCurrentTimeline(ctx);
            if (Objects.nonNull(current.getKeys())) {

                entityName = current.<RecordKeys>getKeys().getEntityName();
                result = pipelineService.getPipeline(current.<RecordKeys>getKeys().getEntityName());

                ctx.currentTimeline(current);
                ctx.keys(current.getKeys());
            }
        }

        // 3. Throw, if nothing works, because this indicates invalid input
        if (Objects.isNull(result)) {
            final String message = "No configured pipeline for entity name '{}'.";
            LOGGER.warn(message, entityName);
            throw new PlatformFailureException(message, DataExceptionIds.EX_DATA_UPSERT_RECORD_NO_SELECTABLE_PIPELINE, entityName);
        }

        return result;
    }
}
