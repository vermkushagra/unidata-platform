package org.unidata.mdm.data.service.segments.relations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.PeriodIdUtils;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.convert.RelationIndexingConverter;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.apply.RelationUpsertChangeSet;
import org.unidata.mdm.data.type.data.EtalonRelation;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.data.UpsertAction;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.meta.type.search.RelationFromIndexId;
import org.unidata.mdm.meta.type.search.RelationToIndexId;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.search.type.id.ManagedIndexId;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Dec 8, 2019
 */
@Component(RelationUpsertIndexingExecutor.SEGMENT_ID)
public class RelationUpsertIndexingExecutor extends Point<UpsertRelationRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_UPSERT_INDEXING]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.upsert.indexing.description";
    /**
     * Constructor.
     */
    public RelationUpsertIndexingExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(UpsertRelationRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Upsert etalon
            if (ctx.relationType() != RelationType.REFERENCES) {
                processRelToOrContainmentIndexing(ctx);
            } else {
                processReferenceIndexing(ctx);
            }

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

    private void processReferenceIndexing(UpsertRelationRequestContext ctx) {

        List<Timeline<OriginRelation>> oldVirtual = ctx.previousReferences();
        List<Timeline<OriginRelation>> newVirtual = ctx.nextReferences();

        if (CollectionUtils.isEmpty(newVirtual)) {
            return;
        }

        RelationKeys keys = ctx.relationKeys();
        RelationUpsertChangeSet set = ctx.changeSet();

        List<EtalonRelation> updates = new ArrayList<>(newVirtual.size());
        List<ManagedIndexId> deletes = new ArrayList<>(oldVirtual.size());

        // Deletes
        for (Timeline<OriginRelation> timeline : oldVirtual) {
            for (TimeInterval<OriginRelation> interval : timeline) {
                EtalonRelation etalon = interval.getCalculationResult();
                deletes.add(RelationFromIndexId.of(
                        etalon.getInfoSection().getFromEntityName(),
                        etalon.getInfoSection().getRelationName(),
                        etalon.getInfoSection().getFromEtalonKey().getId(),
                        etalon.getInfoSection().getToEtalonKey().getId(),
                        PeriodIdUtils.periodIdFromDate(interval.getValidTo())));
                deletes.add(RelationToIndexId.of(
                        etalon.getInfoSection().getToEntityName(),
                        etalon.getInfoSection().getRelationName(),
                        etalon.getInfoSection().getFromEtalonKey().getId(),
                        etalon.getInfoSection().getToEtalonKey().getId(),
                        PeriodIdUtils.periodIdFromDate(interval.getValidTo())));
            }
        }

        // Updates
        for (Timeline<OriginRelation> timeline : newVirtual) {
            for (TimeInterval<OriginRelation> interval : timeline) {
                if (interval.isActive()) {
                    updates.add(interval.getCalculationResult());
                }
            }
        }

        set.getIndexRequestContexts().add(IndexRequestContext.builder()
                .index(RelationIndexingConverter.convert(keys, updates))
                .delete(deletes)
                .drop(!deletes.isEmpty())
                .entity(keys.getFromEntityName())
                .build());
    }

    private void processRelToOrContainmentIndexing(UpsertRelationRequestContext ctx) {

        RelationKeys keys = ctx.relationKeys();
        UpsertAction action = ctx.upsertAction();
        RelationUpsertChangeSet set = ctx.changeSet();
        Timeline<?> prev = ctx.relationType() == RelationType.CONTAINS ? ctx.containmentContext().currentTimeline() : ctx.currentTimeline();
        Date from = ctx.relationType() == RelationType.CONTAINS ? ctx.containmentContext().getValidFrom() : ctx.getValidFrom();
        Date to = ctx.relationType() == RelationType.CONTAINS ? ctx.containmentContext().getValidTo() : ctx.getValidTo();
        Timeline<OriginRelation> next = ctx.nextTimeline();

        prev = prev.reduceBy(from, to);

        List<EtalonRelation> updates = new ArrayList<>(next.size());
        List<ManagedIndexId> deletes = new ArrayList<>(prev.size());

        // Deletes
        for (TimeInterval<?> interval : prev) {

            deletes.add(RelationFromIndexId.of(
                    keys.getFromEntityName(),
                    keys.getRelationName(),
                    keys.getEtalonKey().getFrom().getId(),
                    keys.getEtalonKey().getTo().getId(),
                    PeriodIdUtils.periodIdFromDate(interval.getValidTo())));
            deletes.add(RelationToIndexId.of(
                    keys.getToEntityName(),
                    keys.getRelationName(),
                    keys.getEtalonKey().getFrom().getId(),
                    keys.getEtalonKey().getTo().getId(),
                    PeriodIdUtils.periodIdFromDate(interval.getValidTo())));
        }

        // Updates
        for (TimeInterval<OriginRelation> interval : next) {
            if (interval.isActive()) {
                updates.add(interval.getCalculationResult());
            }
        }

        IndexRequestContext context = IndexRequestContext.builder()
                .index(RelationIndexingConverter.convert(keys, updates))
                .delete(deletes)
                .drop(action == UpsertAction.UPDATE)
                .entity(keys.getFromEntityName())
                .build();

        set.getIndexRequestContexts().add(context);
    }
}
