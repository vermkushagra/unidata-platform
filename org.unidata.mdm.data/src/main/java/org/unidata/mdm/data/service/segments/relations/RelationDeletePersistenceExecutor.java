package org.unidata.mdm.data.service.segments.relations;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.DeleteRelationRequestContext;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.po.EtalonRelationDraftStatePO;
import org.unidata.mdm.data.po.data.RelationEtalonPO;
import org.unidata.mdm.data.po.data.RelationOriginPO;
import org.unidata.mdm.data.po.data.RelationVistoryPO;
import org.unidata.mdm.data.po.keys.RecordKeysPO;
import org.unidata.mdm.data.po.keys.RelationExternalKeyPO;
import org.unidata.mdm.data.po.keys.RelationKeysPO;
import org.unidata.mdm.data.po.keys.RelationOriginKeyPO;
import org.unidata.mdm.data.service.RecordChangeSetProcessor;
import org.unidata.mdm.data.service.RelationChangeSetProcessor;
import org.unidata.mdm.data.type.apply.RecordDeleteChangeSet;
import org.unidata.mdm.data.type.apply.RelationDeleteChangeSet;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.data.type.keys.RelationOriginKey;
import org.unidata.mdm.data.util.RecordFactoryUtils;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Dec 9, 2019
 */
@Component(RelationDeletePersistenceExecutor.SEGMENT_ID)
public class RelationDeletePersistenceExecutor extends Point<DeleteRelationRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_DELETE_PERSISTENCE]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.delete.persistence.description";
    /**
     * Rels change set.
     */
    @Autowired
    private RelationChangeSetProcessor relationChangeSetProcessor;
    /**
     * The record set processor.
     */
    @Autowired
    private RecordChangeSetProcessor recordChangeSetProcessor;
    /**
     * Constructor.
     */
    public RelationDeletePersistenceExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(DeleteRelationRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Prepare set
            prepareChangeSet(ctx);

            // 2. Apply changes
            applyChangeSet(ctx);

        } finally {
            MeasurementPoint.stop();
        }
    }

    private void applyChangeSet(DeleteRelationRequestContext ctx) {

        // Will be applied later in batched fashion.
        if (ctx.isBatchOperation()) {
            return;
        }

        // 2. Apply
        if (ctx.relationType() == RelationType.CONTAINS) {
            DeleteRequestContext uCtx = ctx.containmentContext();
            RecordDeleteChangeSet set = uCtx.changeSet();
            recordChangeSetProcessor.apply(set);
        }

        RelationDeleteChangeSet set = ctx.changeSet();
        relationChangeSetProcessor.apply(set);
    }

    private void prepareChangeSet(DeleteRelationRequestContext ctx) {

        // 2. Delete rel.
        RelationKeys keys = ctx.relationKeys();
        RelationDeleteChangeSet set = ctx.changeSet();
        Date ts = new Date(System.currentTimeMillis());
        String user = SecurityUtils.getCurrentUserName();

        if (ctx.isWipe()) {

            RecordKeysPO from = new RecordKeysPO();
            from.setId(keys.getEtalonKey().getFrom().getId());
            from.setLsn(keys.getEtalonKey().getFrom().getLsn());
            from.setShard(StorageUtils.shard(UUID.fromString(keys.getEtalonKey().getFrom().getId())));

            RecordKeysPO to = new RecordKeysPO();
            to.setId(keys.getEtalonKey().getTo().getId());
            to.setLsn(keys.getEtalonKey().getTo().getLsn());
            to.setShard(StorageUtils.shard(UUID.fromString(keys.getEtalonKey().getTo().getId())));

            RelationKeysPO po = new RelationKeysPO();
            po.setId(keys.getEtalonKey().getId());
            po.setLsn(keys.getEtalonKey().getLsn());
            po.setShard(keys.getShard());
            po.setName(keys.getRelationName());
            po.setRelationType(keys.getRelationType());
            po.setFromKeys(from);
            po.setToKeys(to);
            po.setOriginKeys(keys.getSupplementaryKeys().stream()
                .map(k -> {

                    RelationOriginKeyPO rkpo = new RelationOriginKeyPO();
                    rkpo.setId(UUID.fromString(k.getId()));
                    rkpo.setFromKey(UUID.fromString(k.getFrom().getId()));
                    rkpo.setToKey(UUID.fromString(k.getTo().getId()));

                    return rkpo;
                })
                .collect(Collectors.toList()));

            RelationExternalKeyPO ext = new RelationExternalKeyPO();
            ext.setFromShard(from.getShard());
            ext.setToShard(to.getShard());
            ext.setFromRecordEtalonId(UUID.fromString(from.getId()));
            ext.setToRecordEtalonId(UUID.fromString(to.getId()));
            ext.setRelationName(po.getName());
            ext.setRelationEtalonId(UUID.fromString(keys.getEtalonKey().getId()));

            set.getWipeRelationKeys().add(po);
            set.getWipeExternalKeys().add(ext);
        } else {

            ApprovalState state = ApprovalState.APPROVED;

            // 2. Relation etalon ID. Deactivate etalon and origins + WF support.
            if (ctx.isInactivateEtalon()) {
                inactivateEtalon(keys, set, state, ts, user, ctx.getOperationId());
            // 3. Relation origin ID. Deactivate relation origin only (no WF support for origins)
            } else if (ctx.isInactivateOrigin()) {

                RelationOriginPO opo = new RelationOriginPO();
                opo.setStatus(RecordStatus.INACTIVE);
                opo.setShard(keys.getShard());
                opo.setUpdateDate(ts);
                opo.setUpdatedBy(user);
                opo.setId(keys.getOriginKey().getId());
                set.getOriginRelationUpdatePOs().add(opo);
            // 4. Inactivate period
            } else if (ctx.isInactivatePeriod() && ctx.relationType() != RelationType.CONTAINS) {

                boolean hasActive = false;
                Timeline<OriginRelation> next = ctx.nextTimeline();
                for (TimeInterval<OriginRelation> it : next) {

                    if (it.isActive()) {
                        hasActive = true;
                        continue;
                    }

                    // No modifications expected for containments.
                    for (Entry<String, List<CalculableHolder<OriginRelation>>> entry : it.unlock().toModifications().entrySet()) {

                        for (CalculableHolder<OriginRelation> ch : entry.getValue()) {
                            RelationVistoryPO version
                                = RecordFactoryUtils.createInactiveRelationVistoryPO(
                                        keys,
                                        ctx.getOperationId(),
                                        ch.getValue().getInfoSection().getValidFrom(),
                                        ch.getValue().getInfoSection().getValidTo(),
                                        state);

                            set.getOriginsVistoryRelationsPOs().add(version);
                        }
                    }
                }

                // Timeline is completely inactive. Inactivate etalon.
                if (!hasActive) {
                    inactivateEtalon(keys, set, state, ts, user, ctx.getOperationId());
                }
            }
        }
    }

    private void inactivateEtalon(RelationKeys keys, RelationDeleteChangeSet set, ApprovalState state, Date ts, String user, String operationId) {

        if (state == ApprovalState.PENDING) {

            RelationEtalonPO epo = new RelationEtalonPO();
            epo.setId(keys.getEtalonKey().getId());
            epo.setShard(keys.getShard());
            epo.setStatus(keys.getEtalonKey().getStatus());
            epo.setApproval(state);
            epo.setUpdateDate(ts);
            epo.setUpdatedBy(user);
            epo.setOperationId(operationId);
            set.getEtalonRelationUpdatePOs().add(epo);

            EtalonRelationDraftStatePO draft = new EtalonRelationDraftStatePO();
            draft.setCreateDate(ts);
            draft.setCreatedBy(user);
            draft.setEtalonId(keys.getEtalonKey().getId());
            draft.setStatus(RecordStatus.INACTIVE);
            set.getEtalonRelationDraftStatePOs().add(draft);
        } else {

            for (RelationOriginKey key : keys.getSupplementaryKeys()) {

                RelationOriginPO opo = new RelationOriginPO();
                opo.setStatus(RecordStatus.INACTIVE);
                opo.setShard(keys.getShard());
                opo.setUpdateDate(ts);
                opo.setUpdatedBy(user);
                opo.setId(key.getId());
                set.getOriginRelationUpdatePOs().add(opo);
            }

            RelationEtalonPO epo = new RelationEtalonPO();
            epo.setId(keys.getEtalonKey().getId());
            epo.setShard(keys.getShard());
            epo.setStatus(RecordStatus.INACTIVE);
            epo.setApproval(state);
            epo.setUpdateDate(ts);
            epo.setUpdatedBy(user);
            epo.setOperationId(operationId);
            set.getEtalonRelationUpdatePOs().add(epo);
        }

    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return DeleteRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
