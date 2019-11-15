package org.unidata.mdm.data.service.segments;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.timeline.MutableTimeInterval;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.po.data.RecordOriginPO;
import org.unidata.mdm.data.po.data.RecordVistoryPO;
import org.unidata.mdm.data.po.keys.RecordExternalKeysPO;
import org.unidata.mdm.data.service.RecordChangeSetProcessor;
import org.unidata.mdm.data.service.impl.RecordComposerComponent;
import org.unidata.mdm.data.type.apply.RecordUpsertChangeSet;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.UpsertAction;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;
import org.unidata.mdm.data.util.RecordFactoryUtils;
import org.unidata.mdm.system.service.PlatformConfiguration;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;
import org.unidata.mdm.system.type.support.IdentityHashSet;
import org.unidata.mdm.system.util.IdUtils;

/**
 * Persists collected changes (the change set).
 * @author Mikhail Mikhailov on Nov 8, 2019
 */
@Component(RecordUpsertPersistenceExecutor.SEGMENT_ID)
public class RecordUpsertPersistenceExecutor extends Point<UpsertRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_UPSERT_PERSISTENCE]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.upsert.persistence.description";
    /**
     * The set processor.
     */
    @Autowired
    private RecordChangeSetProcessor recordChangeSetProcessor;
    /**
     * Platform configuration.
     */
    @Autowired
    private PlatformConfiguration platformConfiguration;
    /**
     * Calculator.
     */
    @Autowired
    private RecordComposerComponent recordComposerComponent;
    /**
     * Constructor.
     * @param id
     * @param description
     */
    public RecordUpsertPersistenceExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void point(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            UpsertAction action = ctx.upsertAction();
            if (action == UpsertAction.NO_ACTION) {
                return;
            }

            // 1. Prepare set
            prepareChangeSet(ctx);

            // 2. Apply changes
            applyChangeSet(ctx);

        } finally {
            MeasurementPoint.stop();
        }
    }

    private void applyChangeSet(UpsertRequestContext ctx) {
        RecordUpsertChangeSet set = ctx.changeSet();
        recordChangeSetProcessor.apply(set);
    }

    private void prepareChangeSet(UpsertRequestContext ctx) {

        Date ts = ctx.timestamp();
        String user = SecurityUtils.getCurrentUserName();
        RecordKeys keys = ctx.keys();
        RecordUpsertChangeSet set = ctx.changeSet();
        Timeline<OriginRecord> next = ctx.nextTimeline();

        // 1. Versions stop list
        Set<CalculableHolder<OriginRecord>> revisions = new IdentityHashSet<>();

        for (TimeInterval<OriginRecord> i : next) {

            MutableTimeInterval<OriginRecord> mti = i.unlock();

            // IM-788 Do not re-write pre-calculated etalon result,
            // already processed by EtalonAttributesProcessingAfterExecutor.
            if (Objects.isNull(mti.getCalculationResult())) {
                recordComposerComponent.toEtalon(keys, mti, ts, user);
            }

            EtalonRecord etalon = mti.getCalculationResult();
            if (etalon == null) {
                continue;
            }

            for (Entry<String, List<CalculableHolder<OriginRecord>>> entry : mti.toModifications().entrySet()) {
                for (CalculableHolder<OriginRecord> ch : entry.getValue()) {

                    // Sort out revisions, which were already added.
                    if (revisions.contains(ch)) {
                        continue;
                    }

                    RecordOriginKey oKey = processOriginKey(entry.getKey(), keys, ch, ctx);

                    // Set okey, which may miss id field.
                    ch.getValue().getInfoSection().withOriginKey(oKey);

                    // Create vistory record and add it to the box.
                    RecordVistoryPO result = new RecordVistoryPO();
                    result.setShard(keys.getShard());
                    result.setCreatedBy(user);
                    result.setApproval(ApprovalState.APPROVED);
                    result.setShift(ch.getValue().getInfoSection().getShift());
                    result.setData(ch.getValue());
                    result.setId(IdUtils.v1String());
                    result.setOriginId(ch.getValue().getInfoSection().getOriginKey().getId());
                    result.setOperationId(ctx.getOperationId());
                    result.setOperationType(ch.getValue().getInfoSection().getOperationType());
                    result.setMajor(platformConfiguration.getPlatformMajor());
                    result.setMinor(platformConfiguration.getPlatformMinor());
                    result.setValidFrom(ch.getValue().getInfoSection().getValidFrom());
                    result.setValidTo(ch.getValue().getInfoSection().getValidTo());
                    result.setCreateDate(ctx.getLastUpdate() != null ? ctx.getLastUpdate() : ch.getValue().getInfoSection().getCreateDate());

                    set.getOriginsVistoryRecordPOs().add(result);

                    revisions.add(ch);
                }
            }
        }
    }

    private RecordOriginKey processOriginKey(String boxKey, RecordKeys keys, CalculableHolder<OriginRecord> ch, UpsertRequestContext ctx) {

        RecordOriginKey oKey = keys.findByBoxKey(boxKey);
        if (Objects.nonNull(oKey)) {
            return oKey;
        }

        // Origin doesn't exist. Create it.
        RecordUpsertChangeSet set = ctx.changeSet();
        RecordKeys enrichmentKeys = RecordKeys.builder()
                .entityName(keys.getEntityName())
                .etalonKey(keys.getEtalonKey())
                .shard(keys.getShard())
                .node(keys.getNode())
                .build();

        UpsertRequestContext enrichmentCtx = UpsertRequestContext.builder()
                .originKey(ch.getValue().getInfoSection().getOriginKey())
                .validFrom(ch.getValue().getInfoSection().getValidFrom())
                .validTo(ch.getValue().getInfoSection().getValidFrom())
                .record(ch.getValue())
                .enrichment(true)
                .etalonKey(keys.getEtalonKey())
                .batchUpsert(ctx.isBatchUpsert())
                .auditLevel(ctx.getAuditLevel())
                .lastUpdate(ctx.getLastUpdate())
                .build();

        enrichmentCtx.timestamp(ch.getValue().getInfoSection().getUpdateDate());

        RecordOriginPO okpo = RecordFactoryUtils.createOriginRecordPO(enrichmentCtx, enrichmentKeys, RecordStatus.ACTIVE);

        RecordExternalKeysPO ekpo = new RecordExternalKeysPO();
        ekpo.setExternalId(okpo.getExternalId(), okpo.getName(), okpo.getSourceSystem());
        ekpo.setEtalonId(UUID.fromString(okpo.getEtalonId()));

        set.getOriginRecordInsertPOs().add(okpo);
        set.getExternalKeysInsertPOs().add(ekpo);

        oKey = RecordOriginKey.builder()
                .enrichment(true)
                .entityName(keys.getEntityName())
                .id(okpo.getId())
                .initialOwner(UUID.fromString(keys.getEtalonKey().getId()))
                .revision(0)
                .externalId(ch.getValue().getInfoSection().getOriginKey().getExternalId())
                .sourceSystem(ch.getValue().getInfoSection().getOriginKey().getSourceSystem())
                .status(ch.getValue().getInfoSection().getStatus())
                .build();

        // The map is live. Crap.
        keys.getSupplementaryKeysByBoxKey().put(oKey.toBoxKey(), oKey);
        return oKey;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
