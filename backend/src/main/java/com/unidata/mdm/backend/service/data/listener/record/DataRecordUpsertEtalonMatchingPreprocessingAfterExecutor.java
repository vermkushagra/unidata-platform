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

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimeIntervalDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.PeriodIdUtils;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.matching.MatchingService;
import com.unidata.mdm.backend.service.search.SearchServiceExt;

public class DataRecordUpsertEtalonMatchingPreprocessingAfterExecutor implements DataRecordAfterExecutor<UpsertRequestContext> {

    @Autowired
    private MatchingService matchingService;
    /**
     * Search service.
     */
    @Autowired
    private SearchServiceExt searchService;

    @Override
    public boolean execute(UpsertRequestContext ctx) {

        UpsertAction action = ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
        EtalonRecord etalon = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);

        // 1. Check action / input
        if (ctx.isSkipMatchingPreprocessing() || etalon == null || action == UpsertAction.NO_ACTION) {
            return true;
        }

        MeasurementPoint.start();
        try{

            RecordKeys keys = ctx.keys();
            WorkflowTimeIntervalDTO interval = ctx.getFromStorage(StorageId.DATA_UPSERT_WORKFLOW_INTERVAL);

            Boolean intervalIsPending = interval != null ? interval.isPending() : keys.getEtalonState() == ApprovalState.PENDING;
            Boolean intervalIsDeleted = interval != null ? interval.isDeleted() : keys.getEtalonStatus() == RecordStatus.INACTIVE;
            EtalonRecordInfoSection infoSection = etalon.getInfoSection();
            final String etalonId = infoSection.getEtalonKey().getId();

            // 2. New clusters can be created only in case when it is a approved change. Skip, if doesn't apply
            if (keys.getEtalonStatus() == RecordStatus.INACTIVE || intervalIsPending || intervalIsDeleted) {
                return true;
            }

            Date upsertDate = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);

            if (upsertDate == null) {
                upsertDate = new Date();
            }
            Collection<Cluster> clusters = matchingService.constructPreprocessing(etalon, upsertDate);

            // 3. Remove potential cluster duplicate info from elastic
            if (CollectionUtils.isNotEmpty(clusters) && !ctx.isRestore() && !ctx.isInitialLoad()) {
                ComplexSearchRequestContext multiCtx = ComplexSearchRequestContext.multi(
                        clusters.stream()
                        .map(cluster ->
                                SearchRequestContext.forMatching(infoSection.getEntityName())
                                    .onlyQuery(true)
                                    .count(1000)
                                    .form(FormFieldsGroup.createAndGroup(FormField.strictString("_id",
                                            PeriodIdUtils.childPeriodId(
                                                    infoSection.getPeriodId(),
                                                    etalonId,
                                                    cluster.getMetaData().getRuleId().toString()))))
                                    .build())
                        .collect(Collectors.toList()));
                searchService.deleteFoundResult(multiCtx);
            }

            // 4. Calculate cluster/block data, to be used by indexing
            ctx.putToStorage(StorageId.DATA_UPSERT_ETALON_MATCHING_UPDATE, clusters);

            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }
}
