/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;


/**
 * @author Mikhail Mikhailov
 * Etalon materializer.
 */
public class DataRecordDeleteEtalonMaterializer implements DataRecordBeforeExecutor<DeleteRequestContext> {

    /**
     * Etalon records component.
     */
    @Autowired
    private EtalonRecordsComponent etalonComponent;

    /**
     * Constructor.
     */
    public DataRecordDeleteEtalonMaterializer() {
        super();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.data.listener.DataRecordExecutor#execute(com.unidata.mdm.backend.common.context.CommonRequestContext)
     */
    @Override
    public boolean execute(DeleteRequestContext ctx) {

        // Skip on origin deactivate.
        if ((ctx.isOriginRecordKey() || ctx.isOriginExternalId()) && !ctx.isInactivatePeriod()) {
            return true;
        }

        Date asOf = ctx.isInactivatePeriod()
                ? ctx.getValidFrom() != null ? ctx.getValidFrom() : ctx.getValidTo()
                : null;

        RecordKeys keys = ctx.getFromStorage(StorageId.DATA_DELETE_KEYS);
        EtalonRecord etalon = etalonComponent.loadEtalonData(keys.getEtalonKey().getId(), asOf,
                null, null, null,
                ctx.isInactivatePeriod(), false);

        ctx.putToStorage(StorageId.DATA_DELETE_ETALON_RECORD, etalon);
        return true;
    }

}
