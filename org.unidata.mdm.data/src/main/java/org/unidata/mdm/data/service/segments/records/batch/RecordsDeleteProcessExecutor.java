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

package org.unidata.mdm.data.service.segments.records.batch;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.dto.DeleteRecordDTO;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.apply.batch.impl.RecordDeleteBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RecordDeleteBatchSetStatistics;
import org.unidata.mdm.system.service.ExecutionService;
import org.unidata.mdm.system.type.batch.BatchIterator;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.pipeline.batch.BatchedPoint;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Dec 12, 2019
 */
@Component(RecordsDeleteProcessExecutor.SEGMENT_ID)
public class RecordsDeleteProcessExecutor extends BatchedPoint<RecordDeleteBatchSetAccumulator> {
    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordsDeleteProcessExecutor.class);
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[BATCH_RECORD_DELETE_PROCESS]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".batch.record.delete.process.description";
    /**
     * The ES.
     */
    @Autowired
    private ExecutionService executionService;
    /**
     * Constructor.
     */
    public RecordsDeleteProcessExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(RecordDeleteBatchSetAccumulator accumulator) {

        MeasurementPoint.start();
        try {

            for (BatchIterator<DeleteRequestContext> li = accumulator.iterator(); li.hasNext(); ) {

                DeleteRequestContext ctx = li.next();
                RecordDeleteBatchSetStatistics statistics = accumulator.statistics();
                try {

                    DeleteRecordDTO result;
                    if (Objects.isNull(accumulator.pipeline())) {
                        result = executionService.execute(ctx);
                    } else {
                        result = executionService.execute(accumulator.pipeline(), ctx);
                    }

                    if (ctx.isInactivatePeriod()) {
                        statistics.incrementUpdated();
                    } else {
                        statistics.incrementDeleted();
                    }

                    if (statistics.collectResults()) {
                        statistics.addResult(result);
                    }
                } catch (Exception e) {

                    statistics.incrementFailed();

                    LOGGER.warn("Bulk upsert, exception caught.", e);
                    if (accumulator.isAbortOnFailure()) {
                        throw e;
                    }

                    li.remove();
                }
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
        return RecordDeleteBatchSetAccumulator.class.isAssignableFrom(start.getInputTypeClass());
    }
}
