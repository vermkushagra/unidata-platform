package com.unidata.mdm.backend.service.data.listener.record;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.integration.exits.UpsertListener;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.conf.impl.UpsertImpl;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;

public class DataRecordUpsertEtalonUserExitAfterExecutor implements DataRecordAfterExecutor<UpsertRequestContext>{
    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationService configurationService;

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

            EtalonRecord etalon = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);
            UpsertImpl upsert = configurationService.getUpsert();
            RecordKeys keys = ctx.getFromStorage(StorageId.DATA_UPSERT_KEYS);
            if (upsert != null && etalon != null && keys != null) {
                String entityName = keys.getEntityName();
                UpsertListener listener = upsert.getAfterEtalonCompositionInstances().get(entityName);
                if (listener != null) {
                    if (action == UpsertAction.UPDATE) {
                        listener.afterUpdateEtalonComposition(etalon, ctx);
                    } else if (action == UpsertAction.INSERT) {
                        listener.afterInsertEtalonComposition(etalon, ctx);
                    }
                }
            }

            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }

}
