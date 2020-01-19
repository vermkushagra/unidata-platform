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

import org.springframework.stereotype.Component;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.apply.batch.impl.RecordUpsertBatchSetAccumulator;
import org.unidata.mdm.system.type.pipeline.batch.BatchedStart;

/**
 * @author Mikhail Mikhailov on Dec 12, 2019
 */
@Component(RecordsUpsertStartExecutor.SEGMENT_ID)
public class RecordsUpsertStartExecutor extends BatchedStart<RecordUpsertBatchSetAccumulator> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[BATCH_RECORD_UPSERT_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".batch.record.upsert.start.description";
    /**
     * Constructor.
     */
    public RecordsUpsertStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, RecordUpsertBatchSetAccumulator.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(RecordUpsertBatchSetAccumulator ctx) {
        // Nothing. Possibly some kind of validation in the fuutre
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(RecordUpsertBatchSetAccumulator ctx) {
        // No subjects for batched segments so far
        return null;
    }
}
