/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.unidata.mdm.data.service.segments.relations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.timeline.MutableTimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.impl.CommonRelationsComponent;
import org.unidata.mdm.data.service.impl.RelationComposerComponent;
import org.unidata.mdm.data.service.segments.ContainmentRelationSupport;
import org.unidata.mdm.data.type.apply.RelationUpsertChangeSet;
import org.unidata.mdm.data.type.apply.batch.impl.RelationUpsertBatchSet;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Dec 8, 2019
 */
@Component(RelationUpsertTimelineExecutor.SEGMENT_ID)
public class RelationUpsertTimelineExecutor extends Point<UpsertRelationRequestContext> implements ContainmentRelationSupport {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_UPSERT_TIMELINE]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.upsert.timeline.description";
    /**
     * The composer.
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
    public RelationUpsertTimelineExecutor() {
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
            if (ctx.relationType() == RelationType.CONTAINS) {
                processContainment(ctx);
            } else if (ctx.relationType() == RelationType.REFERENCES) {
                processReference(ctx);
            } else {
                processRelTo(ctx);
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

    /**
     * Does real rel to etalon calculation.
     * @param ctx the context
     * @return etalon relation
     */
    private void processReference(UpsertRelationRequestContext ctx) {

        RelationKeys keys = ctx.relationKeys();
        RelationUpsertChangeSet set = ctx.changeSet();

        Date ts = ctx.timestamp();
        String user = SecurityUtils.getCurrentUserName();

        // Merge result to a next TL
        Timeline<OriginRelation> prev = ctx.currentTimeline();
        Timeline<OriginRelation> next = null;
        MutableTimeInterval<OriginRelation> box = ctx.modificationBox();

        // UN-10682 Cannot be reduced due to batch processing
        // prev = prev.reduceBy(box.getValidFrom(), box.getValidTo());
        next = prev.merge(box);

        // Refresh view
        next.forEach(i -> relationComposerComponent.toEtalon(keys, i, ts, user));
        ctx.nextTimeline(next);

        // UN-10682 May be read from batch cache
        List<Timeline<OriginRelation>> oldVirtual = commonRelationsComponent.loadOrReuseCachedTimelines(ctx);
        List<Timeline<OriginRelation>> newVirtual = new ArrayList<>(oldVirtual);

        newVirtual.removeIf(timeline -> keys.getEtalonKey().getId().equals(timeline.getKeys().getEtalonKey().getId()));
        newVirtual.add(next);

        oldVirtual = commonRelationsComponent.buildVirtualTimelinesForReferences(oldVirtual);
        newVirtual = commonRelationsComponent.buildVirtualTimelinesForReferences(newVirtual);

        // Special case for references
        // Add this to 'current' and to 'next'
        // to enable post-processing by other segments
        ctx.previousReferences(oldVirtual);
        ctx.nextReferences(newVirtual);

        // Save for possible subsequent calls in case of batched execution
        if (set instanceof RelationUpsertBatchSet) {
            ((RelationUpsertBatchSet) set)
                .addCachedReferenceTimelines(
                        keys.getEtalonKey().getFrom().getId(),
                        keys.getRelationName(), newVirtual);
        }
    }

    /**
     * Does real rel to etalon calculation.
     * @param ctx the context
     * @return etalon relation
     */
    private void processRelTo(UpsertRelationRequestContext ctx) {

        RelationKeys keys = ctx.relationKeys();
        Date ts = ctx.timestamp();
        String user = SecurityUtils.getCurrentUserName();

        // Merge result to a next TL
        Timeline<OriginRelation> prev = ctx.currentTimeline();
        Timeline<OriginRelation> next = null;
        MutableTimeInterval<OriginRelation> box = ctx.modificationBox();

        next = prev.merge(box).reduceBy(prev.earliestFrom(box.getValidFrom()), prev.latestTo(box.getValidTo()));
        next.forEach(i -> relationComposerComponent.toEtalon(keys, i, ts, user));

        ctx.nextTimeline(next);
    }
    /**
     * Does real conntainnment etalon calculation.
     * @param ctx the context
     * @return etalon relation
     */
    private void processContainment(UpsertRelationRequestContext ctx) {

        UpsertRequestContext uCtx = ctx.containmentContext();
        RelationKeys keys = ctx.relationKeys();

        ctx.nextTimeline(mirrorTimeline(keys, uCtx.nextTimeline()));
    }
}
