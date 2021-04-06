package com.unidata.mdm.backend.service.data.listener.record;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.measurement.MeasuredAttributeValueConverter;

/**
 * Enrich number attributes in record
 */
public class DataRecordUpsertEnrichMeasuredAttributesBeforeExecutor implements DataRecordBeforeExecutor<UpsertRequestContext> {

    /**
     * Measured attribute converter
     */
    @Autowired
    private MeasuredAttributeValueConverter measuredAttributeConverter;

    /**
     * @param upsertRequestContext
     * @return
     */
    @Override
    public boolean execute(UpsertRequestContext upsertRequestContext) {

        MeasurementPoint.start();
        try {

            DataRecord record = upsertRequestContext.getRecord();
            if (record == null) {
                return true;
            }
            measuredAttributeConverter.enrichMeasuredAttributesByBase(record);
            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }

}
