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

package org.unidata.mdm.data.service.segments.records;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.calculables.ModificationBox;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.OperationType;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.timeline.MutableTimeInterval;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.impl.RecordComposerComponent;
import org.unidata.mdm.data.type.apply.batch.impl.RecordUpsertBatchSet;
import org.unidata.mdm.data.type.calculables.impl.DataRecordHolder;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.UpsertAction;
import org.unidata.mdm.data.type.data.impl.OriginRecordImpl;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.timeline.RecordTimeline;
import org.unidata.mdm.data.util.DataDiffUtils;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Nov 10, 2019
 */
@Component(RecordUpsertTimelineExecutor.SEGMENT_ID)
public class RecordUpsertTimelineExecutor extends Point<UpsertRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_UPSERT_TIMELINE]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.upsert.timeline.description";
    /**
     * The MMS.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * The composer.
     */
    @Autowired
    private RecordComposerComponent recordComposerComponent;
    /**
     * Constructor.
     * @param id
     * @param description
     */
    public RecordUpsertTimelineExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // No action on NO_ACTION
            if (ctx.upsertAction() == UpsertAction.NO_ACTION) {
                return;
            }

            // 1. Reset mod box
            postProcessOrigin(ctx);

            // 2. Pre process timeline
            preProcessEtalon(ctx);

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
     * Resets the mod box and, possibly, reduces the supplied origin to diff, if this was submitted from the admin source system.
     * @param ctx the context to process
     */
    private void postProcessOrigin(UpsertRequestContext ctx) {

        // Finish origins part:
        // - Peek last modified OV from box and reset the box (we can store all abject in the future, if required)
        // - Check approval state and reset keys if needed
        ModificationBox<OriginRecord> box = ctx.modificationBox();
        if (Objects.isNull(box)) {
            // This might be recalculate call
            return;
        }

        CalculableHolder<OriginRecord> top = null;
        if (box.modifications(ctx.toBoxKey()) > 1) {

            // All versions. The box should be empty afterwards
            List<CalculableHolder<OriginRecord>> collected = box.reset(ctx.toBoxKey());
            top = collected.get(collected.size() - 1);
        } else {
            top = box.pop(ctx.toBoxKey());
        }

        // Check the upsert for being an update of the admin SS
        // If so, try to create diff origin.
        // Don't push on null diff (no changes)
        if (ctx.upsertAction() == UpsertAction.UPDATE
         && metaModelService.isAdminSourceSystem(top.getSourceSystem())) {
            top = postProcessAdminInput(ctx, top);
        }

        // Re-insert only the very last version to the box,
        // as we don't store intermediate versions and thus can save space
        if (top != null) {
            box.push(top);
        }
    }

    /**
     * Returns diff origin if the data has been submitted
     * for the admin source system and the action is an update.
     * @param ctx the context
     * @return diff origin or null
     */
    private CalculableHolder<OriginRecord> postProcessAdminInput(UpsertRequestContext ctx, CalculableHolder<OriginRecord> top) {

        Timeline<OriginRecord> current = ctx.currentTimeline();
        if (Objects.isNull(current) || current.isEmpty()) {
            return top;
        }

        List<TimeInterval<OriginRecord>> selection = current.selectBy(ctx.getValidFrom(), ctx.getValidTo());
        if (CollectionUtils.isEmpty(selection)
         || selection.size() > 1
         || !selection.get(0).isExact(ctx.getValidFrom(), ctx.getValidTo())
         // Special case, sometimes we need force create vistory. Example UN-9439
         || ctx.isApplyDraft()) {
            return top;
        }

        EtalonRecord prev = selection.get(0).getCalculationResult();
        if (Objects.isNull(prev) || (selection.get(0).isActive() != (top.getStatus() == RecordStatus.ACTIVE))) {
            // Forced to return original input on no data for a period
            // This is because we create a single revision for the whole period
            // and the only data version has to have all the necessary attributes.
            return top;
        }

        CalculableHolder<OriginRecord> base  = selection.get(0).unlock().peek(ctx.toBoxKey());
        boolean forceCodeAttributesCheck = ctx.operationType() == OperationType.COPY;
        DataRecord diff = DataDiffUtils.diffAsRecord(top.getTypeName(), top.getValue(), prev, base == null ? null : base.getValue(), forceCodeAttributesCheck);

        if (diff != null) {
            return new DataRecordHolder(new OriginRecordImpl()
                .withDataRecord(diff)
                .withInfoSection(top.getValue().getInfoSection()));
        }

        return null;
    }
    /**
     * Creates the 'next' timeline.
     * @param ctx the context
     */
    private void preProcessEtalon(UpsertRequestContext ctx) {

        UpsertAction action = ctx.upsertAction();

        Date ts = ctx.timestamp();
        String user = SecurityUtils.getCurrentUserName();
        RecordKeys keys = ctx.keys();
        MutableTimeInterval<OriginRecord> box = ctx.modificationBox();

        if (action == UpsertAction.INSERT) {

            recordComposerComponent.toEtalon(keys, box, ts, user);

            Timeline<OriginRecord> prev = ctx.currentTimeline();
            Timeline<OriginRecord> next = prev.merge(box);

            next.forEach(i -> recordComposerComponent.toEtalon(keys, i, ts, user));
            ctx.nextTimeline(next);

        } else if (action == UpsertAction.UPDATE) {

            Timeline<OriginRecord> prev = ctx.currentTimeline();
            Timeline<OriginRecord> next = null;

            // 2. Process box
            // If box is null
            if (Objects.isNull(box) || box.isEmpty()) {
                // Reindex or similar job
                // Recalculate (delete + add) the whole current timeline
                if (ctx.isRecalculateWholeTimeline()) {
                    next = prev;
                // Unclear. Let's delete the TL from index.
                } else {
                    next = new RecordTimeline(keys);
                }
            // If box is not null
            } else {
                 next = prev;
                 prev = applyCachedModificationsIfNeed(ctx, prev);
                 next = applyCachedModificationsIfNeed(ctx, next);

                next = next.merge(box).reduceBy(prev.earliestFrom(ctx.getValidFrom()), prev.latestTo(ctx.getValidTo()));
                prev = prev.reduceBy(ctx.getValidFrom(), ctx.getValidTo());
                next.forEach(i -> recordComposerComponent.toEtalon(keys, i, ts, user));
            }

            ctx.currentTimeline(prev);
            ctx.nextTimeline(next);
        }
    }
    /**
     * Does apply of possib
     * @param ctx
     * @param next
     * @return
     */
    private Timeline<OriginRecord> applyCachedModificationsIfNeed(UpsertRequestContext ctx, Timeline<OriginRecord> next) {

        if (ctx.changeSet() instanceof RecordUpsertBatchSet) {

            List<MutableTimeInterval<OriginRecord>> cachedModifications = ctx.<RecordUpsertBatchSet>changeSet()
                    .getRecordsAccumulator()
                    .findCachedModifications(ctx);

            if (CollectionUtils.isNotEmpty(cachedModifications)) {
                for (MutableTimeInterval<OriginRecord> cachedModification : cachedModifications) {
                    next = next.merge(cachedModification);
                }
            }
        }

        return next;
    }
}
