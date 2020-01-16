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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.dto.ErrorInfoDTO;
import org.unidata.mdm.data.dto.UpsertRecordDTO;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Nov 8, 2019
 */
@Component(RecordUpsertFinishExecutor.SEGMENT_ID)
public class RecordUpsertFinishExecutor extends Finish<UpsertRequestContext, UpsertRecordDTO> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_UPSERT_FINISH]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.upsert.finish.description";
    /**
     * Constructor.
     * @param id
     * @param description
     * @param outputTypeClass
     */
    public RecordUpsertFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, UpsertRecordDTO.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertRecordDTO finish(UpsertRequestContext ctx) {

        // Content from RSCI.
        MeasurementPoint.start();
        try {

            Timeline<OriginRecord> next = ctx.nextTimeline();
            EtalonRecord etalon = null;
            List<EtalonRecord> periods = new ArrayList<>(next.size());
            Date point = ctx.getValidFrom() == null ? ctx.getValidTo() : ctx.getValidFrom();
            for (TimeInterval<OriginRecord> interval : next) {

                if (interval.isInRange(point)) {
                    etalon = interval.getCalculationResult();
                }

                // Take just the first one, since we can't handle multiple periods yet.
                periods.add(interval.getCalculationResult());
            }

            // @Modules TODO
            List<ErrorInfoDTO> erros = Collections.emptyList(); //ctx.getFromStorage(StorageId.PROCESS_ERRORS);
//            if (CollectionUtils.isNotEmpty(erros)) {
//                result.setErrors(erros);
//            }

            UpsertRecordDTO result = new UpsertRecordDTO();
            result.setAction(ctx.upsertAction());
            result.setRecordKeys(ctx.keys());
            result.setEtalon(etalon);
            result.setPeriods(periods);
            result.setErrors(erros);

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
        return UpsertRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
