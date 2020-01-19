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

import org.springframework.stereotype.Component;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.dto.DeleteRecordDTO;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Nov 21, 2019
 */
@Component(RecordDeleteFinishExecutor.SEGMENT_ID)
public class RecordDeleteFinishExecutor extends Finish<DeleteRequestContext, DeleteRecordDTO> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_DELETE_FINISH]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.delete.finish.description";
    /**
     * Constructor.
     */
    public RecordDeleteFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, DeleteRecordDTO.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteRecordDTO finish(DeleteRequestContext ctx) {

        MeasurementPoint.start();
        try {
            // Not much here so far.
            return new DeleteRecordDTO(ctx.keys());
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return DeleteRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
