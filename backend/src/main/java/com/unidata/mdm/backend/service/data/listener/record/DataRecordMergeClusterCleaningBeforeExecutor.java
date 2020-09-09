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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.common.service.ClusterService;

public class DataRecordMergeClusterCleaningBeforeExecutor implements DataRecordBeforeExecutor<MergeRequestContext> {

    @Autowired
    private ClusterService clusterService;

    @Override
    public boolean execute(MergeRequestContext ctx) {
        MeasurementPoint.start();
        try {
            Collection<String> etalonIds = new ArrayList<>();
            RecordKeys masterKey = ctx.getFromStorage(StorageId.DATA_MERGE_KEYS);
            List<RecordKeys> duplicatesKeys = ctx.getFromStorage(StorageId.DATA_MERGE_DUPLICATES_KEYS);
            etalonIds.add(masterKey.getEtalonKey().getId());
            Collection<String> duplicateIds = duplicatesKeys.stream()
                    .map(key -> key.getEtalonKey().getId())
                    .collect(Collectors.toCollection(() -> etalonIds));
            List<String> notFoundEtalonKeys = ctx.getFromStorage(StorageId.DATA_MERGE_NOT_FOUND_ETALON_KEYS);
            if (notFoundEtalonKeys != null) {
                etalonIds.addAll(notFoundEtalonKeys);
            }
            if (!ctx.isBatchUpsert()) {
                clusterService.excludeFromClusters(masterKey.getEntityName(), etalonIds);
                clusterService.dropFromBlockList(duplicateIds);
            }
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }
}
