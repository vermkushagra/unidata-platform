package com.unidata.mdm.backend.service.data.listener.record;

import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.UpsertRequestContext;
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
public class DataRecordUpsertCheckDatesBeforeExecutor
    extends AbstractValidityRangeCheckExecutor<UpsertRequestContext>
    implements DataRecordBeforeExecutor<UpsertRequestContext> {
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {
            RecordKeys keys = ctx.keys();
            String entityName = ctx.getEntityName() != null
                    ? ctx.getEntityName()
                    : keys != null
                    ? keys.getEntityName()
                    : null;

            Date factoryValidFrom = null;
            Date factoryValidTo = null;

            EntityWrapper ew = metaModelService.getValueById(entityName, EntityWrapper.class);
            if (Objects.nonNull(ew)) {
                factoryValidFrom = ew.getValidityStart();
                factoryValidTo = ew.getValidityEnd();
            } else {
                LookupEntityWrapper lew = metaModelService.getValueById(entityName, LookupEntityWrapper.class);
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
