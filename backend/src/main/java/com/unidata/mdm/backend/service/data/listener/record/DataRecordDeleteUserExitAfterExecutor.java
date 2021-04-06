/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.integration.exits.DeleteListener;
import com.unidata.mdm.backend.conf.impl.DeleteImpl;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.common.types.EtalonRecord;


/**
 * @author Mikhail Mikhailov
 * Delete user exit 'after' listener.
 */
public class DataRecordDeleteUserExitAfterExecutor implements DataRecordAfterExecutor<DeleteRequestContext> {

    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationService configurationService;

    /**
     * Constructor.
     */
    public DataRecordDeleteUserExitAfterExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(DeleteRequestContext ctx) {
        EtalonRecord etalon = ctx.getFromStorage(StorageId.DATA_DELETE_ETALON_RECORD);
        DeleteImpl delete = configurationService.getDelete();
        if (delete != null && etalon != null) {
            DeleteListener listener = delete.getAfterEtalonDeactivationInstances()
                    .get(etalon.getInfoSection().getEntityName());
            if (listener != null) {
                listener.afterEtalonDeactivation(etalon, ctx);
            }
        }

        return true;
    }

}
