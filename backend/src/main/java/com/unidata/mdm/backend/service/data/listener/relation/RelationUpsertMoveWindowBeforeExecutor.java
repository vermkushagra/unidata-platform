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
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.GetRelationRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.TimeInterval;
import com.unidata.mdm.backend.common.types.TimeIntervalContributorInfo;
import com.unidata.mdm.backend.common.types.Timeline;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.relations.CommonRelationsComponent;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

/**
 * Executor responsible for modifying relations have an alias key.
 */
public class RelationUpsertMoveWindowBeforeExecutor implements DataRecordBeforeExecutor<UpsertRelationRequestContext> {
    /**
     * Common relations component.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRelationRequestContext uCtx) {

        RelationDef relationDef = uCtx.getFromStorage(StorageId.RELATIONS_META_DEF);
        if (relationDef.getRelType() != RelType.MANY_TO_MANY) {
            return true;
        }

        RelationKeys keys = commonRelationsComponent.ensureAndGetRelationKeys(relationDef.getName(), uCtx);
        if (keys != null) {

            // There must be always only one moving window (active interval)
            GetRelationRequestContext ctx = GetRelationRequestContext.builder()
                    .tasks(true)
                    .includeDrafts(false)
                    .forDatesFrame(Pair.<Date, Date>of(null, null))
                    .build();

            ctx.putToStorage(ctx.relationKeysId(), keys);

            Timeline<TimeIntervalContributorInfo> timeline = commonRelationsComponent.loadRelationTimeline(ctx, false);
            if (timeline == null) {
                return true;
            }
            for (TimeInterval<TimeIntervalContributorInfo> interval : timeline) {

                if (!interval.isActive()) {
                    continue;
                }

                // Order of arguments matters,
                // since interval.getValid*() is a java.sql.Timestamp
                // and ctx.getValid*() is a java.util.Date!
                boolean fromMatches = Objects.equals(uCtx.getValidFrom(), interval.getValidFrom());
                boolean toMatches = Objects.equals(uCtx.getValidTo(), interval.getValidTo());
                if (fromMatches && toMatches) {
                    break;
                }

                ApprovalState state = DataRecordUtils.calculateVersionState(uCtx, keys.getFrom(),
                        uCtx.getFromStorage(StorageId.RELATIONS_FROM_WF_ASSIGNMENTS));

                OriginsVistoryRelationsPO version
                    = DataRecordUtils.createInactiveRelationsVistoryRecordPO(
                        keys.getOriginId(), uCtx.getOperationId(),
                        interval.getValidFrom(),
                        interval.getValidTo(), state);

                commonRelationsComponent.putVersion(uCtx, version);

                if (state == ApprovalState.PENDING) {
                    commonRelationsComponent.possiblyResetPendingState(keys, uCtx);
                }

                break;
            }
        }

        return true;
    }
}
