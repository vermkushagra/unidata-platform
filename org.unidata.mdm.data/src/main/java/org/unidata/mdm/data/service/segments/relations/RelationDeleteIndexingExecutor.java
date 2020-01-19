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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.PeriodIdUtils;
import org.unidata.mdm.data.context.DeleteRelationRequestContext;
import org.unidata.mdm.data.convert.RelationIndexingConverter;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.apply.RelationDeleteChangeSet;
import org.unidata.mdm.data.type.apply.RelationUpsertChangeSet;
import org.unidata.mdm.data.type.data.EtalonRelation;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.meta.type.search.RelationFromIndexId;
import org.unidata.mdm.meta.type.search.RelationToIndexId;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.search.type.id.ManagedIndexId;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * Executor responsible for modifying relations have an alias key.
 */
@Component(RelationDeleteIndexingExecutor.SEGMENT_ID)
public class RelationDeleteIndexingExecutor extends Point<DeleteRelationRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_DELETE_INDEXING]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.delete.indexing.description";
    /**
     * Constructor.
     */
    public RelationDeleteIndexingExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(DeleteRelationRequestContext dCtx) {

        // Only iactivateEtalon is handled right now
        if (dCtx.isInactivateOrigin()) {
            return;
        }

        if (dCtx.relationType() == RelationType.REFERENCES) {
            handleReference(dCtx);
        } else {

            if (dCtx.isInactivatePeriod()) {
                handleDeletePeriod(dCtx);
            } else {
                handleDeleteRecord(dCtx);
            }
        }
    }

    private void handleReference(DeleteRelationRequestContext ctx) {

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

    private void handleDeletePeriod(DeleteRelationRequestContext dCtx) {

        RelationKeys keys = dCtx.relationKeys();
        Date from = dCtx.relationType() == RelationType.CONTAINS ? dCtx.containmentContext().getValidFrom() : dCtx.getValidFrom();
        Date to = dCtx.relationType() == RelationType.CONTAINS ? dCtx.containmentContext().getValidTo() : dCtx.getValidTo();

        List<TimeInterval<OriginRelation>> prev = dCtx.currentTimeline().selectBy(from, to);
        List<TimeInterval<OriginRelation>> next = dCtx.nextTimeline().selectBy(from, to);

        List<ManagedIndexId> deletes = new ArrayList<>();
        for (TimeInterval<OriginRelation> it : prev) {

            deletes.addAll(Arrays.asList(
                    RelationFromIndexId.of(
                            keys.getFromEntityName(),
                            keys.getRelationName(),
                            keys.getEtalonKey().getFrom().getId(),
                            keys.getEtalonKey().getTo().getId(),
                            PeriodIdUtils.ensureDateValue(it.getValidTo())),
                    RelationToIndexId.of(
                            keys.getToEntityName(),
                            keys.getRelationName(),
                            keys.getEtalonKey().getFrom().getId(),
                            keys.getEtalonKey().getTo().getId(),
                            PeriodIdUtils.ensureDateValue(it.getValidTo()))
                 ));
        }

        List<EtalonRelation> updates = new ArrayList<>();
        for (TimeInterval<OriginRelation> it : next) {
            if (it.isActive()) {
                updates.add(it.getCalculationResult());
            }
        }

        RelationDeleteChangeSet set = dCtx.changeSet();
        set.getIndexRequestContexts().add(IndexRequestContext.builder()
                .drop(true)
                .delete(deletes)
                // Keys should be already inactive to result in inactive state for the whole record
                .index(RelationIndexingConverter.convert(keys, updates))
                .entity(keys.getFromEntityName())
                .build());
    }

    private void handleDeleteRecord(DeleteRelationRequestContext dCtx) {

        RelationKeys keys = dCtx.relationKeys();
        Timeline<OriginRelation> prev = dCtx.currentTimeline();

        List<ManagedIndexId> deletes = new ArrayList<>();
        List<EtalonRelation> updates = new ArrayList<>();

        for (TimeInterval<OriginRelation> it : prev) {

            deletes.addAll(Arrays.asList(
                    RelationFromIndexId.of(
                            keys.getFromEntityName(),
                            keys.getRelationName(),
                            keys.getEtalonKey().getFrom().getId(),
                            keys.getEtalonKey().getTo().getId(),
                            PeriodIdUtils.ensureDateValue(it.getValidTo())),
                    RelationToIndexId.of(
                            keys.getToEntityName(),
                            keys.getRelationName(),
                            keys.getEtalonKey().getFrom().getId(),
                            keys.getEtalonKey().getTo().getId(),
                            PeriodIdUtils.ensureDateValue(it.getValidTo()))
                 ));

            updates.add(it.getCalculationResult());
        }

        RelationDeleteChangeSet changeSet = dCtx.changeSet();
        changeSet.getIndexRequestContexts().add(IndexRequestContext.builder()
                .drop(true)
                .entity(keys.getFromEntityName())
                // Keys should be already inactive to result in inactive state for the whole record
                .index(RelationIndexingConverter.convert(keys, updates))
                .delete(deletes)
                .build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return DeleteRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
