/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.integration.exits.UpsertListener;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.conf.impl.UpsertImpl;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;


/**
 * @author Mikhail Mikhailov
 * User exit 'before' executor.
 */
public class DataRecordUpsertOriginUserExitBeforeExecutor implements DataRecordBeforeExecutor<UpsertRequestContext> {

    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationService configurationService;

    /**
     * Constructor.
     */
    public DataRecordUpsertOriginUserExitBeforeExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        UpsertAction action = ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
        if (ctx.isBypassExtensionPoints() || action == UpsertAction.NO_ACTION) {
            return true;
        }

        MeasurementPoint.start();
        try {

            OriginRecord origin = ctx.getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD);
            RecordKeys keys = ctx.keys();
            String entityName = keys == null ? ctx.getEntityName() : keys.getEntityName();
            UpsertImpl upsert = configurationService.getUpsert();
            if (upsert != null && origin != null && entityName != null) {
                UpsertListener listener = upsert.getBeforeOriginUpsertInstances().get(entityName);
                if (listener != null) {
                    if (action == UpsertAction.UPDATE) {
                        return listener.beforeOriginUpdate(origin, ctx);
                    } else if (action == UpsertAction.INSERT) {
                        return listener.beforeOriginInsert(origin, ctx);
                    }
                }
            }

            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }

}
