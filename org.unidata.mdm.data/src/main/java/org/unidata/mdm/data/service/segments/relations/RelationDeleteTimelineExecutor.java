package org.unidata.mdm.data.service.segments.relations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.DataShift;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.data.impl.SerializableDataRecord;
import org.unidata.mdm.core.type.timeline.MutableTimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.DeleteRelationRequestContext;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.impl.CommonRelationsComponent;
import org.unidata.mdm.data.service.impl.RelationComposerComponent;
import org.unidata.mdm.data.service.segments.ContainmentRelationSupport;
import org.unidata.mdm.data.type.apply.RelationDeleteChangeSet;
import org.unidata.mdm.data.type.apply.batch.impl.RelationDeleteBatchSet;
import org.unidata.mdm.data.type.calculables.impl.RelationRecordHolder;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.OriginRelationInfoSection;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.data.impl.OriginRelationImpl;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.data.type.timeline.RelationTimeInterval;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov
 * Prepares delete context.
 */
@Component(RelationDeleteTimelineExecutor.SEGMENT_ID)
public class RelationDeleteTimelineExecutor extends Point<DeleteRelationRequestContext> implements ContainmentRelationSupport {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_DELETE_TIMELINE]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.delete.timeline.description";
    /**
     * Composer component.
     */
    @Autowired
    private RelationComposerComponent relationComposerComponent;
    /**
     * The CC.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * Constructor.
     */
    public RelationDeleteTimelineExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(DeleteRelationRequestContext ctx) {

        // Containments are processed by record services entirely
        // Updates timeline for periods
        if (ctx.relationType() == RelationType.CONTAINS) {
            processContainment(ctx);
        } else if (ctx.relationType() == RelationType.REFERENCES) {
            processReference(ctx);
        } else {
            processRelTo(ctx);
        }
    }

    private void processContainment(DeleteRelationRequestContext ctx) {

        DeleteRequestContext uCtx = ctx.containmentContext();
        RelationKeys keys = ctx.relationKeys();

        ctx.nextTimeline(mirrorTimeline(keys, uCtx.nextTimeline()));
    }

    /**
     * Does real rel to etalon calculation.
     * @param ctx the context
     * @return etalon relation
     */
    private void processReference(DeleteRelationRequestContext ctx) {

        processRelTo(ctx);

        RelationKeys keys = ctx.relationKeys();
        Timeline<OriginRelation> next = ctx.isInactivatePeriod() ? ctx.nextTimeline() : null;

        // UN-10682 May be read from batch cache
        List<Timeline<OriginRelation>> oldVirtual = commonRelationsComponent.loadOrReuseCachedTimelines(ctx);
        List<Timeline<OriginRelation>> newVirtual = new ArrayList<>(oldVirtual);

        newVirtual.removeIf(timeline -> keys.getEtalonKey().getId().equals(timeline.getKeys().getEtalonKey().getId()));
        if (ctx.isInactivatePeriod()) {
            newVirtual.add(next);
        }

        oldVirtual = commonRelationsComponent.buildVirtualTimelinesForReferences(oldVirtual);
        newVirtual = commonRelationsComponent.buildVirtualTimelinesForReferences(newVirtual);

        // Special case for references
        // Add this to 'current' and to 'next'
        // to enable post-processing by other segments
        ctx.previousReferences(oldVirtual);
        ctx.nextReferences(newVirtual);

        // Save for possible subsequent calls in case of batched execution
        RelationDeleteChangeSet set = ctx.changeSet();
        if (set instanceof RelationDeleteBatchSet) {
            ((RelationDeleteBatchSet) set)
                .addCachedReferenceTimelines(
                        keys.getEtalonKey().getFrom().getId(),
                        keys.getRelationName(), newVirtual);
        }
    }

    private void processRelTo(DeleteRelationRequestContext ctx) {

        if (ctx.isInactivatePeriod()) {

            Date ts = ctx.timestamp();
            RelationKeys relationKeys = ctx.relationKeys();
            String user = SecurityUtils.getCurrentUserName();

            // Push upsert
            OriginRelation origin = new OriginRelationImpl()
                    .withDataRecord(new SerializableDataRecord())
                    .withInfoSection(new OriginRelationInfoSection()
                            .withRelationName(relationKeys.getRelationName())
                            .withRelationType(relationKeys.getRelationType())
                            .withValidFrom(ctx.getValidFrom())
                            .withValidTo(ctx.getValidTo())
                            .withFromEntityName(relationKeys.getFromEntityName())
                            .withToEntityName(relationKeys.getToEntityName())
                            .withStatus(RecordStatus.INACTIVE)
                            .withApproval(relationKeys.getEtalonKey().getState()) // <-- will be recalculated later
                            .withShift(DataShift.PRISTINE)
                            .withRelationOriginKey(relationKeys.getOriginKey())
                            .withCreateDate(ts)
                            .withUpdateDate(ts)
                            .withCreatedBy(user)
                            .withUpdatedBy(user));

            MutableTimeInterval<OriginRelation> box
                = new RelationTimeInterval(
                    ctx.getValidFrom(), ctx.getValidTo(),
                        Collections.singletonList(new RelationRecordHolder(origin)));

            // Not reducing the TL, because we have to understand,
            // if there are still some active period or the rel may be deleted entirely
            Timeline<OriginRelation> current = ctx.currentTimeline();
            Timeline<OriginRelation> next = current.merge(box);

            // Refresh view
            next.forEach(i -> relationComposerComponent.toEtalon(relationKeys, i, ts, user));

            ctx.nextTimeline(next);
        }
        // TODO : Generate inactive timeline for all other cases, especially for references.
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return DeleteRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
