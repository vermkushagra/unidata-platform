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

package org.unidata.mdm.data.service.segments.relations.batch;

import org.springframework.stereotype.Component;
import org.unidata.mdm.data.dto.RelationsBulkResultDTO;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.apply.batch.impl.RelationUpsertBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RelationUpsertBatchSetStatistics;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.pipeline.batch.BatchedFinish;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Dec 12, 2019
 */
@Component(RelationsUpsertFinishExecutor.SEGMENT_ID)
public class RelationsUpsertFinishExecutor extends BatchedFinish<RelationUpsertBatchSetAccumulator, RelationsBulkResultDTO> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[BATCH_RELATIONS_UPSERT_FINISH]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".batch.relations.upsert.finish.description";
    /**
     * Constructor.
     */
    public RelationsUpsertFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, RelationsBulkResultDTO.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RelationsBulkResultDTO finish(RelationUpsertBatchSetAccumulator accumulator) {

        MeasurementPoint.start();
        try {
            RelationsBulkResultDTO result = new RelationsBulkResultDTO();
            RelationUpsertBatchSetStatistics statistics = accumulator.statistics();

            result.setInserted(statistics.getInserted());
            result.setUpdated(statistics.getUpdated());
            result.setFailed(statistics.getFailed());
            result.setSkipped(statistics.getSkipped());

            if (statistics.collectResults()) {
                result.setUpsertRelations(statistics.getResults());
            }

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
        return RelationUpsertBatchSetAccumulator.class.isAssignableFrom(start.getInputTypeClass());
    }
}
