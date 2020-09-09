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

/**
 * @author Mikhail Mikhailov
 *         Record delete batch set.
 */
public class RecordDeleteBatchSet extends RecordBatchSet {

    private List<String> etalonsForWipeDelete;

    private List<String> originsForWipeDelete;
    /**
     * Accumulator.
     */
    private RecordDeleteBatchSetAccumulator accumulator;

    /**
     * Constructor.
     *
     * @param accumulator
     */
    public RecordDeleteBatchSet(RecordDeleteBatchSetAccumulator accumulator) {
        super();
        this.accumulator = accumulator;
    }

    /**
     * @return the accumulator
     */
    public RecordDeleteBatchSetAccumulator getRecordsAccumulator() {
        return accumulator;
    }

    public List<String> getEtalonsForWipeDelete() {
        return etalonsForWipeDelete;
    }

    public List<String> getOriginsForWipeDelete() {
        return originsForWipeDelete;
    }

    public void addEtalonsForWipeDelete(List<String> ids) {
        if (etalonsForWipeDelete == null) {
            etalonsForWipeDelete = new ArrayList<>();
        }
        etalonsForWipeDelete.addAll(ids);
    }

    public void addOriginsForWipeDelete(List<String> ids) {
        if (originsForWipeDelete == null) {
            originsForWipeDelete = new ArrayList<>();
        }
        originsForWipeDelete.addAll(ids);
    }
}
