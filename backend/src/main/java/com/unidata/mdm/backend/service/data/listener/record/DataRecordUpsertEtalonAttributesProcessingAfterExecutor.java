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
                    processRecors(Collections.singletonList(upsertRecord), Collections.emptyList());
                }
            }
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

}
