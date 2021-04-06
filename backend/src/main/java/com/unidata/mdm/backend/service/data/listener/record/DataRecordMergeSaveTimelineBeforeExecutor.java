package com.unidata.mdm.backend.service.data.listener.record;

import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mikhail Mikhailov
 * Save old timeline for various purposes (ES and notifications processing).
 */
public class DataRecordMergeSaveTimelineBeforeExecutor implements DataRecordBeforeExecutor<MergeRequestContext> {
    /**
     * ORC.
     */
    @Autowired
    private OriginRecordsComponent originRecordsComponent;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(MergeRequestContext ctx) {

        MeasurementPoint.start();
        try {
            RecordKeys masterKey = ctx.getFromStorage(StorageId.DATA_MERGE_KEYS);
            final GetRequestContext gCtx = GetRequestContext.builder()
                    .build();

            gCtx.putToStorage(gCtx.keysId(), masterKey);

            WorkflowTimelineDTO workflowTimelineDTO = originRecordsComponent.loadWorkflowTimeline(gCtx, false);
            ctx.putToStorage(StorageId.DATA_UPSERT_PREVIOUS_TIMELINE, workflowTimelineDTO);
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

}
