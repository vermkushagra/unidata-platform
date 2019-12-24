package org.unidata.mdm.data.notification;

import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.context.GetRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.type.pipeline.PipelineInput;

/**
 * @author Alexander Malyshev
 */
public final class NotificationDataUtils {
    private NotificationDataUtils() { }

    public static String auditEventType(PipelineInput pipelineExecutionContext) {
        if (pipelineExecutionContext instanceof UpsertRequestContext) {
            return NotificationDataConstants.RECORD_UPSERT_EVENT_TYPE;
        }
        if (pipelineExecutionContext instanceof GetRequestContext) {
            return NotificationDataConstants.RECORD_GET_EVENT_TYPE;
        }
        if (pipelineExecutionContext instanceof DeleteRequestContext) {
            return NotificationDataConstants.RECORD_DELETE_EVENT_TYPE;
        }
        final String contextType = pipelineExecutionContext.getClass().getName();
        throw new PlatformFailureException(
                "Unknown context type " + contextType,
                DataExceptionIds.EX_DATA_AUDIT_UNKNOW_PIPELINE_EXECUTION_CONTEXT,
                contextType
        );
    }
}
