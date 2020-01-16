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

package org.unidata.mdm.data.type.apply.batch.impl;

import org.unidata.mdm.data.type.apply.RecordUpsertChangeSet;

/**
 * @author Mikhail Mikhailov
 * Simple record batch set - objects needed, for a record to be upserted in a batched fashion.
 */
public final class RecordUpsertBatchSet extends RecordUpsertChangeSet {
    /**
     * Accumulator link.
     */
    private RecordUpsertBatchSetAccumulator accumulator;
    /**
     * Constructor.
     * @param accumulator link to accumulator.
     */
    public RecordUpsertBatchSet(RecordUpsertBatchSetAccumulator accumulator) {
        super();
        this.accumulator = accumulator;
    }
    /**
     * @return the accumulator
     */
    public RecordUpsertBatchSetAccumulator getRecordsAccumulator() {
        return accumulator;
    }
}
