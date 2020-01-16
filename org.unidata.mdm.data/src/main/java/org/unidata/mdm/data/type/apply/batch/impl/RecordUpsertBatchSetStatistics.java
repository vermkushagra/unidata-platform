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

import org.unidata.mdm.data.dto.UpsertRecordDTO;

/**
 * @author Mikhail Mikhailov on Dec 13, 2019
 */
public class RecordUpsertBatchSetStatistics extends AbstractBatchSetStatistics<UpsertRecordDTO> {
    /**
     * Number of inserted
     */
    private long inserted = 0L;
    /**
     * Constructor.
     */
    public RecordUpsertBatchSetStatistics() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        super.reset();
        this.inserted = 0L;
    }
    /**
     * @param inserted the inserted to add
     */
    public void incrementInserted() {
        incrementFailed(1L);
    }
    /**
     * @param inserted the inserted to add
     */
    public void incrementInserted(long inserted) {
        this.inserted += inserted;
    }
    /**
     * @return the inserted
     */
    public long getInserted() {
        return inserted;
    }
}
