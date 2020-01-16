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

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.GetRequestContext;
import org.unidata.mdm.data.dto.GetRecordDTO;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Nov 11, 2019
 */
@Component(RecordGetFinishExecutor.SEGMENT_ID)
public class RecordGetFinishExecutor extends Finish<GetRequestContext, GetRecordDTO> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_GET_FINISH]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.get.finish.description";
    /**
     * Constructor.
     */
    public RecordGetFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, GetRecordDTO.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public GetRecordDTO finish(GetRequestContext ctx) {

        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.keys();

            // UN-202, select current etalon if no date supplied. Compose etalon record, if a date is supplied
            GetRecordDTO result = new GetRecordDTO(keys);

            // 1. Set data.
            Timeline<OriginRecord> timeline = ctx.currentTimeline();
            TimeInterval<OriginRecord> data = timeline.isSingleton() ? timeline.first() : null;
            if (data != null) {

                // 1.1. Process etalon data
                if (data.getCalculationResult() != null) {

                    EtalonRecord etalon = data.getCalculationResult();
                    result.setEtalon(etalon);
                }

                // 1.2. Load origins if requested
                if (ctx.isFetchOrigins()) {

                    List<OriginRecord> origins = data.toValueList();
                    result.setOrigins(origins);
                }

                // 1.3. Load diff to draft state, if requested
                if (ctx.isDiffToDraft() && keys.isPending() && Objects.nonNull(data.getCalculationResult())) {
                    result.setDiffToDraft(ctx.diffToDraft());
                }
            }

            // 2. Rights post-processing
            result.setRights(SecurityUtils.calculateRightsForTopLevelResource(
                    keys.getEntityName(),
                    result.getEtalon() != null ? result.getEtalon().getInfoSection().getStatus() : null,
                    result.getEtalon() != null ? result.getEtalon().getInfoSection().getApproval() : null,
                    false,
                    false,
                    result.getEtalon() != null ? result.getEtalon().getInfoSection().getOperationType() : null));

            return result;
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return GetRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
