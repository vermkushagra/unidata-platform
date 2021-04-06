package com.unidata.mdm.backend.service.data.listener.record;

import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.integration.exits.ExitConstants;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;

/**
 * @author Mikhail Mikhailov
 * Old 'ensure after' part of the {@linkplain OriginRecordsComponent}.
 */
public class DataRecordUpsertEnsureAfterExecutor
    implements DataRecordAfterExecutor<UpsertRequestContext> {
    /**
     * Constructor.
     */
    public DataRecordUpsertEnsureAfterExecutor() {
        super();
    }
    /**
     * TODO Subject to remove.
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {
            RecordKeys keys = ctx.keys();

            // 5. Expose some details to a possibly existing OLD user exit
            ctx.putToUserContext(ExitConstants.IN_UPSERT_CURRENT_RECORD_ETALON_ID.name(),
                    keys.getEtalonKey().getId());
            ctx.putToUserContext(ExitConstants.IN_UPSERT_CURRENT_RECORD_ORIGIN_ID.name(),
                    keys.getOriginKey().getId());
            ctx.putToUserContext(ExitConstants.IN_UPSERT_CURRENT_RECORD_VALID_FROM.name(),
                    ctx.getValidFrom());
            ctx.putToUserContext(ExitConstants.IN_UPSERT_CURRENT_RECORD_VALID_TO.name(),
                    ctx.getValidTo());

            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

}
