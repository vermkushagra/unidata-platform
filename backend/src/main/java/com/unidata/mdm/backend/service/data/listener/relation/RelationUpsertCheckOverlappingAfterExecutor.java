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

package com.unidata.mdm.backend.service.data.listener.relation;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.unidata.mdm.backend.common.context.GetRelationsRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.search.PeriodIdUtils;
import com.unidata.mdm.backend.common.search.id.RelationFromIndexId;
import com.unidata.mdm.backend.common.search.id.RelationToIndexId;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.Calculable;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.TimeInterval;
import com.unidata.mdm.backend.common.types.Timeline;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.data.relations.CommonRelationsComponent;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Executor responsible for modifying relations have an alias key.
 */
public class RelationUpsertCheckOverlappingAfterExecutor implements DataRecordAfterExecutor<UpsertRelationRequestContext> {
    /**
     * Common relations component.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;

    @Autowired
    private RelationsServiceComponent relationsServiceComponent;

    @Autowired
    private SearchServiceExt searchService;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRelationRequestContext uCtx) {

        RelationDef relationDef = uCtx.getFromStorage(StorageId.RELATIONS_META_DEF);
        if (relationDef.getRelType() != RelType.REFERENCES) {
            return true;
        }

        RelationKeys keys = uCtx.relationKeys();
        Date validFrom = uCtx.getValidFrom();
        Date validTo = uCtx.getValidTo();

        // Check references for overlapping. Only one reference of a type is allowed for a period
        GetRelationsRequestContext ctx = GetRelationsRequestContext.builder()
                .forDatesFrame(new ImmutablePair<Date, Date>(validFrom, validTo))
                .etalonKey(keys.getFrom().getEtalonKey().getId())
                .relationNames(relationDef.getName())
                .build();

        ctx.putToStorage(StorageId.RELATIONS_META_DEF, relationDef);
        ctx.putToStorage(ctx.keysId(), keys.getFrom());

        List<Timeline<Calculable>> timelines = commonRelationsComponent.loadRelationsTimeline(ctx, false);
        for (Timeline<Calculable> timeline : timelines) {

            // Skip self
            RelationKeys other = timeline.getKeys();
            if (Objects.equals(other.getEtalonId(), keys.getEtalonId())) {
                continue;
            }

            boolean alreadyInactive = false;
            for (TimeInterval<Calculable> interval : timeline) {

                alreadyInactive
                        = Objects.equals(validFrom, interval.getValidFrom())
                        && Objects.equals(validTo, interval.getValidTo())
                        && !interval.isActive();

                if (alreadyInactive) {
                    break;
                }
            }

            // Create inactive period
            if (!alreadyInactive) {

                ApprovalState state = DataRecordUtils.calculateVersionState(uCtx, keys.getFrom(),
                        uCtx.getFromStorage(StorageId.RELATIONS_FROM_WF_ASSIGNMENTS));

                OriginsVistoryRelationsPO version
                        = DataRecordUtils.createInactiveRelationsVistoryRecordPO(
                        other.getOriginId(), uCtx.getOperationId(),
                        validFrom,
                        validTo, state);

                commonRelationsComponent.putVersion(uCtx, version);
                IndexRequestContext.IndexRequestContextBuilder ircBuilder = IndexRequestContext.builder()
                        .drop(true);
                for (TimeInterval<Calculable> timeInterval : timeline) {
                    ircBuilder.relationToDelete(
                            RelationFromIndexId.of(
                                    relationDef.getFromEntity(),
                                    relationDef.getName(),
                                    other.getFrom().getEtalonKey().getId(),
                                    other.getTo().getEtalonKey().getId(),
                                    PeriodIdUtils.periodIdFromDate(timeInterval.getValidTo())));
                    ircBuilder.relationToDelete(
                            RelationToIndexId.of(
                                    relationDef.getToEntity(),
                                    relationDef.getName(),
                                    other.getFrom().getEtalonKey().getId(),
                                    other.getTo().getEtalonKey().getId(),
                                    PeriodIdUtils.periodIdFromDate(timeInterval.getValidTo())));
                }

                if (!uCtx.isBatchUpsert()) {
                    List<EtalonRelation> relations = relationsServiceComponent.loadActiveEtalonsRelationsByFromSideAsList(keys.getFrom(), null);
                    ircBuilder.relations(relations);
                }
                searchService.index(ircBuilder.build());
            }
        }

        return true;
    }

}
