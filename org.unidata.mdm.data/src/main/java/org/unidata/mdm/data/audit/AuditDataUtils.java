package org.unidata.mdm.data.audit;

import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.context.GetRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.exception.PlatformFailureException;

/**
 * @author Alexander Malyshev
 */
public final class AuditDataUtils {
    private AuditDataUtils() { }

    public static String auditEventType(PipelineExecutionContext pipelineExecutionContext) {
        if (pipelineExecutionContext instanceof UpsertRequestContext) {
            return AuditDataConstants.RECORD_UPSERT_EVENT_TYPE;
        }
        if (pipelineExecutionContext instanceof GetRequestContext) {
            return AuditDataConstants.RECORD_GET_EVENT_TYPE;
        }
        if (pipelineExecutionContext instanceof DeleteRequestContext) {
            return AuditDataConstants.RECORD_DELETE_EVENT_TYPE;
        }
        final String contextType = pipelineExecutionContext.getClass().getName();
        throw new PlatformFailureException(
                "Unknown context type " + contextType,
                DataExceptionIds.EX_DATA_AUDIT_UNKNOW_PIPELINE_EXECUTION_CONTEXT,
                contextType
        );
    }
}
