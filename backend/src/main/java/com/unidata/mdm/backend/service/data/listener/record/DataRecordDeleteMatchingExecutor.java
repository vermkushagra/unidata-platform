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

import java.util.Collections;

import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.common.service.ClusterService;

public class DataRecordDeleteMatchingExecutor implements DataRecordAfterExecutor<DeleteRequestContext> {

    @Autowired
    private ClusterService clusterService;

    @Override
    public boolean execute(DeleteRequestContext deleteRequestContext) {
        MeasurementPoint.start();
        try {
            RecordKeys keys = deleteRequestContext.keys();

            String etalonId = keys.getEtalonKey().getId();
            String entityName = keys.getEntityName();
            clusterService.excludeFromClusters(entityName, Collections.singleton(etalonId));
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

}
