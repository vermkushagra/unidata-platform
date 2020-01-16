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

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.impl.SimpleAttributesDiff;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.context.GetRecordIntervalRequestContext;
import org.unidata.mdm.data.context.GetRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.impl.CommonRecordsComponent;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.util.DataDiffUtils;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov on Nov 11, 2019
 */
@Component(RecordGetDiffExecutor.SEGMENT_ID)
public class RecordGetDiffExecutor extends Point<GetRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_GET_DIFF]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.get.diff.description";
    /**
     * CRC.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * Constructor.
     * @param id
     * @param description
     */
    public RecordGetDiffExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(GetRequestContext ctx) {

        RecordKeys keys = ctx.keys();
        Timeline<OriginRecord> timeline = ctx.currentTimeline();
        TimeInterval<OriginRecord> data = timeline.isSingleton() ? timeline.first() : null;

        if (Objects.isNull(data) || Objects.isNull(data.getCalculationResult())) {
            return;
        }

        // Load diff to draft state, if requested
        if (ctx.isDiffToDraft() && keys.isPending()) {

            GetRecordIntervalRequestContext diffCtx = GetRecordIntervalRequestContext.builder(ctx)
                    .includeDrafts(false)
                    .build();

            diffCtx.keys(keys);

            Timeline<OriginRecord> prevTimeline = commonRecordsComponent.loadInterval(diffCtx);
            TimeInterval<OriginRecord> prevData = prevTimeline.isSingleton() ? prevTimeline.first() : null;
            if (prevData != null && prevData.getCalculationResult() != null) {

                SimpleAttributesDiff diffToDraft
                    = DataDiffUtils.diffAsAttributesTable(keys.getEntityName(), data.getCalculationResult(),
                        prevData.getCalculationResult(), true);

                ctx.diffToDraft(diffToDraft);
            }
        }

        // TODO Load diff to previous
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return GetRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
