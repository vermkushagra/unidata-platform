package org.unidata.mdm.data.service.segments.relations;

import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.calculables.ModificationBox;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.po.data.RelationVistoryPO;
import org.unidata.mdm.data.service.RecordChangeSetProcessor;
import org.unidata.mdm.data.service.RelationChangeSetProcessor;
import org.unidata.mdm.data.type.apply.RecordUpsertChangeSet;
import org.unidata.mdm.data.type.apply.RelationUpsertChangeSet;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.data.UpsertAction;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.service.PlatformConfiguration;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;
import org.unidata.mdm.system.util.IdUtils;

/**
 * @author Mikhail Mikhailov on Dec 8, 2019
 */
@Component(RelationUpsertPersistenceExecutor.SEGMENT_ID)
public class RelationUpsertPersistenceExecutor extends Point<UpsertRelationRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_UPSERT_PERSISTENCE]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.upsert.persistence.description";
    /**
     * PC link.
     */
    @Autowired
    private PlatformConfiguration platformConfiguration;
    /**
     * The set processor.
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
    public RelationUpsertPersistenceExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(UpsertRelationRequestContext ctx) {

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
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }

    private void applyChangeSet(UpsertRelationRequestContext ctx) {

        // Will be applied later in batched fashion.
        if (ctx.isBatchOperation()) {
            return;
        }

        // 2. Apply
        if (ctx.relationType() == RelationType.CONTAINS) {
            UpsertRequestContext uCtx = ctx.containmentContext();
            RecordUpsertChangeSet set = uCtx.changeSet();
            recordChangeSetProcessor.apply(set);
        }

        RelationUpsertChangeSet set = ctx.changeSet();
        relationChangeSetProcessor.apply(set);
    }

    private void prepareChangeSet(UpsertRelationRequestContext ctx) {

        // 2. Collect objects
        if (ctx.relationType() != RelationType.CONTAINS) {

            RelationKeys keys = ctx.relationKeys();
            String user = SecurityUtils.getCurrentUserName();
            RelationUpsertChangeSet set = ctx.changeSet();
            ModificationBox<OriginRelation> box = ctx.modificationBox();
            ApprovalState state = ApprovalState.APPROVED;

            for (Entry<String, List<CalculableHolder<OriginRelation>>> entry : box.toModifications().entrySet()) {

                for (CalculableHolder<OriginRelation> ch : entry.getValue()) {

                    RelationVistoryPO po = new RelationVistoryPO();
                    po.setId(IdUtils.v1String());
                    po.setShard(keys.getShard());
                    po.setOriginId(ch.getValue().getInfoSection().getRelationOriginKey().getId());
                    po.setOperationId(ctx.getOperationId());
                    po.setValidFrom(ch.getValue().getInfoSection().getValidFrom());
                    po.setValidTo(ch.getValue().getInfoSection().getValidTo());
                    po.setCreatedBy(user);
                    po.setCreateDate(ch.getValue().getInfoSection().getCreateDate());
                    po.setData(ch.getValue());
                    po.setStatus(ch.getValue().getInfoSection().getStatus());
                    po.setShift(ch.getValue().getInfoSection().getShift());
                    po.setOperationType(ch.getValue().getInfoSection().getOperationType());
                    po.setApproval(state);
                    po.setMajor(platformConfiguration.getPlatformMajor());
                    po.setMinor(platformConfiguration.getPlatformMinor());

                    set.getOriginsVistoryRelationsPOs().add(po);
                }
            }
        }
    }
}
