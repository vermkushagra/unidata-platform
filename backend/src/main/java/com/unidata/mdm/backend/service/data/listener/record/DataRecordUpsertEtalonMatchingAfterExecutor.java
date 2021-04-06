package com.unidata.mdm.backend.service.data.listener.record;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimeIntervalDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.matching.MatchingService;

public class DataRecordUpsertEtalonMatchingAfterExecutor implements DataRecordAfterExecutor<UpsertRequestContext> {

    @Autowired
    private MatchingService matchingService;
    /*
    @Autowired
    private ClusterService clusterService;
    */

    @Override
    public boolean execute(UpsertRequestContext ctx) {

        UpsertAction action = ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
        EtalonRecord etalon = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);

        // 1. Check action / input
        if (ctx.isSkipMatching() || etalon == null || action == UpsertAction.NO_ACTION) {
            return true;
        }

        MeasurementPoint.start();
        try{

            RecordKeys keys = ctx.keys();
            WorkflowTimeIntervalDTO interval = ctx.getFromStorage(StorageId.DATA_UPSERT_WORKFLOW_INTERVAL);

            Boolean intervalIsPending = interval != null ? interval.isPending() : keys.getEtalonState() == ApprovalState.PENDING;
            Boolean intervalIsDeleted = interval != null ? interval.isDeleted() : keys.getEtalonStatus() == RecordStatus.INACTIVE;

            // 2. New clusters can be created only in case when it is a approved change. Skip, if doesn't apply
            if (keys.getEtalonStatus() == RecordStatus.INACTIVE || intervalIsPending || intervalIsDeleted) {
                return true;
            }

            /*
            String entityName = keys.getEntityName();
            String etalonId = keys.getEtalonKey().getId();
            */
            //clean clusters from record
            //todo optimize delete(remove only if something was changed - can be done in match method)
            // clusterService.excludeFromClusters(entityName, Collections.singleton(etalonId));

            Date upsertDate = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);

            if (upsertDate == null) {
                upsertDate = new Date();
            }

            // 3. Calculate cluster/block data, to be used by indexing
            ctx.putToStorage(StorageId.DATA_UPSERT_ETALON_MATCHING_UPDATE, matchingService.construct(etalon, upsertDate));

            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }
}
