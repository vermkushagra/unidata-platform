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
import org.unidata.mdm.data.type.apply.batch.impl.RecordDeleteBatchSetAccumulator;
import org.unidata.mdm.system.type.pipeline.batch.BatchedStart;

/**
 * @author Mikhail Mikhailov on Dec 12, 2019
 */
@Component(RecordsDeleteStartExecutor.SEGMENT_ID)
public class RecordsDeleteStartExecutor extends BatchedStart<RecordDeleteBatchSetAccumulator> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[BATCH_RECORD_DELETE_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".batch.record.delete.start.description";
    /**
     * Constructor.
     */
    public RecordsDeleteStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, RecordDeleteBatchSetAccumulator.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(RecordDeleteBatchSetAccumulator ctx) {
        // Nothing. Possibly some kind of validation in the fuutre
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(RecordDeleteBatchSetAccumulator ctx) {
        // No subjects for batched segments so far
        return null;
    }
}
