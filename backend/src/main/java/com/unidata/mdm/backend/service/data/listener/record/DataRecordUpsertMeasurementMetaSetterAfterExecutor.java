package com.unidata.mdm.backend.service.data.listener.record;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;

/**
 * Normalize measured attributes after DQ phase
 */
public class DataRecordUpsertMeasurementMetaSetterAfterExecutor extends AbstractDataRecordUpsertMeasurementMetaSetter
        implements DataRecordAfterExecutor<UpsertRequestContext> {


    @Override
    public boolean execute(UpsertRequestContext upsertRequestContext) {
        MeasurementPoint.start();
        try {
            Collection<DataRecord> records = upsertRequestContext.getFromStorage(StorageId.DATA_UPSERT_ETALON_ENRICHMENT);
            if (CollectionUtils.isNotEmpty(records)) {
                String entityName = upsertRequestContext.keys() == null ?
                        upsertRequestContext.getEntityName() :
                        upsertRequestContext.keys().getEntityName();
                records.forEach(record -> processDataRecord(record, entityName, ""));
            }

            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }
}
