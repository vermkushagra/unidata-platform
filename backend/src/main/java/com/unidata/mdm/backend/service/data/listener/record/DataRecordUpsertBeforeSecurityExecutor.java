package com.unidata.mdm.backend.service.data.listener.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemSecurityException;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.security.Endpoint;
import com.unidata.mdm.backend.common.security.SecurityToken;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.data.listener.AbstractSecurityInfoContextEnricher;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.conf.WorkflowProcessType;

public class DataRecordUpsertBeforeSecurityExecutor
    extends AbstractSecurityInfoContextEnricher<UpsertRequestContext>
    implements DataRecordBeforeExecutor<UpsertRequestContext> {

    /**
     * Logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(DataRecordUpsertBeforeSecurityExecutor.class);

    @Override
    public boolean execute(UpsertRequestContext requestContext) {

        MeasurementPoint.start();
        try {

            RecordKeys keys = requestContext.getFromStorage(StorageId.DATA_UPSERT_KEYS);
            String entityName = keys != null ? keys.getEntityName() : requestContext.getEntityName();

            super.putResourceRights(requestContext, StorageId.DATA_UPSERT_RIGHTS, entityName);

            Right rights = requestContext.getFromStorage(StorageId.DATA_UPSERT_RIGHTS);
            UpsertAction upsertAction = requestContext.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
            if (!rights.isCreate() && upsertAction == UpsertAction.INSERT) {
                final String message = "The user '{}' has no or unsufficient insert rights for resource '{}'. Insert denied.";
                LOGGER.info(message, SecurityUtils.getCurrentUserName(), entityName);
                throw new SystemSecurityException(message,
                        ExceptionId.EX_DATA_UPSERT_INSERT_NO_RIGHTS, SecurityUtils.getCurrentUserName(), entityName);
            }

            if (!rights.isUpdate() && upsertAction == UpsertAction.UPDATE) {
                final String message = "The user '{}' has no or unsufficient update rights for resource '{}'. Update denied.";
                LOGGER.info(message, SecurityUtils.getCurrentUserName(), entityName);
                throw new SystemSecurityException(message,
                        ExceptionId.EX_DATA_UPSERT_UPDATE_NO_RIGHTS, SecurityUtils.getCurrentUserName(), entityName);
            }

            super.putWorkflowAssignments(requestContext, StorageId.DATA_UPSERT_WF_ASSIGNMENTS, entityName, WorkflowProcessType.RECORD_EDIT);

            // New record
            if (keys == null) {
                return true;
            }

            SecurityToken securityToken = SecurityUtils.getSecurityTokenForCurrentUser();
            if (keys.isPending() && securityToken != null && securityToken.getEndpoint() != Endpoint.REST) {
                throw new DataProcessingException("Only REST users able to modify records in pending approval state",
                        ExceptionId.EX_DATA_UPSERT_NOT_ACCEPTED_HAS_PENDING_RECORD);
            }
            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }
}
