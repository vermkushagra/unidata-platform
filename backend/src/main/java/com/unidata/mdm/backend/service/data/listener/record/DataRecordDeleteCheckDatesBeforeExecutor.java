package com.unidata.mdm.backend.service.data.listener.record;

import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.service.data.listener.AbstractValidityRangeCheckExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;

/**
 * @author Mikhail Mikhailov
 * Single point for checking and possible adjustment of from - to dates.
 */
public class DataRecordDeleteCheckDatesBeforeExecutor
    extends AbstractValidityRangeCheckExecutor<DeleteRequestContext>
    implements DataRecordBeforeExecutor<DeleteRequestContext> {
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(DeleteRequestContext ctx) {

        if (!ctx.isInactivatePeriod()) {
            return true;
        }

        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.keys();
            Date factoryValidFrom = null;
            Date factoryValidTo = null;

            EntityWrapper ew = metaModelService.getValueById(keys.getEntityName(), EntityWrapper.class);
            if (Objects.nonNull(ew)) {
                factoryValidFrom = ew.getValidityStart();
                factoryValidTo = ew.getValidityEnd();
            } else {
                LookupEntityWrapper lew = metaModelService.getValueById(keys.getEntityName(), LookupEntityWrapper.class);
                factoryValidFrom = lew.getValidityStart();
                factoryValidTo = lew.getValidityEnd();
            }

            super.execute(ctx, factoryValidFrom, factoryValidTo);
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }
}
