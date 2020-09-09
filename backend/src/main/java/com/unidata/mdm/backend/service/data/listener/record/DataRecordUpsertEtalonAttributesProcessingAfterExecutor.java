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

package com.unidata.mdm.backend.service.data.listener.record;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;

import java.util.Collections;

/**
 * @author Dmitry Kopin
 */
public class DataRecordUpsertEtalonAttributesProcessingAfterExecutor extends AbstractDataRecordAttributesProcessingExecutor implements DataRecordAfterExecutor<UpsertRequestContext> {

    /**
     * Constructor.
     */
    public DataRecordUpsertEtalonAttributesProcessingAfterExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {
            if (ctx.sendNotification()) {
                EtalonRecord upsertRecord = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);

                if (upsertRecord != null) {
                    processRecords(Collections.singletonList(upsertRecord), Collections.emptyList());
                }
            }
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

}
