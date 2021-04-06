package com.unidata.mdm.backend.service.data.listener.record;

import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;

/**
 * Normalize measured attributes before upsert record
 */
public class DataRecordUpsertMeasurementMetaSetterBeforeExecutor extends AbstractDataRecordUpsertMeasurementMetaSetter
        implements DataRecordBeforeExecutor<UpsertRequestContext> {


    @Override
    public boolean execute(UpsertRequestContext upsertRequestContext) {
        MeasurementPoint.start();
        try {
            DataRecord record = upsertRequestContext.getRecord();
            if (record != null) {
                String entityName = upsertRequestContext.keys() == null ?
                        upsertRequestContext.getEntityName() :
                        upsertRequestContext.keys().getEntityName();

                processDataRecord(record, entityName, "");
            }

            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }
}
