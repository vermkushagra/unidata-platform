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

package com.unidata.mdm.backend.service.data.batch;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.po.EtalonRecordPO;
import com.unidata.mdm.backend.po.OriginRecordPO;

/**
 * @author Mikhail Mikhailov
 * Simple record batch set - objects needed, for a record to be upserted in a batched fashion.
 */
public final class RecordUpsertBatchSet extends RecordBatchSet {
    /**
     * Accumulator link.
     */
    private RecordUpsertBatchSetAccumulator accumulator;
    /**
     * Etalon record persistant object.
     */
    protected EtalonRecordPO etalonRecordInsertPO;
    /**
     * Origin record persistant objects.
     */
    protected List<OriginRecordPO> originRecordInsertPOs = new ArrayList<>(2);
    /**
     * Constructor.
     * @param accumulator link to accumulator.
     */
    public RecordUpsertBatchSet(RecordUpsertBatchSetAccumulator accumulator) {
        super();
        this.accumulator = accumulator;
    }
    /**
     * @return the etalonRecordPO
     */
    public EtalonRecordPO getEtalonRecordInsertPO() {
        return etalonRecordInsertPO;
    }
    /**
     * @param etalonRecordPO the etalonRecordPO to set
     */
    public void setEtalonRecordInsertPO(EtalonRecordPO etalonRecordPO) {
        this.etalonRecordInsertPO = etalonRecordPO;
    }
    /**
     * @return the originRecordPOs
     */
    public List<OriginRecordPO> getOriginRecordInsertPOs() {
        return originRecordInsertPOs;
    }
    /**
     * @return the accumulator
     */
    public RecordUpsertBatchSetAccumulator getRecordsAccumulator() {
        return accumulator;
    }
}
