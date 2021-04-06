/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.integration.exits.ExitConstants;
import com.unidata.mdm.backend.common.integration.exits.UpsertListener;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.conf.impl.UpsertImpl;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;


/**
 * @author Mikhail Mikhailov
 * User exit 'after' executor.
 */
public class DataRecordUpsertOriginUserExitAfterExecutor implements DataRecordAfterExecutor<UpsertRequestContext> {

    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationService configurationService;

    /**
     * Constructor.
     */
    public DataRecordUpsertOriginUserExitAfterExecutor() {
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
            UpsertImpl upsert = configurationService.getUpsert();
            RecordKeys keys = ctx.getFromStorage(StorageId.DATA_UPSERT_KEYS);
            if (upsert != null && origin != null && keys != null) {
                String entityName = keys.getEntityName();
                UpsertListener listener = upsert.getAfterOriginUpsertInstances().get(entityName);
                if (listener != null) {
                    if (action == UpsertAction.UPDATE) {
                        listener.afterOriginUpdate(origin, ctx);
                    } else if (action == UpsertAction.INSERT) {
                        listener.afterOriginInsert(origin, ctx);
                    }

                    ctx.putToStorage(StorageId.DATA_UPSERT_IS_MODIFIED,
                            ctx.getFromUserContext(ExitConstants.OUT_UPSERT_CURRENT_RECORD_IS_MODIFIED.name()));
                }
            }

            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }

}
