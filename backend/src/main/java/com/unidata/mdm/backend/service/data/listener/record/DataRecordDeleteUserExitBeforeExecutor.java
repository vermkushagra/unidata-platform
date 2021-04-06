/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.integration.exits.DeleteListener;
import com.unidata.mdm.backend.conf.impl.DeleteImpl;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.common.types.EtalonRecord;


/**
 * @author Mikhail Mikhailov
 * Delete user exit 'before' listener.
 */
public class DataRecordDeleteUserExitBeforeExecutor implements DataRecordBeforeExecutor<DeleteRequestContext> {

    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationService configurationService;

    /**
     * Constructor.
     */
    public DataRecordDeleteUserExitBeforeExecutor() {
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
            DeleteListener listener = delete.getBeforeEtalonDeactivationInstances()
                    .get(etalon.getInfoSection().getEntityName());
            if (listener != null) {
                boolean userExitResult = listener.beforeEtalonDeactivation(etalon, ctx);
                if(!userExitResult){
                    throw new DataProcessingException("Error occurred during run before delete user exit",
                            ExceptionId.EX_DATA_DELETE_RECORD_BEFORE_USER_EXIT_ERROR);
                }
                return userExitResult;
            }
        }

        return true;
    }

}
